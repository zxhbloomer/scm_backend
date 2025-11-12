# AI Chat与Workflow集成架构分析

## 用户的预期架构

### 1. Workflow独立Chat测试
- **功能**：每个workflow都有自己的独立chat测试功能
- **数据存储**：测试数据保存在workflow专用表中
- **表结构**：
  - `ai_workflow` - 工作流定义
  - `ai_workflow_component` - 组件库
  - `ai_workflow_conversation_content` - **Workflow专用对话记录**
  - `ai_workflow_edge` - 连线定义
  - `ai_workflow_node` - 节点定义
  - `ai_workflow_runtime` - 运行时实例
  - `ai_workflow_runtime_node` - 运行时节点执行

### 2. AI Chat生产使用
- **功能**：通过逻辑集成AI工作流路由和意图识别，动态选择workflow执行
- **数据存储**：执行结果的聊天记录保存在AI Chat自己的表中
- **表结构**：
  - `ai_conversation` - AI对话会话
  - `ai_conversation_content` - **AI Chat专用对话记录**
  - `ai_conversation_content_ref_embedding` - 向量引用
  - `ai_conversation_content_ref_graph` - 图谱引用
  - `ai_conversation_preset` - 预设会话（角色）
  - `ai_conversation_preset_rel` - 预设关系

### 3. 关键分离点
```
【Workflow测试场景】
用户 → Workflow Chat测试接口 → WorkflowEngine执行
     → 保存到 ai_workflow_conversation_content

【AI Chat生产场景】
用户 → AI Chat接口 → WorkflowRoutingService路由决策
     → WorkflowEngine执行 → 保存到 ai_conversation_content
```

---

## 当前实现逻辑分析

### ✅ 符合预期的部分

#### 1. **Workflow独立对话记录存储** ✅
**代码位置**: `WorkflowConversationAdvisor.java`

**实现机制**:
```java
// WorkflowConversationAdvisor 保存到 ai_workflow_conversation_content
private void saveMessage(String conversationId, String runtimeUuid,
                        String messageType, String content) {
    conversationContentService.saveMessage(
        conversationId, messageType, content, runtimeUuid,
        null, null, null, null
    );
}
```

**数据流向**:
- `WorkflowConversationAdvisor` → `AiWorkflowConversationContentService.saveMessage()`
- 保存到表: `ai_workflow_conversation_content`

**结论**: ✅ **完全符合** - Workflow执行时的对话记录保存在专用表中

---

#### 2. **AI Chat独立对话记录存储** ✅
**代码位置**: `AiConversationContentService.java`

**实现机制**:
```java
// AiConversationContentService 保存到 ai_conversation_content
public AiConversationContentVo saveConversationContent(
    String conversationId, String type, String content,
    String modelSourceId, String providerName, String baseName, Long operatorId) {

    AiConversationContentEntity entity = new AiConversationContentEntity();
    // ... 设置字段
    aiConversationContentMapper.insert(entity);
}
```

**数据流向**:
- `AiConversationContentService` → `AiConversationContentMapper.insert()`
- 保存到表: `ai_conversation_content`

**结论**: ✅ **完全符合** - AI Chat有独立的对话记录服务和存储

---

#### 3. **两套独立的ChatClient和Advisor** ✅
**代码位置**: `AiChatMemoryConfig.java`

**实现架构**:
```java
// AI Chat领域 - 使用MessageChatMemoryAdvisor
@Bean
@ConditionalOnProperty(name = "spring.ai.ollama.chat.enabled", havingValue = "true")
public ChatClient.Builder ollamaChatClientBuilder() {
    return ChatClient.builder(ollamaChatModel)
        .defaultAdvisors(messageChatMemoryAdvisor);  // AI Chat专用
}

// Workflow领域 - 使用WorkflowConversationAdvisor
@Bean("workflowDomainChatClient")
public ChatClient workflowDomainChatClient() {
    return ChatClient.builder(ollamaChatModel)
        .defaultAdvisors(workflowConversationAdvisor)  // Workflow专用
        .build();
}
```

**结论**: ✅ **完全符合** - 两个领域有独立的ChatClient和对话记录Advisor

---

### ❓ 需要确认的部分

#### 1. **AI Chat如何调用Workflow** ❓
**预期流程**:
```
AI Chat → WorkflowRoutingService路由决策 → 选择Workflow → WorkflowEngine执行
```

**当前缺失信息**:
- ❓ AI Chat Controller中是否有调用WorkflowRoutingService的入口？
- ❓ WorkflowRoutingService的路由决策逻辑在哪里？
- ❓ AI Chat调用Workflow后，对话记录保存到哪张表？

**需要查找**:
1. AI Chat的Controller代码
2. WorkflowRoutingService的完整实现
3. AI Chat调用Workflow的集成点

---

#### 2. **Workflow测试入口在哪里** ❓
**预期功能**:
- 每个workflow都可以进行独立的chat测试
- 测试数据保存在`ai_workflow_conversation_content`

**当前缺失信息**:
- ❓ Workflow测试的Controller入口在哪里？
- ❓ 前端如何调用Workflow测试接口？
- ❓ 测试模式和生产模式如何区分？

---

#### 3. **对话记录保存的完整逻辑** ❓

**场景1: Workflow独立测试**
```
用户 → Workflow测试接口 → WorkflowEngine → WorkflowConversationAdvisor
     → ai_workflow_conversation_content ✅
```
**状态**: ✅ 已实现

**场景2: AI Chat调用Workflow**
```
用户 → AI Chat接口 → WorkflowEngine → ???
     → ai_conversation_content ❓
```
**关键问题**:
- ❓ AI Chat调用Workflow时，使用哪个Advisor？
- ❓ 如果使用WorkflowConversationAdvisor，数据会保存到`ai_workflow_conversation_content`
- ❓ 如果要保存到`ai_conversation_content`，需要使用不同的机制

---

## 架构符合度总结

### ✅ 已完全符合的部分 (60%)

1. **Workflow专用对话记录表** ✅
   - 表: `ai_workflow_conversation_content`
   - 服务: `AiWorkflowConversationContentService`
   - Advisor: `WorkflowConversationAdvisor`

2. **AI Chat专用对话记录表** ✅
   - 表: `ai_conversation_content`
   - 服务: `AiConversationContentService`
   - Advisor: `MessageChatMemoryAdvisor`

3. **两套独立的ChatClient** ✅
   - Workflow领域: `workflowDomainChatClient`
   - AI Chat领域: 默认的`ChatClient.Builder`

### ❓ 需要补充确认的部分 (40%)

1. **AI Chat如何调用Workflow** ❓
   - WorkflowRoutingService的实现
   - AI Chat Controller的集成点
   - 路由决策逻辑

2. **Workflow测试入口** ❓
   - Workflow测试Controller
   - 测试模式和生产模式的区分

3. **AI Chat调用Workflow时的对话记录保存** ❓
   - 使用哪个Advisor？
   - 保存到哪张表？
   - 如何区分测试场景和生产场景？

---

## 关键技术问题

### 问题1: AI Chat调用Workflow时的对话记录归属

**现状**:
- Workflow执行时使用`WorkflowConversationAdvisor`
- 数据自动保存到`ai_workflow_conversation_content`

**用户期望**:
- AI Chat调用Workflow时，数据应该保存到`ai_conversation_content`

**潜在解决方案**:
1. **方案A**: 在AI Chat调用Workflow时，传递一个标志，告诉Workflow使用不同的保存逻辑
2. **方案B**: AI Chat调用Workflow后，手动将结果保存到`ai_conversation_content`
3. **方案C**: 使用不同的ChatClient，一个用于测试（保存到workflow表），一个用于生产（保存到ai_chat表）

**需要明确**:
- ❓ 当前是否已经实现了AI Chat调用Workflow的功能？
- ❓ 如果已实现，对话记录现在保存在哪张表？

---

## 下一步行动建议

### 1. 查找AI Chat调用Workflow的代码
```bash
# 查找AI Chat的Controller
grep -r "class.*AiChatController" scm-ai/src/main/java/

# 查找WorkflowRoutingService的实现
grep -r "WorkflowRoutingService" scm-ai/src/main/java/

# 查找路由决策的逻辑
grep -r "路由" scm-ai/src/main/java/
```

### 2. 查找Workflow测试接口
```bash
# 查找Workflow的Controller
grep -r "class.*WorkflowController" scm-ai/src/main/java/

# 查找测试相关的接口
grep -r "@PostMapping.*test" scm-ai/src/main/java/
```

### 3. 验证对话记录保存逻辑
```sql
-- 查看两张表的记录，确认数据流向
SELECT 'ai_conversation_content' as table_name, COUNT(*) as count
FROM ai_conversation_content
UNION ALL
SELECT 'ai_workflow_conversation_content' as table_name, COUNT(*) as count
FROM ai_workflow_conversation_content;
```

---

## 结论

**符合度评分**: ✅ **60%已实现 + ❓ 40%待确认**

**已完全符合的核心架构**:
1. ✅ 两套独立的对话记录表（workflow表 vs ai_chat表）
2. ✅ 两套独立的对话记录服务和Advisor
3. ✅ Workflow执行时的对话记录保存机制

**需要进一步确认的部分**:
1. ❓ AI Chat如何集成和调用Workflow
2. ❓ Workflow测试入口和生产入口的分离
3. ❓ AI Chat调用Workflow时的对话记录归属问题

**建议**: 需要查看AI Chat Controller和WorkflowRoutingService的代码，才能完整评估架构的符合度。
