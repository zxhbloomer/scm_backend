# Orchestrator-Workers模式改造 - 设计方案

**文档版本**: v1.0
**创建时间**: 2025-11-25 20:51:29
**作者**: zzxxhh (AI Architect)
**状态**: ✅ 设计完成,待实施

---

## 📋 执行摘要

### 核心判断
**✅ 值得做** - 真实业务需求 + 简洁技术方案 + 零破坏性改造

### 关键指标
- **代码量**: ~180行核心代码
- **复杂度**: 2层缩进,4个核心概念
- **破坏性**: 零(完全向后兼容)
- **开发工时**: 3.5小时(2.5h开发 + 1h测试)
- **业务价值**: 解锁MCP+Workflow混合调用,支持复杂任务分解

---

## 🎯 业务背景

### 当前痛点

**Routing模式的核心限制**:
1. **单一执行限制**: 只能选择1个workflow,复杂任务需要拆分成多次对话
2. **MCP+Workflow隔离**: 无法混合调用,无法实现"查询+处理+通知"链路
3. **并行能力缺失**: 串行执行,响应时间长
4. **扩展性瓶颈**: 新增MCP/Workflow需要重新设计路由逻辑

### 真实业务需求

**用户原话**: "后面会有大量的mcp和workflow混合使用的场景"

**典型场景**:
1. **场景A**: 查询库存(Workflow) + 生成报表(Workflow) → 需要2个workflow并行执行
2. **场景B**: 权限查询(MCP) + 打开页面(MCP) + 记录日志(Workflow) → 需要MCP+Workflow混合
3. **场景C**: 数据验证(MCP) + 数据处理(Workflow) + 通知发送(MCP) → 复杂链路

**当前系统规模**:
- MCP工具: 5个 (Bin, Location, Warehouse, PageContext, Permission)
- Workflow: 5个已启用
- 趋势: 持续增长中

---

## 🏗️ 技术方案

### 架构设计原则

**基于Spring AI官方模式** (参考: `spring-ai-examples/agentic-patterns/orchestrator-workers`)

**核心理念**:
- ✅ **Worker = ChatClient调用** (不是复杂的wrapper)
- ✅ **数据结构极简** (3个record类,无抽象层)
- ✅ **零破坏性改造** (保持方法签名不变)
- ✅ **2层缩进标准** (符合Linus好品味原则)

### 数据结构设计

#### 1. SubTask - 子任务描述
```java
/**
 * Orchestrator分解的子任务
 * 参考: OrchestratorWorkers.java:110-111
 */
public record SubTask(
    String type,                // "workflow" | "mcp"
    String target,              // workflow_uuid 或 mcp_tool_name
    String description,         // 任务描述(给LLM看)
    Map<String, Object> params  // 执行参数
) {}
```

#### 2. OrchestratorResponse - Orchestrator响应
```java
/**
 * Orchestrator的任务分解结果
 * 参考: OrchestratorWorkers.java:122-123
 */
public record OrchestratorResponse(
    String analysis,      // Orchestrator的任务理解
    List<SubTask> tasks   // 分解的子任务列表(2-5个)
) {}
```

#### 3. OrchestratorFinalResponse - 最终响应
```java
/**
 * Orchestrator-Workers最终响应
 * 参考: OrchestratorWorkers.java:134
 */
public record OrchestratorFinalResponse(
    String analysis,               // Orchestrator的任务理解
    List<String> workerResults     // Workers的执行结果(字符串列表)
) {}
```

### 核心组件实现

#### 1. WorkflowToolCallback - Workflow包装器

**职责**: 将Workflow包装为ToolCallback,让Orchestrator能动态调用

**关键实现**:
```java
/**
 * Workflow的ToolCallback包装
 * 让workflow能被Orchestrator动态调用
 */
public class WorkflowToolCallback implements ToolCallback {

    private final String workflowUuid;
    private final String workflowTitle;
    private final String workflowDesc;
    private final String inputConfigJson;  // workflow的inputConfig
    private final WorkflowStarter workflowStarter;

    @Override
    public ToolDefinition getToolDefinition() {
        return ToolDefinition.builder()
            .name("workflow_" + workflowUuid)
            .description(workflowTitle + ": " + workflowDesc)
            .inputSchema(inputConfigJson)  // 直接使用workflow的inputConfig
            .build();
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        try {
            // 1. 解析toolInput为workflow inputs
            JSONObject inputs = JSON.parseObject(toolInput);

            // 2. 从ToolContext提取必要参数
            String tenantCode = (String) toolContext.getContext().get("tenantCode");
            String conversationId = (String) toolContext.getContext().get("conversationId");
            Map<String, Object> pageContext = (Map) toolContext.getContext().get("pageContext");

            // 3. 调用workflow执行引擎
            Flux<WorkflowEventVo> events = workflowStarter.streaming(
                workflowUuid,
                inputs,
                tenantCode,
                WorkflowCallSource.AI_CHAT_ORCHESTRATOR,  // 新来源标识
                conversationId,
                pageContext
            );

            // 4. 阻塞等待workflow完成,收集最终结果
            StringBuilder result = new StringBuilder();
            events.doOnNext(event -> {
                if (event.getEventType() == WorkflowEventType.COMPLETED) {
                    result.append(event.getData());
                }
            }).blockLast();  // ← 关键: 阻塞直到Flux完成

            return result.toString();

        } catch (Exception e) {
            log.error("WorkflowToolCallback执行失败: workflowUuid={}", workflowUuid, e);
            return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
```

**设计要点**:
- ✅ **处理Flux异步**: 使用`blockLast()`阻塞等待完成
- ✅ **动态inputSchema**: 直接使用workflow的inputConfig JSON
- ✅ **错误处理**: 异常转换为JSON错误响应
- ✅ **日志追踪**: 新增`WorkflowCallSource.AI_CHAT_ORCHESTRATOR`标识

#### 2. WorkflowRoutingService改造

**关键设计**: 保持`route()`方法签名不变,内部改造Layer 2

**修改前**:
```java
public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
    // Layer 1: 用户指定
    if (StringUtils.isNotBlank(specifiedWorkflowUuid)) {
        return specifiedWorkflowUuid;
    }

    // Layer 2: LLM路由 (选择1个workflow)
    String workflowUuid = routeByLLM(userInput, workflows);
    if (StringUtils.isNotBlank(workflowUuid)) {
        return workflowUuid;
    }

    // Layer 3: 兜底策略
    return getDefaultWorkflowUuid();
}
```

**修改后**:
```java
public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
    // Layer 1: 用户指定 (保持不变)
    if (StringUtils.isNotBlank(specifiedWorkflowUuid)) {
        return specifiedWorkflowUuid;
    }

    // Layer 2: Orchestrator-Workers (内部改造,外部无感)
    String workflowUuid = orchestrateAndExecute(userInput, userId);
    if (StringUtils.isNotBlank(workflowUuid)) {
        return workflowUuid;
    }

    // Layer 3: 兜底策略 (保持不变)
    return getDefaultWorkflowUuid();
}
```

**新增核心方法**:
```java
/**
 * Orchestrator-Workers模式执行
 * 1. Orchestrator分解任务
 * 2. Workers并行执行(MCP + Workflow混合)
 * 3. 保存结果到对话历史
 * 4. 返回第一个workflow UUID(向后兼容)
 */
private String orchestrateAndExecute(String userInput, Long userId) {
    try {
        // Step 1: Orchestrator分解任务
        OrchestratorResponse response = orchestratorChatClient.prompt()
            .user(u -> u.text(orchestratorPrompt).param("task", userInput))
            .call()
            .entity(OrchestratorResponse.class);

        log.info("【Orchestrator分解】analysis: {}, tasks: {}",
            response.analysis(), response.tasks().size());

        // Step 2: Workers并行执行
        List<String> results = response.tasks().stream()
            .parallel()  // ← 并行执行
            .map(task -> executeWorker(task, userId))
            .toList();

        log.info("【Workers执行完成】总任务数: {}, 完成数: {}",
            response.tasks().size(), results.size());

        // Step 3: 保存结果到对话历史(供前端展示)
        saveOrchestratorResults(userId, response.analysis(), results);

        // Step 4: 返回第一个workflow UUID(向后兼容)
        // 如果没有workflow subtask,返回null触发Layer 3兜底
        return response.tasks().stream()
            .filter(t -> "workflow".equals(t.type()))
            .map(SubTask::target)
            .findFirst()
            .orElse(null);

    } catch (Exception e) {
        log.error("Orchestrator-Workers执行失败: userInput={}", userInput, e);
        return null;  // 失败返回null,触发Layer 3兜底
    }
}

/**
 * 执行单个Worker(workflow或MCP)
 */
private String executeWorker(SubTask task, Long userId) {
    try {
        if ("workflow".equals(task.type())) {
            // 执行workflow
            WorkflowToolCallback callback = workflowToolCallbackMap.get(task.target());
            if (callback == null) {
                return "{\"success\": false, \"error\": \"Workflow not found: " + task.target() + "\"}";
            }
            return callback.call(
                JSON.toJSONString(task.params()),
                createToolContext(userId)
            );

        } else if ("mcp".equals(task.type())) {
            // 执行MCP Tool
            ToolCallback mcpCallback = mcpToolCallbackMap.get(task.target());
            if (mcpCallback == null) {
                return "{\"success\": false, \"error\": \"MCP Tool not found: " + task.target() + "\"}";
            }
            return mcpCallback.call(
                JSON.toJSONString(task.params()),
                createToolContext(userId)
            );

        } else {
            return "{\"success\": false, \"error\": \"Unknown task type: " + task.type() + "\"}";
        }

    } catch (Exception e) {
        log.error("Worker执行失败: task={}", task, e);
        return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
    }
}

/**
 * 创建ToolContext(包含tenantCode, userId, conversationId, pageContext)
 */
private ToolContext createToolContext(Long userId) {
    Map<String, Object> context = new HashMap<>();
    context.put("tenantCode", DataSourceHelper.getCurrentDataSourceName());
    context.put("userId", userId);
    context.put("conversationId", /* 从当前会话获取 */);
    context.put("pageContext", /* 从请求中获取 */);
    return ToolContext.of(context);
}
```

#### 3. Orchestrator ChatClient配置

**在AiChatConfig中注册Bean**:
```java
/**
 * Orchestrator ChatClient - 用于任务分解
 */
@Bean
public ChatClient orchestratorChatClient(ChatModel chatModel) {
    return ChatClient.builder(chatModel)
        .defaultSystem("""
            你是一个智能任务分解专家。
            你的职责是:
            1. 分析用户的复杂任务
            2. 将任务分解为2-5个可并行执行的子任务
            3. 为每个子任务指定执行器类型(workflow或mcp)和目标工具
            4. 确定子任务的执行参数

            返回JSON格式:
            {
                "analysis": "你对任务的理解和分解策略",
                "tasks": [
                    {
                        "type": "workflow",
                        "target": "inventory_query_workflow_uuid",
                        "description": "查询当前库存",
                        "params": {"goods_code": "...", "warehouse_code": "..."}
                    },
                    {
                        "type": "mcp",
                        "target": "WarehouseMcpTools.getWarehouseInfo",
                        "description": "获取仓库信息",
                        "params": {"warehouse_code": "..."}
                    }
                ]
            }

            注意:
            - 只分解可以并行执行的子任务
            - 如果任务需要串行,仍然分解但标注依赖关系
            - 如果任务简单无需分解,返回单个子任务
            """)
        .build();
}

/**
 * 初始化WorkflowToolCallback映射表
 */
@Bean
public Map<String, WorkflowToolCallback> workflowToolCallbackMap(
        List<AiWorkflowEntity> workflows,
        WorkflowStarter workflowStarter) {

    Map<String, WorkflowToolCallback> map = new HashMap<>();

    for (AiWorkflowEntity workflow : workflows) {
        if (workflow.getIsEnable() == 1 && workflow.getIsDeleted() == 0) {
            WorkflowToolCallback callback = new WorkflowToolCallback(
                workflow.getWorkflowUuid(),
                workflow.getTitle(),
                workflow.getDesc(),
                workflow.getInputConfig(),  // 使用workflow的inputConfig作为schema
                workflowStarter
            );
            map.put(workflow.getWorkflowUuid(), callback);
        }
    }

    log.info("初始化WorkflowToolCallback映射表完成, 共{}个workflow", map.size());
    return map;
}

/**
 * 初始化MCP ToolCallback映射表
 */
@Bean
public Map<String, ToolCallback> mcpToolCallbackMap(ToolCallbackProvider mcpToolCallbackProvider) {
    Map<String, ToolCallback> map = new HashMap<>();

    for (ToolCallback callback : mcpToolCallbackProvider.getToolCallbacks()) {
        String toolName = callback.getToolDefinition().name();
        map.put(toolName, callback);
    }

    log.info("初始化MCP ToolCallback映射表完成, 共{}个MCP工具", map.size());
    return map;
}
```

---

## 🔍 关键技术决策

### 1. Worker的本质定义

**参考Spring AI源码** (`OrchestratorWorkers.java:189-195`):
```java
// Worker就是对ChatClient的直接调用,不是复杂的wrapper
List<String> workerResponses = orchestratorResponse.tasks().stream()
    .map(task -> this.chatClient.prompt()  // ← 这就是Worker!
        .user(u -> u.text(this.workerPrompt)
            .param("task_type", task.type())
            .param("task_description", task.description()))
        .call()
        .content())
    .toList();
```

**本项目应用**:
- MCP Tool已经是ToolCallback → **保持原样,不需要包装**
- Workflow需要被动态调用 → **创建WorkflowToolCallback包装**

### 2. 零破坏性设计

**关键决策**: `WorkflowRoutingService.route()` **方法签名不变**

**向后兼容策略**:
1. Layer 1 (用户指定workflow): 完全不动
2. Layer 2 (LLM路由): 内部改造为Orchestrator-Workers,返回值保持String
3. Layer 3 (兜底策略): 完全不动

**返回值兼容**:
```java
// 返回第一个workflow UUID,如果没有workflow subtask则返回null触发Layer 3
return response.tasks().stream()
    .filter(t -> "workflow".equals(t.type()))
    .map(SubTask::target)
    .findFirst()
    .orElse(null);
```

**受影响的3个调用点验证**:
- ✅ 调用点1/2 (新意图/新请求路由): Layer 2内部改造,外部无感
- ✅ 调用点3 (路由判断逻辑): 返回第一个workflow UUID保持兼容

### 3. 数据结构极简化

**对比分析**:

| 初始设计 (过度设计) | 修正设计 (极简) | 改进 |
|---|---|---|
| WorkflowToolCallbackFactory | WorkflowToolCallback | ❌ 删除工厂,直接创建 |
| WorkerExecutionResult | String | ❌ 删除复杂结构,直接用字符串 |
| OrchestratorService | orchestrateAndExecute() | ❌ 删除Service,直接在Routing中实现 |

**最终数据结构**: 3个record类 + 1个ToolCallback包装 = 4个核心概念

---

## ✅ 质量保证

### 复杂度审查

**Linus 2层缩进标准**:
```java
// ✅ 核心逻辑只有2层缩进
public OrchestratorFinalResponse processWithOrchestrator(...) {
    // Step 1: Orchestrator分解 (0层)
    OrchestratorResponse response = orchestratorChatClient.prompt()...;

    // Step 2: Workers并行执行 (1层)
    List<String> results = response.tasks().stream()
        .parallel()
        .map(task -> executeWorker(task))  // (2层在方法内)
        .toList();

    // Step 3: 返回结果 (0层)
    return new OrchestratorFinalResponse(...);
}
```

### 特殊情况消除

**已消除的假想特殊情况**:
- ❌ "只有1个subtask怎么办?" → `stream().parallel()`对1个也生效
- ❌ "workflow和MCP怎么统一?" → 不需要统一,混合在列表即可
- ❌ "调用方会破坏吗?" → 保持方法签名不变

**真正需要处理的特殊情况**:
- ✅ Workflow的Flux异步 → `blockLast()`阻塞等待
- ✅ Workflow的动态schema → 从`inputConfig`读取

### 破坏性分析

| 受影响组件 | 破坏风险 | 解决方案 | 结论 |
|---|---|---|---|
| `WorkflowRoutingService.route()` | 🟢 低 | 保持方法签名不变 | ✅ 零破坏 |
| 调用点1/2 (新意图/新请求路由) | 🟢 低 | Layer 2内部改造 | ✅ 零破坏 |
| 调用点3 (路由判断逻辑) | 🟢 低 | 返回第一个workflow UUID | ✅ 零破坏 |
| Layer 1 (用户指定workflow) | 🟢 无 | 完全不动 | ✅ 零破坏 |
| Layer 3 (兜底策略) | 🟢 无 | 完全不动 | ✅ 零破坏 |

**总体评估**: ✅ **实现零破坏性改造**

### 实用性验证

**问题严重性**: 🔴 高 (复杂任务无法实现,影响核心业务)
**解决方案复杂度**: 🟢 低 (~180行代码,无新抽象)
**匹配度评分**: ✅ **9/10**

**真实业务场景**:
1. 查询库存(Workflow) + 生成报表(Workflow) → 需要2个workflow
2. 权限查询(MCP) + 打开页面(MCP) + 日志(Workflow) → MCP+Workflow混合
3. 验证(MCP) + 处理(Workflow) + 通知(MCP) → 复杂链路

---

## 📊 实施计划

### 核心文件修改清单

| 文件 | 修改类型 | 代码量 | 说明 |
|---|---|---|---|
| `SubTask.java` | 新建 | 10行 | Record类,Orchestrator任务描述 |
| `OrchestratorResponse.java` | 新建 | 10行 | Record类,Orchestrator响应 |
| `OrchestratorFinalResponse.java` | 新建 | 10行 | Record类,最终响应 |
| `WorkflowToolCallback.java` | 新建 | 80行 | Workflow的ToolCallback包装 |
| `WorkflowRoutingService.java` | 修改 | +50行 | 新增orchestrateAndExecute方法 |
| `AiChatConfig.java` | 修改 | +30行 | 注册orchestratorChatClient Bean |
| **总计** | - | **~180行** | - |

### 实施步骤

#### Phase 1: 数据结构创建 (~10分钟)

**创建位置**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/workflow/orchestrator/`

1. `SubTask.java`
2. `OrchestratorResponse.java`
3. `OrchestratorFinalResponse.java`

#### Phase 2: WorkflowToolCallback实现 (~30分钟)

**创建位置**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/workflow/orchestrator/WorkflowToolCallback.java`

**关键实现点**:
- 实现`ToolCallback`接口
- 处理Flux异步阻塞(`blockLast()`)
- 动态构建inputSchema(从workflow的inputConfig读取)
- 错误处理和日志追踪

#### Phase 3: WorkflowRoutingService改造 (~40分钟)

**修改文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java`

**关键实现点**:
- 新增`orchestrateAndExecute()`方法
- 新增`executeWorker()`方法
- 新增`createToolContext()`方法
- 新增`saveOrchestratorResults()`方法
- 集成到`route()`的Layer 2逻辑
- 添加详细日志

#### Phase 4: ChatClient配置 (~20分钟)

**修改文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/AiChatConfig.java`

**关键实现点**:
- 注册`orchestratorChatClient` Bean
- 注册`workflowToolCallbackMap` Bean
- 注册`mcpToolCallbackMap` Bean
- 配置orchestrator system prompt

#### Phase 5: 测试验证 (~60分钟)

**单元测试**:
- `WorkflowToolCallbackTest`: 测试workflow包装和调用
- `WorkflowRoutingServiceTest`: 测试orchestrateAndExecute逻辑

**集成测试**:
- 测试完整的Orchestrator-Workers流程
- 测试MCP+Workflow混合调用
- 测试并行执行性能

**E2E测试**:
- 场景1: 查询库存 + 生成报表(2个workflow)
- 场景2: 权限查询(MCP) + 打开页面(MCP) + 日志(Workflow)
- 场景3: 简单任务回退到单个workflow(向后兼容验证)

### 工时估算

| 阶段 | 工时 | 说明 |
|---|---|---|
| Phase 1: 数据结构创建 | 0.2h | 3个简单record类 |
| Phase 2: WorkflowToolCallback实现 | 0.5h | 核心包装逻辑 |
| Phase 3: WorkflowRoutingService改造 | 0.7h | 集成到现有路由 |
| Phase 4: ChatClient配置 | 0.3h | Bean注册和配置 |
| Phase 5: 测试验证 | 1.0h | 单元测试+集成测试+E2E |
| **总计** | **2.7h** | **约3小时** |

---

## 📖 参考资料

### Spring AI官方文档
- **Agentic Patterns**: https://docs.spring.io/spring-ai/reference/concepts/agentic-patterns.html
- **Orchestrator-Workers Pattern**: `spring-ai-examples/agentic-patterns/orchestrator-workers`
- **ToolCallback Interface**: `spring-ai-main/spring-ai-model/src/main/java/org/springframework/ai/tool/ToolCallback.java`

### 本项目相关文档
- **Routing模式可行性调研**: `docs/design/2025-11-25-091428-AI路由改造为Orchestrator-Workers模式-可行性调研.md`
- **AI Chat与Workflow集成架构**: `docs/design/2025-11-11-AI-Chat与Workflow集成架构分析-最终版.md`

### 关键源码参考
- **OrchestratorWorkers实现**: `D:\2025_project\20_project_in_github\99_tools\spring-ai-examples-main\agentic-patterns\orchestrator-workers\src\main\java\com\example\agentic\OrchestratorWorkers.java`
- **当前Routing实现**: `scm-ai\src\main\java\com\xinyirun\scm\ai\core\service\workflow\WorkflowRoutingService.java`

---

## ✨ 总结

### 核心价值

1. **业务价值**: 解锁MCP+Workflow混合调用,支持复杂任务分解和并行执行
2. **技术价值**: 基于Spring AI标准模式,代码简洁优雅,易于维护
3. **兼容价值**: 零破坏性改造,现有功能完全不受影响

### 成功标准

- ✅ **功能完整**: 支持MCP+Workflow混合调用和并行执行
- ✅ **性能提升**: 并行执行相比串行提升50%以上
- ✅ **向后兼容**: Layer 1和Layer 3完全不变,现有调用无感知
- ✅ **代码质量**: 符合Linus 2层缩进标准,无特殊情况分支
- ✅ **测试覆盖**: 单元测试+集成测试+E2E测试全覆盖

### 风险评估

| 风险项 | 概率 | 影响 | 缓解措施 |
|---|---|---|---|
| Flux阻塞导致性能问题 | 🟡 中 | 🟡 中 | 使用timeout限制,超时返回错误 |
| Orchestrator分解不准确 | 🟡 中 | 🟡 中 | 优化prompt,增加样例训练 |
| 并行执行资源竞争 | 🟢 低 | 🟢 低 | 使用线程池控制并发度 |
| 向后兼容性破坏 | 🟢 极低 | 🔴 高 | 方法签名不变,充分测试 |

### 下一步行动

1. **评审确认**: 技术方案评审和用户需求确认
2. **环境准备**: 开发环境和测试数据准备
3. **分支创建**: `feature/orchestrator-workers-pattern`
4. **实施开发**: 按照5个Phase顺序实施
5. **测试验证**: 单元测试 → 集成测试 → E2E测试
6. **代码审查**: Code review和性能测试
7. **发布上线**: 灰度发布和监控

---

**文档状态**: ✅ 设计完成,待实施
**预期工时**: 3小时开发 + 1小时测试
**风险等级**: 🟢 低 (零破坏性 + 简洁实现)
**推荐度**: ⭐⭐⭐⭐⭐ (5/5星)
