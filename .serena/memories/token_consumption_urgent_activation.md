# Token消耗功能紧急激活方案

## 核心问题
- Token录制基础设施完整但被禁用（代码注释）
- 事件驱动架构已实现但未连接到数据库持久化
- 数据库中只有测试数据（OpenAI/Claude），实际应记录DeepSeek使用情况

## 紧急激活步骤

### 1. 取消注释关键代码
**文件1：TokenUsageEventListener.java**
```java
// 当前状态：注释掉
// aiTokenUsageMapper.insertSelective(tokenUsage);

// 激活状态：取消注释
aiTokenUsageMapper.insertSelective(tokenUsage);
```

**文件2：AiTokenUsageService.java**
```java
// 当前状态：注释掉  
// aiTokenUsageMapper.insertSelective(tokenUsage);

// 激活状态：取消注释
aiTokenUsageMapper.insertSelective(tokenUsage);
```

### 2. Spring AI集成模式
基于Spring AI文档的标准模式：

```java
// 在DeepSeek调用后获取Usage信息
ChatResponse response = chatClient.call(prompt);
Usage usage = response.getResult().getMetadata().getUsage();

// 发布事件
LlmTokenUsageEvent event = new LlmTokenUsageEvent(
    this,
    conversationId,
    modelSourceId, 
    userId,
    tenant,
    "deepseek", // aiProvider
    modelType,
    usage.getPromptTokens(),
    usage.getCompletionTokens(),
    true,
    responseTime,
    tokenUnitPrice,
    calculateCost(usage.getTotalTokens(), tokenUnitPrice),
    System.currentTimeMillis()
);

applicationEventPublisher.publishEvent(event);
```

### 3. 验证步骤
1. 重启应用
2. 执行一次DeepSeek对话
3. 检查`ai_token_usage`表中是否有新的DeepSeek记录
4. 检查`ai_user_quota`表中的使用量更新

## 技术要点
- 使用Spring AI的Usage接口标准模式
- 事件驱动异步处理（@EventListener + @Async）
- MyBatis持久化层已就绪
- 用户配额管理存储过程已实现