# SCM AI与Spring AI Alibaba框架对齐调研方案

## 一、调研背景

### 1.1 问题起源

工作流ID=34中，Switcher节点"权限判断条件"配置了一个条件`pZkTvFMANeJ6mCvJ66dsACLrlgGxqgod`对应2个目标节点：
- 临时知识库（QWV9vpC10Lt7dL5Ilh2sgVDTwls2IJFp）
- 项目管理知识库检索（AbBFxq_lUJYT7OZohGI1UFzYN2mIkGbq）

当前尝试通过"虚拟并行分发节点"方案绕过框架限制，但compile时报错：
```
parallel node [virtual_parallel_...] must have only one target
```

### 1.2 调研目标

1. 理解Spring AI Alibaba框架的标准实现方式
2. 分析SCM AI当前实现与框架的差异
3. 设计对齐方案（不使用虚拟节点）
4. 日志记录机制也一并对齐

---

## 二、Spring AI Alibaba框架机制调研

### 2.1 节点日志机制：GraphLifecycleListener

**源码位置**：`spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/GraphLifecycleListener.java`

**接口定义**：
```java
public interface GraphLifecycleListener {
    // 节点执行前
    default void before(String nodeId, Map<String, Object> state,
                        RunnableConfig config, Long curTime) {}

    // 节点执行后
    default void after(String nodeId, Map<String, Object> state,
                       RunnableConfig config, Long curTime) {}

    // 节点出错
    default void onError(String nodeId, Map<String, Object> state,
                         Throwable ex, RunnableConfig config) {}

    // 图开始
    default void onStart(String nodeId, Map<String, Object> state,
                         RunnableConfig config) {}

    // 图完成
    default void onComplete(String nodeId, Map<String, Object> state,
                            RunnableConfig config) {}
}
```

**配置方式**（CompileConfig.java）：
```java
CompileConfig config = CompileConfig.builder()
    .withLifecycleListener(new GraphLifecycleListener() {
        @Override
        public void before(String nodeId, Map<String, Object> state,
                           RunnableConfig config, Long curTime) {
            // 节点执行前：创建运行时记录、记录输入
        }

        @Override
        public void after(String nodeId, Map<String, Object> state,
                          RunnableConfig config, Long curTime) {
            // 节点执行后：记录输出、更新状态
        }
    })
    .build();

stateGraph.compile(config);
```

**官方实现参考**（GraphObservationLifecycleListener.java）：
```java
public class GraphObservationLifecycleListener implements GraphLifecycleListener {
    private final Map<String, Observation> nodeObservations = new ConcurrentHashMap<>();

    @Override
    public void before(String nodeId, Map<String, Object> state,
                       RunnableConfig config, Long curTime) {
        // 创建节点观测，记录输入状态
        Observation nodeObservation = Observation.createNotStarted(...);
        nodeObservation.highCardinalityKeyValue("NODE_BEFOR_STATE", state.toString());
        nodeObservation.start();
        nodeObservations.put(nodeId, nodeObservation);
    }

    @Override
    public void after(String nodeId, Map<String, Object> state,
                      RunnableConfig config, Long curTime) {
        // 记录输出状态，停止观测
        Observation nodeObservation = nodeObservations.remove(nodeId);
        nodeObservation.highCardinalityKeyValue("NODE_AFTER_STATE", state.toString());
        nodeObservation.stop();
    }
}
```

**关键特性**：
1. 框架自动在每个节点执行前后调用监听器
2. 只要节点被执行，就会触发before/after回调
3. 支持多个监听器（通过Deque管理，LIFO顺序处理）

---

### 2.2 条件路由机制：addConditionalEdges

**源码位置**：`spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/StateGraph.java`

**方法签名**：
```java
public StateGraph addConditionalEdges(
    String sourceId,
    AsyncCommandAction condition,
    Map<String, String> mappings  // 关键：Map<String, String>，一个key只能对应一个target
) throws GraphStateException
```

**标准使用方式**（MultiAgentSupervisorExample.java）：
```java
StateGraph workflow = new StateGraph(keyStrategyFactory)
    .addNode("supervisor", node_async(supervisor))
    .addNode("researcher", node_async(researcher))
    .addNode("coder", node_async(coder))
    .addEdge(START, "supervisor")
    .addConditionalEdges(
        "supervisor",
        edge_async(state -> {
            String next = (String) state.value("next").orElse("FINISH");
            return next;  // 返回单个字符串
        }),
        Map.of(
            "FINISH", END,
            "researcher", "researcher",
            "coder", "coder"
        )
    )
    .addEdge("researcher", "supervisor")
    .addEdge("coder", "supervisor");
```

**框架限制（EdgeCondition.java）**：
```java
// mappings是Map<String, String>，每个条件key只能对应一个target
public record EdgeCondition(AsyncCommandAction action, Map<String, String> mappings) {}
```

---

### 2.3 并行执行机制：addEdge

**源码位置**：`spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/StateGraph.java`

**并行分支实现**：
```java
// 从同一sourceId多次调用addEdge会自动合并到targets列表
public StateGraph addEdge(String sourceId, String targetId) {
    var newEdge = new Edge(sourceId, new EdgeValue(targetId));
    int index = edges.elements.indexOf(newEdge);
    if (index >= 0) {
        // 已存在从sourceId出发的边，追加到targets列表
        var newTargets = new ArrayList<>(edges.elements.get(index).targets());
        newTargets.add(newEdge.target());
        edges.elements.set(index, new Edge(sourceId, newTargets));
    } else {
        edges.elements.add(newEdge);
    }
    return this;
}
```

**标准并行分支示例**（NodeAfterListenerTest.java）：
```java
var workflow = new StateGraph(createKeyStrategyFactory())
    .addNode("A", makeNode("A"))
    .addNode("A1", makeNode("A1"))
    .addNode("A2", makeNode("A2"))
    .addNode("A3", makeNode("A3"))
    .addNode("B", makeNode("B"))
    .addEdge(START, "A")
    .addEdge("A", "A1")  // A 到 A1
    .addEdge("A", "A2")  // A 到 A2（并行）
    .addEdge("A", "A3")  // A 到 A3（并行）
    .addEdge("A1", "B")  // A1 汇聚到 B
    .addEdge("A2", "B")  // A2 汇聚到 B
    .addEdge("A3", "B")  // A3 汇聚到 B
    .addEdge("B", END);

// 配置并行执行器
app.stream(Map.of(), RunnableConfig.builder()
    .addParallelNodeExecutor("A", ForkJoinPool.commonPool())
    .build());
```

**框架要求（Edge.java）**：
```java
public boolean isParallel() {
    return targets.size() > 1;  // targets > 1 表示并行
}
```

**关键约束**：
1. 并行分支通过多次`addEdge`从同一sourceId创建
2. 框架自动识别并创建内部ParallelNode处理
3. **并行分支必须汇聚到同一个节点**（框架校验）

---

## 三、SCM AI当前实现分析

### 3.1 节点日志记录（runNode方法）

**当前实现**：
```java
// WorkflowEngine.java runNode方法
// 1. 创建运行时节点记录
AiWorkflowRuntimeNodeVo nodeVo = workflowRuntimeNodeService.createByState(
    userId, wfNode.getId(), runtimeId, nodeState);
streamHandler.sendNodeRun(wfNode.getUuid(), JSONObject.toJSONString(nodeVo));

// 2. 执行节点，带输入输出回调
NodeProcessResult processResult = abstractWfNode.process(
    // 输入回调
    (is) -> {
        workflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
        streamHandler.sendNodeInput(wfNode.getUuid(), ...);
    },
    // 输出回调
    (is) -> {
        workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
        streamHandler.sendNodeOutput(nodeUuid, ...);
    }
);
```

**问题**：
- 日志记录逻辑嵌入在runNode方法中
- 需要手动处理各种节点类型的特殊情况
- 没有使用框架的GraphLifecycleListener机制

### 3.2 条件路由实现（processConditionalEdges方法）

**当前实现**：
```java
// WorkflowEngine.java processConditionalEdges方法
for (Map.Entry<String, List<AiWorkflowEdgeEntity>> entry : edgesBySourceHandle.entrySet()) {
    String sourceHandle = entry.getKey();
    Set<String> uniqueTargets = ...;

    if (uniqueTargets.size() == 1) {
        // 单目标：直接映射
        mappings.put(sourceHandle, targetUuid);
    } else {
        // 多目标：创建虚拟并行分发节点（问题所在）
        String virtualNodeUuid = createParallelDispatchNode(...);
        mappings.put(sourceHandle, virtualNodeUuid);
    }
}
```

**问题**：
- 尝试用虚拟节点绕过框架限制
- 虚拟节点有多个出边，框架校验失败
- 两个目标的下游节点不同（不汇聚），不符合框架要求

---

## 四、差异对比

| 维度 | Spring AI Alibaba | SCM AI当前实现 |
|------|-------------------|----------------|
| **日志机制** | GraphLifecycleListener回调 | runNode方法内嵌逻辑 |
| **配置方式** | CompileConfig.withLifecycleListener() | 硬编码在runNode中 |
| **条件路由** | Map<String, String>，一对一 | 尝试支持一对多（失败） |
| **并行分支** | addEdge多次调用，必须汇聚 | 虚拟节点方案（失败） |
| **可扩展性** | 支持多个监听器 | 不支持扩展 |

---

## 五、对齐方案设计

### 5.1 日志机制对齐

**方案**：实现自定义GraphLifecycleListener

```java
public class ScmWorkflowLifecycleListener implements GraphLifecycleListener {

    private final WorkflowRuntimeNodeService runtimeNodeService;
    private final StreamHandler streamHandler;
    private final Map<String, Long> nodeRuntimeIds = new ConcurrentHashMap<>();

    @Override
    public void before(String nodeId, Map<String, Object> state,
                       RunnableConfig config, Long curTime) {
        // 1. 创建运行时节点记录
        AiWorkflowRuntimeNodeVo nodeVo = runtimeNodeService.createByState(...);
        nodeRuntimeIds.put(nodeId, nodeVo.getId());

        // 2. 发送节点运行开始消息
        streamHandler.sendNodeRun(nodeId, ...);

        // 3. 记录输入
        runtimeNodeService.updateInput(nodeVo.getId(), ...);
        streamHandler.sendNodeInput(nodeId, ...);
    }

    @Override
    public void after(String nodeId, Map<String, Object> state,
                      RunnableConfig config, Long curTime) {
        Long runtimeNodeId = nodeRuntimeIds.remove(nodeId);

        // 1. 记录输出
        runtimeNodeService.updateOutput(runtimeNodeId, ...);

        // 2. 发送节点输出消息
        streamHandler.sendNodeOutput(nodeId, ...);
    }

    @Override
    public void onError(String nodeId, Map<String, Object> state,
                        Throwable ex, RunnableConfig config) {
        // 记录错误状态
        Long runtimeNodeId = nodeRuntimeIds.remove(nodeId);
        runtimeNodeService.updateStatus(runtimeNodeId, FAIL, ex.getMessage());
    }
}
```

**编译配置**：
```java
// WorkflowEngine.java
ScmWorkflowLifecycleListener listener = new ScmWorkflowLifecycleListener(
    runtimeNodeService, streamHandler, ...);

CompileConfig config = CompileConfig.builder()
    .withLifecycleListener(listener)
    .build();

app = mainStateGraph.compile(config);
```

### 5.2 条件路由对齐

**方案**：接受框架限制，一个条件只能路由到一个目标

```java
// 简化processConditionalEdges方法
for (Map.Entry<String, List<AiWorkflowEdgeEntity>> entry : edgesBySourceHandle.entrySet()) {
    String sourceHandle = entry.getKey();
    Set<String> uniqueTargets = ...;

    // 每个sourceHandle只取第一个目标节点（符合框架设计）
    String targetUuid = uniqueTargets.iterator().next();
    mappings.put(sourceHandle, targetUuid);

    if (uniqueTargets.size() > 1) {
        log.warn("[processConditionalEdges] sourceHandle {} 配置了多个目标节点，" +
                "但框架限制只能路由到一个目标，已选择第一个目标: {}，" +
                "其他目标将被忽略: {}",
                sourceHandle, targetUuid, uniqueTargets);
    }
}
```

### 5.3 删除虚拟节点逻辑

**需要删除的代码**：

1. `WorkflowConstants.java`：
   - VIRTUAL_PARALLEL_NODE_PREFIX
   - COMPONENT_NAME_PARALLEL_DISPATCH

2. `WorkflowEngine.java`：
   - createParallelDispatchNode方法
   - runNode中的虚拟节点特殊处理
   - isEndNode中的虚拟节点判断
   - addNodeToStateGraph中的虚拟节点处理

3. `WfNodeFactory.java`：
   - ParallelDispatch节点注册

4. `ParallelDispatchNode.java`：
   - 删除整个文件

---

## 六、数据库表使用

日志记录继续使用SCM AI现有数据库表：

- `ai_workflow_runtime` - 工作流运行时记录
- `ai_workflow_runtime_node` - 节点运行时记录
- `ai_conversation_runtime` - 会话运行时记录
- `ai_conversation_runtime_node` - 会话节点运行时记录

GraphLifecycleListener只是改变了调用时机，数据存储逻辑不变。

---

## 七、预期效果

### 7.1 功能行为

**修改前**：
- Switcher条件匹配后，尝试创建虚拟节点路由到多个目标
- compile()时报错，工作流无法执行

**修改后**：
- Switcher条件匹配后，路由到**第一个**配置的目标节点
- 其他目标节点被忽略，记录警告日志
- compile()成功，工作流正常执行
- 日志记录由框架自动触发，代码更简洁

### 7.2 代码质量

- 删除约150行虚拟节点代码
- 日志逻辑从runNode中解耦
- 符合框架设计哲学
- 可扩展性提升（支持添加更多监听器）

---

## 八、替代方案（用户侧）

如果用户确实需要一个条件触发多个分支，可以：

### 方案A：显式并行节点
```
权限判断条件 → 触发检索 → [并行分发节点] → 临时知识库
                                        → 项目管理知识库检索
```

### 方案B：多个条件
```
权限判断条件 → 条件A（临时知识库） → 临时知识库
              ↓
              条件B（项目管理知识库）→ 项目管理知识库检索
```

---

## 九、实施建议

1. **优先级**：先实现条件路由对齐（删除虚拟节点），再实现日志机制对齐
2. **测试**：确保现有工作流不受影响
3. **文档**：更新工作流设计文档，说明"一个条件一个目标"的限制
4. **前端**：X6编辑器中，当用户配置多目标时显示警告提示

---

## 十、参考资料

- Spring AI Alibaba源码：`D:\2025_project\20_project_in_github\99_tools\spring-ai-alibaba`
- 关键文件：
  - `GraphLifecycleListener.java` - 节点生命周期监听接口
  - `GraphObservationLifecycleListener.java` - 官方实现参考
  - `StateGraph.java` - 图构建核心类
  - `EdgeCondition.java` - 条件边定义
  - `NodeAfterListenerTest.java` - 监听器测试用例
  - `MultiAgentSupervisorExample.java` - 条件路由示例
