# 工作流执行步骤显示增强设计文档

日期：2026-03-09

## 背景

当前工作流执行步骤（ThinkingSteps）存在以下问题：
1. `buildSummary` 的 `outputMap` 永远为 null（List vs Map 类型不匹配），所有摘要逻辑失效
2. Switcher（路由判断）节点不在 VISIBLE_NODES，步骤列表看不到路由判断这一步
3. OpenPage 节点已加入 VISIBLE_NODES，但 `onOpenPageCommand` 又手动 push `__open_page__` 步骤，导致重复显示
4. 子工作流（SubWorkflow）内部节点事件被丢弃，步骤列表看不到子工作流执行过程

## 需求

### 需求 1：修复 buildSummary outputMap 永远为 null

state 中存储的是 `List<NodeIOData>`，buildSummary 错误地将其 cast 为 `Map<String,Object>`。

修复：改为读 `List<NodeIOData>`，按 `name` 字段查找值。

### 需求 2：Switcher 节点加入执行步骤显示

SwitcherNode 执行后需要：
- 在 content 中追加 `matched_case_name` 字段（匹配到的 case 名称，default 时写"默认分支"）
- 加入 VISIBLE_NODES
- buildSummary 增加 Switcher case，读 matched_case_name 作为 outputText

### 需求 3：删除 `__open_page__` 手动步骤

`onOpenPageCommand` 中删除手动 push `__open_page__` 步骤的代码，只保留导航逻辑。
`onComplete` 中的 `hasOpenPageCommand` 相关逻辑相应简化。

### 需求 4：子工作流 Dify 折叠步骤显示

子工作流同步执行时收集内部节点事件，作为 `summary.steps` 传递给父工作流，前端渲染折叠面板。

## 方案设计

### 后端架构

#### 1. buildSummary 修复（WorkflowEngine.java）

```java
// 修复前（死代码）
Object outputObj = state.get(outputKey);
Map<String, Object> outputMap = (outputObj instanceof Map) ? (Map<String, Object>) outputObj : null;

// 修复后
Object outputObj = state.get(outputKey);
List<NodeIOData> outputList = (outputObj instanceof List) ? (List<NodeIOData>) outputObj : null;

// 辅助方法：按 name 查找值
private String findOutputValue(List<NodeIOData> list, String name) {
    if (list == null) return null;
    return list.stream()
        .filter(d -> name.equals(d.getName()))
        .findFirst()
        .map(d -> d.getContent() != null ? String.valueOf(d.getContent().getValue()) : null)
        .orElse(null);
}
```

各节点 summary 读取：
- `KnowledgeRetrieval`：读 `matchCount` 字段
- `Classifier`：读 `result` 或 `output` 字段
- `McpTool`：读 `toolName` 字段
- `OpenPage`：读 `page_mode` 字段

#### 2. SwitcherNode 追加 matched_case_name

```java
// SwitcherNode.onProcess() 末尾，在 changeInputsToOutputs 基础上追加
List<NodeIOData> outputs = new ArrayList<>(changeInputsToOutputs(state.getInputs()));
String caseName = "default_handle".equals(matchedSourceHandle) ? "默认分支"
    : nodeConfig.getCases().stream()
        .filter(c -> matchedSourceHandle.equals(c.getUuid()))
        .findFirst()
        .map(c -> c.getName() != null ? c.getName() : "分支")
        .orElse("分支");
outputs.add(NodeIOData.createByText("matched_case_name", "匹配分支", caseName));
return NodeProcessResult.builder().nextSourceHandle(matchedSourceHandle).content(outputs).build();
```

#### 3. SubWorkflow 收集子步骤

新增 `SubWorkflowResult.java`：
```java
@Data
@Builder
public class SubWorkflowResult {
    private Map<String, Object> outputs;       // 原有输出
    private List<Map<String, Object>> subSteps; // 子工作流节点步骤
}
```

`WorkflowStarter.runSync()` 改造：
- 在 `doOnNext` 里同时收集 `node_complete` 事件（type = "node_complete"）
- 每个 subStep 包含：`nodeUuid`, `nodeName`, `nodeTitle`, `duration`, `summary`
- 返回 `SubWorkflowResult` 而非 `Map<String,Object>`

`SubWorkflowNode.onProcess()` 改造：
- 接收 `SubWorkflowResult`
- 把 `subSteps` 写入 wfState（通过 `nodeState` 存储，供 buildSummary 读取）

`buildSummary` 增加 SubWorkflow case：
```java
case "SubWorkflow": {
    List<Map<String, Object>> subSteps = readSubSteps(nodeId); // 从 wfState 读
    String workflowName = getSubWorkflowName(nodeId);
    if (subSteps != null && !subSteps.isEmpty()) {
        summary = new HashMap<>();
        summary.put("workflowName", workflowName);
        summary.put("steps", subSteps);
    }
    break;
}
```

VISIBLE_NODES 加入：`"Switcher"`, `"SubWorkflow"`

### 前端架构

#### ThinkingSteps.vue 折叠面板

当 `step.summary && step.summary.steps` 存在时，在步骤行下方渲染折叠面板：

```
[SubWorkflow 步骤行]  子工作流名称  展开▼
  └─ [折叠面板，默认折叠]
       ├─ ● 知识库检索  完成  0.3s
       ├─ ● 问题分析完成  1.2s
       └─ ● 答案生成完成  2.1s
```

折叠面板交互：
- 默认折叠
- 点击展开/收起
- 子步骤样式与主步骤一致，无需额外缩进（面板本身已有缩进）

#### NODE_CONFIG 补充

```js
Switcher: { title: '路由判断', runningText: '判断中...' },
SubWorkflow: { title: '子工作流', runningText: '执行中...' }
```

#### getSummaryText 补充

```js
case 'Switcher':
  return s.outputText || null
case 'SubWorkflow':
  return s.workflowName ? `→ ${s.workflowName}` : null
```

#### chat.js onOpenPageCommand 简化

删除手动 push `__open_page__` 步骤的代码块（约 20 行），只保留：
```js
onOpenPageCommand: (command, realMessageId) => {
  import('@/components/70_ai/components/navigator/AiPageRouter.js').then(({ navigateToPage }) => {
    navigateToPage(command, router, { getters: rootGetters, commit, dispatch })
      .then((success) => {
        const routeLabel = command.page_mode === 'new' ? '新增页面' : (command.page_mode === 'edit' ? '编辑页面' : '页面')
        const resultContent = success ? `已为您打开${routeLabel}` : '页面打开失败'
        const finalMsgId = realMessageId || aiMessageId
        commit('UPDATE_MESSAGE', { messageId: finalMsgId, updates: { content: resultContent, status: 'delivered', isStreaming: false } })
      })
  })
}
```

`onComplete` 中 `hasOpenPageCommand` 判断删除，统一走 `workflowSteps` 持久化逻辑。

## 涉及文件

| 文件 | 改动类型 |
|------|---------|
| `WorkflowEngine.java` | 修复 buildSummary，加 Switcher/SubWorkflow case，VISIBLE_NODES 扩展 |
| `SwitcherNode.java` | 追加 matched_case_name 到 content |
| `WorkflowStarter.java` | runSync 收集 subSteps，返回 SubWorkflowResult |
| `SubWorkflowNode.java` | 接收 SubWorkflowResult，写 subSteps 到 nodeState |
| `SubWorkflowResult.java` | 新增 POJO |
| `chat.js` | 删除 `__open_page__` 手动步骤，简化 onComplete |
| `ThinkingSteps.vue` | 折叠面板渲染，NODE_CONFIG/getSummaryText 补充 |

## 执行顺序

1. 后端：修复 buildSummary（需求 1）
2. 后端：Switcher 加入 VISIBLE_NODES + SwitcherNode 追加字段（需求 2）
3. 前端：删除 `__open_page__` 手动步骤（需求 3）
4. 后端 + 前端：SubWorkflow 折叠步骤（需求 4，前后端联动）
