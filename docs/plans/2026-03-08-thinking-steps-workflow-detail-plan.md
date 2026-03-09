# ThinkingSteps 工作流详情展示 实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在深度思考面板中展示"调用agent：xxx"行及工作流内部节点的缩进子步骤，并支持节点级 `show_process_output` 开关控制摘要文本显示。

**Architecture:** 后端 `runtime` 事件追加 `workflowTitle`，`node_complete` 的 `summary` 追加 `outputText`（受 `show_process_output` 控制）；前端 `chat.js` 处理 `runtime` 事件插入 agent 行，子步骤携带 `depth=1`；`ThinkingSteps.vue` 渲染缩进子步骤和输出文本。

**Tech Stack:** Java 17 + Spring Boot 3.1.4 + Vue 2.7 + Vuex 3 + fastjson2

---

## Task 1：后端 — WorkflowEventVo 追加 workflowTitle 参数

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/WorkflowEventVo.java:46-55`

**Step 1: 修改 createRuntimeData 方法签名，追加 workflowTitle 参数**

在 `createRuntimeData` 方法中追加第5个参数 `String workflowTitle`，并写入 JSON：

```java
public static WorkflowEventVo createRuntimeData(String runtimeUuid, Long runtimeId,
                                                 String workflowUuid, String conversationId,
                                                 String workflowTitle) {
    JSONObject json = new JSONObject();
    json.put("type", "runtime");
    json.put("runtimeUuid", runtimeUuid);
    json.put("runtimeId", runtimeId);
    json.put("workflowUuid", workflowUuid);
    json.put("conversationId", conversationId);
    json.put("workflowTitle", workflowTitle != null ? workflowTitle : "");
    return WorkflowEventVo.builder().data(json.toJSONString()).build();
}
```

**Step 2: 修复 WorkflowEngine 中的调用处（编译错误修复）**

文件：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`，约第285行：

```java
// 修改前（4个参数）：
return Flux.just(WorkflowEventVo.createRuntimeData(runtimeUuid, runtimeId,
                workflow.getWorkflowUuid(), conversationId))

// 修改后（5个参数）：
return Flux.just(WorkflowEventVo.createRuntimeData(runtimeUuid, runtimeId,
                workflow.getWorkflowUuid(), conversationId, workflow.getTitle()))
```

**Step 3: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/WorkflowEventVo.java
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): runtime事件追加workflowTitle字段"
```

---

## Task 2：后端 — WorkflowEngine 追加 OpenPage 到 VISIBLE_NODES + buildSummary 支持 outputText

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1598-1704`

**Step 1: VISIBLE_NODES 追加 OpenPage**

约第1598行，将：
```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM"
);
```
改为：
```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM", "OpenPage"
);
```

**Step 2: 在 NodeEventListener 内部追加 getNodeShowProcessOutput 私有方法**

在 `buildSummary` 方法之后（约第1704行之前）追加：

```java
/**
 * 读取节点的 show_process_output 配置，默认 true
 */
private boolean getNodeShowProcessOutput(String nodeId) {
    return wfNodes.stream()
        .filter(n -> nodeId.equals(n.getUuid()))
        .findFirst()
        .map(n -> {
            JSONObject cfg = n.getNodeConfig();
            if (cfg == null) return true;
            Boolean val = cfg.getBoolean("show_process_output");
            return val == null || val;
        })
        .orElse(true);
}
```

**Step 3: buildSummary 中追加 outputText 逻辑**

在 `buildSummary` 方法开头（约第1655行）追加 `showOutput` 变量，并在 `Classifier` 和 `OpenPage` case 中使用：

```java
private Map<String, Object> buildSummary(String componentName, String nodeId, Map<String, Object> state) {
    Map<String, Object> summary = null;
    try {
        String outputKey = NODE_OUTPUT_KEY_PREFIX + nodeId;
        Object outputObj = state.get(outputKey);
        Map<String, Object> outputMap = (outputObj instanceof Map) ? (Map<String, Object>) outputObj : null;

        // 读取节点的 show_process_output 配置
        boolean showOutput = getNodeShowProcessOutput(nodeId);

        switch (componentName) {
            case "KnowledgeRetrieval": {
                if (outputMap != null) {
                    Object matchCount = outputMap.get("matchCount");
                    if (matchCount != null) {
                        summary = new HashMap<>();
                        summary.put("matchCount", matchCount);
                    }
                }
                break;
            }
            case "Classifier": {
                if (outputMap != null) {
                    Object result = outputMap.get("result");
                    if (result == null) result = outputMap.get("output");
                    if (result != null) {
                        summary = new HashMap<>();
                        summary.put("result", String.valueOf(result));
                        // show_process_output=true 时追加 outputText 供前端显示
                        if (showOutput) {
                            summary.put("outputText", String.valueOf(result));
                        }
                    }
                }
                break;
            }
            case "McpTool": {
                if (outputMap != null) {
                    Object toolName = outputMap.get("toolName");
                    if (toolName != null) {
                        summary = new HashMap<>();
                        summary.put("toolName", String.valueOf(toolName));
                    }
                }
                break;
            }
            case "OpenPage": {
                // show_process_output=true 时生成打开页面的描述文本
                if (showOutput && outputMap != null) {
                    Object pageMode = outputMap.get("page_mode");
                    String modeLabel = "new".equals(pageMode) ? "新增页面"
                        : "edit".equals(pageMode) ? "编辑页面"
                        : "view".equals(pageMode) ? "查看页面" : "页面";
                    summary = new HashMap<>();
                    summary.put("outputText", "已为您打开" + modeLabel);
                }
                break;
            }
        }
    } catch (Exception e) {
        log.debug("获取{}节点摘要失败: {}", componentName, e.getMessage());
    }
    // 追加Token消耗（Classifier/Answer/LLM等调用LLM的节点）
    long[] tokens = wfState.getNodeTokens(nodeId);
    if (tokens != null) {
        if (summary == null) summary = new HashMap<>();
        summary.put("totalTokens", tokens[0] + tokens[1]);
    }
    return summary;
}
```

**Step 4: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): OpenPage加入VISIBLE_NODES，buildSummary支持outputText"
```

---

## Task 3：后端 — ChatResponseVo 追加 workflowTitle 字段

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/response/ChatResponseVo.java:86-87`（在 workflowUuid 字段后追加）

**Step 1: 追加字段**

在 `workflowUuid` 字段（约第86行）之后追加：

```java
/**
 * 工作流名称（runtime事件时携带，用于前端展示"调用agent：xxx"行）
 *
 * @since 2026-03-08 ThinkingSteps工作流详情展示
 */
private String workflowTitle;
```

**Step 2: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/response/ChatResponseVo.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): ChatResponseVo追加workflowTitle字段"
```

---

## Task 4：后端 — WorkflowRoutingService 透传 workflowTitle

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java:1433-1435`

**Step 1: 修改 runtime case，设置 nodeEventType 并透传 workflowTitle**

约第1433行，将：
```java
case "runtime":
    // runtime数据: 返回空内容块(前端需要这个事件来初始化)
    return ChatResponseVo.createContentChunk("");
```
改为：
```java
case "runtime": {
    // runtime数据：透传workflowTitle，触发前端插入"调用agent：xxx"行
    ChatResponseVo runtimeResp = ChatResponseVo.createContentChunk("");
    runtimeResp.setNodeEventType("runtime");
    runtimeResp.setWorkflowUuid(eventData.getString("workflowUuid"));
    runtimeResp.setWorkflowTitle(eventData.getString("workflowTitle"));
    return runtimeResp;
}
```

**Step 2: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): WorkflowRoutingService透传runtime事件workflowTitle"
```

---

## Task 5：前端 — chat.js 处理 runtime 事件，子步骤加 depth 字段

**Files:**
- Modify: `src/components/70_ai/store/modules/chat.js`

**Step 1: SET_WORKFLOW_PROCESS_NODE mutation 中，node_start 时追加 depth=1**

约第230行，`steps.push({...})` 中追加 `depth: 1`：

```javascript
steps.push({
  nodeUuid: nodeEvent.nodeUuid,
  nodeName: nodeEvent.nodeName,
  nodeTitle: nodeEvent.nodeTitle,
  status: 'running',
  timestamp: nodeEvent.nodeTimestamp,
  duration: null,
  summary: null,
  depth: 1          // 子步骤（工作流内部节点）
})
```

同样，虚拟步骤替换处（约第211行）也追加 `depth: 1`：

```javascript
steps.splice(virtualIdx, 1, {
  nodeUuid: nodeEvent.nodeUuid,
  nodeName: nodeEvent.nodeName,
  nodeTitle: nodeEvent.nodeTitle,
  status: 'running',
  timestamp: nodeEvent.nodeTimestamp,
  duration: null,
  summary: null,
  depth: 1
})
```

**Step 2: onNodeEvent 回调中，处理 runtime 事件插入 agent 行**

约第399行，`onNodeEvent` 回调改为：

```javascript
onNodeEvent: (nodeEvent) => {
  // runtime 事件：插入"调用agent：xxx"行（depth=0，isAgentRow=true）
  if (nodeEvent.nodeEventType === 'runtime' && nodeEvent.workflowTitle) {
    const agentRow = {
      nodeUuid: `__agent_${nodeEvent.workflowUuid || ''}_${Date.now()}`,
      nodeName: '__agent_row__',
      nodeTitle: nodeEvent.workflowTitle,
      status: 'running',
      timestamp: Date.now(),
      duration: null,
      summary: null,
      depth: 0,
      isAgentRow: true
    }
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent: { nodeEventType: '__agent_row__', agentRow }
    })
    return
  }
  // node_start / node_complete 事件
  commit('SET_WORKFLOW_PROCESS_NODE', {
    messageId: aiMessageId,
    nodeEvent
  })
},
```

**Step 3: SET_WORKFLOW_PROCESS_NODE mutation 中，处理 __agent_row__ 事件类型**

约第187行，在 `SET_WORKFLOW_PROCESS_NODE` mutation 开头追加 agent 行处理：

```javascript
SET_WORKFLOW_PROCESS_NODE (state, { messageId, nodeEvent }) {
  if (!state.workflowProcessNodes[messageId]) {
    state.workflowProcessNodes = { ...state.workflowProcessNodes, [messageId]: { steps: [], pendingComplete: null }}
  }
  const processData = state.workflowProcessNodes[messageId]
  const steps = processData.steps

  // agent 行：直接 push，不走 node_start/node_complete 逻辑
  if (nodeEvent.nodeEventType === '__agent_row__') {
    steps.push(nodeEvent.agentRow)
    return
  }

  if (nodeEvent.nodeEventType === 'node_start') {
    // ... 现有逻辑不变 ...
```

**Step 4: FLUSH_PENDING_NODE_COMPLETE 时，检查子步骤全完成 → 更新 agent 行状态**

约第246行，`FLUSH_PENDING_NODE_COMPLETE` mutation 末尾追加：

```javascript
FLUSH_PENDING_NODE_COMPLETE (state, messageId) {
  const processData = state.workflowProcessNodes[messageId]
  if (processData && processData.pendingComplete) {
    const pending = processData.pendingComplete
    const step = processData.steps.find(s => s.nodeUuid === pending.nodeUuid)
    if (step) {
      step.status = 'done'
      step.duration = pending.nodeDuration
      step.summary = pending.nodeSummary || null
    }
    processData.pendingComplete = null
  }
  // 子步骤全完成时，更新最近的 agent 行为 done
  if (processData) {
    const subSteps = processData.steps.filter(s => s.depth === 1)
    const allSubDone = subSteps.length > 0 && subSteps.every(s => s.status === 'done')
    if (allSubDone) {
      const agentRow = [...processData.steps].reverse().find(s => s.isAgentRow && s.status === 'running')
      if (agentRow) {
        agentRow.status = 'done'
        agentRow.duration = subSteps.reduce((sum, s) => sum + (s.duration || 0), 0)
      }
    }
  }
}
```

**Step 5: Commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/store/modules/chat.js
git -C 01_scm_frontend/scm_frontend commit -m "feat(ai): chat.js处理runtime事件插入agent行，子步骤加depth字段"
```

---

## Task 6：前端 — ThinkingSteps.vue 渲染 agent 行、子步骤缩进、outputText

**Files:**
- Modify: `src/components/70_ai/components/chat/messages/ThinkingSteps.vue`

**Step 1: 模板 — 区分 agent 行和普通步骤行**

将现有的 `v-for` 步骤行改为：

```html
<div
  v-for="(step, index) in steps"
  :key="step.nodeUuid"
  class="step-row"
  :class="{ 'step-row--sub': step.depth === 1 }"
>
  <!-- agent 行：特殊渲染，无圆点 -->
  <template v-if="step.isAgentRow">
    <div class="agent-row">
      <span class="agent-icon">⚡</span>
      <span class="agent-label" :class="step.status">
        {{ step.status === 'running' ? `调用agent：${step.nodeTitle}` : `调用agent完成` }}
      </span>
      <span v-if="step.duration != null" class="step-duration">{{ formatDuration(step.duration) }}</span>
    </div>
  </template>

  <!-- 普通步骤行（含子步骤） -->
  <template v-else>
    <!-- 左侧：圆点 + 连接线 -->
    <div class="step-timeline">
      <div class="step-circle" :class="step.status">
        <template v-if="step.status === 'running'">
          <div class="circle-pulse" />
        </template>
        <template v-else>
          <svg viewBox="0 0 12 12" class="check-icon">
            <path d="M3.5 6L5.5 8L8.5 4" stroke="white" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
        </template>
      </div>
      <div v-if="index < steps.length - 1 && !steps[index + 1].isAgentRow" class="step-line" />
    </div>

    <!-- 右侧：步骤内容 -->
    <div class="step-content" :class="{ 'step-content--last': index === steps.length - 1 }">
      <div class="step-header">
        <span class="step-title" :class="step.status">{{ getStepText(step) }}</span>
        <span v-if="step.duration != null" class="step-duration">{{ formatDuration(step.duration) }}</span>
      </div>
      <!-- outputText：show_process_output=true 时后端在 summary 中携带 -->
      <div v-if="step.summary && step.summary.outputText" class="step-output">
        {{ step.summary.outputText }}
      </div>
    </div>
  </template>
</div>
```

**Step 2: isCompleted computed 追加 __agent_row__ 终端判断**

```javascript
isCompleted () {
  if (!this.isAllDone) return false
  if (this.streamComplete) return true
  const lastStep = this.steps[this.steps.length - 1]
  return lastStep && (
    lastStep.nodeName === 'Answer' ||
    lastStep.nodeName === 'LLM' ||
    lastStep.nodeName === 'OpenPage' ||
    (lastStep.isAgentRow && lastStep.status === 'done')
  ) && lastStep.status === 'done'
},
```

**Step 3: CSS 追加子步骤缩进、agent 行、outputText 样式**

在 `<style scoped>` 末尾追加：

```css
/* 子步骤缩进 */
.step-row--sub {
  padding-left: 16px;
}

/* agent 行 */
.agent-row {
  display: flex;
  align-items: center;
  padding: 4px 0 4px 2px;
  width: 100%;
  min-height: 30px;
}

.agent-icon {
  font-size: 12px;
  margin-right: 6px;
  flex-shrink: 0;
}

.agent-label {
  font-size: 13px;
  font-weight: 600;
  color: #1f2329;
  flex: 1;
}

.agent-label.running {
  color: #1890ff;
}

/* 输出文本（show_process_output=true 时显示） */
.step-output {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
  padding-left: 2px;
  line-height: 1.4;
}
```

**Step 4: Commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/chat/messages/ThinkingSteps.vue
git -C 01_scm_frontend/scm_frontend commit -m "feat(ai): ThinkingSteps渲染agent行、子步骤缩进、outputText"
```

---

## 验证方式

1. 启动后端，打开 AI 聊天界面
2. 发送一条会触发"通用-打开页面-流程"工作流的消息
3. 观察深度思考面板：
   - 应出现"调用agent：通用-打开页面-流程"行（蓝色，running 状态）
   - 下方缩进显示各节点步骤（Classifier、OpenPage 等）
   - 工作流完成后，agent 行变为"调用agent完成"（绿色）
4. 在工作流编辑器中，将 Classifier 节点的"执行过程输出"开关打开
5. 再次触发，观察 Classifier 步骤下方是否显示分类结果文本
6. 关闭开关，再次触发，确认不显示结果文本（只显示节点名称）
