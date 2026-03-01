# WorkflowStarter流式执行修复方案

## 1. 问题诊断

### 1.1 问题现象
前端调用工作流streaming接口，收到空数组`[]`，没有任何SSE事件。

### 1.2 日志分析
```
17:20:52.328 UPDATE ai_workflow_runtime (before sendComplete)
17:20:52.332 MvcAsync3: "Async result set" (Flux considered complete!)
17:20:52.335 main-async-1: "WorkflowEngine执行完成"
17:20:52.348 "Resume with async result []" (empty array!)
```

### 1.3 根因分析
`@Async`方法`asyncRunWorkflow()`调用后立即返回，导致`Flux.create()`的lambda执行完成，Spring MVC认为Flux已complete，返回空数组给前端。

## 2. 完整调用链路

### 2.1 当前(有问题的)调用链路

```
Controller
    ↓
WorkflowStarter.streaming()
    ↓
Flux.create(fluxSink -> {
    streamHandler = new WorkflowStreamHandler(callback → fluxSink)
    handlerCache.put(executionId, streamHandler)
    self.asyncRunWorkflow(executionId, ...)  ← @Async立即返回!
})  ← lambda执行完成，Flux被认为complete
    ↓
Spring MVC订阅Flux
    ↓
Flux已complete，返回空数组[]

(此时@Async线程才开始执行WorkflowEngine.run())
```

### 2.2 修复后的调用链路

```
Controller
    ↓
WorkflowStarter.streaming()
    ↓
Flux.defer(() -> {  ← 延迟执行，订阅时才开始
    return Flux.create(fluxSink -> {
        streamHandler = new WorkflowStreamHandler(callback → fluxSink)
        runWorkflowInternal(...)  ← 同步执行，事件通过fluxSink发送
    })
}).subscribeOn(Schedulers.boundedElastic())  ← 异步订阅，不阻塞请求线程
    ↓
Spring MVC订阅Flux
    ↓
触发defer内部逻辑执行
    ↓
WorkflowEngine.run() 同步执行
    ↓
streamHandler.sendStart/sendComplete → fluxSink.next/complete
    ↓
SSE事件正确推送给前端
```

## 3. 文件级设计

### 3.1 需要修改的文件

| 文件 | 修改内容 |
|------|---------|
| `WorkflowStarter.java` | 1. `streaming()`方法重构为`Flux.defer()`模式<br>2. 移除`@Async asyncRunWorkflow()`<br>3. 新增内部方法`runWorkflowInternal()`<br>4. 移除`handlerCache`<br>5. 调整`resumeFlowAsFlux()`同步模式 |

### 3.2 不需要修改的文件

| 文件 | 原因 |
|------|------|
| `WorkflowEngine.java` | `run()`方法本身是同步的，不需要改动 |
| `WorkflowStreamHandler.java` | 回调机制不变 |
| `WorkflowEventVo.java` | 事件结构不变 |

## 4. 核心代码设计

### 4.1 streaming()方法重构

**Before (问题代码)**:
```java
public Flux<WorkflowEventVo> streaming(...) {
    String executionId = UUID.randomUUID().toString();

    Flux<WorkflowEventVo> flux = Flux.<WorkflowEventVo>create(fluxSink -> {
        WorkflowStreamHandler streamHandler = new WorkflowStreamHandler(
            new WorkflowStreamHandler.StreamCallback() {
                @Override
                public void onComplete(String data) {
                    fluxSink.next(WorkflowEventVo.createDoneEvent(data));
                    fluxSink.complete();
                }
                // ... other callbacks
            }
        );
        handlerCache.put(executionId, streamHandler);
        self.asyncRunWorkflow(executionId, ...);  // BUG: @Async立即返回
    })
    .subscribeOn(Schedulers.boundedElastic())
    .doFinally(signalType -> {
        handlerCache.remove(executionId);
        DataSourceHelper.close();
    });

    return flux;
}
```

**After (修复代码)**:
```java
public Flux<WorkflowEventVo> streaming(...) {
    Long userId = SecurityUtil.getStaff_id();

    return Flux.defer(() -> {
        return Flux.<WorkflowEventVo>create(fluxSink -> {
            WorkflowStreamHandler streamHandler = new WorkflowStreamHandler(
                new WorkflowStreamHandler.StreamCallback() {
                    @Override
                    public void onStart(String runtimeData) {
                        fluxSink.next(WorkflowEventVo.createStartEvent(runtimeData));
                    }
                    @Override
                    public void onNodeWaitFeedback(String nodeUuid, String tip) {
                        fluxSink.next(WorkflowEventVo.createNodeWaitFeedbackEvent(nodeUuid, tip));
                    }
                    @Override
                    public void onComplete(String data) {
                        fluxSink.next(WorkflowEventVo.createDoneEvent(data));
                        fluxSink.complete();
                    }
                    @Override
                    public void onError(Throwable error) {
                        fluxSink.error(error);
                    }
                }
            );

            try {
                runWorkflowInternal(workflowUuid, userId, userInputs, tenantCode,
                    callSource, conversationId, pageContext, streamHandler);
            } catch (Exception e) {
                log.error("工作流执行异常: workflowUuid={}", workflowUuid, e);
                streamHandler.sendError(e);
            }
        }, FluxSink.OverflowStrategy.BUFFER);
    })
    .subscribeOn(Schedulers.boundedElastic())
    .doFinally(signalType -> {
        DataSourceHelper.close();
    });
}
```

### 4.2 新增runWorkflowInternal()方法

将原`asyncRunWorkflow()`的逻辑移动到同步方法中：

```java
/**
 * 内部同步执行工作流
 *
 * @param workflowUuid 工作流UUID
 * @param userId 用户ID
 * @param userInputs 用户输入
 * @param tenantCode 租户编码
 * @param callSource 调用来源
 * @param conversationId 对话ID
 * @param pageContext 页面上下文
 * @param streamHandler 流式处理器
 */
private void runWorkflowInternal(String workflowUuid,
                                  Long userId,
                                  List<JSONObject> userInputs,
                                  String tenantCode,
                                  WorkflowCallSource callSource,
                                  String conversationId,
                                  Map<String, Object> pageContext,
                                  WorkflowStreamHandler streamHandler) {
    // 切换数据源
    DataSourceHelper.use(tenantCode);

    // 获取工作流配置
    AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

    if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
        streamHandler.sendError(new BusinessException("工作流已禁用"));
        return;
    }

    // 获取组件、节点、边配置
    List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
    List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
    List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

    // 创建工作流引擎
    WorkflowEngine workflowEngine = new WorkflowEngine(
        workflow, streamHandler, components, nodes, edges, callSource,
        workflowRuntimeService, workflowRuntimeNodeService,
        conversationRuntimeService, conversationRuntimeNodeService
    );

    if (pageContext != null) {
        workflowEngine.setPageContext(pageContext);
    }

    // 同步执行工作流
    workflowEngine.run(userId, userInputs, tenantCode, conversationId);

    // 更新测试时间(仅WORKFLOW_TEST模式)
    if (callSource == WorkflowCallSource.WORKFLOW_TEST) {
        try {
            workflowService.updateTestTime(workflowUuid, tenantCode);
        } catch (Exception e) {
            log.error("更新测试时间失败: {}", workflowUuid, e);
        }
    }
}
```

### 4.3 移除的代码

1. **移除`handlerCache`字段**:
   ```java
   // 删除
   private final ConcurrentHashMap<String, WorkflowStreamHandler> handlerCache
           = new ConcurrentHashMap<>();
   ```

2. **移除`asyncRunWorkflow()`方法** (整个方法删除)

3. **移除`self`自注入** (如果不再需要)

### 4.4 resumeFlowAsFlux()调整

当前实现使用`CompletableFuture.runAsync()`，需要调整为类似的`Flux.defer()`模式：

```java
public Flux<WorkflowEventVo> resumeFlowAsFlux(...) {
    WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);

    if (workflowEngine == null) {
        // 过期处理：重新开始工作流
        List<JSONObject> userInputs = List.of(new JSONObject().fluentPut("content", userInput));
        return streaming(workflowUuid, userInputs, tenantId, callSource, conversationId);
    }

    return Flux.defer(() -> {
        return Flux.<WorkflowEventVo>create(fluxSink -> {
            WorkflowStreamHandler newHandler = new WorkflowStreamHandler(
                new WorkflowStreamHandler.StreamCallback() {
                    // ... 回调实现同上
                }
            );

            workflowEngine.setStreamHandler(newHandler);

            try {
                String tenantCode = workflowEngine.getTenantCode();
                if (tenantCode != null) {
                    DataSourceHelper.use(tenantCode);
                }
                workflowEngine.resume(userInput);
            } catch (Exception e) {
                log.error("工作流恢复执行失败, runtimeUuid={}", runtimeUuid, e);
                newHandler.sendError(e);
            }
        }, FluxSink.OverflowStrategy.BUFFER);
    })
    .subscribeOn(Schedulers.boundedElastic())
    .timeout(Duration.ofMinutes(30))
    .onErrorResume(TimeoutException.class, e -> {
        log.warn("工作流恢复执行超时: runtimeUuid={}", runtimeUuid);
        InterruptedFlow.RUNTIME_TO_GRAPH.remove(runtimeUuid);
        return Flux.just(WorkflowEventVo.createErrorEvent("工作流执行超时，已自动取消"));
    })
    .doFinally(signalType -> {
        DataSourceHelper.close();
    });
}
```

## 5. KISS原则7问题回答

| # | 问题 | 回答 |
|---|------|------|
| 1 | 这是真问题还是臆想？ | **真问题** - 日志明确显示"Resume with async result []"，前端收不到任何事件 |
| 2 | 有更简单的方法吗？ | **是最简方案** - 使用`Flux.defer()`替代`Flux.create()+@Async`是Spring Reactor标准模式 |
| 3 | 会破坏什么吗？ | **向后兼容** - 接口签名不变，只改内部实现；resumeFlowAsFlux需同步调整 |
| 4 | 当前项目真的需要？ | **必须修复** - 否则工作流功能完全不可用 |
| 5 | 是否过度设计？信息够吗？ | **信息充足** - 有日志、源码对比、Alibaba参考实现 |
| 6 | 话题是否模糊？ | **清晰明确** - 问题定位精准，方案有据可依 |
| 7 | 是否学习了代码实施注意事项？ | **已学习** - 本次主要是reactive模式重构，不涉及数据库操作 |

## 6. 风险分析

| 风险 | 等级 | 缓解措施 |
|------|------|---------|
| 线程阻塞 | 低 | 使用`subscribeOn(Schedulers.boundedElastic())`确保异步执行 |
| 数据源上下文丢失 | 中 | 在defer内部显式调用`DataSourceHelper.use()` |
| resumeFlowAsFlux遗漏 | 中 | 同步重构resumeFlowAsFlux方法 |

## 7. 测试计划

1. 单元测试：验证`streaming()`返回的Flux能正确发送事件
2. 集成测试：前端调用workflow streaming接口，确认收到SSE事件
3. 回归测试：验证resumeFlowAsFlux在人机交互场景正常工作

## 8. 变更清单

| 文件 | 操作 | 变更内容 |
|------|------|---------|
| `WorkflowStarter.java` | 修改 | 重构streaming/resumeFlowAsFlux，移除@Async |

