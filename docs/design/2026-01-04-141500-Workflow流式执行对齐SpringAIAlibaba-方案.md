# Workflow流式执行对齐Spring AI Alibaba重构方案

## 1. 问题背景

### 1.1 当前问题

SCM 工作流的流式执行存在严重 bug：前端收到空数组 `[]`，无法接收到任何 SSE 事件。

**日志证据**：
```
09:14:46.741 - [Flux调试] onStart被调用, 准备发送start事件
09:14:46.744 - [Flux调试] start事件已发送到fluxSink
09:15:14.075 - [Flux调试] onComplete被调用, 准备发送done事件
09:15:14.076 - [Flux调试] fluxSink.complete()已调用
09:15:14.131 - Resume with async result []  ← 空数组！
```

事件通过 `fluxSink.next()` 发送了，但 Spring MVC 收到的是空数组。

### 1.2 根因分析

当前 SCM 使用 `Flux.create()` + `FluxSink` + `subscribeOn(Schedulers.boundedElastic())` 的回调模式，这与 Spring MVC 处理 SSE 的机制不兼容。

Spring AI Alibaba 使用完全不同的模式：`Flux.just()` + `concatWith(Flux.defer())` 递归模式，事件是 Flux 管道的直接产物，不依赖外部回调。

---

## 2. 调用链路对比

### 2.1 SCM 当前调用链路（问题模式）

```
WorkflowController.run()
    └── workflowStarter.streaming()
            └── Flux.defer(() -> Flux.create(fluxSink -> {...}))  ← 问题1
                    └── subscribeOn(Schedulers.boundedElastic())   ← 问题2
                            └── WorkflowEngine.run() [void]
                                    └── streamHandler.sendXxx()    ← 问题3: 回调
                                            └── fluxSink.next()    ← 事件丢失！
```

**问题点**：
1. `Flux.create()` 捕获 `fluxSink` 到 lambda
2. `subscribeOn()` 导致订阅在不同线程
3. 回调模式：事件通过 `fluxSink.next()` 外部推送，不是 Flux 管道的一部分

### 2.2 Spring AI Alibaba 调用链路（目标模式）

```
GraphRunner.run()
    └── Flux.defer(() -> mainGraphExecutor.execute())
            └── Flux.just(startOutput)                    ← 直接产生事件
                    └── .concatWith(Flux.defer(() -> nodeExecutor.execute()))
                            └── Mono.fromFuture(action.apply())
                                    └── .flatMapMany(handleActionResult)
                                            └── Flux.just(nodeOutput)
                                                    └── .concatWith(Flux.defer(() -> execute()))  ← 递归
```

**核心特点**：
1. 无 `Flux.create()` - 不使用 FluxSink
2. 无 `subscribeOn()` - 异步通过 `Mono.fromFuture()` 处理
3. 无回调 - 事件通过 `Flux.just()` 直接产生
4. 递归模式 - `concatWith(Flux.defer())` 自然展开

---

## 3. 需要修改的文件清单

| 序号 | 文件路径 | 操作 | 说明 |
|------|----------|------|------|
| 1 | `scm-ai/.../workflow/WorkflowEngine.java` | 重构 | `run()` 从 `void` 改为返回 `Flux<WorkflowEventVo>` |
| 2 | `scm-ai/.../workflow/WorkflowStarter.java` | 重构 | 移除 `Flux.create()` 和 `subscribeOn()`，直接调用 Engine |
| 3 | `scm-ai/.../workflow/WorkflowStreamHandler.java` | 删除 | 不再需要回调机制 |

**保持不变的文件**：
- `AbstractWfNode.java` - `process()` 保持同步返回 `NodeProcessResult`
- `NodeProcessResult.java` - 保持现有结构
- `WorkflowEventVo.java` - 保持现有结构
- `WorkflowController.java` - 无需修改，仍然接收 `Flux<WorkflowEventVo>`

---

## 4. 详细设计

### 4.1 WorkflowEngine.java 重构

#### 4.1.1 核心变更：`run()` 方法返回 Flux

**当前签名**：
```java
public void run(Long userId, List<JSONObject> userInputs, String tenantCode, String parentConversationId)
```

**改为**：
```java
public Flux<WorkflowEventVo> run(Long userId, List<JSONObject> userInputs, String tenantCode, String parentConversationId)
```

#### 4.1.2 执行流程重构

```java
public Flux<WorkflowEventVo> run(Long userId, List<JSONObject> userInputs,
                                  String tenantCode, String parentConversationId) {
    return Flux.defer(() -> {
        // 1. 初始化（同步执行）
        this.userId = userId;
        this.tenantCode = tenantCode;
        DataSourceHelper.use(this.tenantCode);

        // 2. 创建 runtime 记录，获取 runtimeData
        String runtimeData = initializeRuntime(userId, userInputs, tenantCode, parentConversationId);

        // 3. 构建 StateGraph 并编译
        buildAndCompileStateGraph();

        // 4. 发送 start 事件，然后执行节点
        return Flux.just(WorkflowEventVo.createStartEvent(runtimeData))
                .concatWith(executeWorkflow());
    });
}
```

#### 4.1.3 节点执行改造

**当前**：使用 `AsyncGenerator` + for 循环遍历

```java
AsyncGenerator<NodeOutput> outputs = app.stream(Map.of(), config);
for (NodeOutput out : outputs) {
    // 处理输出
}
```

**改为**：使用 `Flux.defer()` + 递归

```java
private Flux<WorkflowEventVo> executeWorkflow() {
    return Flux.defer(() -> {
        try {
            // 获取下一个要执行的节点
            String nextNodeId = getNextNodeId();

            if (nextNodeId == null || END.equals(nextNodeId)) {
                // 工作流完成
                return handleWorkflowCompletion();
            }

            // 执行当前节点
            return executeNode(nextNodeId)
                    .concatWith(Flux.defer(this::executeWorkflow));  // 递归执行下一个
        } catch (Exception e) {
            return Flux.just(WorkflowEventVo.createErrorEvent(e.getMessage()));
        }
    });
}

private Flux<WorkflowEventVo> executeNode(String nodeId) {
    return Mono.fromCallable(() -> {
        // 同步执行节点
        AiWorkflowNodeVo wfNode = getNodeByUuid(nodeId);
        WfNodeState nodeState = new WfNodeState(wfState.data());
        return runNode(wfNode, nodeState);
    })
    .flatMapMany(result -> {
        // 节点执行完成，可以发送节点输出事件（如果需要）
        // 返回空 Flux，事件在完成时统一发送
        return Flux.empty();
    });
}
```

#### 4.1.4 工作流完成处理

```java
private Flux<WorkflowEventVo> handleWorkflowCompletion() {
    return Flux.defer(() -> {
        // 检查是否需要中断（人机交互）
        String nextInterruptNode = findNextInterruptNode();

        if (nextInterruptNode != null) {
            // 发送 NODE_WAIT_FEEDBACK 事件
            String tip = getHumanFeedbackTip(nextInterruptNode);
            InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);
            return Flux.just(WorkflowEventVo.createNodeWaitFeedbackEvent(nextInterruptNode, tip));
        }

        // 正常完成
        String completeData = buildCompleteData();
        return Flux.just(WorkflowEventVo.createDoneEvent(completeData));
    });
}
```

#### 4.1.5 恢复执行改造

```java
public Flux<WorkflowEventVo> resume(String userInput) {
    return Flux.defer(() -> {
        try {
            app.updateState(RunnableConfig.builder().build(),
                           Map.of(HUMAN_FEEDBACK_KEY, userInput), null);
            return executeWorkflow();
        } catch (Exception e) {
            return Flux.just(WorkflowEventVo.createErrorEvent(e.getMessage()));
        }
    });
}
```

### 4.2 WorkflowStarter.java 重构

#### 4.2.1 streaming() 方法简化

**当前**：
```java
public Flux<WorkflowEventVo> streaming(...) {
    return Flux.defer(() -> {
        return Flux.<WorkflowEventVo>create(fluxSink -> {
            WorkflowStreamHandler handler = new WorkflowStreamHandler(callback);
            runWorkflowInternal(..., handler);
        }, FluxSink.OverflowStrategy.BUFFER);
    })
    .subscribeOn(Schedulers.boundedElastic())
    .timeout(...)
    .onErrorResume(...)
    .doFinally(...);
}
```

**改为**：
```java
public Flux<WorkflowEventVo> streaming(String workflowUuid, List<JSONObject> userInputs,
                                        String tenantCode, WorkflowCallSource callSource,
                                        String conversationId, Map<String, Object> pageContext) {
    Long userId = SecurityUtil.getStaff_id();

    return Flux.defer(() -> {
        // 切换数据源
        DataSourceHelper.use(tenantCode);

        // 获取工作流配置
        AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);
        if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
            return Flux.just(WorkflowEventVo.createErrorEvent("工作流已禁用"));
        }

        // 获取组件、节点、边配置
        List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
        List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
        List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

        // 创建工作流引擎
        WorkflowEngine workflowEngine = new WorkflowEngine(
                workflow, components, nodes, edges, callSource,
                workflowRuntimeService, workflowRuntimeNodeService,
                conversationRuntimeService, conversationRuntimeNodeService
        );

        if (pageContext != null) {
            workflowEngine.setPageContext(pageContext);
        }

        // 直接调用引擎执行，返回 Flux
        return workflowEngine.run(userId, userInputs, tenantCode, conversationId);
    })
    .timeout(Duration.ofMinutes(30))
    .onErrorResume(TimeoutException.class, e ->
        Flux.just(WorkflowEventVo.createErrorEvent("工作流执行超时，已自动取消")))
    .onErrorResume(e ->
        Flux.just(WorkflowEventVo.createErrorEvent("工作流执行失败: " + e.getMessage())))
    .doFinally(signalType -> DataSourceHelper.close());
}
```

#### 4.2.2 resumeFlowAsFlux() 方法简化

```java
public Flux<WorkflowEventVo> resumeFlowAsFlux(String runtimeUuid, String workflowUuid,
                                               String userInput, String tenantId,
                                               WorkflowCallSource callSource,
                                               String conversationId) {
    WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);

    if (workflowEngine == null) {
        // 过期降级：重新开始工作流
        List<JSONObject> userInputs = List.of(
            new JSONObject().fluentPut("content", userInput)
        );
        return streaming(workflowUuid, userInputs, tenantId, callSource, conversationId, null);
    }

    return Flux.defer(() -> {
        String tenantCode = workflowEngine.getTenantCode();
        if (tenantCode != null) {
            DataSourceHelper.use(tenantCode);
        }
        return workflowEngine.resume(userInput);
    })
    .timeout(Duration.ofMinutes(30))
    .onErrorResume(TimeoutException.class, e -> {
        InterruptedFlow.RUNTIME_TO_GRAPH.remove(runtimeUuid);
        return Flux.just(WorkflowEventVo.createErrorEvent("工作流执行超时，已自动取消"));
    })
    .doFinally(signalType -> DataSourceHelper.close());
}
```

### 4.3 删除 WorkflowStreamHandler.java

整个文件删除，不再需要回调机制。

---

## 5. 架构对比图

### 5.1 重构前（回调模式）

```
┌─────────────────────────────────────────────────────────────┐
│  WorkflowController                                          │
│    ↓                                                        │
│  WorkflowStarter.streaming()                                │
│    ├── Flux.create(fluxSink -> ...)                        │
│    ├── subscribeOn(boundedElastic)  ← 线程切换              │
│    │                                                        │
│    └── runWorkflowInternal()                               │
│          ↓                                                  │
│        WorkflowEngine.run() [void]                          │
│          ├── streamHandler.sendStart()  ← 回调              │
│          ├── app.stream() → AsyncGenerator                  │
│          ├── for loop → streamingResult()                   │
│          └── streamHandler.sendComplete()  ← 回调           │
│                    ↓                                        │
│                fluxSink.next(event)  ← 事件丢失！           │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 重构后（Alibaba 模式）

```
┌─────────────────────────────────────────────────────────────┐
│  WorkflowController                                          │
│    ↓                                                        │
│  WorkflowStarter.streaming()                                │
│    └── Flux.defer(() -> workflowEngine.run())              │
│          ↓                                                  │
│        WorkflowEngine.run() → Flux<WorkflowEventVo>         │
│          ├── Flux.just(startEvent)  ← 直接产生事件          │
│          └── .concatWith(executeWorkflow())                 │
│                ├── Mono.fromCallable(runNode)               │
│                └── .concatWith(Flux.defer(executeWorkflow)) │
│                      ↓ 递归                                 │
│                    Flux.just(doneEvent)  ← 直接产生事件     │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. KISS 原则 7 问题回答

| # | 问题 | 回答 |
|---|------|------|
| 1 | 这是个真问题还是臆想出来的？ | **真问题** - 日志证明事件丢失，前端收到空数组 |
| 2 | 有更简单的方法吗？ | **这是最简方案** - 对齐成熟框架，消除回调复杂性 |
| 3 | 会破坏什么吗？ | **会破坏**现有调用方式，但用户明确不考虑兼容性 |
| 4 | 当前项目真的需要这个功能吗？ | **需要** - 流式输出是 AI 工作流核心功能 |
| 5 | 过度设计了吗？有缺少必要信息吗？ | **不是过度设计** - 是修复架构缺陷，信息充足 |
| 6 | 话题是否模糊，会导致幻觉？ | **不模糊** - 已完整分析 Alibaba 源码 |
| 7 | 是否已学习代码实施注意事项？ | **已学习** - 涉及 Service/Mapper 规范 |

---

## 7. 风险分析与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 节点执行异步性 | 当前节点在 `CompletableFuture.supplyAsync()` 中执行 | 使用 `Mono.fromCallable()` 包装同步调用 |
| 人机交互中断 | 需要保持工作流状态 | 保留 `InterruptedFlow.RUNTIME_TO_GRAPH` 缓存 |
| 子工作流调用 | `runSync()` 仍需同步返回结果 | 保留 `runSync()` 方法，使用 `blockLast()` 收集结果 |
| 数据源上下文 | ThreadLocal 在异步中丢失 | 在 `Flux.defer()` 内部显式设置 `DataSourceHelper.use()` |

---

## 8. 测试验证

重构完成后，验证以下场景：

1. **正常工作流执行**：前端能收到 `start` → `done` 事件
2. **人机交互节点**：能正确发送 `NODE_WAIT_FEEDBACK` 事件并暂停
3. **恢复执行**：用户输入后能继续执行并发送 `done` 事件
4. **错误处理**：异常时能发送 `error` 事件
5. **超时处理**：30 分钟超时能正确处理

---

## 9. 实施顺序

1. **删除** `WorkflowStreamHandler.java`
2. **重构** `WorkflowEngine.java` - 核心改造
3. **重构** `WorkflowStarter.java` - 适配新接口
4. **测试验证** - 各场景测试
