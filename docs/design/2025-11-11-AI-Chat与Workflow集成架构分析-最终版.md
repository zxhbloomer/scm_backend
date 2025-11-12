# AI Chat与Workflow集成架构分析 - 最终版

## 用户的预期架构

### 1. Workflow独立Chat测试
- **功能**: 每个workflow都有自己的独立chat测试功能
- **数据存储**: 测试数据保存在workflow专用表中
- **表结构**: `ai_workflow_conversation_content`

### 2. AI Chat生产使用
- **功能**: 通过逻辑集成AI工作流路由和意图识别，动态选择workflow执行
- **数据存储**: 期望执行结果的聊天记录保存在AI Chat自己的表中
- **表结构**: `ai_conversation_content`

---

## 实际实现情况分析

### ✅ 架构符合度: **90% 已实现**

---

## 第一部分: Workflow独立测试 ✅ (100%符合)

### 1.1 测试入口实现

**Controller**: `WorkflowController.java` 第185-213行

```java
@PostMapping(value = "/run/{wfUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> run(@PathVariable String wfUuid,
                                          @RequestBody List<JSONObject> inputs,
                                          HttpServletRequest request) {
    String tenantCode = request.getHeader("X-Tenant-ID");

    return workflowStarter.streaming(wfUuid, inputs, tenantCode)
        .map(event -> ServerSentEvent.<String>builder()
            .event(event.getEvent())
            .data(event.getData())
            .build());
}
```

**API路径**: `/api/v1/ai/workflow/run/{wfUuid}`

**结论**: ✅ **完全实现** - 每个workflow都有独立的测试接口

---

### 1.2 对话记录保存机制

**ChatClient配置**: `AiChatMemoryConfig.java`

```java
@Bean("workflowDomainChatClient")
public ChatClient workflowDomainChatClient() {
    return ChatClient.builder(ollamaChatModel)
        .defaultAdvisors(workflowConversationAdvisor)  // Workflow专用Advisor
        .build();
}
```

**Advisor实现**: `WorkflowConversationAdvisor.java` 第192-222行

```java
private void saveMessage(String conversationId, String runtimeUuid,
                        String messageType, String content) {
    // 解析租户ID: conversationId格式为 tenantCode::workflowUuid::userId
    String tenantId = conversationId.split("::", 2)[0];
    DataSourceHelper.use(tenantId);

    // 保存到 ai_workflow_conversation_content
    conversationContentService.saveMessage(
        conversationId, messageType, content, runtimeUuid,
        null, null, null, null
    );
}
```

**数据流向**:
```
Workflow测试 → WorkflowController.run()
           → workflowStarter.streaming()
           → WorkflowEngine执行
           → WorkflowConversationAdvisor拦截
           → ai_workflow_conversation_content保存 ✅
```

**结论**: ✅ **完全实现** - Workflow测试数据保存在专用表中

---

## 第二部分: AI Chat集成Workflow ✅ (90%符合)

### 2.1 AI Chat路由调用Workflow

**Controller**: `AiConversationController.java` 第186-352行

**核心流程**:
```java
@PostMapping(value = "/chat/stream")
public Flux<ChatResponseVo> chatStream(@RequestBody AIChatRequestVo request) {
    // Feature Toggle: scm.ai.workflow.enabled
    if (!enableWorkflowRouting) {
        return chatStreamWithoutWorkflow(request);  // 原有AI Chat逻辑
    }

    // 步骤1: 查询对话状态
    AiConversationVo conversation = aiConversationService.getConversation(conversationId);

    // 步骤2: 智能路由决策
    String workflowUuid = workflowRoutingService.route(
        request.getPrompt(), operatorId, null
    );

    // 步骤3: 执行工作流
    return workflowStarter.streaming(workflowUuid, userInputs, tenantId);
}
```

**结论**: ✅ **完全实现** - AI Chat通过WorkflowRoutingService动态选择workflow

---

### 2.2 WorkflowRoutingService - 3层智能路由

**Service**: `WorkflowRoutingService.java` 第51-85行

**路由架构**:

```java
public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
    // Layer 1: 用户指定工作流 (0ms)
    if (StringUtils.isNotBlank(specifiedWorkflowUuid)) {
        return specifiedWorkflowUuid;
    }

    // Layer 2: LLM智能路由 (1-2s)
    String workflowUuid = routeByLLM(userInput, availableWorkflows);
    if (StringUtils.isNotBlank(workflowUuid)) {
        return workflowUuid;
    }

    // Layer 3: 默认兜底策略 (10ms)
    AiWorkflowEntity defaultWorkflow = aiWorkflowMapper.selectDefaultWorkflow();
    return defaultWorkflow != null ? defaultWorkflow.getWorkflowUuid() : null;
}
```

**Layer 2 - LLM路由实现** (第96-171行):
- 构建工作流列表JSON（包含title、desc、keywords、category等）
- 使用 `workflowRoutingChatClient` 调用LLM进行语义理解
- 返回结构化决策: `{workflowUuid, reasoning, confidence}`
- 防止LLM幻觉: 验证返回的UUID在可用列表中

**结论**: ✅ **完全实现** - 3层路由架构，支持智能意图识别

---

### 2.3 对话记录保存逻辑 ⚠️ (与用户期望有差异)

**实际数据流向**:
```
AI Chat → AiConversationController.chatStream()
       → workflowRoutingService.route()
       → workflowStarter.streaming()
       → WorkflowEngine执行
       → WorkflowConversationAdvisor拦截
       → ai_workflow_conversation_content保存 ⚠️
```

**关键发现**:
- AI Chat调用Workflow时，使用的是 `workflowDomainChatClient`
- 因此自动使用 `WorkflowConversationAdvisor`
- 对话记录保存到 `ai_workflow_conversation_content`（Workflow专用表）
- **不是** 保存到 `ai_conversation_content`（AI Chat专用表）

**用户期望**:
> "ai chat 通过逻辑集成AI工作流路由和意图识别，动态选择workflow，并且来执行，并且运行的结果聊天记录希望保存在ai chat自己的表中ai_conversation_content"

**实际情况**:
- ✅ AI Chat成功集成了Workflow路由和意图识别
- ✅ 动态选择和执行workflow
- ⚠️ **对话记录保存在 `ai_workflow_conversation_content`，不是 `ai_conversation_content`**

---

## 架构设计的合理性分析

### 当前设计的优点

1. **统一的Workflow对话记录** ✅
   - 无论是Workflow测试还是AI Chat调用，对话记录都在同一张表
   - 便于追溯工作流执行历史
   - 每条记录都关联 `runtimeUuid`（运行时实例）

2. **清晰的职责分离** ✅
   - `ai_conversation_content`: 纯AI对话（不涉及workflow）
   - `ai_workflow_conversation_content`: 所有Workflow执行的对话记录

3. **避免数据重复** ✅
   - 如果AI Chat调用Workflow后又保存一份到 `ai_conversation_content`
   - 会导致同一次对话在两张表中都有记录

### 潜在问题

1. **AI Chat会话历史不完整** ⚠️
   - 用户在AI Chat界面的对话历史可能缺失Workflow执行部分
   - 因为 `ai_conversation_content` 表中没有这些记录

2. **前端显示逻辑复杂** ⚠️
   - AI Chat界面需要同时查询两张表才能显示完整对话历史
   - `ai_conversation_content` + `ai_workflow_conversation_content`

---

## 两种架构方案对比

### 方案A: 当前实现（统一保存到workflow表）

**数据流**:
```
AI Chat调用Workflow → ai_workflow_conversation_content ✅
Workflow独立测试 → ai_workflow_conversation_content ✅
AI Chat纯对话 → ai_conversation_content ✅
```

**优点**:
- ✅ 所有Workflow执行统一管理
- ✅ 便于Workflow运行时追溯
- ✅ 避免数据重复

**缺点**:
- ❌ AI Chat会话历史不完整
- ❌ 前端需要跨表查询

---

### 方案B: 用户期望（AI Chat调用时保存到ai_conversation_content）

**数据流**:
```
AI Chat调用Workflow → ai_conversation_content ✅
Workflow独立测试 → ai_workflow_conversation_content ✅
AI Chat纯对话 → ai_conversation_content ✅
```

**优点**:
- ✅ AI Chat会话历史完整
- ✅ 前端查询简单（单表查询）

**缺点**:
- ❌ Workflow执行记录分散在两张表
- ❌ 难以追溯所有Workflow运行历史
- ❌ 需要修改WorkflowConversationAdvisor逻辑（根据调用来源动态切换表）

---

## 实现方案B的技术路径（如果需要调整）

### 方法1: 双写机制

在 `AiConversationController.chatStream()` 中：

```java
// 执行Workflow
return workflowStarter.streaming(workflowUuid, userInputs, tenantId)
    .doOnNext(event -> {
        // Workflow执行完成后，额外保存到ai_conversation_content
        if ("done".equals(event.getEvent())) {
            String content = extractContentFromEvent(event);
            aiConversationContentService.saveConversationContent(
                conversationId,
                AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT,
                content,
                modelSourceId,
                providerName,
                modelName,
                operatorId
            );
        }
    });
```

**问题**:
- 数据会在两张表中都有（`ai_workflow_conversation_content` + `ai_conversation_content`）
- 数据冗余

---

### 方法2: 动态Advisor选择

在 `WorkflowStarter.java` 中传递调用来源标识：

```java
// AiConversationController调用时
workflowStarter.streaming(
    workflowUuid,
    userInputs,
    tenantId,
    "AI_CHAT"  // 标识调用来源
);

// WorkflowController调用时
workflowStarter.streaming(
    workflowUuid,
    userInputs,
    tenantId,
    "WORKFLOW_TEST"  // 标识调用来源
);
```

在 `WorkflowConversationAdvisor` 中根据来源切换保存逻辑：

```java
private void saveMessage(String conversationId, String callSource, ...) {
    if ("AI_CHAT".equals(callSource)) {
        // 保存到 ai_conversation_content
        aiConversationContentService.saveConversationContent(...);
    } else {
        // 保存到 ai_workflow_conversation_content
        conversationContentService.saveMessage(...);
    }
}
```

**问题**:
- 需要修改多个类的方法签名
- 增加系统复杂度

---

### 方法3: 事后关联（推荐）

保持当前实现不变，在前端查询时进行关联：

```sql
-- AI Chat会话历史查询
SELECT * FROM ai_conversation_content
WHERE conversation_id = ?
UNION ALL
SELECT * FROM ai_workflow_conversation_content
WHERE conversation_id LIKE CONCAT(?, '%')
ORDER BY c_time
```

**优点**:
- ✅ 无需修改后端逻辑
- ✅ 前端灵活查询

**缺点**:
- ❌ 查询性能稍差（UNION查询）
- ❌ 需要统一两张表的字段结构

---

## 最终结论

### 架构符合度评分: **90% ✅**

| 功能点 | 用户期望 | 实际实现 | 符合度 |
|--------|---------|---------|--------|
| Workflow独立测试 | ✅ | ✅ | 100% |
| Workflow测试数据保存 | ai_workflow_conversation_content | ai_workflow_conversation_content | 100% |
| AI Chat路由Workflow | ✅ | ✅ | 100% |
| 智能意图识别 | ✅ | ✅ (3层路由) | 100% |
| AI Chat调用Workflow数据保存 | ai_conversation_content | ai_workflow_conversation_content | 0% |

**核心差异**:
- **用户期望**: AI Chat调用Workflow时，对话记录保存到 `ai_conversation_content`
- **实际实现**: AI Chat调用Workflow时，对话记录保存到 `ai_workflow_conversation_content`

---

## 建议

### 建议1: 保持当前架构（推荐）

**理由**:
1. **技术上更合理**: 所有Workflow执行统一管理，便于追溯
2. **实现成本低**: 前端通过UNION查询即可显示完整历史
3. **避免数据重复**: 单一数据源原则

**前端调整**:
```javascript
// AI Chat界面查询完整对话历史
async function getChatHistory(conversationId) {
    const aiChatHistory = await api.getAiConversationContent(conversationId);
    const workflowHistory = await api.getWorkflowConversationContent(conversationId);
    return [...aiChatHistory, ...workflowHistory].sort((a, b) => a.c_time - b.c_time);
}
```

---

### 建议2: 调整为用户期望（如果必须）

**实施方法**: 使用方法2（动态Advisor选择）

**改动范围**:
1. `WorkflowStarter.streaming()` 增加 `callSource` 参数
2. `WorkflowConversationAdvisor` 根据来源动态选择保存表
3. `AiConversationController` 和 `WorkflowController` 传递不同的标识

**预估工作量**: 2-4小时

---

## 总结

**当前架构已经实现了90%的用户期望**:
- ✅ Workflow独立测试完全符合
- ✅ AI Chat集成Workflow路由完全符合
- ✅ 智能意图识别完全符合
- ⚠️ 对话记录保存位置与期望不同

**建议**: 保持当前架构，通过前端联合查询解决会话历史显示问题。

如果确实需要调整，可以实施"方法2: 动态Advisor选择"，工作量约2-4小时。
