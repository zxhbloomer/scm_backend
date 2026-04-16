# Sink 全面替换实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 SCM 工作流引擎中的 `reactor.core.publisher.Sinks.Many` 全面替换为 `Flux.create(FluxSink)`，消除竞态问题，简化架构。

**Architecture:** 用单条 `Flux.create` 流统一承载所有事件（node_start/complete、LLM chunk、人机交互、OpenPage），删除 `sinkRef` 字段和 `Flux.merge` 结构。LLM 流式输出改为 alibaba 框架原生 `getEmbedFlux` 机制（节点返回 `Flux<ChatResponse>`，框架自动转为 `StreamingOutput`）。

**Tech Stack:** Java 17, Spring Boot 3.1.4, Reactor Core 3.7.x (`Flux.create`, `FluxSink`, `Disposable`), Spring AI Alibaba (`GraphLifecycleListener`, `NodeExecutor.getEmbedFlux`)

---

## Chunk 1: WfState + NodeProcessResult 基础改动

### Task 1: 修改 `WfState.java` — eventSink 类型替换

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java`

**当前代码（line 112-141）：**
```java
import reactor.core.publisher.Sinks;
private transient Sinks.Many<WorkflowEventVo> eventSink;
public Sinks.Many<WorkflowEventVo> getEventSink() { return eventSink; }
public void setEventSink(Sinks.Many<WorkflowEventVo> eventSink) { this.eventSink = eventSink; }
```

- [ ] **Step 1: 修改 `WfState.java` 的 import**

将 `import reactor.core.publisher.Sinks;` 替换为 `import reactor.core.publisher.FluxSink;`

- [ ] **Step 2: 修改 eventSink 字段类型**

```java
// 改前
private transient Sinks.Many<WorkflowEventVo> eventSink;

// 改后
private transient FluxSink<WorkflowEventVo> eventSink;
```

- [ ] **Step 3: 修改 getter/setter**

```java
// 改前
public Sinks.Many<WorkflowEventVo> getEventSink() { return eventSink; }
public void setEventSink(Sinks.Many<WorkflowEventVo> eventSink) { this.eventSink = eventSink; }

// 改后
public FluxSink<WorkflowEventVo> getEventSink() { return eventSink; }
public void setEventSink(FluxSink<WorkflowEventVo> eventSink) { this.eventSink = eventSink; }
```

- [ ] **Step 4: 修改注释**

```java
// 改前
/** 事件Sink引用，供LLM流式调用时发送chunk事件到前端 */

// 改后
/** FluxSink引用，供NodeEventListener和handleInterruption发送事件到前端 */
```

---

### Task 2: 修改 `NodeProcessResult.java` — 新增 streamingFlux 字段

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/NodeProcessResult.java`

- [ ] **Step 1: 新增 import**

在文件顶部 import 区域新增：
```java
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
```

- [ ] **Step 2: 新增 streamingFlux 字段**

在 `nextSourceHandle` 字段之后新增：
```java
/**
 * LLM流式输出Flux（非silentMode时由LLMAnswerNode设置）
 * WorkflowEngine.runNode()检测此字段，若非null则放入返回Map供框架getEmbedFlux使用
 */
private Flux<ChatResponse> streamingFlux;
```

---

## Chunk 2: WorkflowUtil — streamingInvokeLLM 返回 Flux

### Task 3: 修改 `WorkflowUtil.java` — streamingInvokeLLM 签名与实现

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowUtil.java`

**当前签名（line 149-150）：**
```java
public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                       String modelName, String prompt, boolean silentMode)
```

**当前流式处理（line 253-286）：**
```java
streamSpec.chatResponse()
    .doOnNext(chatResponse -> {
        ...
        if (!silentMode && wfState.getEventSink() != null) {
            wfState.markNodeStreamed(node.getUuid());
            wfState.getEventSink().tryEmitNext(
                WorkflowEventVo.createChunkData(node.getUuid(), content)
            );
        }
        ...
    })
    ...
    .blockLast();
```

- [ ] **Step 1: 修改方法签名返回类型**

```java
// 改前
public static void streamingInvokeLLM(...)

// 改后
public static Flux<ChatResponse> streamingInvokeLLM(...)
```

- [ ] **Step 2: 修改 silentMode=true 路径（MCP工具节点已在前面 return，不受影响）**

`silentMode=true` 时保持 `blockLast()`，方法末尾 `return null`：

在方法末尾（当前 `blockLast()` 之后的逻辑不变），在方法最后 `return` 语句处：
```java
// silentMode=true：保持 blockLast，返回 null（调用方不放 Flux 进 Map）
if (silentMode) {
    streamSpec.chatResponse()
        .doOnNext(chatResponse -> { /* 原有 doOnNext 逻辑，去掉 tryEmitNext 部分 */ })
        .doOnComplete(...)
        .doOnError(...)
        .doOnCancel(...)
        .timeout(java.time.Duration.ofSeconds(120))
        .blockLast();
    // 保存 token、构建 output 等原有逻辑
    return null;
}
```

- [ ] **Step 3: 修改 silentMode=false 路径 — 不 blockLast，返回 Flux**

```java
// silentMode=false：不 blockLast，返回 Flux<ChatResponse>（含副作用）
return streamSpec.chatResponse()
    .doOnNext(chatResponse -> {
        if (wfState.getTenantCode() != null) {
            DataSourceHelper.use(wfState.getTenantCode());
        }
        String content = chatResponse.getResult().getOutput().getText();
        if (StringUtils.isNotEmpty(content)) {
            fullResponse.append(content);
            wfState.markNodeStreamed(node.getUuid());
        }
        if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
            finalUsage[0] = chatResponse.getMetadata().getUsage();
        }
    })
    .doOnComplete(() -> {
        log.debug("LLM Flux完成(onComplete)");
        recordWorkflowTokenUsage(wfState, node, finalUsage[0], modelConfig, startTime);
        if (finalUsage[0] != null) {
            long pt = finalUsage[0].getPromptTokens() != null ? finalUsage[0].getPromptTokens() : 0;
            long ct = finalUsage[0].getCompletionTokens() != null ? finalUsage[0].getCompletionTokens() : 0;
            wfState.recordNodeTokens(node.getUuid(), pt, ct);
        }
        String response = fullResponse.toString().trim();
        NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", response);
        nodeState.getOutputs().add(output);
    })
    .doOnError(e -> log.error("LLM Flux发生错误(onError)", e))
    .doOnCancel(() -> log.warn("LLM Flux被取消(onCancel)"))
    .timeout(java.time.Duration.ofSeconds(120));
```

**注意**：`silentMode=false` 路径中，`tryEmitNext` 调用完全删除（chunk 由框架 `getEmbedFlux` 直接输出）。`fullResponse`、`finalUsage`、`nodeState.getOutputs().add(output)` 等副作用移入 `doOnNext`/`doOnComplete`。

- [ ] **Step 4: 修改无参重载方法（如果存在）**

检查是否有 `streamingInvokeLLM(wfState, nodeState, node, modelName, prompt)` 无参重载，若有则同步更新为调用 `silentMode=false` 版本并返回 `Flux<ChatResponse>`。

---

## Chunk 3: LLMAnswerNode — 接收并传递 streamingFlux

### Task 4: 修改 `LLMAnswerNode.java`

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/answer/LLMAnswerNode.java`

**当前代码（line 43-45）：**
```java
WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, prompt);
return new NodeProcessResult();
```

- [ ] **Step 1: 修改 onProcess() 接收 Flux 并放入 NodeProcessResult**

```java
// 改前
WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, prompt);
return new NodeProcessResult();

// 改后
Flux<ChatResponse> streamingFlux = WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, prompt, false);
return NodeProcessResult.builder().streamingFlux(streamingFlux).build();
```

- [ ] **Step 2: 新增 import**

```java
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
```

---

## Chunk 4: WorkflowEngine — runNode 集成 streamingFlux

### Task 5: 修改 `WorkflowEngine.java` — runNode() 检测 streamingFlux

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**当前 runNode() 末尾（line 863-879）：**
```java
// 7. 设置节点名称
resultMap.put("name", wfNode.getTitle());
// 8. 将节点输出放入返回Map
String outputKey = NODE_OUTPUT_KEY_PREFIX + wfNode.getUuid();
resultMap.put(outputKey, nodeState.getOutputs());
// 缓存节点输出/输入
nodeOutputCache.put(wfNode.getUuid(), nodeState.getOutputs());
nodeInputCache.put(wfNode.getUuid(), nodeState.getInputs());
...
return resultMap;
```

- [ ] **Step 1: 在 `resultMap.put("name", ...)` 之前插入 streamingFlux 检测**

```java
// 检测 streamingFlux：若非 null，放入 Map 供框架 getEmbedFlux 使用（LLM 打字机效果）
if (processResult.getStreamingFlux() != null) {
    resultMap.put(wfNode.getUuid() + "_flux", processResult.getStreamingFlux());
}
```

---

## Chunk 5: WorkflowEngine — executeWorkflow 改 Flux.create

### Task 6: 修改 `WorkflowEngine.java` — executeWorkflow() 核心改造

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**当前 executeWorkflow() 核心（line 345-394）：**
```java
Sinks.Many<WorkflowEventVo> localSink = sinkRef.get();
Flux<WorkflowEventVo> graphEventFlux = graphStream
    .doOnSubscribe(...)
    .doFirst(...)
    .doOnNext(...)
    .doOnComplete(() -> {
        updateWorkflowComplete();
        if (... && localSink != null) { localSink.tryEmitNext(...); }
        if (localSink != null) { localSink.tryEmitComplete(); }
    })
    .doOnError(e -> { if (localSink != null) { localSink.tryEmitError(e); } })
    .doOnCancel(...)
    .flatMap(graphResponse -> handleGraphResponse(graphResponse))
    .doOnNext(...)
    .doOnSubscribe(...)
    .doOnComplete(...);
Flux<WorkflowEventVo> nodeEventFlux = (localSink != null) ? localSink.asFlux() : Flux.empty();
return Flux.merge(graphEventFlux, nodeEventFlux);
```

- [ ] **Step 1: 将整个 `Sinks.Many<WorkflowEventVo> localSink = sinkRef.get()` 到 `return Flux.merge(...)` 替换为 `Flux.create`**

```java
return Flux.create(fluxSink -> {
    // 将 fluxSink 注入 wfState，供 NodeEventListener.before/after 使用
    wfState.setEventSink(fluxSink);

    Disposable d = graphStream
        .doOnSubscribe(sub -> log.info("[graphStream] 已被订阅"))
        .doFirst(() -> log.info("[graphStream] 开始执行"))
        .doOnNext(resp -> log.info("[graphStream] 收到元素: isDone={}, isError={}, hasOutput={}",
                resp.isDone(), resp.isError(), resp.getOutput() != null))
        .doOnCancel(() -> log.warn("[graphStream] 被取消"))
        .flatMap(graphResponse -> {
            log.info("[flatMap] 处理graphResponse, isDone={}, isError={}",
                    graphResponse.isDone(), graphResponse.isError());
            return handleGraphResponse(graphResponse)
                    .doOnNext(evt -> log.info("[flatMap] handleGraphResponse返回事件"))
                    .doOnComplete(() -> log.debug("[flatMap] handleGraphResponse Flux完成"));
        })
        .doOnNext(evt -> log.info("[executeWorkflow] flatMap后收到事件"))
        .doOnComplete(() -> {
            log.info("[graphStream] 完成");
            // 工作流完成时更新状态
            updateWorkflowComplete();
            // OpenPage节点：将JSON数据通过事件流传递给前端
            if (wfState.getAi_open_dialog_para() != null
                    || wfState.getOpen_page_command() != null
                    || wfState.getInteraction_request() != null) {
                fluxSink.next(
                    WorkflowEventVo.createAiOpenDialogParaEvent(
                        wfState.getAi_open_dialog_para(),
                        wfState.getOpen_page_command(),
                        wfState.getInteraction_request())
                );
            }
            fluxSink.complete();
        })
        .subscribe(
            fluxSink::next,
            fluxSink::error,
            () -> {} // complete 已在 doOnComplete 里处理
        );

    // SSE 断开时取消工作流执行
    fluxSink.onDispose(d::dispose);
});
```

- [ ] **Step 2: 新增 import**

```java
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;
```

---

## Chunk 6: WorkflowEngine — start() + resume() + NodeEventListener 清理

### Task 7: 修改 `WorkflowEngine.java` — start() 删除 Sink 创建

**文件：**
- 修改: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**当前 start() 中（line 284-286）：**
```java
Sinks.Many<WorkflowEventVo> newSink = Sinks.many().replay().all();
sinkRef.set(newSink);
wfState.setEventSink(newSink);
```

- [ ] **Step 1: 删除以上 3 行**

删除后，`CompileConfig compileConfig = CompileConfig.builder()...` 直接跟在 `buildStateGraph(mainStateGraph, startNode)` 之后。

---

### Task 8: 修改 `WorkflowEngine.java` — resume() 删除 Sink 重建

**当前 resume() 中（line 739-742）：**
```java
// resume时重新创建Sink：上一次执行已tryEmitComplete()，旧Sink已终止
Sinks.Many<WorkflowEventVo> newSink = Sinks.many().replay().all();
sinkRef.set(newSink);
wfState.setEventSink(newSink);
```

- [ ] **Step 1: 删除以上 4 行（含注释）**

`Flux.create` 每次调用 `executeWorkflow` 时自动创建新的 `fluxSink`，无需手动重建。

---

### Task 9: 修改 `WorkflowEngine.java` — NodeEventListener 改用 fluxSink

**当前 NodeEventListener.before()（line 1529-1532）：**
```java
Sinks.Many<WorkflowEventVo> sink = sinkRef.get();
if (sink != null) {
    sink.emitNext(..., Sinks.EmitFailureHandler.busyLooping(java.time.Duration.ofSeconds(5)));
}
```

**当前 NodeEventListener.after()（line 1550-1553）：**
```java
Sinks.Many<WorkflowEventVo> sink = sinkRef.get();
if (sink != null) {
    sink.emitNext(..., Sinks.EmitFailureHandler.busyLooping(java.time.Duration.ofSeconds(5)));
}
```

- [ ] **Step 1: 修改 before() 中的 Sink 调用**

```java
// 改前
Sinks.Many<WorkflowEventVo> sink = sinkRef.get();
if (sink != null) {
    sink.emitNext(WorkflowEventVo.createNodeStartData(nodeId, componentName, nodeTitle, curTime),
        Sinks.EmitFailureHandler.busyLooping(java.time.Duration.ofSeconds(5)));
}

// 改后
FluxSink<WorkflowEventVo> sink = wfState.getEventSink();
if (sink != null) {
    sink.next(WorkflowEventVo.createNodeStartData(nodeId, componentName, nodeTitle, curTime));
}
```

- [ ] **Step 2: 修改 after() 中的 Sink 调用**

```java
// 改前
Sinks.Many<WorkflowEventVo> sink = sinkRef.get();
if (sink != null) {
    sink.emitNext(WorkflowEventVo.createNodeCompleteData(nodeId, componentName, nodeTitle, duration, summary),
        Sinks.EmitFailureHandler.busyLooping(java.time.Duration.ofSeconds(5)));
}

// 改后
FluxSink<WorkflowEventVo> sink = wfState.getEventSink();
if (sink != null) {
    sink.next(WorkflowEventVo.createNodeCompleteData(nodeId, componentName, nodeTitle, duration, summary));
}
```

---

### Task 10: 修改 `WorkflowEngine.java` — 删除 sinkRef 字段和 Sinks import

- [ ] **Step 1: 删除 sinkRef 字段（line 89）**

```java
// 删除此字段
private final AtomicReference<Sinks.Many<WorkflowEventVo>> sinkRef = new AtomicReference<>();
```

- [ ] **Step 2: 删除 Sinks import（line 23）**

```java
// 删除
import reactor.core.publisher.Sinks;
```

- [ ] **Step 3: 检查 AtomicReference import 是否还有其他用途**

搜索 `AtomicReference` 在 `WorkflowEngine.java` 中的其他使用，若无则删除 `import java.util.concurrent.atomic.AtomicReference`。

---

## Chunk 7: 验证与收尾

### Task 11: 全局搜索确认无残留 Sinks 引用

- [ ] **Step 1: 搜索 scm-ai 模块中所有 Sinks 引用**

在 `scm-ai/src/main/java/` 目录下搜索 `Sinks`，确认只有以下文件可能还有引用（应全部为 0）：
- `WorkflowEngine.java`
- `WfState.java`
- `WorkflowUtil.java`

- [ ] **Step 2: 搜索 tryEmitNext / tryEmitComplete / tryEmitError**

确认 scm-ai 模块中无任何 `tryEmitNext`、`tryEmitComplete`、`tryEmitError` 调用。

- [ ] **Step 3: 搜索 sinkRef**

确认 `WorkflowEngine.java` 中无 `sinkRef` 引用。

---

### Task 12: Commit

- [ ] **Step 1: 提交所有改动**

```bash
git -C 00_scm_backend/scm_backend add \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/NodeProcessResult.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowUtil.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/answer/LLMAnswerNode.java
git -C 00_scm_backend/scm_backend commit -m "refactor(ai): 全面替换Sinks为Flux.create，LLM流式输出改用框架原生getEmbedFlux"
```
