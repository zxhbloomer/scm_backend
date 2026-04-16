# 节点生命周期事件主流化 Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `node_start`/`node_complete` SSE 事件从 `FluxSink` 侧通道移入 Reactor 主流，彻底消除并行节点场景下的竞态问题。

**Architecture:** `NodeEventListener.before/after` 退化为纯观察者（只记录 `nodeStartTimes`），`handleGraphResponse` 接管所有节点生命周期事件的发送。`WfState` 补充完整的边拓扑数据，供 `resolveNextNodes` 推断下一个节点。

**Tech Stack:** Java 17, Spring AI Alibaba Graph, Reactor (Project Reactor), Vue.js 2 + Vuex

---

## 文件改动清单

| 文件 | 路径 | 改动 |
|------|------|------|
| `WfState.java` | `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java` | `completedNodes` 改 `CopyOnWriteArrayList`；新增 `getEdgeTargets`、`addConditionalEdgeByHandle`、`getConditionalEdgeTargets` 方法；`conditionalEdges` 结构调整 |
| `WorkflowEngine.java` | `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java` | `buildStateGraph` 填充 `wfState.edges`；`processConditionalEdges` 填充 `wfState.conditionalEdges`；新增 `resolveNextNodes`；`handleGraphResponse` 发节点事件；`buildSummary` 迁移；`NodeEventListener` 退化 |
| `chat.js` | `src/components/70_ai/store/modules/chat.js`（前端仓库） | `executeWorkflowCommand` 补充 `onNodeEvent` 回调 |

---

## Chunk 1: WfState 数据结构升级

### Task 1: completedNodes 改为 CopyOnWriteArrayList

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java:15,65`

- [ ] **Step 1: 修改 import 和字段声明**

在 `WfState.java` 顶部 import 区域加入：
```java
import java.util.concurrent.CopyOnWriteArrayList;
```

将第 65 行：
```java
private List<AbstractWfNode> completedNodes = new LinkedList<>();
```
改为：
```java
private List<AbstractWfNode> completedNodes = new CopyOnWriteArrayList<>();
```

- [ ] **Step 2: 确认编译无误**

`getLatestOutputs()` 调用了 `completedNodes.get(index)`，`CopyOnWriteArrayList` 支持随机访问，无需改动。

---

### Task 2: WfState 新增边查询方法

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java`

当前 `conditionalEdges` 的结构是 `Map<String, List<String>>`（sourceUuid → targetUuids），没有 handle 维度。需要调整为支持按 handle 查询。

- [ ] **Step 1: 新增 conditionalEdgesByHandle 字段**

在 `WfState.java` 的字段区域，在 `conditionalEdges` 字段之后新增：

```java
/**
 * 条件分支边（按handle分组）
 * key: sourceNodeUuid + "|" + sourceHandle
 * value: 该handle对应的目标节点UUID列表
 */
private Map<String, List<String>> conditionalEdgesByHandle = new HashMap<>();
```

- [ ] **Step 2: 新增 addConditionalEdgeByHandle 方法**

在 `addConditionalEdge` 方法之后新增：

```java
/**
 * 新增条件分支边（按handle分组）
 *
 * @param sourceNodeUuid 条件节点UUID
 * @param sourceHandle   sourceHandle（如 "case_uuid_xxx" 或 "default_handle"）
 * @param targetNodeUuids 该handle对应的目标节点UUID列表
 */
public void addConditionalEdgeByHandle(String sourceNodeUuid, String sourceHandle, List<String> targetNodeUuids) {
    String key = sourceNodeUuid + "|" + sourceHandle;
    conditionalEdgesByHandle.put(key, new ArrayList<>(targetNodeUuids));
}
```

- [ ] **Step 3: 新增 getConditionalEdgeTargets 方法**

```java
/**
 * 获取条件分支节点指定handle的目标节点列表
 *
 * @param sourceNodeUuid 条件节点UUID
 * @param sourceHandle   sourceHandle
 * @return 目标节点UUID列表，未找到返回空列表
 */
public List<String> getConditionalEdgeTargets(String sourceNodeUuid, String sourceHandle) {
    String key = sourceNodeUuid + "|" + sourceHandle;
    List<String> targets = conditionalEdgesByHandle.get(key);
    return targets != null ? targets : Collections.emptyList();
}
```

- [ ] **Step 4: 新增 getEdgeTargets 方法**

```java
/**
 * 获取普通边的目标节点列表
 *
 * @param sourceNodeUuid 源节点UUID
 * @return 目标节点UUID列表，未找到返回空列表
 */
public List<String> getEdgeTargets(String sourceNodeUuid) {
    List<String> targets = edges.get(sourceNodeUuid);
    return targets != null ? targets : Collections.emptyList();
}
```

- [ ] **Step 5: 确认 Collections import 已存在**

`WfState.java` 顶部已有 `import java.util.*;`，`Collections` 已包含，无需额外 import。

---

## Chunk 2: buildStateGraph 填充完整边拓扑

### Task 3: buildStateGraph 填充 wfState.edges

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:999-1048`

当前代码（第 1007 行）只有一次 `wfState.addEdge(START, startNode.getUuid())`，其余边没有填充。

- [ ] **Step 1: 在 linearizeDivergentPaths 调用之前填充普通边**

找到 `buildStateGraph` 方法中第 1022 行注释 `// 3.5 线性化发散并行路径` 之前，在第 1020 行（`}`，conditionalNodeUuids 识别结束）之后插入：

```java
// 3.5 填充 wfState.edges（基于原始拓扑，在线性化之前）
// 只填充普通边（非条件分支节点的出边），条件边在 processConditionalEdges 中填充
for (AiWorkflowEdgeEntity edge : wfEdges) {
    if (!conditionalNodeUuids.contains(edge.getSourceNodeUuid())) {
        wfState.addEdge(edge.getSourceNodeUuid(), edge.getTargetNodeUuid());
    }
}
```

**注意**：这段代码必须在 `linearizeDivergentPaths(conditionalNodeUuids)` 调用之前，因为 `linearizeDivergentPaths` 会修改 `wfEdges`，我们需要基于原始拓扑填充。

- [ ] **Step 2: 确认 addEdgeToStateGraph 不调用 wfState.addEdge**

检查 `addEdgeToStateGraph` 方法（第 1228 行），确认它只调用 `stateGraph.addEdge(source, target)`，不调用 `wfState.addEdge()`。当前代码已经是这样，无需改动。

---

### Task 4: processConditionalEdges 填充 wfState.conditionalEdgesByHandle

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1100-1134`

- [ ] **Step 1: 在 uniqueTargets 确定后调用 addConditionalEdgeByHandle**

找到 `processConditionalEdges` 方法中 `for (Map.Entry<String, List<AiWorkflowEdgeEntity>> entry : edgesBySourceHandle.entrySet())` 循环（第 1100 行），在 `uniqueTargets` 确定之后（第 1108 行之后），在 `if (uniqueTargets.size() == 1)` 判断之前，插入：

```java
// 填充 wfState.conditionalEdgesByHandle，供 resolveNextNodes 推断下一个节点
wfState.addConditionalEdgeByHandle(switcherUuid, sourceHandle, uniqueTargets);
```

插入位置：第 1109 行（`if (uniqueTargets.size() == 1) {` 之前），即：

```java
// 去重后的 uniqueTargets 已确定
List<String> uniqueTargets = edges.stream()
    .map(AiWorkflowEdgeEntity::getTargetNodeUuid)
    .distinct()
    .collect(Collectors.toList());

// 新增：填充 wfState.conditionalEdgesByHandle
wfState.addConditionalEdgeByHandle(switcherUuid, sourceHandle, uniqueTargets);

if (uniqueTargets.size() == 1) {
    // ... 原有逻辑不变 ...
```

**注意**：`uniqueTargets` 在单目标和多目标两个分支之前就已确定（第 1105-1108 行），只需在 `if` 判断之前插入一行，不需要在两个分支里各插一行。

---

## Chunk 3: handleGraphResponse 接管节点事件

### Task 5: 提升 conditionalNodeUuids 为实例字段 + 新增 resolveNextNodes

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

`conditionalNodeUuids` 当前是 `buildStateGraph`（第 1011 行）和 `processConditionalEdges`（第 1063 行）各自的局部变量，`resolveNextNodes` 需要访问它，必须提升为实例字段。

- [ ] **Step 1: 在 WorkflowEngine 类顶部声明实例字段**

在类的字段声明区域（`nodeStartTimes`、`nodeOutputCache` 附近）新增：

```java
/** 条件分支节点UUID集合（Switcher/Classifier），buildStateGraph 阶段填充 */
private Set<String> conditionalNodeUuids = new HashSet<>();
```

- [ ] **Step 2: buildStateGraph 中改为赋值（不再声明）**

将第 1011 行：
```java
Set<String> conditionalNodeUuids = new HashSet<>();
```
改为：
```java
this.conditionalNodeUuids = new HashSet<>();
```

- [ ] **Step 3: processConditionalEdges 中删除局部变量声明，直接用实例字段**

将第 1063 行：
```java
Set<String> conditionalNodeUuids = new HashSet<>();
```
改为（删除声明，直接用实例字段，但 `processConditionalEdges` 是在 `buildStateGraph` 之后调用的，实例字段已填充）：

**注意**：`processConditionalEdges` 内部重新构建了一份 `conditionalNodeUuids`（第 1063-1072 行），这是冗余的。改为直接使用实例字段 `this.conditionalNodeUuids`，删除第 1063-1072 行的局部变量声明和填充逻辑。

- [ ] **Step 4: 新增 resolveNextNodes 方法**

在 `handleGraphResponse` 方法之后新增：

```java
/**
 * 推断当前节点完成后的下一个节点列表
 * 用于 handleGraphResponse 发送 node_start 预告事件
 */
private List<String> resolveNextNodes(String currentNodeId, NodeOutput nodeOutput) {
    List<String> result = new ArrayList<>();

    // 条件分支节点（Switcher/Classifier）：先判断当前节点是否是条件节点
    // 只有条件节点才读 next_source_handle，避免 OverAllState 累积快照污染普通节点判断
    if (conditionalNodeUuids.contains(currentNodeId)) {
        Object nextSourceHandle = nodeOutput.state().data().get("next_source_handle");
        if (nextSourceHandle != null) {
            List<String> targets = wfState.getConditionalEdgeTargets(currentNodeId, nextSourceHandle.toString());
            result.addAll(targets);
        }
        return result;
    }

    // 普通节点：从 wfState.edges 查
    result.addAll(wfState.getEdgeTargets(currentNodeId));
    return result;
}

---

### Task 6: 将 VISIBLE_NODES / findComponentName / findNodeTitle 提升到外部类 + buildSummaryFromNodeOutput

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

`VISIBLE_NODES`、`findComponentName`、`findNodeTitle` 当前是 `NodeEventListener` 内部类的私有成员，`handleGraphResponse`（外部类方法）需要使用它们。

- [ ] **Step 1: 将 VISIBLE_NODES 提升为外部类的 private static final 字段**

在 `WorkflowEngine` 类的常量区域（`nodeStartTimes` 等字段附近）新增：

```java
private static final Set<String> VISIBLE_NODES = Set.of(
    "Start", "End",
    "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
    "Answer", "McpTool", "DocumentExtractor", "LLM", "OpenPage", "Switcher", "SubWorkflow", "Template"
);
```

然后删除 `NodeEventListener` 内部类中的同名字段（第 1500-1504 行）。

- [ ] **Step 2: 将 findComponentName / findNodeTitle 提升为外部类的私有方法**

将 `NodeEventListener` 内部类中的 `findComponentName`（第 1542-1552 行）和 `findNodeTitle`（第 1554-1560 行）方法移到外部类（`NodeEventListener` 类定义之前），方法签名不变：

```java
private String findComponentName(String nodeId) {
    return wfNodes.stream()
        .filter(n -> nodeId.equals(n.getUuid()))
        .findFirst()
        .map(n -> components.stream()
            .filter(c -> c.getId().equals(n.getWorkflowComponentId()))
            .findFirst()
            .map(AiWorkflowComponentEntity::getName)
            .orElse(""))
        .orElse("");
}

private String findNodeTitle(String nodeId) {
    return wfNodes.stream()
        .filter(n -> nodeId.equals(n.getUuid()))
        .findFirst()
        .map(AiWorkflowNodeVo::getTitle)
        .orElse("");
}
```

然后删除 `NodeEventListener` 内部类中的这两个方法（内部类可以直接调用外部类的方法，无需保留副本）。

- [ ] **Step 3: 新增 buildSummaryFromNodeOutput 方法**

将原 `buildSummary(String componentName, String nodeId, Map<String, Object> state)` 方法（在 `NodeEventListener` 内部类中，第 1562 行）复制到外部类，改名为 `buildSummaryFromNodeOutput`，参数改为 `NodeOutput nodeOutput`，并将 `Start` 分支中的 `state.get(io.getName())` 改为 `nodeOutput.state().data().get(io.getName())`：

```java
private Map<String, Object> buildSummaryFromNodeOutput(String componentName, String nodeId, NodeOutput nodeOutput) {
    Map<String, Object> summary = null;
    try {
        List<NodeIOData> outputList = nodeOutputCache.get(nodeId);
        if (outputList == null) {
            String outputKey = NODE_OUTPUT_KEY_PREFIX + nodeId;
            Object outputObj = nodeOutput.state().data().get(outputKey);
            @SuppressWarnings("unchecked")
            List<NodeIOData> stateOutput = (outputObj instanceof List) ? (List<NodeIOData>) outputObj : null;
            outputList = stateOutput;
        }

        boolean showOutput = getNodeShowProcessOutput(nodeId);

        switch (componentName) {
            case "Start": {
                // Start 分支：从 OverAllState 取初始输入值
                // createOverAllState() 将 wfState.getInput() 以 name 为 key 放入 stateData
                AiWorkflowNodeVo startNodeVo = wfNodes.stream()
                    .filter(n -> nodeId.equals(n.getUuid())).findFirst().orElse(null);
                if (startNodeVo != null && startNodeVo.getInputConfig() != null
                        && startNodeVo.getInputConfig().getUserInputs() != null) {
                    summary = new HashMap<>();
                    List<Map<String, String>> params = new ArrayList<>();
                    for (AiWfNodeIOVo io : startNodeVo.getInputConfig().getUserInputs()) {
                        Map<String, String> p = new HashMap<>();
                        p.put("name", io.getName());
                        p.put("title", io.getTitle());
                        // 从 OverAllState 取初始输入值（原来从 state.get(io.getName())）
                        Object stateValue = nodeOutput.state().data().get(io.getName());
                        if (stateValue == null) {
                            for (NodeIOData wfInput : wfState.getInput()) {
                                if (io.getName().equals(wfInput.getName())) {
                                    stateValue = wfInput.getContent().getValue();
                                    break;
                                }
                            }
                        }
                        if (stateValue != null) {
                            p.put("value", String.valueOf(stateValue));
                        }
                        params.add(p);
                    }
                    summary.put("params", params);
                }
                break;
            }
            // 其余 case 分支与原 buildSummary 完全相同，直接复制
            // （KnowledgeRetrieval、Classifier、Answer、LLM、McpTool、OpenPage、SubWorkflow 等）
        }
    } catch (Exception e) {
        log.warn("buildSummaryFromNodeOutput 异常: nodeId={}", nodeId, e);
    }
    return summary;
}
```

**注意**：`switch` 中除 `Start` 分支外，其余分支（`KnowledgeRetrieval`、`Classifier`、`Answer`、`LLM`、`McpTool`、`OpenPage`、`SubWorkflow` 等）与原 `buildSummary` 完全相同，直接从原方法复制，不需要修改。

---

### Task 7: handleGraphResponse 发送节点生命周期事件

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:436-497`

这是核心改动。在 `handleGraphResponse` 的正常节点输出分支（第 463 行 `return Mono.fromFuture(...)` 块）中，在 `processNodeOutput(nodeOutput)` 之后，发送 `node_complete` 和 `node_start` 预告。

- [ ] **Step 1: 在 flatMapMany 块中发送节点事件**

找到第 465 行 `.flatMapMany(nodeOutput -> {` 块，在 `processNodeOutput(nodeOutput)` 调用之后，`String nodeId = nodeOutput.node()` 之前，插入节点事件发送逻辑：

```java
return Mono.fromFuture(graphResponse.getOutput())
    .flatMapMany(nodeOutput -> {
        processNodeOutput(nodeOutput);

        String nodeId = nodeOutput.node();
        String componentName = findComponentName(nodeId);
        List<WorkflowEventVo> events = new ArrayList<>();

        // 发送 node_complete（当前节点完成）
        if (VISIBLE_NODES.contains(componentName)) {
            Long startTime = nodeStartTimes.remove(nodeId);
            long duration = (startTime != null) ? (System.currentTimeMillis() - startTime) : 0L;
            String nodeTitle = findNodeTitle(nodeId);
            Map<String, Object> summary = buildSummaryFromNodeOutput(componentName, nodeId, nodeOutput);
            nodeOutputCache.remove(nodeId);
            nodeInputCache.remove(nodeId);
            events.add(WorkflowEventVo.createNodeCompleteData(nodeId, componentName, nodeTitle, duration, summary));
        }

        // 预告下一个节点（node_start）
        List<String> nextNodes = resolveNextNodes(nodeId, nodeOutput);
        for (String nextNodeId : nextNodes) {
            String nextComponentName = findComponentName(nextNodeId);
            if (VISIBLE_NODES.contains(nextComponentName)) {
                String nextTitle = findNodeTitle(nextNodeId);
                long now = System.currentTimeMillis();
                events.add(WorkflowEventVo.createNodeStartData(nextNodeId, nextComponentName, nextTitle, now));
            }
        }

        // 原有的 output 事件逻辑（保持不变）
        AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
            .filter(item -> item.getNode().getUuid().equals(nodeId))
            .findFirst()
            .orElse(null);

        if (abstractWfNode != null) {
            if (wfState.hasNodeStreamed(nodeId)) {
                log.debug("节点{}已流式输出，跳过output事件", nodeId);
                return Flux.fromIterable(events);
            }
            List<NodeIOData> outputList = abstractWfNode.getState().getOutputs();
            Map<String, Object> outputs = outputList.stream()
                .collect(Collectors.toMap(NodeIOData::getName, nodeIOData -> nodeIOData, (v1, v2) -> v2));
            String compName = abstractWfNode.getWfComponent() != null ? abstractWfNode.getWfComponent().getName() : "";
            events.add(WorkflowEventVo.createNodeOutputData(nodeId, compName, outputs));
        }

        return Flux.fromIterable(events);
    })
```

**关键**：原来的 `return Flux.just(WorkflowEventVo.createNodeOutputData(...))` 改为 `return Flux.fromIterable(events)`，把 `node_complete`、`node_start` 预告、`output` 三类事件一起发出。

---

### Task 8: NodeEventListener 退化为纯观察者

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1496-1540`

- [ ] **Step 1: 修改 before() 方法**

将 `before()` 方法改为只记录 `nodeStartTimes`，删除 `sink.next(node_start)`：

```java
@Override
public void before(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
    String componentName = findComponentName(nodeId);
    log.debug("[NodeEventListener.before] nodeId={}, componentName={}, thread={}", nodeId, componentName, Thread.currentThread().getName());
    if (!VISIBLE_NODES.contains(componentName)) {
        return;
    }
    nodeStartTimes.put(nodeId, curTime);
}
```

删除的内容：`findNodeTitle`、`FluxSink sink = wfState.getEventSink()`、`sink.next(node_start)` 调用。

- [ ] **Step 2: 修改 after() 方法**

将 `after()` 方法改为只保留日志，删除所有数据操作和 `sink.next`：

```java
@Override
public void after(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
    String componentName = findComponentName(nodeId);
    log.debug("[NodeEventListener.after] nodeId={}, componentName={}, thread={}", nodeId, componentName, Thread.currentThread().getName());
}
```

删除的内容：`nodeStartTimes.remove()`、`nodeOutputCache.remove()`、`nodeInputCache.remove()`、`buildSummary()`、`sink.next(node_complete)` 调用。

**注意**：`nodeOutputCache.remove()` 和 `nodeInputCache.remove()` 已移到 Task 7 的 `handleGraphResponse` 中执行。

---

## Chunk 4: 前端 bug fix

### Task 9: executeWorkflowCommand 补充 onNodeEvent 回调

**Files:**
- Modify: `src/components/70_ai/store/modules/chat.js:788-793`（前端仓库）

前端仓库路径：`D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js`

- [ ] **Step 1: 在 executeWorkflowCommand 的回调对象中补充 onNodeEvent**

找到第 788 行 `_cancelFunction = aiChatService.executeWorkflowCommand({...}, {` 的回调对象，在 `onStart` 之后、`onContent` 之前插入：

```javascript
onNodeEvent: (nodeEvent) => {
  if (nodeEvent.nodeEventType === 'node_start' || nodeEvent.nodeEventType === 'node_complete') {
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent
    })
  }
},
```

- [ ] **Step 2: 确认 onComplete 中已有 FLUSH_PENDING_NODE_COMPLETE**

检查第 887 行，确认 `commit('FLUSH_PENDING_NODE_COMPLETE', aiMessageId)` 已存在，无需改动。

---

## Chunk 5: commit

### Task 10: 提交所有改动

- [ ] **Step 1: 提交后端改动**

```bash
git -C D:/2025_project/20_project_in_github/00_scm_backend/scm_backend add \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java \
  scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C D:/2025_project/20_project_in_github/00_scm_backend/scm_backend \
  commit -m "refactor(ai): 节点生命周期事件主流化，消除FluxSink侧通道竞态"
```

- [ ] **Step 2: 提交前端改动**

```bash
git -C D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend add \
  src/components/70_ai/store/modules/chat.js
git -C D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend \
  commit -m "fix(ai): executeWorkflowCommand补充onNodeEvent回调"
```

---

## 验证清单

完成所有 Task 后，人工验证：

- [ ] 串行工作流执行：前端步骤列表按顺序显示，每个节点先转圈后完成
- [ ] 并行工作流（Switcher 多目标）：各分支节点事件均正确，无丢失
- [ ] 第一个节点的 `node_start` 正确发出（页面加载后第一个节点立即转圈）
- [ ] `executeWorkflowCommand` 模式（`@workflow 命令`）：步骤列表正常显示
- [ ] 后端日志无 `NodeEventListener.after` 里的 `sink.next` 调用（已删除）
