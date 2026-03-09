# 工作流执行步骤显示增强 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复工作流步骤显示的 4 个问题：buildSummary 类型 bug、Switcher 节点步骤显示、OpenPage 重复步骤、子工作流折叠步骤。

**Architecture:** 后端修复 buildSummary 从 Map 改为读 List<NodeIOData>，扩展 VISIBLE_NODES 加入 Switcher/SubWorkflow，WorkflowStarter.runSync 收集子步骤并通过 SubWorkflowResult 传递；前端删除 __open_page__ 手动步骤，ThinkingSteps.vue 增加折叠面板渲染子步骤。

**Tech Stack:** Java 17 + Spring Boot 3.1.4，Vue 2.7 + Vuex 3，SSE 流式事件

---

## Task 1：修复 buildSummary 的 outputMap 类型 bug

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`（NodeEventListener 内部类的 buildSummary 方法，约 1653-1723 行）

**背景：**
state 中 `NODE_OUTPUT_KEY_PREFIX + nodeId` 存的是 `List<NodeIOData>`（见 WorkflowEngine.java:850 `resultMap.put(outputKey, nodeState.getOutputs())`），但 buildSummary 把它 cast 成 `Map<String,Object>`，导致 outputMap 永远是 null。

**Step 1: 修改 buildSummary，将 outputMap 改为 outputList**

将 `buildSummary` 方法中的以下代码：
```java
Object outputObj = state.get(outputKey);
Map<String, Object> outputMap = (outputObj instanceof Map) ? (Map<String, Object>) outputObj : null;
```
替换为：
```java
Object outputObj = state.get(outputKey);
@SuppressWarnings("unchecked")
List<NodeIOData> outputList = (outputObj instanceof List) ? (List<NodeIOData>) outputObj : null;
```

**Step 2: 在 NodeEventListener 内部类中添加辅助方法 findOutputValue**

在 `getNodeShowProcessOutput` 方法之前添加：
```java
/**
 * 从节点输出列表中按 name 查找值
 */
private String findOutputValue(List<NodeIOData> list, String name) {
    if (list == null) return null;
    return list.stream()
        .filter(d -> name.equals(d.getName()))
        .findFirst()
        .map(d -> d.getContent() != null ? String.valueOf(d.getContent().getValue()) : null)
        .orElse(null);
}
```

**Step 3: 修改各 case 使用 outputList**

将 switch 中各 case 的 `outputMap` 全部改为 `outputList` + `findOutputValue`：

```java
case "KnowledgeRetrieval": {
    String matchCount = findOutputValue(outputList, "matchCount");
    if (matchCount != null) {
        summary = new HashMap<>();
        summary.put("matchCount", matchCount);
    }
    break;
}
case "Classifier": {
    String result = findOutputValue(outputList, "result");
    if (result == null) result = findOutputValue(outputList, "output");
    if (result != null) {
        summary = new HashMap<>();
        summary.put("result", result);
        if (showOutput) {
            summary.put("outputText", result);
        }
    }
    break;
}
case "McpTool": {
    String toolName = findOutputValue(outputList, "toolName");
    if (toolName != null) {
        summary = new HashMap<>();
        summary.put("toolName", toolName);
    }
    break;
}
case "OpenPage": {
    if (showOutput) {
        String pageMode = findOutputValue(outputList, "page_mode");
        String modeLabel = "new".equals(pageMode) ? "新增页面"
            : "edit".equals(pageMode) ? "编辑页面"
            : "view".equals(pageMode) ? "查看页面" : "页面";
        summary = new HashMap<>();
        summary.put("outputText", "已为您打开" + modeLabel);
    }
    break;
}
```

注意：`findOutputValue` 方法需要 import `NodeIOData`，确认文件顶部已有：
```java
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
```

**Step 4: Commit**

```bash
git -C "D:/2025_project/20_project_in_github/00_scm_backend/scm_backend" add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C "D:/2025_project/20_project_in_github/00_scm_backend/scm_backend" commit -m "fix(ai): 修复buildSummary读取List<NodeIOData>而非Map"
```

---

## Task 2：Switcher 节点加入执行步骤显示

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/switcher/SwitcherNode.java`
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**Step 1: SwitcherNode 追加 matched_case_name 到 content**

在 `SwitcherNode.onProcess()` 末尾，将原来的 return 语句：
```java
return NodeProcessResult.builder()
        .nextSourceHandle(matchedSourceHandle)
        .content(changeInputsToOutputs(state.getInputs()))
        .build();
```
替换为：
```java
// 构建输出：透传输入 + 追加匹配的分支名称
List<NodeIOData> outputs = new ArrayList<>(changeInputsToOutputs(state.getInputs()));
String caseName;
if ("default_handle".equals(matchedSourceHandle)) {
    caseName = nodeConfig.getDefaultCaseName() != null ? nodeConfig.getDefaultCaseName() : "默认分支";
} else {
    caseName = nodeConfig.getCases().stream()
        .filter(c -> matchedSourceHandle.equals(c.getUuid()))
        .findFirst()
        .map(c -> c.getName() != null && !c.getName().isEmpty() ? c.getName() : "分支")
        .orElse("分支");
}
outputs.add(NodeIOData.createByText("matched_case_name", "匹配分支", caseName));
return NodeProcessResult.builder()
        .nextSourceHandle(matchedSourceHandle)
        .content(outputs)
        .build();
```

需要在文件顶部确认 import：
```java
import java.util.ArrayList;
import java.util.List;
```

**Step 2: WorkflowEngine 的 VISIBLE_NODES 加入 Switcher**

将：
```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM", "OpenPage"
);
```
改为：
```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM", "OpenPage", "Switcher"
);
```

**Step 3: buildSummary 增加 Switcher case**

在 `case "OpenPage"` 之后，`}` 关闭 switch 之前，添加：
```java
case "Switcher": {
    String caseName = findOutputValue(outputList, "matched_case_name");
    if (caseName != null && showOutput) {
        summary = new HashMap<>();
        summary.put("outputText", "→ " + caseName);
    }
    break;
}
```

**Step 4: 前端 ThinkingSteps.vue 的 NODE_CONFIG 补充 Switcher**

文件：`src/components/70_ai/components/chat/messages/ThinkingSteps.vue`

在 `NODE_CONFIG` 对象中添加：
```js
Switcher: { title: '路由判断', runningText: '判断中...' },
```

**Step 5: Commit**

```bash
git -C "D:/2025_project/20_project_in_github/00_scm_backend/scm_backend" add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/switcher/SwitcherNode.java scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C "D:/2025_project/20_project_in_github/00_scm_backend/scm_backend" commit -m "feat(ai): Switcher节点加入执行步骤显示"

git -C "D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend" add src/components/70_ai/components/chat/messages/ThinkingSteps.vue
git -C "D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend" commit -m "feat(ai): ThinkingSteps补充Switcher节点配置"
```

---

## Task 3：删除 `__open_page__` 手动步骤，消除重复显示

**Files:**
- Modify: `src/components/70_ai/store/modules/chat.js`

**背景：**
OpenPage 已在 VISIBLE_NODES，后端发 node_start/node_complete 事件，前端步骤列表已有"打开页面"步骤。
`onOpenPageCommand` 又手动 push `__open_page__` 步骤（约 450-507 行），导致重复。

**Step 1: 简化 onOpenPageCommand，删除手动步骤逻辑**

将 `onOpenPageCommand` 回调（约 446-508 行）替换为：
```js
onOpenPageCommand: (command, realMessageId) => {
  import('@/components/70_ai/components/navigator/AiPageRouter.js').then(({ navigateToPage }) => {
    navigateToPage(command, router, { getters: rootGetters, commit, dispatch })
      .then((success) => {
        const routeLabel = command.page_mode === 'new' ? '新增页面'
          : (command.page_mode === 'edit' ? '编辑页面' : '页面')
        const resultContent = success ? `已为您打开${routeLabel}` : '页面打开失败'
        const finalMsgId = realMessageId || aiMessageId
        commit('UPDATE_MESSAGE', {
          messageId: finalMsgId,
          updates: {
            content: resultContent,
            status: 'delivered',
            isStreaming: false
          }
        })
      })
  })
},
```

**Step 2: 简化 onComplete 中的 hasOpenPageCommand 逻辑**

找到 `onComplete` 中的：
```js
// 有open_page_command时，消息内容由onOpenPageCommand负责写入，onComplete不覆盖
const hasOpenPageCommand = !!(chatResponse?.open_page_command)
```
以及后续的：
```js
isHidden: !hasOpenPageCommand && !hasEnoughContent,
// open_page_command时不覆盖workflowSteps（onOpenPageCommand已追加__open_page__步骤）
...(hasOpenPageCommand ? {} : { workflowSteps: workflowSteps }),
```

将这段逻辑简化为（删除 hasOpenPageCommand 相关判断）：
```js
isHidden: !hasEnoughContent,
workflowSteps: workflowSteps,
```

注意：同时删除 `const hasOpenPageCommand = ...` 这行声明。

**Step 3: Commit**

```bash
git -C "D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend" add src/components/70_ai/store/modules/chat.js
git -C "D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend" commit -m "fix(ai): 删除__open_page__手动步骤，消除打开页面重复显示"
```

---

## Task 4：子工作流折叠步骤显示（后端）

**Files:**
- Create: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/subworkflow/SubWorkflowResult.java`
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowStarter.java`
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/subworkflow/SubWorkflowNode.java`
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**Step 1: 新建 SubWorkflowResult.java**

```java
package com.xinyirun.scm.ai.workflow.node.subworkflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 子工作流执行结果
 * 包含最终输出和内部节点执行步骤（用于父工作流步骤面板折叠显示）
 */
@Data
@Builder
public class SubWorkflowResult {
    /** 子工作流最终输出（原 runSync 返回的 Map） */
    private Map<String, Object> outputs;
    /** 子工作流内部节点步骤（node_complete 事件摘要列表） */
    private List<Map<String, Object>> subSteps;
}
```

**Step 2: 修改 WorkflowStarter.runSync，收集 subSteps 并返回 SubWorkflowResult**

将 `runSync` 方法的返回类型从 `Map<String, Object>` 改为 `SubWorkflowResult`。

在方法内部，在 `final Map<String, Object> result = new HashMap<>()` 之后添加：
```java
final List<Map<String, Object>> subSteps = new ArrayList<>();
```

在 `doOnNext` 的 `try` 块中，在处理 `"output"` type 之后，添加收集 `node_complete` 事件的逻辑：
```java
// 收集 node_complete 事件作为子步骤
if ("node_complete".equals(type)) {
    JSONObject data = dataJson.getJSONObject("data");
    if (data != null) {
        Map<String, Object> step = new HashMap<>();
        step.put("nodeUuid", data.getString("nodeUuid"));
        step.put("nodeName", data.getString("nodeName"));
        step.put("nodeTitle", data.getString("nodeTitle"));
        step.put("duration", data.getLong("duration"));
        Object summaryObj = data.get("summary");
        if (summaryObj != null) {
            step.put("summary", summaryObj);
        }
        subSteps.add(step);
    }
}
```

将方法末尾的 `return result;` 改为：
```java
return SubWorkflowResult.builder()
        .outputs(result)
        .subSteps(subSteps)
        .build();
```

需要添加 import：
```java
import com.xinyirun.scm.ai.workflow.node.subworkflow.SubWorkflowResult;
import java.util.ArrayList;
```

**Step 3: 修改 SubWorkflowNode.onProcess，接收 SubWorkflowResult 并写 subSteps**

将调用 `workflowStarter.runSync(...)` 的返回类型从 `Map<String, Object>` 改为 `SubWorkflowResult`：
```java
SubWorkflowResult subResult = workflowStarter.runSync(
    subWorkflowUuid,
    convertToInputList(subInputs),
    wfState.getTenantCode(),
    wfState.getUserId(),
    wfState.getExecutionStack(),
    wfState.getConversationId(),
    wfState.getUuid(),
    wfState.getCallSource()
);
Map<String, Object> subOutputs = subResult.getOutputs();
```

在 `// 6. 设置输出` 之前，将 subSteps 写入 nodeState（通过追加到 outputs 的特殊 key）：
```java
// 将子步骤存入 nodeState，供 buildSummary 读取
// 使用特殊 key "__sub_steps__" 存储，不影响正常输出传递
if (subResult.getSubSteps() != null && !subResult.getSubSteps().isEmpty()) {
    nodeState.putExtra("__sub_steps__", subResult.getSubSteps());
}
```

注意：需要确认 `WfNodeState` 是否有 `putExtra` 方法，如果没有，改为直接存入 wfState：
```java
wfState.putExtra(node.getUuid() + "__sub_steps__", subResult.getSubSteps());
```

**Step 4: 确认 WfNodeState / WfState 的存储方式**

读取 `WfNodeState.java` 和 `WfState.java`，确认存储 subSteps 的正确方式，然后相应调整 Step 3 和 buildSummary 的读取逻辑。

**Step 5: WorkflowEngine 的 VISIBLE_NODES 加入 SubWorkflow，buildSummary 增加 SubWorkflow case**

VISIBLE_NODES 改为：
```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM", "OpenPage", "Switcher", "SubWorkflow"
);
```

buildSummary 增加 SubWorkflow case（在 Switcher case 之后）：
```java
case "SubWorkflow": {
    // 读取子工作流名称
    String workflowName = wfNodes.stream()
        .filter(n -> nodeId.equals(n.getUuid()))
        .findFirst()
        .map(n -> {
            if (n.getNodeConfig() == null) return null;
            return n.getNodeConfig().getString("workflowName");
        })
        .orElse(null);
    // 读取子步骤（从 wfState 中读取，key 为 nodeId + "__sub_steps__"）
    Object subStepsObj = wfState.getExtra(nodeId + "__sub_steps__");
    if (subStepsObj != null) {
        summary = new HashMap<>();
        if (workflowName != null) summary.put("workflowName", workflowName);
        summary.put("steps", subStepsObj);
    }
    break;
}
```

注意：`wfState` 在 `NodeEventListener` 内部类中需要通过外部类引用访问，确认访问方式。

**Step 6: Commit**

```bash
git -C "D:/2025_project/20_project_in_github/00_scm_backend/scm_backend" add \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/subworkflow/SubWorkflowResult.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowStarter.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/subworkflow/SubWorkflowNode.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C "D:/2025_project/20_project_in_github/00_scm_backend/scm_backend" commit -m "feat(ai): 子工作流收集内部节点步骤用于折叠显示"
```

---

## Task 5：子工作流折叠步骤显示（前端）

**Files:**
- Modify: `src/components/70_ai/components/chat/messages/ThinkingSteps.vue`

**Step 1: NODE_CONFIG 补充 SubWorkflow**

在 `NODE_CONFIG` 对象中添加：
```js
SubWorkflow: { title: '子工作流', runningText: '执行中...' },
```

**Step 2: getSummaryText 补充 SubWorkflow**

在 `getSummaryText` 方法的 switch 中添加：
```js
case 'SubWorkflow':
  return s.workflowName ? `→ ${s.workflowName}` : null
```

**Step 3: 模板中增加折叠面板渲染**

在 `<!-- 右侧：步骤内容 -->` 的 `step-content` div 内，`step-output` div 之后，添加子步骤折叠面板：

```html
<!-- 子工作流折叠面板：summary.steps 存在时显示 -->
<div v-if="step.summary && step.summary.steps && step.summary.steps.length" class="sub-steps-panel">
  <div class="sub-steps-toggle" @click.stop="toggleSubSteps(step.nodeUuid)">
    <span>查看详情</span>
    <svg class="sub-steps-arrow" :class="{ expanded: expandedSubSteps[step.nodeUuid] }" viewBox="0 0 12 12" width="10" height="10">
      <path d="M3 5L6 8L9 5" stroke="currentColor" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round" />
    </svg>
  </div>
  <div v-show="expandedSubSteps[step.nodeUuid]" class="sub-steps-body">
    <div v-for="subStep in step.summary.steps" :key="subStep.nodeUuid" class="sub-step-row">
      <div class="sub-step-circle" :class="'done'" />
      <div class="sub-step-content">
        <span class="sub-step-title">{{ getSubStepText(subStep) }}</span>
        <span v-if="subStep.duration != null" class="step-duration">{{ formatDuration(subStep.duration) }}</span>
      </div>
    </div>
  </div>
</div>
```

**Step 4: data() 中添加 expandedSubSteps**

```js
data () {
  return {
    collapsed: ...,
    expandedSubSteps: {}  // 记录各子工作流步骤面板的展开状态，key 为 nodeUuid
  }
}
```

**Step 5: methods 中添加 toggleSubSteps 和 getSubStepText**

```js
toggleSubSteps (nodeUuid) {
  this.$set(this.expandedSubSteps, nodeUuid, !this.expandedSubSteps[nodeUuid])
},

getSubStepText (subStep) {
  const name = subStep.nodeName
  const cfg = NODE_CONFIG[name]
  const title = cfg ? cfg.title : (subStep.nodeTitle || name || '执行')
  const summary = subStep.summary
  if (!summary) return `${title}  完成`
  if (summary.outputText) return `${title}  ${summary.outputText}`
  if (name === 'KnowledgeRetrieval' && summary.matchCount != null) return `${title}  命中${summary.matchCount}条`
  if (name === 'McpTool' && summary.toolName) return `${title}  → ${summary.toolName}`
  return `${title}  完成`
},
```

**Step 6: CSS 添加子步骤面板样式**

```css
/* 子工作流折叠面板 */
.sub-steps-panel {
  margin-top: 4px;
}

.sub-steps-toggle {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #1890ff;
  cursor: pointer;
  user-select: none;
}

.sub-steps-toggle:hover {
  opacity: 0.8;
}

.sub-steps-arrow {
  transition: transform 0.2s ease;
  color: #1890ff;
}

.sub-steps-arrow.expanded {
  transform: rotate(180deg);
}

.sub-steps-body {
  margin-top: 6px;
  padding-left: 8px;
  border-left: 2px solid #e8e8ec;
}

.sub-step-row {
  display: flex;
  align-items: center;
  min-height: 24px;
  gap: 8px;
  padding: 2px 0;
}

.sub-step-circle {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  background-color: #1a9c72;
}

.sub-step-content {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 8px;
}

.sub-step-title {
  font-size: 12px;
  color: #606266;
}
```

**Step 7: Commit**

```bash
git -C "D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend" add src/components/70_ai/components/chat/messages/ThinkingSteps.vue
git -C "D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend" commit -m "feat(ai): ThinkingSteps支持子工作流折叠步骤面板"
```

---

## 注意事项

1. **Task 4 Step 3/5 的存储方式**：执行前必须先读 `WfNodeState.java` 和 `WfState.java`，确认是否有 `putExtra`/`getExtra` 方法，再决定 subSteps 的存储和读取方式。

2. **Task 4 Step 5 的 wfState 访问**：`NodeEventListener` 是 `WorkflowEngine` 的内部类，可以直接访问外部类的 `wfState` 字段，确认字段名。

3. **执行顺序**：Task 1 → Task 2 → Task 3 → Task 4 → Task 5，Task 1 是基础，必须先做。

4. **不需要编译和运行**，用户自己编译验证。
