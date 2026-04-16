# WrapCall 节点完成事件重构 Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 `node_complete` 事件发送逻辑从三个分散方法收拢到 `addNodeToStateGraph()` 的 WrapCall 闭包中，删除三个 ConcurrentHashMap。

**Architecture:** 在 `addNodeToStateGraph()` 的 action lambda 内捕获 `startTime` 和 `nodeState`，通过 `CompletableFuture.whenComplete()` 在节点执行完成后直接发送 `node_complete` 事件，无需跨方法传递数据。

**Tech Stack:** Java 17, Spring AI Alibaba graph-core, Project Reactor (Flux/Mono)

**Spec:** `docs/superpowers/specs/2026-03-16-wrapcall-node-complete-refactor-design.md`

---

## 涉及文件

| 文件 | 操作 |
|------|------|
| `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java` | 修改（全部改动集中在此文件） |

---

## Chunk 1: 新增 buildSummaryFromNodeState 方法

### Task 1: 新增 `buildSummaryFromNodeState()` 方法

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:463`

- [ ] **Step 1: 在 `buildSummaryFromNodeOutput()` 方法之后，新增 `buildSummaryFromNodeState()` 方法**

在第 673 行（`buildSummaryFromNodeOutput` 结束的 `}` 之后）插入以下方法：

```java
/**
 * 从WfNodeState构建节点摘要（WrapCall模式使用）
 * 替代 buildSummaryFromNodeOutput，直接从闭包内的 nodeState 读取数据
 */
private Map<String, Object> buildSummaryFromNodeState(String componentName, String nodeId, WfNodeState nodeState) {
    Map<String, Object> summary = null;
    try {
        List<NodeIOData> outputList = nodeState.getOutputs();

        boolean showOutput = getNodeShowProcessOutput(nodeId);

        switch (componentName) {
            case "Start": {
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
                        Object stateValue = nodeState.data().get(io.getName());
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
                String outputText = findOutputValue(outputList, DEFAULT_OUTPUT_PARAM_NAME);
                if (toolName != null || outputText != null) {
                    summary = new HashMap<>();
                    if (toolName != null) summary.put("toolName", toolName);
                    if (showOutput && outputText != null) summary.put("outputText", outputText);
                }
                break;
            }
            case "OpenPage": {
                String commandJson = findOutputValue(outputList, "open_page_command");
                String route = null;
                String pageMode = null;
                if (commandJson != null) {
                    try {
                        com.alibaba.fastjson2.JSONObject cmd = com.alibaba.fastjson2.JSONObject.parseObject(commandJson);
                        route = cmd.getString("route");
                        pageMode = cmd.getString("page_mode");
                    } catch (Exception ignored) {}
                }
                summary = new HashMap<>();
                List<Map<String, String>> params = new ArrayList<>();
                if (route != null) {
                    Map<String, String> p = new HashMap<>();
                    p.put("name", "route");
                    p.put("title", "路由");
                    p.put("value", route);
                    params.add(p);
                }
                if (pageMode != null) {
                    Map<String, String> p = new HashMap<>();
                    p.put("name", "page_mode");
                    p.put("title", "页面模式");
                    p.put("value", pageMode);
                    params.add(p);
                }
                try {
                    com.alibaba.fastjson2.JSONObject cmd2 = com.alibaba.fastjson2.JSONObject.parseObject(commandJson);
                    Object formData = cmd2.get("form_data");
                    if (formData != null) {
                        Map<String, String> p = new HashMap<>();
                        p.put("name", "form_data");
                        p.put("title", "参数");
                        p.put("value", formData.toString());
                        params.add(p);
                    }
                } catch (Exception ignored) {}
                // 从 nodeState.getInputs() 读取节点输入（替代 nodeInputCache）
                List<NodeIOData> nodeInputs = nodeState.getInputs();
                if (nodeInputs != null) {
                    Set<String> addedNames = new HashSet<>();
                    for (NodeIOData input : nodeInputs) {
                        String inputName = input.getName();
                        if (inputName == null || "input".equals(inputName)) continue;
                        if (!addedNames.add(inputName)) continue;
                        if (input.getContent() != null && input.getContent().getValue() != null) {
                            Map<String, String> p = new HashMap<>();
                            p.put("name", inputName);
                            String inputTitle = input.getContent().getTitle();
                            p.put("title", inputTitle != null && !inputTitle.isEmpty() ? inputTitle : inputName);
                            p.put("value", input.valueToString());
                            params.add(p);
                        }
                    }
                }
                if (!params.isEmpty()) {
                    summary.put("params", params);
                }
                String llmRawOutput = findOutputValue(outputList, DEFAULT_OUTPUT_PARAM_NAME);
                if (llmRawOutput != null && !llmRawOutput.isEmpty()) {
                    Map<String, String> p = new HashMap<>();
                    p.put("name", "llm_output");
                    p.put("title", "大模型返回");
                    p.put("value", llmRawOutput);
                    params.add(p);
                    summary.put("params", params);
                }
                if (showOutput) {
                    String modeLabel = "new".equals(pageMode) ? "新增"
                        : "edit".equals(pageMode) ? "编辑"
                        : "view".equals(pageMode) ? "查看"
                        : "approve".equals(pageMode) ? "审批"
                        : "list".equals(pageMode) ? "列表" : "";
                    String text = route != null
                        ? "已为您打开" + modeLabel + "页面: " + route
                        : "已为您打开页面";
                    summary.put("outputText", text);
                }
                break;
            }
            case "Switcher": {
                String caseName = findOutputValue(outputList, "matched_case_name");
                if (caseName != null && showOutput) {
                    summary = new HashMap<>();
                    summary.put("outputText", "-> " + caseName);
                }
                break;
            }
            case "Answer":
            case "LLM": {
                if (showOutput) {
                    String outputText = findOutputValue(outputList, DEFAULT_OUTPUT_PARAM_NAME);
                    if (outputText != null) {
                        summary = new HashMap<>();
                        summary.put("outputText", outputText);
                    }
                }
                break;
            }
            case "Template": {
                if (showOutput) {
                    String outputText = findOutputValue(outputList, DEFAULT_OUTPUT_PARAM_NAME);
                    if (outputText != null) {
                        summary = new HashMap<>();
                        summary.put("outputText", outputText);
                    }
                }
                break;
            }
            case "SubWorkflow": {
                String workflowName = wfNodes.stream()
                    .filter(n -> nodeId.equals(n.getUuid()))
                    .findFirst()
                    .map(n -> n.getNodeConfig() != null ? n.getNodeConfig().getString("workflow_name") : null)
                    .orElse(null);
                String subStepsJson = findOutputValue(outputList, "__sub_steps__");
                if (subStepsJson != null) {
                    summary = new HashMap<>();
                    if (workflowName != null) summary.put("workflowName", workflowName);
                    summary.put("steps", JSON.parseArray(subStepsJson));
                }
                break;
            }
        }
    } catch (Exception e) {
        log.debug("获取{}节点摘要失败: {}", componentName, e.getMessage());
    }
    long[] tokens = wfState.getNodeTokens(nodeId);
    if (tokens != null) {
        if (summary == null) summary = new HashMap<>();
        summary.put("totalTokens", tokens[0] + tokens[1]);
    }
    return summary;
}
```

- [ ] **Step 2: 确认新方法插入位置正确**

检查第 673 行后是否有 `buildSummaryFromNodeState` 方法，且旧的 `buildSummaryFromNodeOutput` 仍然存在（两个方法并存）。

---

## Chunk 2: 修改 addNodeToStateGraph 加入 WrapCall 逻辑

### Task 2: 修改 `addNodeToStateGraph()` 加入 WrapCall 逻辑

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1520-1524`

- [ ] **Step 1: 替换 `addNodeToStateGraph()` 中的 `stateGraph.addNode(...)` 调用**

将第 1520-1524 行：
```java
stateGraph.addNode(stateGraphNodeUuid, state -> CompletableFuture.supplyAsync(() -> {
    // 从OverAllState创建WfNodeState（复制data数据）
    WfNodeState nodeState = new WfNodeState(state.data());
    return runNode(wfNode, nodeState);
}));
```

替换为：
```java
String componentName = findComponentName(stateGraphNodeUuid);
stateGraph.addNode(stateGraphNodeUuid, state -> {
    WfNodeState nodeState = new WfNodeState(state.data());
    long startTime = System.currentTimeMillis();                    // WrapCall before
    return CompletableFuture.supplyAsync(() -> runNode(wfNode, nodeState))
        .whenComplete((result, ex) -> {                             // WrapCall after
            if (ex != null) return;                                 // 节点失败不发事件
            if (!VISIBLE_NODES.contains(componentName)) return;
            if (eventSink == null) return;
            long elapsed = System.currentTimeMillis() - startTime;
            String nodeTitle = findNodeTitle(stateGraphNodeUuid);
            Map<String, Object> summary = buildSummaryFromNodeState(
                componentName, stateGraphNodeUuid, nodeState);
            eventSink.next(WorkflowEventVo.createNodeCompleteData(
                stateGraphNodeUuid, componentName, nodeTitle, elapsed, summary));
        });
});
```

- [ ] **Step 2: 确认改动正确**

检查 `addNodeToStateGraph()` 方法体，确认：
- `componentName` 在 lambda 外捕获
- `startTime` 在 action lambda 内（`state -> {` 之后）
- `whenComplete` 有 `ex != null` 检查
- 调用的是 `buildSummaryFromNodeState`（新方法）

---

## Chunk 3: 删除 runNode 末尾 cache 写入

### Task 3: 删除 `runNode()` 末尾的 cache 写入

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1167-1169`

- [ ] **Step 1: 删除第 1167-1169 行的两行 cache put**

找到 `runNode()` 末尾（第 1164 行 `resultMap.put(outputKey, nodeState.getOutputs());` 之后），删除：
```java
// 缓存节点输出，供after回调中的buildSummary使用（框架after时输出尚未合并进state）
nodeOutputCache.put(wfNode.getUuid(), nodeState.getOutputs());
// 缓存节点输入，供after回调中的buildSummary使用
nodeInputCache.put(wfNode.getUuid(), nodeState.getInputs());
```

保留第 1164 行（`resultMap.put(outputKey, nodeState.getOutputs())`）和第 1171 行（`log.debug`）不变。

- [ ] **Step 2: 确认删除正确**

检查 `runNode()` 末尾，确认只有 `resultMap.put(outputKey, ...)` 和 `log.debug`，没有 `nodeOutputCache.put` 和 `nodeInputCache.put`。

---

## Chunk 4: 删除 handleGraphResponse 中的 node_complete 发送逻辑

### Task 4: 删除普通节点的 node_complete 发送逻辑

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:767-779`

- [ ] **Step 1: 删除第 767-779 行（普通节点 node_complete 块）**

找到 `handleGraphResponse()` 中以下代码块并删除：
```java
// 发送 node_complete（当前节点完成）
// 用 nodeStartTimes.remove 的返回值判断：只有第一次（startTime != null）才发送，避免流式节点每个chunk都重复发送
if (VISIBLE_NODES.contains(componentName)) {
    Long startTime = nodeStartTimes.remove(nodeId);
    if (startTime != null) {
        long duration = System.currentTimeMillis() - startTime;
        String nodeTitle = findNodeTitle(nodeId);
        Map<String, Object> summary = buildSummaryFromNodeOutput(componentName, nodeId, nodeOutput);
        nodeOutputCache.remove(nodeId);
        nodeInputCache.remove(nodeId);
        events.add(WorkflowEventVo.createNodeCompleteData(nodeId, componentName, nodeTitle, duration, summary));
    }
}
```

- [ ] **Step 2: 确认删除正确**

检查 `handleGraphResponse()` 中普通节点处理部分（非 `__PARALLEL__` 分支），确认没有 `nodeStartTimes.remove`、`nodeOutputCache.remove`、`nodeInputCache.remove` 的调用。

### Task 5: 删除并行节点的 node_complete 发送逻辑

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:724-764`

- [ ] **Step 1: 删除 `__PARALLEL__` 分支中的 node_complete 发送部分（第 744-751 行）**

找到 `__PARALLEL__` 分支中的 for 循环，删除以下代码（保留 output 事件部分）：
```java
Long startTime = nodeStartTimes.remove(parallelChildId);
if (startTime == null) continue;
long duration = now - startTime;
String childTitle = findNodeTitle(parallelChildId);
Map<String, Object> childSummary = buildSummaryFromNodeOutput(childComponentName, parallelChildId, nodeOutput);
nodeOutputCache.remove(parallelChildId);
nodeInputCache.remove(parallelChildId);
events.add(WorkflowEventVo.createNodeCompleteData(parallelChildId, childComponentName, childTitle, duration, childSummary));
```

同时删除 `__PARALLEL__` 分支开头的 `long now = System.currentTimeMillis();`（如果只被上面代码使用）。

- [ ] **Step 2: 确认 `__PARALLEL__` 分支只保留 output 事件逻辑**

检查 `__PARALLEL__` 分支的 for 循环，确认只剩下 output 事件部分（第 752-762 行）：
```java
AbstractWfNode childNode = wfState.getCompletedNodes().stream()
    .filter(item -> item.getNode().getUuid().equals(parallelChildId))
    .findFirst().orElse(null);
if (childNode != null && !wfState.hasNodeStreamed(parallelChildId)) {
    List<NodeIOData> outputList = childNode.getState().getOutputs();
    Map<String, Object> outputs = outputList.stream()
        .collect(Collectors.toMap(NodeIOData::getName, d -> d, (v1, v2) -> v2));
    String compName = childNode.getWfComponent() != null ? childNode.getWfComponent().getName() : "";
    events.add(WorkflowEventVo.createNodeOutputData(parallelChildId, compName, outputs));
}
```

---

## Chunk 5: 简化 NodeEventListener，删除字段和旧方法

### Task 6: 简化 `NodeEventListener.before()`

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1808-1815`

- [ ] **Step 1: 删除 `NodeEventListener.before()` 中的 `nodeStartTimes.put()`**

找到 `NodeEventListener` 内部类的 `before()` 方法，删除：
```java
nodeStartTimes.put(nodeId, curTime);
```

保留 `log.debug` 行不变。

- [ ] **Step 2: 确认 `before()` 只剩日志**

检查 `NodeEventListener.before()` 方法体，确认只有 `log.debug` 一行，没有 `nodeStartTimes.put`。

### Task 7: 删除三个 ConcurrentHashMap 字段

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:88-106`

- [ ] **Step 1: 删除三个字段声明**

删除第 84-106 行中的三个字段（含注释）：

```java
/**
 * 节点开始时间记录，用于计算节点执行耗时
 * 格式：nodeUuid → 开始时间戳（毫秒）
 */
private final ConcurrentHashMap<String, Long> nodeStartTimes = new ConcurrentHashMap<>();
```

```java
/**
 * 节点输出缓存，用于在after回调中获取当前节点输出
 * 框架after回调时当前节点输出尚未合并进state，通过此缓存传递
 * 格式：nodeUuid → 节点输出列表
 */
private final ConcurrentHashMap<String, List<NodeIOData>> nodeOutputCache = new ConcurrentHashMap<>();
```

```java
/**
 * 节点输入缓存：nodeUuid → 节点输入列表
 * 供after回调中的buildSummary读取（after时WfNodeState已不可访问）
 */
private final ConcurrentHashMap<String, List<NodeIOData>> nodeInputCache = new ConcurrentHashMap<>();
```

- [ ] **Step 2: 确认字段已删除，编译无报错**

检查文件中不再有 `nodeStartTimes`、`nodeOutputCache`、`nodeInputCache` 的声明。此时如果前面步骤都完成，编译应该通过。

### Task 8: 删除旧的 `buildSummaryFromNodeOutput()` 方法

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:463-673`

- [ ] **Step 1: 删除 `buildSummaryFromNodeOutput()` 整个方法**

删除第 463-673 行的整个 `buildSummaryFromNodeOutput` 方法（从方法签名到最后的 `}`）。

- [ ] **Step 2: 确认旧方法已删除**

搜索文件中是否还有 `buildSummaryFromNodeOutput`，确认不存在。

---

## Chunk 6: 提交

### Task 9: Git 提交

- [ ] **Step 1: 提交改动**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "refactor(ai): WrapCall模式重构node_complete事件，删除三个ConcurrentHashMap"
```
