# 系统Token计算机制调研报告

## 一、核心组件

### 1.1 核心服务类

#### `AiTokenUsageService`
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiTokenUsageService.java`

**职责**:
- AI Token使用记录管理
- 异步记录Token消耗
- 支持Token统计和查询

**核心方法**:
```java
public void recordTokenUsageAsync(
    String conversationId,           // 对话ID
    String conversationContentId,    // ASSISTANT消息ID(关联ai_conversation_content)
    String modelSourceId,            // 模型源ID
    String userId,                   // 用户ID
    String aiProvider,               // AI提供商(如OpenAI, Claude等)
    String aiModelType,              // 模型类型(如gpt-4, claude-3等)
    Long promptTokens,               // 输入Token数
    Long completionTokens,           // 输出Token数
    Boolean success,                 // 是否成功
    Long responseTime                // 响应时间(毫秒)
)
```

**特点**:
- `@Transactional(rollbackFor = Exception.class)` - 事务保护
- 异常不抛出，仅记录日志，避免影响主业务
- 自动生成UUID作为主键
- `total_tokens`由数据库自动计算（`prompt_tokens + completion_tokens`）

### 1.2 数据实体

#### `AiTokenUsageEntity`
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/statistics/AiTokenUsageEntity.java`

**字段说明**:
```java
@TableName("ai_token_usage")
public class AiTokenUsageEntity {
    private String id;                          // 主键UUID
    private String conversationId;              // 对话ID
    private String userId;                      // 用户ID
    private String modelSourceId;               // 模型源ID
    private String conversationContentId;       // 消息ID(关联ASSISTANT消息)
    private String providerName;                // AI提供商
    private String modelType;                   // 模型类型
    private Long promptTokens;                  // 输入Token
    private Long completionTokens;              // 输出Token

    // 由数据库自动计算，不手动设置
    @TableField(insertStrategy = FieldStrategy.NEVER,
                updateStrategy = FieldStrategy.NEVER)
    private Long totalTokens;                   // 总Token(自动计算列)

    private LocalDateTime usageTime;            // 使用时间
    private BigDecimal tokenUnitPrice;          // Token单价
    private BigDecimal cost;                    // 费用
    private Boolean success;                    // 是否成功
    private Long responseTime;                  // 响应时间(毫秒)
    private String aiConfigId;                  // AI配置ID(可选)
}
```

### 1.3 适配器类

#### `AiEngineAdapter.AiResponse`
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/adapter/AiEngineAdapter.java`

**职责**: 统一不同AI提供商的响应格式

**Token相关字段**:
```java
public static class AiResponse {
    private Usage usage;                // Spring AI Usage接口
    private Long promptTokens;          // 输入Token
    private Long completionTokens;      // 输出Token
    private Long totalTokens;           // 总Token

    // 从Spring AI Usage设置Token信息
    public void setUsageFromSpringAi(Usage usage) {
        if (usage != null) {
            this.usage = usage;
            this.promptTokens = usage.getPromptTokens().longValue();
            this.completionTokens = usage.getCompletionTokens().longValue();
            this.totalTokens = usage.getTotalTokens().longValue();
        }
    }
}
```

## 二、数据库表结构

### 2.1 `ai_token_usage` 表
**用途**: 记录每次AI调用的Token消耗

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | varchar(50) | 主键UUID |
| conversation_id | varchar(100) | 对话ID |
| user_id | varchar(50) | 用户ID |
| model_source_id | varchar(50) | 模型源ID |
| conversation_content_id | varchar(50) | 消息ID(关联ASSISTANT消息) |
| provider_name | varchar(255) | AI提供商 |
| model_type | varchar(255) | 模型类型 |
| **prompt_tokens** | bigint | **输入Token数** |
| **completion_tokens** | bigint | **输出Token数** |
| **total_tokens** | bigint | **总Token(计算列)** |
| usage_time | datetime | 使用时间 |
| token_unit_price | decimal(12,8) | Token单价 |
| cost | decimal(10,4) | 费用 |
| success | tinyint(1) | 是否成功 |
| response_time | bigint | 响应时间(毫秒) |
| ai_config_id | varchar(50) | AI配置ID |

**索引**:
- `idx_ai_token_usage_conversation` - 按对话ID查询
- `idx_ai_token_usage_provider` - 按提供商+时间查询
- `idx_ai_token_usage_user_time` - 按用户+时间查询
- `idx_conversation_content` - 按消息ID查询

### 2.2 `ai_conversation_content` 表
**用途**: 存储对话消息内容

**Token相关扩展字段**:
| 字段名 | 类型 | 说明 | 当前是否存在 |
|--------|------|------|------------|
| message_id | varchar(50) | 消息主键ID | ✅ 已存在 |
| conversation_id | varchar(100) | 对话ID | ✅ 已存在 |
| runtime_uuid | varchar(32) | 运行时UUID(关联workflow) | ✅ 已存在 |
| provider_name | varchar(255) | AI提供商 | ✅ 已存在 |
| model_source_id | varchar(50) | 模型源ID | ✅ 已存在 |
| base_name | varchar(255) | 基础模型名称 | ✅ 已存在 |
| **prompt_tokens** | bigint | **输入Token** | ❌ **需要新增** |
| **completion_tokens** | bigint | **输出Token** | ❌ **需要新增** |
| **total_tokens** | bigint | **总Token** | ❌ **需要新增** |

### 2.3 `ai_conversation_workflow_runtime_node` 表
**用途**: 记录workflow节点执行详情

**Token相关扩展字段**:
| 字段名 | 类型 | 说明 | 当前是否存在 |
|--------|------|------|------------|
| runtime_node_uuid | varchar(100) | 节点UUID | ✅ 已存在 |
| node_id | bigint | 节点ID | ✅ 已存在 |
| input_data | json | 输入数据 | ✅ 已存在 |
| output_data | json | 输出数据 | ✅ 已存在 |
| status | tinyint | 执行状态 | ✅ 已存在 |
| **prompt_tokens** | bigint | **输入Token** | ❌ **需要新增** |
| **completion_tokens** | bigint | **输出Token** | ❌ **需要新增** |
| **total_tokens** | bigint | **总Token** | ❌ **需要新增** |

## 三、现有Token记录调用链

### 3.1 Chat领域 - 流式聊天
**入口**: `AiConversationController.chatStream()`

**流程**:
1. 用户发起聊天请求
2. LLM流式返回响应
3. 从`ChatResponse`中获取`Usage`信息
4. 调用`AiConversationService.recordTokenUsageFromSpringAI()`
5. 内部调用`AiTokenUsageService.recordTokenUsageAsync()`
6. 保存到`ai_token_usage`表

**代码位置**:
```java
// AiConversationService.java:92-113
.doOnNext(chatResponse -> {
    // 获取内容片段
    String content = chatResponse.getResult().getOutput().getText();
    streamHandler.onContent(content);
    completeContent.append(content);

    // 保存最后一个响应的Usage信息
    if (chatResponse.getMetadata() != null &&
        chatResponse.getMetadata().getUsage() != null) {
        finalUsage[0] = chatResponse.getMetadata().getUsage();
    }
})
.doOnComplete(() -> {
    // 流式完成后记录Token
    if (finalUsage[0] != null) {
        finalResponse.setUsageFromSpringAi(finalUsage[0]);
    }
    streamHandler.onComplete(finalResponse);
})
```

**调用示例**:
```java
// AiConversationService.java:304-329
public void recordTokenUsageFromSpringAI(
    String conversationId,
    String conversationContentId,  // ASSISTANT消息ID
    String userId,
    String aiProvider,
    String modelSourceId,
    String modelType,
    Long promptTokens,
    Long completionTokens
) {
    aiTokenUsageService.recordTokenUsageAsync(
        conversationId,
        conversationContentId,  // 关联ASSISTANT消息
        modelSourceId,
        userId,
        aiProvider,
        modelType,
        promptTokens,
        completionTokens,
        true,  // success
        0L     // responseTime
    );
}
```

### 3.2 Workflow领域 - 工作流执行
**当前状态**: ❌ **未实现Token记录**

**潜在位置**:
- `WorkflowStarter.streaming()` - workflow执行入口
- `AbstractWfNode.execute()` - 各节点执行时
- LLM节点需要记录Token消耗

## 四、Orchestrator-Workers模式Token计算需求

### 4.1 涉及的调用类型

#### 1) Orchestrator LLM调用
**位置**: `WorkflowRoutingService.orchestrateAndExecute()` 第313-318行
```java
OrchestratorResponse orchestratorResponse = orchestratorChatClient.prompt()
    .user(orchestratorPrompt)
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
    .call()
    .entity(OrchestratorResponse.class);
```
**需记录**: Orchestrator任务分解的Token消耗

#### 2) Worker执行 - Workflow调用
**位置**: `WorkflowRoutingService.executeWorker()` 第586-614行
```java
if ("workflow".equals(task.type())) {
    // 执行workflow
    ToolCallback callback = workflowCallbacks.stream()
        .filter(cb -> cb.getToolDefinition().name().equals(workflowName))
        .findFirst()
        .orElseThrow();

    // 调用workflow并获取结果
    String result = callback.call(params, toolContext);
}
```
**需记录**: 每个workflow内部LLM节点的Token消耗

#### 3) Worker执行 - MCP工具调用
**位置**: `WorkflowRoutingService.executeWorker()` 第615-631行
```java
else if ("mcp".equals(task.type())) {
    // 执行MCP工具
    ToolCallback mcpCallback = mcpToolCallbackMap.get(task.target());
    String result = mcpCallback.call(params, toolContext);
}
```
**需记录**: MCP工具如果调用LLM的Token消耗

#### 4) Synthesizer LLM调用
**位置**: `WorkflowRoutingService.convertOrchestratorResponseToChatResponseStream()` 第862-916行
```java
chatDomainChatClient.prompt()
    .user(synthesizerPrompt.toString())
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
    .stream()
    .chatResponse()
```
**需记录**: Synthesizer合成最终回复的Token消耗

### 4.2 记录策略建议

#### 策略A: 细粒度记录（推荐）
**优点**:
- 清晰追踪每个LLM调用
- 便于成本分析和优化
- 支持节点级别统计

**记录点**:
1. Orchestrator调用 → 记录到`ai_token_usage`
2. 每个Worker的workflow节点 → 记录到`ai_conversation_workflow_runtime_node`
3. 每个Worker的MCP工具(如有LLM调用) → 记录到`ai_conversation_workflow_runtime_node`
4. Synthesizer调用 → 记录到`ai_conversation_content`(ASSISTANT消息)

#### 策略B: 汇总记录
**优点**:
- 记录简单，性能好

**缺点**:
- 无法追踪细节
- 难以优化

## 五、实现方案建议

### 5.1 数据库表扩展

#### 扩展 `ai_conversation_content` 表
```sql
ALTER TABLE ai_conversation_content
ADD COLUMN prompt_tokens BIGINT DEFAULT 0 COMMENT '输入Token数',
ADD COLUMN completion_tokens BIGINT DEFAULT 0 COMMENT '输出Token数',
ADD COLUMN total_tokens BIGINT GENERATED ALWAYS AS (prompt_tokens + completion_tokens) STORED COMMENT '总Token数(计算列)';

-- 添加索引优化查询
CREATE INDEX idx_ai_conversation_content_tokens ON ai_conversation_content(total_tokens);
```

#### 扩展 `ai_conversation_workflow_runtime_node` 表
```sql
ALTER TABLE ai_conversation_workflow_runtime_node
ADD COLUMN prompt_tokens BIGINT DEFAULT 0 COMMENT '输入Token数',
ADD COLUMN completion_tokens BIGINT DEFAULT 0 COMMENT '输出Token数',
ADD COLUMN total_tokens BIGINT GENERATED ALWAYS AS (prompt_tokens + completion_tokens) STORED COMMENT '总Token数(计算列)',
ADD COLUMN model_source_id VARCHAR(50) COMMENT '模型源ID',
ADD COLUMN provider_name VARCHAR(255) COMMENT 'AI提供商',
ADD COLUMN base_name VARCHAR(255) COMMENT '基础模型名称';

-- 添加索引
CREATE INDEX idx_ai_conversation_workflow_runtime_node_tokens ON ai_conversation_workflow_runtime_node(total_tokens);
```

### 5.2 代码实现步骤

#### 步骤1: 增强实体类
```java
// AiConversationContentEntity.java
@TableField("prompt_tokens")
private Long promptTokens;

@TableField("completion_tokens")
private Long completionTokens;

@TableField(value = "total_tokens", insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NEVER)
private Long totalTokens;  // 数据库自动计算

// AiConversationWorkflowRuntimeNodeEntity.java
@TableField("prompt_tokens")
private Long promptTokens;

@TableField("completion_tokens")
private Long completionTokens;

@TableField(value = "total_tokens", insertStrategy = FieldStrategy.NEVER,
            updateStrategy = FieldStrategy.NEVER)
private Long totalTokens;  // 数据库自动计算

@TableField("model_source_id")
private String modelSourceId;

@TableField("provider_name")
private String providerName;

@TableField("base_name")
private String baseName;
```

#### 步骤2: 修改`WorkflowRoutingService.orchestrateAndExecute()`
```java
// 在第318行后，获取Orchestrator的Usage
OrchestratorResponse orchestratorResponse = orchestratorChatClient.prompt()
    .user(orchestratorPrompt)
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
    .call()
    .entity(OrchestratorResponse.class);

// 【新增】记录Orchestrator的Token消耗
// 注意：需要从ChatResponse中获取Usage，而不是从entity()结果
// 建议修改为先获取ChatResponse，再提取entity和usage
```

#### 步骤3: 修改`WorkflowRoutingService.updateWorkerNodeRecord()`
```java
// 在第563行后，添加Token记录参数
private void updateWorkerNodeRecord(Long nodeRecordId, String result,
                                     boolean success, String errorMsg,
                                     String tenantCode,
                                     // 【新增】Token参数
                                     Long promptTokens,
                                     Long completionTokens,
                                     String modelSourceId,
                                     String providerName,
                                     String baseName) {
    // ...
    nodeRecord.setPromptTokens(promptTokens);
    nodeRecord.setCompletionTokens(completionTokens);
    nodeRecord.setModelSourceId(modelSourceId);
    nodeRecord.setProviderName(providerName);
    nodeRecord.setBaseName(baseName);

    conversationWorkflowRuntimeNodeMapper.updateById(nodeRecord);
}
```

#### 步骤4: 修改`WorkflowRoutingService.executeWorker()`
```java
// 需要从ToolCallback调用结果中提取Token信息
// 这可能需要修改ToolCallback接口或WorkflowToolCallback实现
// 使其能够返回Usage信息

// 方案A: 修改返回结果为复合对象
public class WorkerExecutionResult {
    private String result;           // 执行结果
    private Long promptTokens;       // 输入Token
    private Long completionTokens;   // 输出Token
    private String modelInfo;        // 模型信息
}

// 方案B: 通过ThreadLocal传递Usage信息
// 方案C: 修改ToolCallback接口返回值包含metadata
```

#### 步骤5: 修改`WorkflowRoutingService.convertOrchestratorResponseToChatResponseStream()`
```java
// 在第862-916行的Synthesizer调用处
// 从stream().chatResponse()中获取Usage信息
// 在doOnComplete中记录到ai_conversation_content的token字段
.doOnComplete(() -> {
    log.info("【Synthesizer】LLM流式回复完成, 最终内容长度={}",
             contentAccumulator[0].length());

    // 【新增】记录Synthesizer的Token到ai_conversation_content
    if (finalUsage[0] != null) {
        // 更新ASSISTANT消息的token字段
        aiConversationContentService.updateTokenUsage(
            messageId,
            finalUsage[0].getPromptTokens(),
            finalUsage[0].getCompletionTokens()
        );
    }
})
```

### 5.3 新增Service方法

#### `AiConversationContentService`
```java
/**
 * 更新消息的Token使用情况
 * @param messageId 消息ID
 * @param promptTokens 输入Token
 * @param completionTokens 输出Token
 */
public void updateTokenUsage(String messageId, Long promptTokens, Long completionTokens) {
    AiConversationContentEntity entity = mapper.selectByMessageId(messageId);
    if (entity != null) {
        entity.setPromptTokens(promptTokens);
        entity.setCompletionTokens(completionTokens);
        // total_tokens由数据库自动计算
        mapper.updateById(entity);
    }
}
```

## 六、关键问题与解决方案

### 问题1: `ToolCallback`接口不返回Usage信息
**现状**:
- `ToolCallback.call()`返回`String`
- 无法直接获取Token消耗

**解决方案**:
1. **方案A**: 修改`WorkflowToolCallback`，使用`ThreadLocal`传递Usage
2. **方案B**: 修改返回值为JSON，包含result和usage
3. **方案C**: 在`WorkflowStarter.streaming()`中记录Token，通过事件传递

**推荐**: 方案C - 在workflow执行引擎中记录Token

### 问题2: MCP工具调用的Token获取
**现状**:
- MCP工具通过`mcpToolCallbackMap`调用
- 无法直接获取Token信息

**解决方案**:
- MCP工具如果有LLM调用，应在工具内部记录Token
- 返回结果中包含Token信息
- 或在`output_data`中添加Token字段

### 问题3: Orchestrator和Synthesizer的Usage获取
**现状**:
- 使用`.call().entity()` 和 `.stream().chatResponse()`
- 无法同时获取entity和usage

**解决方案**:
```java
// Orchestrator
ChatResponse chatResponse = orchestratorChatClient.prompt()
    .user(orchestratorPrompt)
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
    .call()
    .chatResponse();  // 先获取ChatResponse

// 提取entity
OrchestratorResponse orchestratorResponse =
    chatResponse.getResult().getOutput().getContent();  // 需要JSON反序列化

// 提取usage
Usage usage = chatResponse.getMetadata().getUsage();
Long promptTokens = usage.getPromptTokens();
Long completionTokens = usage.getCompletionTokens();
```

## 七、总结

### 现有能力
✅ `ai_token_usage`表完整支持Token记录
✅ `AiTokenUsageService`提供异步记录能力
✅ Chat领域流式聊天已实现Token记录
✅ Spring AI `Usage`接口统一Token获取

### 需要增强
❌ `ai_conversation_content`表缺少Token字段
❌ `ai_conversation_workflow_runtime_node`表缺少Token字段
❌ Workflow执行引擎未记录Token
❌ Orchestrator-Workers模式未记录Token
❌ MCP工具调用未记录Token

### 实施优先级
1. **P0**: 扩展数据库表结构（`ai_conversation_content`, `ai_conversation_workflow_runtime_node`）
2. **P0**: 修改Orchestrator和Synthesizer调用，记录Token到`ai_conversation_content`
3. **P1**: 修改Worker执行，记录Token到`ai_conversation_workflow_runtime_node`
4. **P1**: 增强`WorkflowToolCallback`，传递workflow内部Token信息
5. **P2**: MCP工具Token记录机制设计和实现

### 技术难点
1. `ToolCallback`接口限制，无法直接返回Usage
2. Workflow执行引擎与Token记录的解耦
3. 多层LLM调用的Token汇总策略
4. MCP工具LLM调用的Token获取

---

**编写时间**: 2025-11-26
**编写者**: 系统调研agent
**版本**: v1.0
