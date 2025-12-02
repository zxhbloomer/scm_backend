# Spring AI Alibaba Graph模块迁移方案设计文档

## 1. 变更概述

### 1.1 变更目标
将SCM AI模块的工作流引擎从LangGraph4j迁移到Spring AI Alibaba Graph模块。

### 1.2 变更范围

| 文件 | 变更类型 | 说明 |
|------|----------|------|
| `scm-ai/pom.xml` | 依赖替换 | 移除LangGraph4j，新增spring-ai-alibaba-graph-core |
| `WfNodeState.java` | 重构 | 从继承AgentState改为继承OverAllState |
| `WorkflowEngine.java` | 重构 | 更新import、API调用、流式处理逻辑 |

---

## 2. API兼容性分析

### 2.1 状态类对比

| 特性 | LangGraph4j AgentState | Spring AI Alibaba OverAllState |
|------|------------------------|--------------------------------|
| 数据容器 | `Map<String, Object>` | `Map<String, Object>` |
| data()方法 | `Map<String, Object> data()` | `Map<String, Object> data()` |
| value()方法 | `<T> Optional<T> value(String key)` | `<T> Optional<T> value(String key)` |
| 构造函数 | `AgentState(Map<String, Object>)` | `OverAllState(Map<String, Object>)` |

**结论**: 核心方法签名完全一致，迁移无痛。

### 2.2 StateGraph对比

| 特性 | LangGraph4j | Spring AI Alibaba |
|------|-------------|-------------------|
| 类型 | `StateGraph<T>` (泛型) | `StateGraph` (非泛型) |
| 构造 | `new StateGraph<>(serializer)` | `new StateGraph()` |
| START常量 | `StateGraph.START` | `StateGraph.START` |
| END常量 | `StateGraph.END` | `StateGraph.END` |
| addNode | `addNode(id, node_async(state -> ...))` | `addNode(id, state -> ...)` |
| addEdge | `addEdge(source, target)` | `addEdge(source, target)` |
| addConditionalEdges | `addConditionalEdges(source, edge_async(...), mappings)` | `addConditionalEdges(source, AsyncCommandAction.of(...), mappings)` |
| compile | `compile()` | `compile()` |

### 2.3 流式处理对比

| 特性 | LangGraph4j | Spring AI Alibaba |
|------|-------------|-------------------|
| 返回类型 | `AsyncGenerator<NodeOutput<T>>` | `Flux<NodeOutput>` |
| 迭代方式 | `for (NodeOutput out : outputs)` | `.subscribe()` 或 `.blockLast()` |
| NodeOutput | `NodeOutput<WfNodeState>` | `NodeOutput` (非泛型) |
| 获取节点ID | `out.node()` | `out.node()` |
| 获取状态 | `out.state()` | `out.state()` |

---

## 3. 详细变更方案

### 3.1 pom.xml变更

**移除依赖**:
```xml
<!-- 移除 -->
<dependency>
    <groupId>org.bsc.langgraph4j</groupId>
    <artifactId>langgraph4j-core</artifactId>
</dependency>
<dependency>
    <groupId>org.bsc.langgraph4j</groupId>
    <artifactId>langgraph4j-langchain4j</artifactId>
</dependency>
```

**新增依赖**:
```xml
<!-- 新增 -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-graph-core</artifactId>
</dependency>
```

### 3.2 WfNodeState.java变更

**当前代码** (第8行, 第29行):
```java
import org.bsc.langgraph4j.state.AgentState;

public class WfNodeState extends AgentState implements Serializable {
    public WfNodeState(Map<String, Object> initData) {
        super(initData);
    }
    public WfNodeState() {
        super(Map.of());
    }
}
```

**目标代码**:
```java
import com.alibaba.cloud.ai.graph.OverAllState;

public class WfNodeState extends OverAllState implements Serializable {
    public WfNodeState(Map<String, Object> initData) {
        super(initData);
    }
    public WfNodeState() {
        super(Map.of());
    }
}
```

**变更点**: 仅替换import和extends，构造函数签名完全兼容。

### 3.3 WorkflowEngine.java变更

#### 3.3.1 Import替换

**移除** (第17-27行):
```java
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
```

**新增**:
```java
import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncCommandAction;
import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import reactor.core.publisher.Flux;
```

#### 3.3.2 字段变更

**移除** (第52-54行):
```java
private final ObjectStreamStateSerializer<WfNodeState> stateSerializer = new ObjectStreamStateSerializer<>(WfNodeState::new);
private final Map<String, List<StateGraph<WfNodeState>>> stateGraphNodes = new HashMap<>();
private final Map<String, List<StateGraph<WfNodeState>>> stateGraphEdges = new HashMap<>();
```

**替换为**:
```java
private final Map<String, List<StateGraph>> stateGraphNodes = new HashMap<>();
private final Map<String, List<StateGraph>> stateGraphEdges = new HashMap<>();
```

**变更** (第39行):
```java
// 旧
private CompiledGraph<WfNodeState> app;
// 新
private CompiledGraph app;
```

#### 3.3.3 StateGraph创建 (第268行)

**旧代码**:
```java
StateGraph<WfNodeState> mainStateGraph = new StateGraph<>(stateSerializer);
```

**新代码**:
```java
StateGraph mainStateGraph = new StateGraph();
```

#### 3.3.4 节点添加 (第918行)

**旧代码**:
```java
stateGraph.addNode(stateGraphNodeUuid, node_async((state) -> runNode(wfNode, state)));
```

**新代码**:
```java
stateGraph.addNode(stateGraphNodeUuid, state -> {
    return java.util.concurrent.CompletableFuture.supplyAsync(() ->
        runNode(wfNode, (WfNodeState) state));
});
```

#### 3.3.5 条件边 (第849-853行)

**旧代码**:
```java
stateGraph.addConditionalEdges(
    stateGraphNodeUuid,
    edge_async(state -> state.data().get("next").toString()),
    mappings
);
```

**新代码**:
```java
stateGraph.addConditionalEdges(
    stateGraphNodeUuid,
    AsyncCommandAction.of(state ->
        java.util.concurrent.CompletableFuture.completedFuture(
            state.data().get("next").toString())),
    mappings
);
```

#### 3.3.6 流式处理 (exe方法, 第290-291行)

**旧代码**:
```java
AsyncGenerator<NodeOutput<WfNodeState>> outputs = app.stream(resume ? null : Map.of(), invokeConfig);
streamingResult(wfState, outputs);
```

**新代码**:
```java
Flux<NodeOutput> outputFlux = app.stream(resume ? Map.of() : Map.of(), invokeConfig);
streamingResultReactive(wfState, outputFlux);
```

#### 3.3.7 streamingResult方法重构 (第568-619行)

**旧代码**:
```java
private void streamingResult(WfState wfState, AsyncGenerator<NodeOutput<WfNodeState>> outputs) {
    for (NodeOutput<WfNodeState> out : outputs) {
        // ...处理逻辑
    }
}
```

**新代码**:
```java
private void streamingResultReactive(WfState wfState, Flux<NodeOutput> outputFlux) {
    outputFlux.doOnNext(out -> {
        DataSourceHelper.use(this.tenantCode);

        AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
                .filter(item -> item.getNode().getUuid().endsWith(out.node()))
                .findFirst()
                .orElse(null);

        if (null != abstractWfNode) {
            // 转换OverAllState为WfNodeState
            WfNodeState nodeState = abstractWfNode.getState();

            Long runtimeNodeId = null;
            if (callSource == WorkflowCallSource.AI_CHAT) {
                log.debug("AI Chat场景下streamingResult暂时跳过node更新");
            } else {
                AiWorkflowRuntimeNodeVo runtimeNodeVo = wfState.getRuntimeNodeByNodeUuid(out.node());
                if (null != runtimeNodeVo) {
                    runtimeNodeId = runtimeNodeVo.getId();
                }
            }

            if (runtimeNodeId != null) {
                if (callSource == WorkflowCallSource.AI_CHAT) {
                    conversationRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
                } else {
                    workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
                }
            } else {
                log.warn("Can not find runtime node, node uuid:{}", out.node());
            }

            wfState.setOutput(nodeState.getOutputs());
        } else {
            log.warn("Can not find node state,node uuid:{}", out.node());
        }
    }).blockLast(); // 阻塞等待所有节点执行完成
}
```

#### 3.3.8 buildStateGraph方法签名 (第793行)

**旧代码**:
```java
private void buildStateGraph(CompileNode upstreamCompileNode, StateGraph<WfNodeState> stateGraph, CompileNode compileNode)
```

**新代码**:
```java
private void buildStateGraph(CompileNode upstreamCompileNode, StateGraph stateGraph, CompileNode compileNode)
```

#### 3.3.9 addNodeToStateGraph方法签名 (第908行)

**旧代码**:
```java
private void addNodeToStateGraph(StateGraph<WfNodeState> stateGraph, String stateGraphNodeUuid)
```

**新代码**:
```java
private void addNodeToStateGraph(StateGraph stateGraph, String stateGraphNodeUuid)
```

#### 3.3.10 addEdgeToStateGraph方法签名 (第931行)

**旧代码**:
```java
private void addEdgeToStateGraph(StateGraph<WfNodeState> stateGraph, String source, String target)
```

**新代码**:
```java
private void addEdgeToStateGraph(StateGraph stateGraph, String source, String target)
```

#### 3.3.11 getApp方法返回类型 (第976行)

**旧代码**:
```java
public CompiledGraph<WfNodeState> getApp() {
    return app;
}
```

**新代码**:
```java
public CompiledGraph getApp() {
    return app;
}
```

#### 3.3.12 子图添加 (第817行)

**旧代码**:
```java
StateGraph<WfNodeState> subgraph = new StateGraph<>(stateSerializer);
stateGraph.addNode(stateGraphId, subgraph.compile());
```

**新代码**:
```java
StateGraph subgraph = new StateGraph();
stateGraph.addNode(stateGraphId, subgraph.compile());
```

---

## 4. 风险评估与缓解

### 4.1 风险矩阵

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| 流式处理行为差异 | 中 | 高 | 使用blockLast()保持同步语义 |
| 状态类型转换错误 | 低 | 中 | 统一使用`(WfNodeState)state`转换 |
| 异步执行上下文丢失 | 中 | 中 | 保留DataSourceHelper.use()调用 |
| 编译错误 | 低 | 低 | 逐步替换，每步编译验证 |

### 4.2 回滚方案

如果迁移后出现严重问题：
1. 恢复pom.xml的LangGraph4j依赖
2. 使用Git revert恢复WfNodeState.java和WorkflowEngine.java
3. 重新编译测试

---

## 5. 测试计划

### 5.1 单元测试
- [ ] WfNodeState构造和数据访问
- [ ] StateGraph构建（addNode, addEdge, addConditionalEdges）
- [ ] 工作流编译和执行

### 5.2 集成测试
- [ ] 简单线性工作流（Start → LLM → End）
- [ ] 条件分支工作流（Condition节点）
- [ ] 并行执行工作流
- [ ] 人机交互工作流（HumanFeedback节点）
- [ ] 子工作流调用

### 5.3 性能测试
- [ ] 对比迁移前后工作流执行时间
- [ ] 内存使用对比

---

## 6. 实施步骤

| 步骤 | 操作 | 预估时间 |
|------|------|----------|
| 1 | 修改pom.xml，添加Spring AI Alibaba Graph依赖 | 5分钟 |
| 2 | 重构WfNodeState.java | 5分钟 |
| 3 | 重构WorkflowEngine.java - Import替换 | 5分钟 |
| 4 | 重构WorkflowEngine.java - 字段类型修改 | 5分钟 |
| 5 | 重构WorkflowEngine.java - 方法签名修改 | 10分钟 |
| 6 | 重构WorkflowEngine.java - 核心逻辑修改 | 20分钟 |
| 7 | 编译验证 | 5分钟 |
| 8 | 移除LangGraph4j依赖 | 2分钟 |

---

## 7. 审批记录

| 角色 | 姓名 | 状态 | 日期 |
|------|------|------|------|
| 开发 | - | 等待 | - |
| 评审 | - | 等待 | - |

---

## 附录A: Spring AI Alibaba Graph核心类图

```
com.alibaba.cloud.ai.graph
├── StateGraph              # 图构建器（无泛型）
├── CompiledGraph           # 编译后的图（无泛型）
├── OverAllState            # 状态容器
├── NodeOutput              # 节点输出（无泛型）
├── RunnableConfig          # 运行配置
├── CompileConfig           # 编译配置
├── KeyStrategy             # 状态更新策略
└── action/
    ├── AsyncNodeAction     # 异步节点动作
    ├── AsyncEdgeAction     # 异步边动作
    └── AsyncCommandAction  # 异步命令动作
```

## 附录B: 关键差异总结

1. **泛型移除**: Spring AI Alibaba的StateGraph、CompiledGraph、NodeOutput都不使用泛型
2. **序列化器内置**: 不需要手动创建ObjectStreamStateSerializer
3. **流式API**: 从`AsyncGenerator`改为`Flux`（Project Reactor）
4. **节点动作**: 从`node_async()`改为直接传入lambda
5. **条件边动作**: 从`edge_async()`改为`AsyncCommandAction.of()`
