# SCM AI 工作流并行分叉与汇聚功能调研报告

## 调研背景

用户询问 SCM AI 模块的工作流是否支持：
1. **一个节点连线到多个节点**（并行分叉/Fan-out）
2. **多个节点汇聚到一个节点**（汇聚/Fan-in）

调研对象：`ai_workflow.id = 31`（合同审批工作流）

---

## 一、数据分析

### 1.1 工作流拓扑结构

从数据库查询结果分析，该工作流包含以下关键拓扑：

#### 并行分叉（1 → N）：
| 源节点 | 目标节点数 | 目标节点列表 |
|--------|-----------|-------------|
| 开始 | 3 | 常规审查点、法律应用条例审查、首部+尾部主体名称 |
| 首部+尾部主体名称 | 2 | 甲方名称一致性审查、乙方名称一致性审查 |
| 合规性审查分析 | 2 | 甲方企业天眼风险、乙方企业天眼风险 |

#### 汇聚（N → 1）：
| 目标节点 | 源节点数 | 源节点列表 |
|---------|---------|-----------|
| 企业风险总结 | **8个** | 常规审查点、条件分支(分支1)、法律有效性检查、错误返回(3nSw)、错误返回(lnu5)、甲方企业天眼风险、乙方企业天眼风险 |

### 1.2 边数据（关键部分）

```sql
-- 开始节点分叉到3个节点
ea068b030f7d429b8be99609fbc6b8f0 → a7bDBSfWKFNRBL3mgQekclDLBb0dwr (常规审查点)
ea068b030f7d429b8be99609fbc6b8f0 → LbHyqvUTDyfe0U9CrVj3FAfdQWrugCZb (法律应用条例审查)
ea068b030f7d429b8be99609fbc6b8f0 → 2dhkgokXJv5ghnqS9e24bORtBoi2KTCB (首部+尾部主体名称)

-- 多个节点汇聚到"企业风险总结"
a7bDBSfWKFNRBL3mgQekclDLBb0dwr → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi
jVP1G_NVu7ufKlhwy4jvQZcsWzx_TtS → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi (条件分支)
0cHMtISvnyq1li0IkpBvRdanSKjm2gZB → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi (法律有效性检查)
TVuQGpT99xztbBsXrdG_CtrHUF3QIXQN → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi (甲方企业天眼风险)
iAJwWfyhDzJszZkkb14AA1Z8_kLzTrR → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi (乙方企业天眼风险)
3nSw7XS1rKjTt2IoCTtbmKYEH9pQkOED → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi (错误返回1)
lnu5Of2hvvFcYquXakgjmo6ZShKQsxHd → jjqBsV5ITfmXJEcK9k1QxZ4eEChjXLmi (错误返回2)
```

---

## 二、当前 SCM AI 工作流引擎分析

### 2.1 代码位置

`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

### 2.2 并行分叉支持情况

**结论：✅ 支持**

当前实现通过 `GraphCompileNode` 类处理并行分叉：

```java
// WorkflowEngine.java:892-900
private boolean pointToParallelBranch(String nodeUuid) {
    int edgeCount = 0;
    for (AiWorkflowEdgeEntity edge : this.wfEdges) {
        if (edge.getSourceNodeUuid().equals(nodeUuid) && StringUtils.isBlank(edge.getSourceHandle())) {
            edgeCount = edgeCount + 1;
        }
    }
    return edgeCount > 1;  // 出边数 > 1 则判定为并行分叉
}
```

当检测到并行分叉时，会创建 `GraphCompileNode` 作为子图：

```java
// WorkflowEngine.java:866-875
private GraphCompileNode getOrCreateGraphCompileNode(String rootId) {
    GraphCompileNode graphCompileNode = new GraphCompileNode();
    graphCompileNode.setId("parallel_" + rootId);  // 前缀 parallel_ 标识并行子图
    graphCompileNode.setRoot(CompileNode.builder()...);
    nodeToParallelBranch.put(rootId, graphCompileNode);
    return graphCompileNode;
}
```

### 2.3 汇聚支持情况

**结论：⚠️ 部分支持，存在缺陷**

当前实现对多上游节点（汇聚）的处理在 `buildCompileNode` 方法：

```java
// WorkflowEngine.java:767-771
} else {
    // upstreamNodeUuids.size() > 1 的情况
    newNode = CompileNode.builder().id(node.getUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
    GraphCompileNode parallelBranch = nodeToParallelBranch.get(parentNode.getId());
    appendToNextNodes(Objects.requireNonNullElse(parallelBranch, parentNode), newNode);
}
```

**问题**：
1. 没有实现真正的"等待所有上游完成"的汇聚逻辑
2. 汇聚节点只是简单地被添加到某个父节点的下游，而不是等待所有分支完成
3. 对于8个节点汇聚到1个节点的情况，可能导致重复执行或执行顺序混乱

---

## 三、Spring AI Alibaba Graph 实现分析

### 3.1 并行分叉实现

Spring AI Alibaba 的 `StateGraph.addEdge` 方法原生支持同一源节点添加多个目标：

```java
// StateGraph.java:383-401
public StateGraph addEdge(String sourceId, String targetId) throws GraphStateException {
    var newEdge = new Edge(sourceId, new EdgeValue(targetId));

    int index = edges.elements.indexOf(newEdge);
    if (index >= 0) {
        // 关键：如果源节点已存在边，则追加目标（实现并行分叉）
        var newTargets = new ArrayList<>(edges.elements.get(index).targets());
        newTargets.add(newEdge.target());
        edges.elements.set(index, new Edge(sourceId, newTargets));
    } else {
        edges.elements.add(newEdge);
    }
    return this;
}
```

### 3.2 汇聚实现（重点）

Spring AI Alibaba 官方示例 `parallel-node` 展示了正确的汇聚模式：

```java
// ParallelNodeGraphConfiguration.java
StateGraph stateGraph = new StateGraph(keyStrategyFactory)
    .addNode("dispatcher", node_async(new DispatcherNode()))
    .addNode("translator", node_async(new TranslateNode(chatClientBuilder)))
    .addNode("expander", node_async(new ExpanderNode(chatClientBuilder)))
    .addNode("collector", node_async(new CollectorNode()))  // 汇聚节点

    // 并行边（分叉）
    .addEdge("dispatcher", "translator")
    .addEdge("dispatcher", "expander")

    // 汇聚边
    .addEdge("translator", "collector")
    .addEdge("expander", "collector")

    // 汇聚后的条件分支（检查是否所有分支都完成）
    .addConditionalEdges("collector", edge_async(new CollectorDispatcher()),
            Map.of("dispatcher", "dispatcher", END, END));
```

**关键：CollectorNode 的汇聚等待机制**

```java
// CollectorNode.java
public class CollectorNode implements NodeAction {
    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Thread.sleep(TIME_SLEEP);  // 等待时间

        String nextStep = END;
        if (!areAllExecutionResultsPresent(state)) {
            nextStep = "dispatcher";  // 如果未全部完成，返回dispatcher继续等待
        }

        updated.put("collector_next_node", nextStep);
        return updated;
    }

    // 检查所有并行分支是否都已完成
    public boolean areAllExecutionResultsPresent(OverAllState state) {
        return state.value("translate_content").isPresent()
            && state.value("expander_content").isPresent();
    }
}
```

### 3.3 ParallelGraphBuildingStrategy（更高级的实现）

Spring AI Alibaba Agent Framework 提供了更完善的并行图构建策略：

```java
// ParallelGraphBuildingStrategy.java
public StateGraph buildGraph(FlowGraphBuilder.FlowGraphConfig config) throws GraphStateException {
    // 1. 添加根节点（透明节点，负责分发）
    graph.addNode(rootAgent.name(), node_async(new TransparentNode()));
    graph.addEdge(START, rootAgent.name());

    // 2. 添加聚合器节点
    String aggregatorNodeName = rootAgent.name() + "_aggregator";
    graph.addNode(aggregatorNodeName,
        node_async(new EnhancedParallelResultAggregator(...)));

    // 3. 处理子代理的并行执行
    for (Agent subAgent : config.getSubAgents()) {
        FlowGraphBuildingStrategy.addSubAgentNode(subAgent, graph);
        graph.addEdge(rootAgent.name(), subAgent.name());     // 分叉：root → 每个子代理
        graph.addEdge(subAgent.name(), aggregatorNodeName);   // 汇聚：每个子代理 → 聚合器
    }

    // 4. 聚合器连接到结束
    graph.addEdge(aggregatorNodeName, END);

    return graph;
}
```

---

## 四、问题诊断

### 4.1 当前 SCM AI 工作流对汇聚的问题

1. **缺少汇聚等待机制**：当多个节点汇聚到一个节点时，没有等待所有上游节点完成的机制
2. **缺少状态聚合**：没有将多个分支的输出结果合并到汇聚节点
3. **子图边界不清晰**：`GraphCompileNode` 只处理了分叉，没有处理汇聚的收尾

### 4.2 数据流问题

```
当前问题场景：
开始 ──┬── 常规审查点 ──────────────────────────┐
       ├── 法律应用条例审查 → 条件分支 ──────────┤
       └── 首部+尾部主体名称 → ... → 错误返回 ──┼── 企业风险总结 → 结束
                                   ...        │
                            甲方企业天眼风险 ──┤
                            乙方企业天眼风险 ──┘

问题：企业风险总结节点可能在部分上游完成时就开始执行
```

---

## 五、优化方案

### 方案一：轻量级改进（推荐）

**核心思路**：在汇聚节点增加"等待计数器"机制

#### 5.1 修改 WfState 添加汇聚追踪

```java
// WfState.java 新增
private final Map<String, Set<String>> convergenceTracker = new ConcurrentHashMap<>();
private final Map<String, Integer> convergenceExpectedCount = new ConcurrentHashMap<>();

/**
 * 注册汇聚点
 * @param nodeUuid 汇聚节点UUID
 * @param expectedCount 预期的上游分支数量
 */
public void registerConvergencePoint(String nodeUuid, int expectedCount) {
    convergenceExpectedCount.put(nodeUuid, expectedCount);
    convergenceTracker.put(nodeUuid, ConcurrentHashMap.newKeySet());
}

/**
 * 标记某个分支已到达汇聚点
 * @param convergenceNodeUuid 汇聚节点UUID
 * @param branchNodeUuid 到达的分支节点UUID
 * @return true 如果所有分支都已到达
 */
public boolean markBranchArrived(String convergenceNodeUuid, String branchNodeUuid) {
    Set<String> arrivedBranches = convergenceTracker.get(convergenceNodeUuid);
    if (arrivedBranches == null) return true;

    arrivedBranches.add(branchNodeUuid);
    return arrivedBranches.size() >= convergenceExpectedCount.getOrDefault(convergenceNodeUuid, 1);
}

/**
 * 检查汇聚点是否就绪
 */
public boolean isConvergenceReady(String nodeUuid) {
    Set<String> arrived = convergenceTracker.get(nodeUuid);
    Integer expected = convergenceExpectedCount.get(nodeUuid);
    if (arrived == null || expected == null) return true;
    return arrived.size() >= expected;
}
```

#### 5.2 修改 WorkflowEngine 识别汇聚点

```java
// WorkflowEngine.java 新增
private void registerConvergencePoints() {
    // 遍历所有节点，找出有多个上游的节点（汇聚点）
    for (AiWorkflowNodeVo node : wfNodes) {
        List<String> upstreamUuids = getUpstreamNodeUuids(node.getUuid());
        if (upstreamUuids.size() > 1) {
            // 这是一个汇聚点，注册它
            this.wfState.registerConvergencePoint(node.getUuid(), upstreamUuids.size());
            log.info("注册汇聚点: {} 预期上游数: {}", node.getTitle(), upstreamUuids.size());
        }
    }
}
```

#### 5.3 在节点执行完成时标记到达

```java
// WorkflowEngine.java 的 runNode 方法修改
private Map<String, Object> runNode(AiWorkflowNodeVo wfNode, WfNodeState nodeState) {
    // ... 现有逻辑 ...

    Map<String, Object> result = nodeInstance.run(nodeState, streamHandler);

    // 节点执行完成后，标记到达下游汇聚点
    List<String> downstreamUuids = getDownstreamNodeUuids(wfNode.getUuid());
    for (String downstreamUuid : downstreamUuids) {
        if (wfState.isConvergencePoint(downstreamUuid)) {
            boolean ready = wfState.markBranchArrived(downstreamUuid, wfNode.getUuid());
            log.info("分支 {} 到达汇聚点 {}, 是否就绪: {}",
                wfNode.getTitle(), downstreamUuid, ready);
        }
    }

    return result;
}
```

#### 5.4 汇聚节点执行前检查就绪状态

```java
// 在 addNodeToStateGraph 中添加就绪检查
stateGraph.addNode(stateGraphNodeUuid, state -> CompletableFuture.supplyAsync(() -> {
    WfNodeState nodeState = new WfNodeState(state.data());

    // 如果是汇聚点，等待所有上游完成
    if (wfState.isConvergencePoint(stateGraphNodeUuid)) {
        while (!wfState.isConvergenceReady(stateGraphNodeUuid)) {
            try {
                Thread.sleep(100);  // 轮询等待
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("汇聚点 {} 所有上游已就绪，开始执行", stateGraphNodeUuid);
    }

    return runNode(wfNode, nodeState);
}));
```

### 方案二：参考 Spring AI Alibaba 的 CollectorNode 模式

如果想要更优雅的实现，可以创建专门的汇聚组件：

```java
// AiWorkflowConvergenceNode.java
@Component
public class AiWorkflowConvergenceNode extends AbstractWfNode {

    @Override
    public Map<String, Object> run(WfNodeState nodeState, WorkflowStreamHandler streamHandler) {
        // 获取所有上游分支的输出
        Map<String, Object> aggregatedResults = new HashMap<>();

        List<String> upstreamBranches = getUpstreamBranches();
        for (String branch : upstreamBranches) {
            Object branchOutput = nodeState.get("branch_output_" + branch);
            if (branchOutput != null) {
                aggregatedResults.put(branch, branchOutput);
            }
        }

        // 检查是否所有分支都有结果
        if (aggregatedResults.size() < upstreamBranches.size()) {
            // 未全部完成，设置状态让图引擎重试
            return Map.of("convergence_status", "waiting",
                         "arrived_count", aggregatedResults.size(),
                         "expected_count", upstreamBranches.size());
        }

        // 所有分支完成，执行聚合逻辑
        return executeAggregation(aggregatedResults);
    }
}
```

---

## 六、结论

### 6.1 当前支持情况

| 功能 | 支持状态 | 说明 |
|------|---------|------|
| 一个节点连线到多个节点（并行分叉） | ✅ 支持 | 通过 `GraphCompileNode` 和 `parallel_` 前缀子图实现 |
| 多个节点汇聚到一个节点 | ⚠️ 部分支持 | 数据层面支持，但缺少"等待所有上游完成"的机制 |

### 6.2 建议优先级

1. **高优先级**：实现方案一的轻量级改进，解决汇聚节点的等待机制
2. **中优先级**：参考 Spring AI Alibaba 的 `ParallelGraphBuildingStrategy` 优化并行执行
3. **低优先级**：引入专门的汇聚组件类型，在前端工作流编辑器中支持

### 6.3 风险评估

| 风险 | 等级 | 说明 |
|------|-----|------|
| 汇聚节点过早执行 | 高 | 当前实现可能在部分分支完成时就执行汇聚节点 |
| 数据丢失 | 中 | 如果汇聚节点执行时部分分支未完成，其输出数据可能丢失 |
| 执行顺序不确定 | 中 | 并行分支的执行顺序依赖底层线程调度 |

---

## 七、参考资料

1. Spring AI Alibaba Graph 核心源码：`spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/StateGraph.java`
2. 并行节点示例：`spring-ai-alibaba-examples-main/spring-ai-alibaba-graph-example/parallel-node/`
3. 并行图构建策略：`spring-ai-alibaba-agent-framework/src/main/java/com/alibaba/cloud/ai/graph/agent/flow/strategy/ParallelGraphBuildingStrategy.java`
