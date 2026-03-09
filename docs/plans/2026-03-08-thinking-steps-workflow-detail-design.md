# ThinkingSteps 工作流详情展示 设计文档

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在深度思考面板中展示工作流名称（"调用agent：xxx"）及其内部节点的执行步骤，并支持节点级别的"执行过程输出"开关控制摘要显示。

**Architecture:** 后端在 `runtime` 事件中追加 `workflowTitle`，在 `node_complete` 事件的 `summary` 中追加 `outputText`（受 `show_process_output` 控制）；前端 `chat.js` 处理 `runtime` 事件插入"调用agent"行，子步骤携带 `depth=1`；`ThinkingSteps.vue` 渲染缩进子步骤和输出文本。

**Tech Stack:** Java 17 + Spring Boot 3.1.4 + Vue 2.7 + Vuex 3

---

## 1. 目标效果

```
问题分析
|
问题分析完成
|
调用agent：通用-打开页面-流程          ← runtime 事件插入（depth=0，特殊样式）
  ● 开始：参数 xxx                     ← Start 节点（depth=1）
  ● 判断pagecode路由：路由A            ← Classifier 节点（depth=1，show_process_output=true 时显示结果）
  ● 路由判断：已判断                   ← Switcher 节点（depth=1，show_process_output=true 时显示结果）
  ● 打开前端页面：已为您打开新增页面   ← OpenPage 节点（depth=1，show_process_output=true 时显示结果）
  ● 结束1                              ← End 节点（depth=1）
调用agent完成                          ← 所有子步骤 done 后自动变为完成状态
```

---

## 2. 数据结构变更

### 2.1 WorkflowEventVo.createRuntimeData() 追加 workflowTitle

```java
// 现有字段
json.put("type", "runtime");
json.put("runtimeUuid", runtimeUuid);
json.put("runtimeId", runtimeId);
json.put("workflowUuid", workflowUuid);
json.put("conversationId", conversationId);

// 新增字段
json.put("workflowTitle", workflowTitle);  // 工作流名称，如"通用-打开页面-流程"
```

### 2.2 node_complete 事件 summary 追加 outputText

`buildSummary()` 中，当节点的 `nodeConfig.show_process_output == true` 时，追加 `outputText` 字段：

| 节点类型 | outputText 内容 |
|---------|----------------|
| Classifier | LLM 分类结果（`result` 字段） |
| OpenPage | "已为您打开新增页面" / "已为您打开编辑页面" 等 |
| Switcher | 命中的分支名称（`matchedBranch` 字段） |
| KnowledgeRetrieval | 已有 matchCount，不变 |
| McpTool | 已有 toolName，不变 |

### 2.3 前端 step 对象新增字段

```javascript
{
  nodeUuid: 'xxx',
  nodeName: 'Classifier',
  nodeTitle: '判断pagecode路由',
  status: 'running' | 'done',
  timestamp: 1234567890,
  duration: 1200,
  summary: { result: '路由A', outputText: '路由A', totalTokens: 150 },
  depth: 0 | 1,          // 新增：0=顶层，1=子步骤（工作流内部节点）
  isAgentRow: false       // 新增：true 表示"调用agent：xxx"行（特殊渲染）
}
```

---

## 3. 后端改动

### 3.1 WorkflowEventVo.java

**文件：** `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/WorkflowEventVo.java`

`createRuntimeData` 方法签名增加 `workflowTitle` 参数：

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

### 3.2 WorkflowEngine.java

**文件：** `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**改动1：** 调用 `createRuntimeData` 时传入 `workflow.getTitle()`：

```java
// 约第285行
return Flux.just(WorkflowEventVo.createRuntimeData(runtimeUuid, runtimeId,
                workflow.getWorkflowUuid(), conversationId, workflow.getTitle()))
```

**改动2：** `NodeEventListener.VISIBLE_NODES` 追加 `OpenPage`（目前缺失）：

```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM", "OpenPage"
);
```

**改动3：** `buildSummary()` 中读取 `nodeConfig.show_process_output`，追加 `outputText`：

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
                        // show_process_output=true 时追加 outputText
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
    // 追加Token消耗
    long[] tokens = wfState.getNodeTokens(nodeId);
    if (tokens != null) {
        if (summary == null) summary = new HashMap<>();
        summary.put("totalTokens", tokens[0] + tokens[1]);
    }
    return summary;
}

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

---

## 4. 前端改动

### 4.1 chat.js — 处理 runtime 事件，插入"调用agent"行

**文件：** `src/components/70_ai/store/modules/chat.js`

在 `onNodeEvent` 回调中，当 `chatResponse.nodeEventType === 'runtime'`（或解析 `chatResponse` 中的 `type === 'runtime'`）时，插入 agent 行。

> 注意：当前 `aiChatService.js` 中 `onNodeEvent` 是通过 `chatResponse.nodeEventType` 触发的，而 `runtime` 事件是 `WorkflowEventVo` 的 `data` 字段中的 `type`。需要确认 `ChatResponseVo` 如何透传 `runtime` 事件。

**查看 ChatResponseVo 的 nodeEventType 字段如何赋值（见 WorkflowRoutingService）：**

`WorkflowRoutingService` 将 `WorkflowEventVo.data` 解析后映射到 `ChatResponseVo` 的字段。`runtime` 事件目前只设置 `runtimeUuid`、`runtimeId`、`workflowUuid`，需要追加 `workflowTitle`。

**chat.js 中 onNodeEvent 处理逻辑追加：**

```javascript
onNodeEvent: (chatResponse) => {
  const eventType = chatResponse.nodeEventType

  // 新增：处理 runtime 事件 → 插入"调用agent：xxx"行
  if (eventType === 'runtime' && chatResponse.workflowTitle) {
    const agentRow = {
      nodeUuid: `__agent_${chatResponse.workflowUuid}_${Date.now()}`,
      nodeName: '__agent_row__',
      nodeTitle: chatResponse.workflowTitle,
      status: 'running',
      timestamp: Date.now(),
      duration: null,
      summary: null,
      depth: 0,
      isAgentRow: true
    }
    commit('ADD_WORKFLOW_PROCESS_NODE', { messageId: aiMessageId, step: agentRow })
    return
  }

  // 现有 node_start / node_complete 处理
  if (eventType === 'node_start') {
    const step = {
      nodeUuid: chatResponse.nodeUuid,
      nodeName: chatResponse.nodeName,
      nodeTitle: chatResponse.nodeTitle,
      status: 'running',
      timestamp: chatResponse.timestamp || Date.now(),
      duration: null,
      summary: null,
      depth: 1,          // 子步骤
      isAgentRow: false
    }
    commit('ADD_WORKFLOW_PROCESS_NODE', { messageId: aiMessageId, step })
  } else if (eventType === 'node_complete') {
    commit('COMPLETE_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeUuid: chatResponse.nodeUuid,
      duration: chatResponse.nodeDuration,
      summary: chatResponse.nodeSummary
    })
    // 检查是否所有子步骤都完成 → 更新 agent 行状态为 done
    // （在 COMPLETE_WORKFLOW_PROCESS_NODE mutation 中处理）
  }
}
```

### 4.2 chat.js — mutation 更新 agent 行状态

当所有 `depth=1` 的步骤都变为 `done` 时，将最近一个 `isAgentRow=true` 的步骤状态改为 `done`：

```javascript
COMPLETE_WORKFLOW_PROCESS_NODE(state, { messageId, nodeUuid, duration, summary }) {
  const processData = state.workflowProcessNodes[messageId]
  if (!processData) return
  const step = processData.steps.find(s => s.nodeUuid === nodeUuid)
  if (step) {
    step.status = 'done'
    step.duration = duration
    step.summary = summary || null
  }
  // 检查是否所有子步骤完成 → 更新 agent 行
  const allSubDone = processData.steps
    .filter(s => s.depth === 1)
    .every(s => s.status === 'done')
  if (allSubDone) {
    const agentRow = [...processData.steps].reverse().find(s => s.isAgentRow)
    if (agentRow && agentRow.status === 'running') {
      agentRow.status = 'done'
      agentRow.duration = processData.steps
        .filter(s => s.depth === 1)
        .reduce((sum, s) => sum + (s.duration || 0), 0)
    }
  }
}
```

### 4.3 WorkflowRoutingService.java — 透传 workflowTitle

**文件：** `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java`

在处理 `type=runtime` 的 `WorkflowEventVo` 时，将 `workflowTitle` 写入 `ChatResponseVo`：

```java
// 处理 runtime 事件
case "runtime": {
    chatResponse.setRuntimeUuid(data.getString("runtimeUuid"));
    chatResponse.setRuntimeId(data.getLong("runtimeId"));
    chatResponse.setWorkflowUuid(data.getString("workflowUuid"));
    chatResponse.setWorkflowTitle(data.getString("workflowTitle")); // 新增
    chatResponse.setNodeEventType("runtime");
    break;
}
```

### 4.4 ChatResponseVo.java — 追加 workflowTitle 字段

**文件：** `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/response/ChatResponseVo.java`

```java
/**
 * 工作流名称（runtime事件时携带）
 */
private String workflowTitle;
```

### 4.5 ThinkingSteps.vue — 渲染缩进子步骤和 outputText

**文件：** `src/components/70_ai/components/chat/messages/ThinkingSteps.vue`

**模板改动：** 根据 `step.isAgentRow` 和 `step.depth` 渲染不同样式：

```html
<div
  v-for="(step, index) in steps"
  :key="step.nodeUuid"
  class="step-row"
  :class="{ 'step-row--sub': step.depth === 1, 'step-row--agent': step.isAgentRow }"
>
  <!-- agent 行：特殊渲染，无圆点，显示"调用agent：xxx" -->
  <template v-if="step.isAgentRow">
    <div class="agent-row">
      <span class="agent-label" :class="step.status">
        {{ step.status === 'running' ? `调用agent：${step.nodeTitle}` : `调用agent完成` }}
      </span>
      <span v-if="step.duration != null" class="step-duration">{{ formatDuration(step.duration) }}</span>
    </div>
  </template>

  <!-- 普通步骤行（含子步骤） -->
  <template v-else>
    <div class="step-timeline" :class="{ 'step-timeline--sub': step.depth === 1 }">
      <div class="step-circle" :class="step.status">...</div>
      <div v-if="index < steps.length - 1 && !steps[index + 1].isAgentRow" class="step-line" />
    </div>
    <div class="step-content">
      <div class="step-header">
        <span class="step-title" :class="step.status">{{ getStepText(step) }}</span>
        <span v-if="step.duration != null" class="step-duration">{{ formatDuration(step.duration) }}</span>
      </div>
      <!-- outputText：show_process_output=true 时后端会在 summary 中携带 -->
      <div v-if="step.summary && step.summary.outputText" class="step-output">
        {{ step.summary.outputText }}
      </div>
    </div>
  </template>
</div>
```

**CSS 追加：**

```css
/* 子步骤缩进 */
.step-row--sub {
  padding-left: 16px;
}

/* agent 行 */
.agent-row {
  display: flex;
  align-items: center;
  padding: 4px 0;
  width: 100%;
}

.agent-label {
  font-size: 13px;
  font-weight: 600;
  color: #1f2329;
}

.agent-label.running {
  color: #1890ff;
}

/* 输出文本 */
.step-output {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
  padding-left: 2px;
}
```

---

## 5. 实现顺序

1. **后端：** `WorkflowEventVo.createRuntimeData()` 加 `workflowTitle` 参数
2. **后端：** `WorkflowEngine` 调用处传入 `workflow.getTitle()`，`VISIBLE_NODES` 加 `OpenPage`
3. **后端：** `buildSummary()` 加 `getNodeShowProcessOutput()` + `outputText` 逻辑
4. **后端：** `ChatResponseVo` 加 `workflowTitle` 字段
5. **后端：** `WorkflowRoutingService` 处理 `runtime` 事件时透传 `workflowTitle`
6. **前端：** `chat.js` 的 `onNodeEvent` 处理 `runtime` 事件，插入 agent 行；子步骤加 `depth=1`
7. **前端：** `ThinkingSteps.vue` 渲染 agent 行、子步骤缩进、outputText

---

## 6. 不改动范围

- `ClassifierNodeProperty.vue` 和 `OpenPageNodeProperty.vue` 的 `show_process_output` 开关已在本次前置工作中完成
- LLM / Answer 节点不加 `show_process_output` 开关（始终显示）
- 不改动数据库结构
- 不改动 SSE 协议格式（只在现有字段中追加内容）
