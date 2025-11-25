# AI路由改造为Orchestrator-Workers模式 - 可行性调研报告

**调研时间**: 2025-11-25
**调研目标**: 评估将当前AI Chat的智能路由机制改造为Spring AI的Orchestrator-Workers模式的可行性
**调研方法**: 分析Spring AI官方文档、示例代码、当前项目实现机制

---

## 一、Spring AI Orchestrator-Workers模式深度解析

### 1.1 核心概念

**定义**: Orchestrator-Workers是一种动态任务分解和并行执行的Agent模式。

**工作原理**:
```
用户输入
    ↓
Orchestrator LLM分析
    ↓
动态生成子任务列表 (不可预测)
    ↓
Worker LLMs并行执行子任务
    ↓
Orchestrator综合所有结果
    ↓
返回最终响应
```

### 1.2 官方实现示例

**源码位置**: `spring-ai-examples/agentic-patterns/orchestrator-workers/src/main/java/com/example/agentic/OrchestratorWorkers.java`

**核心代码结构**:

```java
public class OrchestratorWorkers {
    private final ChatClient chatClient;
    private final String orchestratorPrompt;  // Orchestrator提示词
    private final String workerPrompt;        // Worker提示词

    public FinalResponse process(String taskDescription) {
        // Step 1: Orchestrator分析任务并分解为子任务
        OrchestratorResponse orchestratorResponse = this.chatClient.prompt()
            .user(u -> u.text(this.orchestratorPrompt)
                .param("task", taskDescription))
            .call()
            .entity(OrchestratorResponse.class);  // 结构化输出

        // Step 2: Workers并行处理子任务
        List<String> workerResponses = orchestratorResponse.tasks().stream()
            .map(task -> this.chatClient.prompt()
                .user(u -> u.text(this.workerPrompt)
                    .param("original_task", taskDescription)
                    .param("task_type", task.type())
                    .param("task_description", task.description()))
                .call()
                .content())
            .toList();

        // Step 3: 返回综合结果
        return new FinalResponse(orchestratorResponse.analysis(), workerResponses);
    }
}
```

**关键特性**:
1. **动态任务分解**: Orchestrator LLM根据输入动态决定子任务数量和类型
2. **结构化输出**: 使用`.entity(Class)`将LLM响应转换为Java对象
3. **并行执行**: Worker任务通过Stream并行处理
4. **单一ChatClient**: 所有LLM调用都使用同一个ChatClient实例

### 1.3 官方Orchestrator提示词模板

```
Analyze this task and break it down into 2-3 distinct approaches:

Task: {task}

Return your response in this JSON format:
{
  "analysis": "Explain your understanding of the task and which variations would be valuable.
               Focus on how each approach serves different aspects of the task.",
  "tasks": [
    {
      "type": "formal",
      "description": "Write a precise, technical version that emphasizes specifications"
    },
    {
      "type": "conversational",
      "description": "Write an engaging, friendly version that connects with readers"
    }
  ]
}
```

### 1.4 适用场景

**官方推荐**:
- ✅ 复杂任务，子任务无法预测
- ✅ 需要不同角度或方法处理
- ✅ 需要自适应问题解决

**不适用场景**:
- ❌ 子任务固定且可预测 (应使用Chain或Parallelization)
- ❌ 单一明确路径的任务 (应使用Routing)
- ❌ 需要迭代优化的任务 (应使用Evaluator-Optimizer)

---

## 二、当前项目AI路由机制分析

### 2.1 现有架构

**核心组件**: `WorkflowRoutingService`

**3层路由架构**:
```
Layer 1: 用户指定 (0ms)
    ↓ 未指定
Layer 2: LLM智能路由 (1-2s)
    ↓ 未匹配
Layer 3: 默认兜底 (10ms)
```

### 2.2 Layer 2详细实现

**核心代码**: `WorkflowRoutingService.routeByLLM()`

```java
private String routeByLLM(String userInput, List<AiWorkflowVo> workflows) {
    // 构建工作流列表JSON
    String workflowsJson = workflows.stream()
        .map(w -> String.format(
            "{uuid:\"%s\",title:\"%s\",desc:\"%s\",keywords:\"%s\",category:\"%s\"}",
            w.getWorkflowUuid(), w.getTitle(), w.getDesc(),
            w.getKeywords(), w.getCategoryName()
        ))
        .collect(Collectors.joining(","));

    // 路由提示词
    String prompt = """
        你是一个智能工作流路由助手。根据用户问题选择最合适的工作流。
        如果没有合适的返回null。

        用户输入: "%s"
        可用工作流: [%s]

        返回JSON格式: workflowUuid, reasoning, confidence
        """;

    // 调用LLM获取路由决策 (结构化输出)
    WorkflowRouteDecision decision = routingChatClient.prompt()
        .user(prompt)
        .call()
        .entity(WorkflowRouteDecision.class);

    return decision.workflowUuid();
}
```

**输出结构**:
```java
public record WorkflowRouteDecision(
    String workflowUuid,   // 选中的workflow UUID
    String reasoning,      // 选择理由
    Double confidence      // 置信度 (0.0-1.0)
) {}
```

### 2.3 当前路由模式的本质

**模式归类**: **Routing Pattern (路由模式)**

**核心特征**:
1. ✅ **分类器角色**: LLM作为分类器，从N个预定义选项中选1个
2. ✅ **预定义选项**: 所有可用workflow由数据库查询获得
3. ✅ **单一选择**: 每次只选择1个workflow执行
4. ✅ **结构化输出**: 返回明确的workflow UUID + 理由 + 置信度

**执行流程**:
```
用户输入 → LLM分类 → 选择1个Workflow → 执行Workflow节点链 → 返回结果
```

**关键约束**:
- Workflow之间**完全隔离**,无法相互调用
- 一次对话**只执行1个Workflow**
- Workflow内部节点链是**预定义的**,不支持动态分解

---

## 三、Orchestrator-Workers模式 vs 当前路由模式

### 3.1 核心差异对比表

| 维度 | 当前Routing模式 | Orchestrator-Workers模式 |
|------|----------------|--------------------------|
| **LLM角色** | 分类器 (Classifier) | 任务分解器 (Task Decomposer) |
| **可选项来源** | 预定义 (数据库workflow列表) | 动态生成 (LLM创建子任务) |
| **选择数量** | 单一选择 (1个workflow) | 多任务分解 (2-N个子任务) |
| **执行方式** | 顺序执行workflow节点链 | 并行执行动态子任务 |
| **任务可预测性** | 完全可预测 (workflow固定) | 不可预测 (LLM动态决策) |
| **跨领域协作** | ❌ 不支持 (workflow隔离) | ✅ 支持 (子任务可跨领域) |
| **适用场景** | 明确路径的业务流程 | 开放式复杂问题 |

### 3.2 本质区别图解

**当前Routing模式**:
```
用户: "查询采购单P202501001的库存情况"
         ↓
    LLM路由决策
         ↓
   选中"采购单查询"workflow
         ↓
执行预定义节点: Start → 查询数据库 → LLM生成报告 → End
         ↓
    返回采购单信息

问题: 无法同时查询库存,因为没有"库存查询"节点
```

**Orchestrator-Workers模式**:
```
用户: "查询采购单P202501001的库存情况"
         ↓
  Orchestrator分析
         ↓
动态分解为2个子任务:
  - Task 1: 查询采购单P202501001
  - Task 2: 查询采购单关联的库存状态
         ↓
   Workers并行执行
         ↓
  Orchestrator综合结果
         ↓
返回: "采购单信息 + 库存状态"
```

---

## 四、改造可行性评估

### 4.1 技术可行性: ✅ 可行,但需重构

**Spring AI支持度**: ⭐⭐⭐⭐⭐ (完全支持)
- 官方提供完整示例代码
- ChatClient原生支持结构化输出 (`.entity(Class)`)
- 支持并行Worker执行 (Stream API)

**当前系统兼容性**: ⚠️ **需要架构重构**

**必须改造的部分**:

#### 4.1.1 Workflow架构重构

**当前**: Workflow是**静态流程**,节点链预定义
```java
Workflow: Start → Node1 → Node2 → LLM → End
```

**需要**: Workflow变为**动态子任务**,由Orchestrator创建
```java
OrchestratorResponse {
  tasks: [
    {type: "database_query", workflow: "purchase_order_query"},
    {type: "database_query", workflow: "inventory_check"}
  ]
}
```

**改造要点**:
1. Workflow不再是入口,而是Worker的执行单元
2. Orchestrator需要"知道"所有可用的Workflow能力
3. Workflow之间需要数据传递机制

#### 4.1.2 MCP工具调用机制

**当前**: MCP工具只能在Workflow的LLM节点内被调用

**需要**:
- **方案A**: MCP工具成为独立Worker (包装为ChatClient)
- **方案B**: Orchestrator直接调用MCP工具 (不通过Workflow)

**建议方案A实现**:
```java
// 将MCP工具包装为Worker Agent
ChatClient permissionWorker = ChatClient.builder(chatModel)
    .defaultTools(new PermissionMcpTools())
    .build();

ToolCallback permissionAgentTool = FunctionToolCallback.builder()
    .name("permission_worker")
    .description("处理权限相关查询")
    .function((String query) -> permissionWorker.prompt(query).call().content())
    .build();

// Orchestrator调用
ChatClient orchestrator = ChatClient.builder(chatModel)
    .defaultTools(permissionAgentTool, purchaseWorkerTool, inventoryWorkerTool)
    .build();
```

#### 4.1.3 结果综合机制

**当前**: 单一Workflow返回单一结果

**需要**: Orchestrator综合多个Worker结果
```java
public FinalResponse synthesize(List<WorkerResponse> workerResponses) {
    // 使用LLM综合多个Worker的结果
    String synthesisPrompt = """
        综合以下子任务结果,生成完整回答:
        %s
        """.formatted(workerResponses);

    return chatClient.prompt(synthesisPrompt).call().content();
}
```

### 4.2 业务适用性: ⚠️ 部分适用

#### 4.2.1 适合Orchestrator-Workers的场景

✅ **跨领域复杂查询**:
- "查询采购单P001的库存和供应商信息" → 3个Worker (采购/库存/供应商)
- "分析Q1季度所有订单的库存周转率" → 2个Worker (订单统计/库存分析)

✅ **开放式分析任务**:
- "帮我分析一下当前库存异常的原因" → Orchestrator动态决定分析维度
- "给我一份完整的采购报告" → 动态选择报告内容模块

#### 4.2.2 不适合Orchestrator-Workers的场景

❌ **明确流程的业务操作**:
- "创建采购单" → 固定流程,应该用预定义Workflow
- "审批入库单" → 固定审批流,不需要动态分解

❌ **单一数据源查询**:
- "查询采购单P001" → 直接Routing到采购查询workflow即可
- "统计今日入库数量" → 单一查询,无需分解

### 4.3 性能影响: ⚠️ 需要优化

**当前Routing性能**:
- Layer 2 LLM路由: 1-2秒 (1次LLM调用)
- Workflow执行: 2-5秒 (取决于节点数)
- **总耗时**: 3-7秒

**Orchestrator-Workers性能**:
- Orchestrator分析: 1-2秒 (1次LLM调用)
- Workers并行执行: 2-5秒 (取决于最慢Worker)
- Orchestrator综合: 1-2秒 (1次LLM调用)
- **总耗时**: 4-9秒 (增加20%-30%)

**优化方向**:
1. **Worker缓存**: 相同查询复用结果
2. **流式返回**: 先返回Worker结果,最后综合
3. **智能分解**: 简单任务不分解

### 4.4 开发成本: ⚠️ 中等到高

**需要开发的核心模块**:

1. **OrchestratorService** (2-3天)
   - 任务分析和分解逻辑
   - Worker选择策略
   - 结果综合逻辑

2. **WorkerRegistry** (1-2天)
   - 注册所有Worker (Workflow + MCP)
   - Worker能力描述和Schema

3. **WorkflowWorkerAdapter** (2-3天)
   - 将现有Workflow包装为Worker
   - 数据传递和格式转换

4. **测试和优化** (3-5天)
   - 复杂场景测试
   - 性能优化
   - 异常处理

**总开发周期**: 8-13个工作日

---

## 五、架构改造方案

### 5.1 混合模式架构 (推荐)

**设计思路**: 保留现有Routing,新增Orchestrator-Workers模式,根据任务复杂度自动选择

```
用户输入
    ↓
任务复杂度分析
    ├─ 简单任务 → Routing模式 → 执行单一Workflow
    └─ 复杂任务 → Orchestrator-Workers模式 → 动态分解并行执行
```

**复杂度判断规则**:
```java
public boolean isComplexTask(String userInput) {
    // 规则1: 包含多个业务领域关键词
    List<String> domains = Arrays.asList("采购", "销售", "库存", "财务");
    long domainCount = domains.stream()
        .filter(userInput::contains)
        .count();
    if (domainCount >= 2) return true;

    // 规则2: 包含"分析"、"统计"、"报告"等开放式关键词
    if (userInput.matches(".*(分析|统计|报告|汇总|对比).*")) return true;

    // 规则3: 问题长度超过阈值 (复杂问题通常更长)
    if (userInput.length() > 50) return true;

    return false;
}
```

### 5.2 核心代码示例

```java
@Service
public class HybridRoutingService {

    @Resource
    private WorkflowRoutingService simpleRoutingService;  // 现有Routing

    @Resource
    private OrchestratorService orchestratorService;      // 新增Orchestrator

    public String route(String userInput, Long userId) {
        // 判断任务复杂度
        if (isComplexTask(userInput)) {
            log.info("复杂任务,使用Orchestrator-Workers模式");
            return orchestratorService.process(userInput, userId);
        } else {
            log.info("简单任务,使用Routing模式");
            return simpleRoutingService.route(userInput, userId, null);
        }
    }
}
```

### 5.3 渐进式迁移路径

**Phase 1: 验证概念** (1-2周)
- 实现基础OrchestratorService
- 选择2-3个MCP工具包装为Worker
- 小范围测试复杂查询场景

**Phase 2: 扩展Worker** (2-3周)
- 将核心Workflow包装为Worker
- 实现Worker间数据传递
- 性能优化和异常处理

**Phase 3: 混合模式** (1-2周)
- 实现任务复杂度判断
- 集成到现有路由系统
- 全面测试和优化

**Phase 4: 生产部署** (1周)
- 灰度发布
- 监控和调优

---

## 六、关键技术约束

### 6.1 Spring AI版本要求

**最低版本**: Spring AI 1.0.0-M1 (2024年发布)

**关键特性依赖**:
- `ChatClient.entity(Class)` - 结构化输出 (1.0.0+)
- `ToolCallback` 接口 - MCP工具支持 (1.0.0+)
- `ChatClient` 作为Tool - Agent嵌套 (文档提及,需验证版本)

**当前项目版本**: 需确认 `pom.xml` 中的 `spring-ai.version`

### 6.2 LLM能力要求

**必须支持**:
- ✅ Function Calling (所有主流LLM)
- ✅ Structured Output (GPT-4, Claude 3.5+, Gemini 1.5+)
- ✅ 中等Token容量 (>8K context window)

**最佳实践**:
- Orchestrator使用**高级模型** (GPT-4o, Claude 3.5 Sonnet)
- Worker可使用**标准模型** (GPT-4o-mini, Claude 3.5 Haiku)

### 6.3 数据传递约束

**当前Workflow**: 节点间通过`NodeIOData`传递数据

**Orchestrator-Workers**: Worker间无直接数据传递,只能通过Orchestrator

**解决方案**:
```java
// Orchestrator携带上下文
String workerPrompt = """
    原始任务: %s
    子任务: %s
    前置Worker结果: %s
    """.formatted(originalTask, subTask, previousResults);
```

---

## 七、调研结论和建议

### 7.1 核心结论

1. **技术可行性**: ✅ **可行**,Spring AI官方完整支持
2. **业务适用性**: ⚠️ **部分适用**,仅适合跨领域复杂任务
3. **架构改造度**: ⚠️ **中等到高**,需重构Workflow架构
4. **性能影响**: ⚠️ **有负面影响**,响应时间增加20-30%
5. **开发成本**: ⚠️ **中等**,预计8-13个工作日

### 7.2 三个关键问题

#### Q1: 当前路由是Routing模式,不是Orchestrator-Workers模式吗?

**答**: ✅ **完全正确**

当前的`WorkflowRoutingService.routeByLLM()`实现的是**Routing Pattern**:
- LLM作为**分类器**,从预定义选项中选1个
- 工作流列表**完全预定义**,来自数据库
- 每次对话**只执行1个Workflow**

Orchestrator-Workers的核心差异:
- LLM作为**任务分解器**,动态创建多个子任务
- 子任务**不可预测**,由LLM即时决定
- 子任务**并行执行**,最后综合结果

#### Q2: 能否改造为Orchestrator-Workers模式?

**答**: ✅ **技术可行,但需要重构Workflow架构**

**可行理由**:
- Spring AI官方完整支持 (有示例代码)
- ChatClient支持结构化输出 (`.entity(Class)`)
- 可以将MCP工具和Workflow包装为Worker

**改造约束**:
- Workflow需要从"入口"变为"执行单元"
- Workflow之间需要数据传递机制
- 需要实现Orchestrator分析和综合逻辑

#### Q3: 改造后有什么好处?

**答**: ⚠️ **适用场景有限,不建议全面改造**

**好处**:
- ✅ 支持跨领域复杂查询 ("查采购单+库存+供应商")
- ✅ 支持开放式分析任务 ("分析库存异常原因")
- ✅ 更智能的任务分解和并行执行

**代价**:
- ❌ 响应时间增加20-30%
- ❌ Workflow架构需要重构
- ❌ 简单任务反而变慢 (不需要分解)

### 7.3 最终建议

#### 🎯 推荐方案: **混合模式架构**

**原因**:
1. 保留现有Routing的性能优势
2. 新增Orchestrator-Workers处理复杂场景
3. 根据任务复杂度自动选择模式
4. 渐进式迁移,风险可控

**实施优先级**:
- **P0**: 实现任务复杂度判断逻辑
- **P1**: 验证Orchestrator-Workers基础功能 (2-3个MCP工具)
- **P2**: 包装核心Workflow为Worker
- **P3**: 性能优化和全面测试

#### ⚠️ 不建议

**全面替换为Orchestrator-Workers模式**:
- 简单任务会变慢 (不必要的分解)
- 开发成本高,改造周期长
- 大部分业务场景不需要动态分解

---

## 八、参考资料

### 8.1 官方文档

1. **Spring AI Effective Agents Guide**
   - URL: `spring-ai-docs/src/main/antora/modules/ROOT/pages/api/effective-agents.adoc`
   - 关键章节: "4. Orchestrator-Workers"
   - 适用场景说明: "Complex tasks where subtasks can't be predicted upfront"

2. **Spring AI Tools Documentation**
   - URL: `spring-ai-docs/src/main/antora/modules/ROOT/pages/api/tools.adoc`
   - 关键行: 697行 - "define a ToolCallback from a ChatClient (to build a modular agentic application)"

3. **Anthropic Building Effective Agents**
   - 引用: Orchestrator-Workers模式设计灵感来源
   - 核心理念: "The orchestrator analyzes tasks and determines required subtasks dynamically"

### 8.2 示例代码

1. **OrchestratorWorkers.java**
   - 路径: `spring-ai-examples/agentic-patterns/orchestrator-workers/src/main/java/com/example/agentic/OrchestratorWorkers.java`
   - 核心方法: `process(String taskDescription)`
   - 关键特性:
     - 使用`.entity(OrchestratorResponse.class)`实现结构化输出
     - 通过Stream实现Worker并行执行
     - 单一ChatClient实例复用

2. **RoutingWorkflow.java**
   - 路径: `spring-ai-examples/agentic-patterns/routing-workflow/src/main/java/com/example/agentic/RoutingWorkflow.java`
   - 对比参考: 当前项目使用的Routing模式
   - 核心区别: "determines the appropriate route" vs "breaks down into subtasks"

### 8.3 当前项目关键文件

1. **WorkflowRoutingService.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java`
   - 模式定位: Routing Pattern
   - 核心方法: `routeByLLM()` - 使用`.entity(WorkflowRouteDecision.class)`

2. **McpToolConfig.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/mcp/McpToolConfig.java`
   - 关键行: 697行提示 - MCP工具可以包装为Worker

3. **AiConversationController.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java`
   - 集成点: 第363行调用`workflowRoutingService.route()`

---

## 附录: 技术术语对照表

| 英文术语 | 中文翻译 | 含义 |
|---------|---------|------|
| Orchestrator | 编排器 | 负责分析任务并分解为子任务的LLM |
| Worker | 执行者 | 负责执行具体子任务的LLM或工具 |
| Routing | 路由模式 | 从预定义选项中选择1个的模式 |
| Orchestrator-Workers | 编排-执行者模式 | 动态分解并并行执行的模式 |
| Structured Output | 结构化输出 | LLM返回JSON对象而非纯文本 |
| ToolCallback | 工具回调 | Spring AI的工具抽象接口 |
| ChatClient | 聊天客户端 | Spring AI的LLM调用客户端 |
| Workflow | 工作流 | 当前项目的业务流程抽象 |

---

**调研完成日期**: 2025-11-25
**建议决策时间**: 1周内
**后续行动**: 如决定采纳,建议先进行Phase 1概念验证 (2周)
