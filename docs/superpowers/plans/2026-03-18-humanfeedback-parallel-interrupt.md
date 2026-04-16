# HumanFeedback 并行分支中断修复 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 HumanFeedback 节点在 ConditionalParallelNode（并行分支）路径中无法触发中断的问题，使其与非并行路径行为一致。

**Architecture:** 利用 `GraphLifecycleListener.after()` 作为并行路径唯一可用的框架扩展点。`HumanFeedbackNode.onProcess()` 在无用户输入时向 `WfState` 写入等待标志，`NodeEventListener.after()` 检测到该标志后触发中断逻辑（构建 DB 记录 + 发送 SSE 事件），`handleGraphResponse()` 处理 `__PARALLEL__` 输出时检测标志并跳过 output 事件。

**Tech Stack:** Java 17, Spring AI Alibaba (GraphLifecycleListener), Project Reactor (Flux/FluxSink), Spring Boot 3.1.4

---

## 背景与根因

`ConditionalParallelNode.evalNodeActionSync()` 直接调用 `action.apply()`，绕过了：
- `InterruptableAction` 接口检查
- `interruptsBefore` 配置（`MainGraphExecutor.shouldInterrupt()`）
- `NodeHooks`（`WrapCall`/`BeforeCall`/`AfterCall`）

但 `GraphLifecycleListener.after()` 通过 `LifeListenerUtil.processListenersLIFO()` 在并行路径内**仍然被调用**，这是唯一可用的扩展点。

## 涉及文件

| 文件 | 操作 | 说明 |
|------|------|------|
| `scm-ai/.../workflow/WfState.java` | 修改 | 添加 `parallelInterruptFired` 原子标志，防止多个并行子节点重复触发中断 |
| `scm-ai/.../workflow/node/humanfeedback/HumanFeedbackNode.java` | 修改 | `onProcess()` 无用户输入时设置 `wfState.waitingInteraction=true` |
| `scm-ai/.../workflow/WorkflowEngine.java` | 修改 | `NodeEventListener.after()` 检测并触发中断；`handleGraphResponse()` 的 `__PARALLEL__` 块加 `waitingInteraction` 检测 |

**前端：无需修改**（`AiInteractionManager.js`、`chat.js`、`workflowApi.js` 已完整支持 `interaction_request` SSE 事件）

---

## Task 1：WfState 添加并行中断原子锁

**目的：** 防止多个并行子节点同时完成时，`after()` 被多次调用导致重复发送中断 SSE。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java`

- [ ] **Step 1: 在 WfState 中添加原子锁字段和方法**

在 `WfState.java` 的 `streamedNodeUuids` 字段（第122行）附近添加：

```java
/**
 * 并行路径人机交互中断锁
 * 防止多个并行子节点同时完成时重复触发中断
 * true=已触发中断，false=未触发
 */
private final java.util.concurrent.atomic.AtomicBoolean parallelInterruptFired =
    new java.util.concurrent.atomic.AtomicBoolean(false);

/**
 * 尝试触发并行中断（CAS操作，只有第一个调用者返回true）
 * @return true=本次调用成功抢占中断权，false=已被其他线程抢占
 */
public boolean tryFireParallelInterrupt() {
    return parallelInterruptFired.compareAndSet(false, true);
}
```

---

## Task 2：HumanFeedbackNode 写入等待标志

**目的：** 当节点在并行路径中执行且无用户输入时，向 `WfState` 写入中断信号，供 `NodeEventListener.after()` 检测。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNode.java`

- [ ] **Step 1: 修改 `onProcess()` 的无输入分支**

找到第 39-42 行：
```java
if (feedbackData == null) {
    log.warn("人机交互节点未获取到用户反馈, nodeUuid: {}", node.getUuid());
    return NodeProcessResult.builder().content(List.of()).build();
}
```

替换为：
```java
if (feedbackData == null) {
    log.info("人机交互节点等待用户输入（并行路径），设置等待标志, nodeUuid: {}", node.getUuid());
    wfState.setWaitingInteraction(true);
    return NodeProcessResult.builder().content(List.of()).build();
}
```

**说明：** 不在这里构建 `interaction_request`，因为 `handleParallelInterruption()` 会从节点配置重新读取，保持与非并行路径一致的逻辑。

---

## Task 3：WorkflowEngine 添加 handleParallelInterruption() 方法

**目的：** 提取并行路径专用的中断处理方法，复用 `handleInterruption()` 的核心逻辑，但不依赖 `GraphResponse`，且不调用 `eventSink.complete()`（避免提前关闭 SSE 流）。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

- [ ] **Step 1: 在 `handleInterruption()` 方法之后添加 `handleParallelInterruption()` 方法**

在第 847 行（`handleInterruption` 结束的 `}` 之后）插入：

```java
/**
 * 处理并行路径人机交互中断（通过 NodeEventListener.after() 触发）
 *
 * 与 handleInterruption(GraphResponse) 逻辑相同，但：
 * 1. 不依赖 GraphResponse，直接接收 nodeUuid
 * 2. 不调用 eventSink.complete()，避免提前关闭 SSE 流
 *    （SSE 流由 graphStream 的 doOnComplete 自然关闭）
 *
 * @param interruptNodeUuid 中断节点UUID
 */
private void handleParallelInterruption(String interruptNodeUuid) {
    try {
        DataSourceHelper.use(this.tenantCode);

        InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);
        wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_READY);
        if (this.wfRuntimeResp != null) {
            workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
        } else if (this.conversationRuntimeResp != null) {
            conversationRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
        }

        // 读取节点配置（与 handleInterruption 相同逻辑）
        HumanFeedbackNodeConfig nodeConfig = getHumanFeedbackConfig(interruptNodeUuid);
        String interactionType = nodeConfig.getEffectiveInteractionType();
        String tip = nodeConfig.getTip() != null ? nodeConfig.getTip() : "请输入您的反馈";
        JSONObject interactionParams = buildInteractionParams(nodeConfig, interactionType);
        String dbInteractionType = "user_" + interactionType;

        // 创建DB交互记录
        JSONObject interactionRequest = null;
        if (interactionService != null) {
            try {
                com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowInteractionEntity entity =
                    interactionService.createInteraction(
                        wfState.getConversationId(),
                        wfState.getUuid(),
                        interruptNodeUuid,
                        dbInteractionType,
                        interactionParams.toJSONString(),
                        tip,
                        nodeConfig.getEffectiveTimeoutMinutes()
                    );
                // 构建 interaction_request JSON（与 handleInterruption 内联逻辑相同）
                interactionRequest = new JSONObject();
                interactionRequest.put("interaction_uuid", entity.getInteractionUuid());
                interactionRequest.put("type", dbInteractionType);
                interactionRequest.put("description", tip);
                interactionRequest.put("timeout_minutes", entity.getTimeoutMinutes());
                interactionRequest.put("timeout_at", entity.getTimeoutAt() != null
                    ? entity.getTimeoutAt().toString() : null);
                interactionRequest.put("params", interactionParams);
                log.info("并行路径人机交互DB记录已创建: interactionUuid={}, type={}",
                    entity.getInteractionUuid(), dbInteractionType);
            } catch (Exception e) {
                log.error("创建并行路径人机交互DB记录失败，降级为基础中断", e);
            }
        }

        // 发送 SSE 中断事件（使用与非并行路径相同的事件类型）
        // 注意：不调用 eventSink.complete()，SSE 流由 graphStream 的 doOnComplete 自然关闭
        if (eventSink != null) {
            WorkflowEventVo interruptEvent = interactionRequest != null
                ? WorkflowEventVo.createInterruptDataWithInteraction(
                    interruptNodeUuid, tip, interactionType, interactionRequest)
                : WorkflowEventVo.createInterruptData(interruptNodeUuid, tip);
            eventSink.next(interruptEvent);
            log.info("并行路径中断事件已发送, nodeUuid={}", interruptNodeUuid);
        }
    } catch (Exception e) {
        log.error("处理并行路径中断失败, nodeUuid={}", interruptNodeUuid, e);
    }
}
```

- [ ] **Step 2: 确认 `getHumanFeedbackConfig()` 方法存在**

在 `WorkflowEngine.java` 中搜索 `getHumanFeedbackConfig`，确认该私有方法存在。如果不存在，需要添加（参考 `handleInterruption()` 中读取节点配置的逻辑）。

---

## Task 4：NodeEventListener.after() 触发并行中断

**目的：** 在 `after()` 回调中检测 `wfState.waitingInteraction`，触发并行路径中断。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

- [ ] **Step 1: 修改 `NodeEventListener.after()` 方法**

找到第 1784-1787 行的 `after()` 方法：
```java
@Override
public void after(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
    String componentName = findComponentName(nodeId);
    log.debug("[NodeEventListener.after] nodeId={}, componentName={}, thread={}", nodeId, componentName, Thread.currentThread().getName());
}
```

替换为：
```java
@Override
public void after(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
    String componentName = findComponentName(nodeId);
    log.debug("[NodeEventListener.after] nodeId={}, componentName={}, thread={}", nodeId, componentName, Thread.currentThread().getName());

    // 并行路径 HumanFeedback 中断检测
    // 条件说明：
    // 1. wfState.isWaitingInteraction() - HumanFeedbackNode 设置了等待标志
    // 2. humanFeedbackNodeUuids.contains(nodeId) - 当前节点是 HumanFeedback 节点
    // 3. !wfState.getInterruptNodes().contains(nodeId) - 排除非并行路径
    //    （非并行路径通过 interruptsBefore 注册到 interruptNodes，由 handleInterruption(GraphResponse) 处理）
    // 4. wfState.tryFireParallelInterrupt() - CAS 保证只触发一次（多个并行子节点竞争时）
    if (wfState != null
            && wfState.isWaitingInteraction()
            && humanFeedbackNodeUuids.contains(nodeId)
            && !wfState.getInterruptNodes().contains(nodeId)
            && wfState.tryFireParallelInterrupt()) {
        log.info("[NodeEventListener.after] 检测到并行路径中断, nodeId={}", nodeId);
        handleParallelInterruption(nodeId);
    }
}
```

**说明：**
- `!wfState.getInterruptNodes().contains(nodeId)` 是关键保护条件：非并行路径的 HumanFeedback 节点通过 `interruptsBefore` 注册到 `interruptNodes`，`interruptsBefore` 会在节点执行前中断，`onProcess()` 根本不会被调用，所以 `waitingInteraction` 不会被设置。但为了双重保险，加上这个条件。
- `humanFeedbackNodeUuids` 是外部类字段，内部类可直接访问。

---

## Task 5：handleGraphResponse 跳过并行中断后的 output 事件

**目的：** 当 `wfState.waitingInteraction=true` 时，`__PARALLEL__` 输出处理块不发送 output 事件，避免中断后继续推进工作流。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

- [ ] **Step 1: 在 `__PARALLEL__` 处理块开头加检测**

找到第 701 行：
```java
if (nodeId.startsWith("__PARALLEL__")) {
```

在该行之后、`String parallelSourceId` 之前插入：
```java
// 并行路径中断检测：如果有子节点触发了 HumanFeedback 中断，跳过 output 事件
// 中断 SSE 已由 NodeEventListener.after() 发送，此处不再重复推进
if (wfState.isWaitingInteraction()) {
    log.info("[handleGraphResponse] 并行路径已中断，跳过 __PARALLEL__ output 事件, nodeId={}", nodeId);
    return Flux.empty();
}
```

---

## Task 6：整体逻辑走读验证

- [ ] **Step 1: 走读并行路径完整流程（人工确认）**

```
ConditionalParallelNode.evalNodeActionSync()
  → LifeListenerUtil.processListenersLIFO(NODE_BEFORE)  [before() 空实现，无影响]
  → action.apply()  →  runNode()  →  HumanFeedbackNode.onProcess()
      feedbackData == null
      → wfState.waitingInteraction = true
      → return 空 List
  → LifeListenerUtil.processListenersLIFO(NODE_AFTER)
      → NodeEventListener.after(nodeId, ...)
          wfState.isWaitingInteraction() == true          ✓
          humanFeedbackNodeUuids.contains(nodeId) == true ✓
          !wfState.getInterruptNodes().contains(nodeId)   ✓（并行路径未注册到 interruptNodes）
          wfState.tryFireParallelInterrupt() == true       ✓（CAS 成功）
          → handleParallelInterruption(nodeId)
              → InterruptedFlow.RUNTIME_TO_GRAPH.put(...)
              → interactionService.createInteraction(...)
              → eventSink.next(createInterruptDataWithInteraction(...))
              [不调用 eventSink.complete()]

handleGraphResponse() 收到 __PARALLEL__ 输出
  → wfState.isWaitingInteraction() == true
  → return Flux.empty()  [跳过 output 事件]

graphStream 自然完成
  → doOnComplete 触发
      → updateWorkflowComplete()
      → wfState.getInteraction_request() != null（由 handleParallelInterruption 设置）
      → fluxSink.next(createAiOpenDialogParaEvent(...))  [携带 interaction_request]
      → fluxSink.complete()
```

**注意：** `doOnComplete` 里的 `createAiOpenDialogParaEvent` 会再次发送 `interaction_request`，这是前端 `aiChatService.js` 第146行监听的事件（`isComplete=true` 时解析 `interaction_request`）。`handleParallelInterruption()` 发送的 `createInterruptDataWithInteraction` 是工作流测试页面用的，聊天场景依赖 `doOnComplete` 里的事件。两者都发送没有问题。

- [ ] **Step 2: 走读非并行路径，确认未受影响**

非并行路径：`interruptsBefore` → `MainGraphExecutor.shouldInterrupt()` → 节点执行前中断 → `HumanFeedbackNode.onProcess()` **不被调用** → `wfState.waitingInteraction` 保持 `false` → `NodeEventListener.after()` 新逻辑不触发。✅

- [ ] **Step 3: 走读 resume 路径**

```
用户提交反馈 → workflowRuntimeResume(runtimeUuid, feedbackData)
  → WorkflowEngine.resume(userInput)
      → app.updateState(HUMAN_FEEDBACK_KEY, userInput)
      → executeWorkflow(true)
          → createOverAllState(resume=true)
          → app.graphResponseStream()
              → 框架从 checkpoint 继续执行
              → HumanFeedbackNode.onProcess() 再次执行
                  feedbackData != null  [已注入]
                  → 正常解析用户反馈，返回输出
```

**关键不确定点：** 并行路径中断后，`app.updateState()` + `executeWorkflow(true)` 能否让框架从 HumanFeedback 节点继续执行？这需要实际运行验证。如果 resume 不正确，需要额外调查框架 checkpoint 机制。

---

## 注意事项

1. **不操作 Git**，所有修改只写代码，由用户自行编译验证
2. **前端无需修改**，现有 `AiInteractionManager.js` + `chat.js` 已完整支持
3. **非并行路径不受影响**，`interruptsBefore` 机制保持原样
4. **resume 路径需要实际运行验证**，这是最大的不确定点
5. **`eventSink.complete()` 不在 `handleParallelInterruption()` 中调用**，由 `graphStream` 自然完成后的 `doOnComplete` 统一处理
