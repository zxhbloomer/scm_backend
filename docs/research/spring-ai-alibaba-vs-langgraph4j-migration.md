# Spring AI Alibaba vs LangGraph4j 迁移调研报告

## 1. 执行摘要

本报告深入分析了SCM项目从LangGraph4j迁移到Spring AI Alibaba Graph模块的可行性和具体方案。

**核心结论**：
- **迁移可行性**: 高度可行，Spring AI Alibaba的Graph模块API设计与LangGraph4j高度兼容
- **迁移难度**: 中等，核心是状态管理类和工作流引擎的重构
- **预期收益**: 统一Spring生态、原生MCP支持、更好的可观察性、社区活跃度更高

---

## 2. 框架对比分析

### 2.1 核心概念对比

| 概念 | LangGraph4j | Spring AI Alibaba Graph | 说明 |
|------|-------------|------------------------|------|
| 状态类 | `AgentState` | `OverAllState` | 两者都是Map<String, Object>封装 |
| 图构建器 | `StateGraph<T>` | `StateGraph` | API几乎一致 |
| 编译图 | `CompiledGraph<T>` | `CompiledGraph` | 方法名相同 |
| 节点动作 | `node_async()` | `AsyncNodeAction` | 函数式接口 |
| 边动作 | `edge_async()` | `AsyncEdgeAction` | 函数式接口 |
| 条件边 | `addConditionalEdges()` | `addConditionalEdges()` | 方法签名一致 |
| 起止常量 | `START`, `END` | `START`, `END` | 完全相同 |
| 序列化器 | `ObjectStreamStateSerializer` | `StateSerializer` | 接口设计 |
| 状态保存 | `MemorySaver` | `MemorySaver/RedisSaver` | Alibaba支持更多存储 |

### 2.2 状态管理对比

#### LangGraph4j AgentState (当前使用)
```java
public class AgentState {
    private final Map<String, Object> data;

    public AgentState(Map<String, Object> initData) {
        this.data = new HashMap<>(initData);
    }

    public final Map<String, Object> data() {
        return unmodifiableMap(data);
    }

    public final <T> Optional<T> value(String key) {
        return ofNullable((T) data().get(key));
    }
}
```

#### Spring AI Alibaba OverAllState (目标)
```java
public final class OverAllState implements Serializable {
    private final Map<String, Object> data;
    private final Map<String, KeyStrategy> keyStrategies;
    private Store store;

    public OverAllState(Map<String, Object> data) {
        this.data = data != null ? new HashMap<>(data) : new HashMap<>();
        this.keyStrategies = new HashMap<>();
    }

    public final Map<String, Object> data() {
        return unmodifiableMap(data);
    }

    public final <T> Optional<T> value(String key) {
        return ofNullable((T) data().get(key));
    }

    // 额外功能: KeyStrategy支持更灵活的状态更新策略
    public OverAllState registerKeyAndStrategy(String key, KeyStrategy strategy);
}
```

**关键差异**:
1. `OverAllState`支持`KeyStrategy`定义每个key的更新策略（Replace/Append等）
2. `OverAllState`支持`Store`进行长期记忆存储
3. `OverAllState`支持快照功能`snapShot()`
4. 两者的`data()`和`value()`方法签名完全一致，迁移成本低

### 2.3 StateGraph对比

#### LangGraph4j StateGraph (当前使用)
```java
StateGraph<WfNodeState> mainStateGraph = new StateGraph<>(stateSerializer);
mainStateGraph.addNode(nodeId, node_async((state) -> runNode(wfNode, state)));
mainStateGraph.addEdge(source, target);
mainStateGraph.addConditionalEdges(sourceId, edge_async(state -> ...), mappings);
CompiledGraph<WfNodeState> app = mainStateGraph.compile();
```

#### Spring AI Alibaba StateGraph (目标)
```java
StateGraph mainStateGraph = new StateGraph();
mainStateGraph.addNode(nodeId, (state, config) -> runNode(wfNode, state));
mainStateGraph.addEdge(source, target);
mainStateGraph.addConditionalEdges(sourceId, AsyncCommandAction.of(condition), mappings);
CompiledGraph app = mainStateGraph.compile();
```

**关键差异**:
1. Spring AI Alibaba的StateGraph不需要泛型参数（使用OverAllState）
2. 节点动作接口略有不同：增加了`config`参数
3. `compile()`方法支持更丰富的配置选项

---

## 3. SCM项目AI模块现状分析

### 3.1 模块结构

```
scm-ai/
├── workflow/                    # 工作流核心（LangGraph4j依赖）
│   ├── WorkflowEngine.java      # ★ 核心引擎，需重点重构
│   ├── WfNodeState.java         # ★ 继承AgentState，需替换
│   ├── WfState.java             # 工作流实例状态
│   ├── WfNodeFactory.java       # 节点工厂
│   └── node/                    # 各类节点实现
├── core/service/
│   ├── chat/                    # 对话服务
│   ├── rag/                     # RAG知识库服务
│   ├── elasticsearch/           # 向量检索服务
│   ├── neo4j/                   # 图谱查询服务
│   └── workflow/                # 工作流管理服务
├── config/                      # 配置类
├── controller/                  # REST API
└── bean/                        # 实体和VO
```

### 3.2 LangGraph4j依赖分析

**当前pom.xml依赖**:
```xml
<!-- LangGraph4j - 工作流编排引擎(基于DAG) -->
<dependency>
    <groupId>org.bsc.langgraph4j</groupId>
    <artifactId>langgraph4j-core</artifactId>
</dependency>
<dependency>
    <groupId>org.bsc.langgraph4j</groupId>
    <artifactId>langgraph4j-langchain4j</artifactId>
</dependency>
```

**受影响的核心文件**:

| 文件 | LangGraph4j依赖 | 修改复杂度 |
|------|----------------|-----------|
| `WorkflowEngine.java` | StateGraph, CompiledGraph, node_async, edge_async, START, END | 高 |
| `WfNodeState.java` | 继承AgentState | 中 |
| `WfState.java` | 无直接依赖 | 低 |

### 3.3 WorkflowEngine.java 详细分析

**当前import**:
```java
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
```

**核心使用点**:
1. 状态序列化器: `ObjectStreamStateSerializer<WfNodeState>`
2. 状态图: `StateGraph<WfNodeState>`
3. 编译图: `CompiledGraph<WfNodeState>`
4. 异步动作: `node_async()`, `edge_async()`
5. 图常量: `START`, `END`
6. 运行配置: `RunnableConfig`
7. 节点输出: `NodeOutput<WfNodeState>`
8. 异步生成器: `AsyncGenerator`

---

## 4. 迁移方案

### 4.1 方案一: 完全替换（推荐）

**步骤**:

#### Step 1: 更新pom.xml依赖
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

<!-- 新增 -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-graph-core</artifactId>
    <version>${spring-ai-alibaba.version}</version>
</dependency>
```

#### Step 2: 重构WfNodeState.java

**当前实现**:
```java
public class WfNodeState extends AgentState implements Serializable {
    private String uuid;
    private Integer processStatus;
    private List<NodeIOData> inputs;
    private List<NodeIOData> outputs;

    public WfNodeState(Map<String, Object> initData) {
        super(initData);
    }
}
```

**目标实现**:
```java
public class WfNodeState extends OverAllState implements Serializable {
    // 保持字段不变
    private String uuid;
    private Integer processStatus;
    private List<NodeIOData> inputs;
    private List<NodeIOData> outputs;

    public WfNodeState(Map<String, Object> initData) {
        super(initData);
    }

    // data()方法签名一致，无需修改调用方
}
```

**或者更简洁的方案** - 不继承，组合使用:
```java
public class WfNodeState implements Serializable {
    private final OverAllState state;
    private String uuid;
    private Integer processStatus;
    private List<NodeIOData> inputs;
    private List<NodeIOData> outputs;

    public WfNodeState(Map<String, Object> initData) {
        this.state = new OverAllState(initData);
    }

    public Map<String, Object> data() {
        return state.data();
    }

    public <T> Optional<T> value(String key) {
        return state.value(key);
    }
}
```

#### Step 3: 重构WorkflowEngine.java

**Import替换**:
```java
// 移除
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

// 新增
import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncCommandAction;
import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
```

**核心代码替换**:

```java
// 1. 序列化器 - 移除（Spring AI Alibaba内置Jackson序列化）
// 旧代码:
// private final ObjectStreamStateSerializer<WfNodeState> stateSerializer = new ObjectStreamStateSerializer<>(WfNodeState::new);

// 2. StateGraph创建
// 旧代码:
// StateGraph<WfNodeState> mainStateGraph = new StateGraph<>(stateSerializer);
// 新代码:
StateGraph mainStateGraph = new StateGraph();

// 3. 节点添加
// 旧代码:
// stateGraph.addNode(nodeId, node_async((state) -> runNode(wfNode, state)));
// 新代码:
stateGraph.addNode(nodeId, (state, config) -> runNode(wfNode, (WfNodeState)state));

// 4. 条件边
// 旧代码:
// stateGraph.addConditionalEdges(sourceId, edge_async(state -> state.data().get("next").toString()), mappings);
// 新代码:
stateGraph.addConditionalEdges(sourceId,
    AsyncCommandAction.of(state -> state.data().get("next").toString()),
    mappings);

// 5. 编译
// 旧代码:
// app = mainStateGraph.compile();
// 新代码:
app = mainStateGraph.compile(); // 方法签名相同

// 6. 执行
// 旧代码:
// AsyncGenerator<NodeOutput<WfNodeState>> outputs = app.stream(Map.of(), invokeConfig);
// 新代码:
// Spring AI Alibaba使用不同的流式API，需要适配
```

### 4.2 方案二: 适配器模式（渐进式）

创建适配层，保持现有调用代码不变:

```java
// 适配器：将Spring AI Alibaba的OverAllState适配为WfNodeState接口
public class WfNodeStateAdapter extends OverAllState {
    private String uuid;
    private Integer processStatus;
    private List<NodeIOData> inputs;
    private List<NodeIOData> outputs;

    // 保持所有现有getter/setter
    // 内部使用OverAllState的data()存储
}
```

---

## 5. 其他AI模块优化建议

### 5.1 知识库模块 (RAG)

**当前实现**:
- 使用Elasticsearch进行向量存储
- 自定义EmbeddingModel (SiliconFlowEmbeddingModel)
- Neo4j进行知识图谱存储

**优化建议**:
1. 使用`spring-ai-alibaba-starter-dashscope`统一Embedding模型
2. 考虑使用Spring AI的`VectorStore`抽象接口
3. 保留Neo4j图谱存储（Spring AI Alibaba暂无替代）

### 5.2 对话模块 (Chat)

**当前实现**:
- 使用Spring AI OpenAI Starter (兼容DeepSeek)
- 自定义流式处理 (WorkflowStreamHandler)

**优化建议**:
1. 统一使用`spring-ai-alibaba-starter-dashscope`
2. 利用Spring AI Alibaba的ChatMemory功能
3. 使用内置的流式响应处理

### 5.3 MCP模块

**当前实现**:
- 使用`spring-ai-starter-mcp-server-webmvc`
- 使用`spring-ai-starter-mcp-client`

**优化建议**:
1. Spring AI Alibaba原生支持MCP，与Graph模块深度集成
2. 可以简化MCP工具注册流程

---

## 6. 实施计划

### Phase 1: 准备阶段（1-2天）
- [ ] 备份现有代码
- [ ] 创建feature分支
- [ ] 添加Spring AI Alibaba Graph依赖

### Phase 2: 核心重构（3-5天）
- [ ] 重构WfNodeState.java
- [ ] 重构WorkflowEngine.java核心逻辑
- [ ] 更新所有节点实现类的状态访问方式

### Phase 3: 测试验证（2-3天）
- [ ] 单元测试各节点功能
- [ ] 集成测试工作流执行
- [ ] 性能测试对比

### Phase 4: 清理完善（1-2天）
- [ ] 移除LangGraph4j依赖
- [ ] 清理无用代码
- [ ] 更新文档

---

## 7. 风险评估

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|--------|------|----------|
| API兼容性问题 | 中 | 高 | 创建适配层，渐进式迁移 |
| 异步执行行为差异 | 低 | 中 | 充分测试异步场景 |
| 序列化兼容问题 | 中 | 中 | 测试checkpoint恢复功能 |
| 性能差异 | 低 | 低 | 性能测试对比 |

---

## 8. 结论

**推荐方案**: 采用方案一（完全替换）

**理由**:
1. Spring AI Alibaba的Graph API与LangGraph4j高度兼容，迁移成本可控
2. 统一使用Spring生态，减少技术栈复杂度
3. Spring AI Alibaba社区活跃，文档完善，后续维护成本低
4. 原生支持MCP、可观察性等高级特性

**预期收益**:
- 技术栈统一，降低学习和维护成本
- 获得Spring AI Alibaba持续更新的新特性
- 更好的可观察性和调试能力
- 原生支持Redis等企业级存储

---

## 附录

### A. Spring AI Alibaba Graph核心类

```
com.alibaba.cloud.ai.graph
├── StateGraph.java          # 图构建器
├── CompiledGraph.java       # 编译后的图
├── OverAllState.java        # 状态容器
├── KeyStrategy.java         # 状态更新策略
├── CompileConfig.java       # 编译配置
├── RunnableConfig.java      # 运行配置
├── action/
│   ├── AsyncNodeAction.java # 节点动作
│   ├── AsyncEdgeAction.java # 边动作
│   └── AsyncCommandAction.java # 命令动作
├── checkpoint/
│   ├── savers/
│   │   ├── MemorySaver.java # 内存保存
│   │   └── RedisSaver.java  # Redis保存
│   └── config/
│       └── SaverConfig.java # 保存配置
└── serializer/
    └── StateSerializer.java # 状态序列化接口
```

### B. 参考资料

1. Spring AI Alibaba官方文档: https://github.com/alibaba/spring-ai-alibaba
2. LangGraph4j官方文档: https://github.com/bsorrentino/langgraph4j
3. SCM项目AI模块: scm-ai/
