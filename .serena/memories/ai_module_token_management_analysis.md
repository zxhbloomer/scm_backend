# SCM AI模块Token管理深度分析

## 数据库表结构分析

### 核心Token管理表

#### 1. ai_token_usage - Token使用记录表
**用途**: 记录每次AI对话的详细Token使用情况
- **主要字段**:
  - `conversation_id`: 对话ID，关联ai_conversation表
  - `model_source_id`: 模型源ID，关联ai_model_source表  
  - `user_id`: 用户ID
  - `tenant`: 租户ID
  - `ai_provider`: AI提供商名称(OpenAI/DeepSeek/智谱AI等)
  - `ai_model_type`: AI模型类型
  - `prompt_tokens`: 输入token数量
  - `completion_tokens`: 输出token数量
  - `total_tokens`: 总token数(计算列)
  - `token_unit_price`: Token单价(美元/1K tokens)
  - `cost`: 费用(美元)
  - `success`: 请求是否成功
  - `response_time`: 响应时间(毫秒)
  - `create_time`: 创建时间戳

#### 2. ai_user_quota - 用户配额管理表
**用途**: 管理每个用户的Token配额限制和使用情况
- **主要字段**:
  - `user_id`: 用户ID
  - `tenant`: 租户ID
  - `daily_limit`: 日Token限额(默认10000)
  - `monthly_limit`: 月Token限额(默认300000)
  - `daily_used`: 当日已使用Token数
  - `monthly_used`: 当月已使用Token数
  - `daily_reset_date`: 日配额重置日期
  - `monthly_reset_date`: 月配额重置日期
  - `total_cost`: 累计费用(美元)
  - `status`: 状态(1启用，0禁用)
- **唯一约束**: `uk_ai_user_quota_user_tenant`(user_id, tenant)

#### 3. ai_token_statistics - Token使用汇总统计表
**用途**: 按不同维度汇总Token使用统计数据
- **主要字段**:
  - `stat_type`: 统计类型(daily, monthly, user_daily, user_monthly, tenant_daily, tenant_monthly)
  - `stat_key`: 统计组合键(如user_id、tenant、model_source_id)
  - `stat_date`: 统计日期
  - `ai_provider`: AI提供商
  - `ai_model_type`: AI模型类型
  - `total_requests`: 总请求次数
  - `success_requests`: 成功请求次数
  - `total_prompt_tokens`: 总输入token
  - `total_completion_tokens`: 总输出token
  - `total_tokens`: 总token数
  - `total_cost`: 总费用(美元)
  - `avg_response_time`: 平均响应时间(毫秒)
  - `min_response_time`: 最小响应时间
  - `max_response_time`: 最大响应时间
- **唯一约束**: `uk_ai_token_statistics`(stat_type, stat_key, stat_date, ai_provider, ai_model_type)

#### 4. ai_config - AI系统配置表
**用途**: 存储AI相关系统配置
- **主要字段**:
  - `config_key`: 配置键
  - `config_value`: 配置值
  - `description`: 配置描述
  - `tenant`: 租户ID
- **唯一约束**: `uk_ai_config_key_tenant`(config_key, tenant)

#### 5. ai_model_source - AI模型源配置表
**用途**: 管理各个AI提供商的模型配置信息
- **主要字段**:
  - `name`: 模型名称
  - `type`: 模型类型(大语言模型/视觉/音频)
  - `provider_name`: 提供商名称
  - `permission_type`: 权限类型(公有/私有)
  - `owner`: 模型拥有者
  - `owner_type`: 拥有者类型(个人/企业)
  - `base_name`: 基础名称
  - `app_key`: 模型key
  - `api_url`: 模型url
  - `adv_settings`: 模型参数配置值
  - `is_default`: 是否默认模型
  - `status`: 模型连接状态

## 代码架构分析

### 事件驱动架构
采用Spring的事件发布-监听机制实现异步Token统计：

#### 1. LlmTokenUsageEvent - Token使用事件
```java
public class LlmTokenUsageEvent extends ApplicationEvent {
    private String conversationId;
    private String modelSourceId;
    private String userId;
    private String tenant;
    private String aiProvider;
    private String aiModelType;
    private Long promptTokens;
    private Long completionTokens;
    private Boolean success;
    private Long responseTime;
    private BigDecimal tokenUnitPrice;
    private BigDecimal cost;
    private Long createTime;
    
    // 计算总Token数
    public Long getTotalTokens() {
        return promptTokens + completionTokens;
    }
}
```

#### 2. TokenUsageEventListener - 异步事件监听器
```java
@Component
@Slf4j
public class TokenUsageEventListener {
    
    @EventListener
    @Async
    @Transactional
    public void handleTokenUsageEvent(LlmTokenUsageEvent event) {
        // 1. 保存Token使用记录
        saveTokenUsageRecord(event);
        
        // 2. 更新用户配额
        updateUserQuota(event);
        
        // 3. 检查配额告警
        checkQuotaAlerts(event);
        
        // 4. 更新统计数据(可选)
        // updateStatistics(event);
    }
}
```

### 服务层设计

#### 1. AiTokenUsageService - Token使用服务
- **recordTokenUsageAsync()**: 异步记录Token使用(推荐方式)
- **recordTokenUsage()**: 同步记录Token使用
- **recordFromSpringAiUsage()**: 从Spring AI Usage接口记录
- **getTodayTokenUsageByUser()**: 查询用户今日使用量
- **getMonthlyTokenUsageByUser()**: 查询用户本月使用量
- **getConversationTokenUsage()**: 查询对话Token统计
- **getUserTokenUsage()**: 查询用户Token统计

#### 2. AiUserQuotaService - 用户配额管理服务
- **getUserQuotaInfo()**: 获取用户配额信息(自动创建默认配额)
- **checkUserQuota()**: 检查用户配额是否足够
- **updateTokenUsage()**: 更新用户Token使用量(调用存储过程)
- **setUserQuota()**: 设置用户配额
- **resetUserDailyQuota()**/**resetUserMonthlyQuota()**: 重置配额
- **resetAllUsersDailyQuota()**/**resetAllUsersMonthlyQuota()**: 批量重置配额

#### 3. AiConfigService - AI配置服务  
- **isTokenStatisticsEnabled()**: 检查是否启用Token统计
- **isTokenQuotaCheckEnabled()**: 检查是否启用配额检查
- **calculateTokenCost()**: 计算Token费用
- **getDefaultDailyTokenLimit()**/**getDefaultMonthlyTokenLimit()**: 获取默认配额限制

## Token管理流程

### 1. Token使用记录流程
```
AI对话请求 → 获取Spring AI Usage → 发布LlmTokenUsageEvent事件
     ↓
异步事件监听器处理:
  1. 保存ai_token_usage记录
  2. 调用存储过程更新ai_user_quota
  3. 检查配额告警(80%警告，90%严重警告)
  4. [可选]更新ai_token_statistics统计
```

### 2. 配额检查流程  
```
AI请求前 → 检查是否启用配额管理 → 获取用户配额信息
    ↓
检查日配额和月配额 → 预估Token数 → 判断是否足够
    ↓
返回检查结果(允许/拒绝)
```

### 3. 配额重置流程
```
定时任务触发:
  - 每日00:00重置所有用户daily_used
  - 每月1日00:00重置所有用户monthly_used
    ↓
调用存储过程:
  - ResetUserDailyQuota()
  - ResetUserMonthlyQuota() 
    ↓
更新ai_user_quota表的reset_date字段
```

## 关键技术特点

### 1. 事件驱动异步处理
- 使用Spring的`ApplicationEventPublisher`发布事件
- `@EventListener`+`@Async`实现异步处理
- 避免Token统计影响主业务流程性能

### 2. 存储过程优化
- `UpdateUserTokenUsage(userId, tenant, tokenCount, cost)`: 原子性更新用户使用量
- `ResetUserDailyQuota()`和`ResetUserMonthlyQuota()`: 批量重置配额
- 避免并发更新问题，提升性能

### 3. 多维度统计支持
- 按用户、租户、模型、日期等多个维度统计
- `stat_type`字段支持: daily, monthly, user_daily, user_monthly, tenant_daily, tenant_monthly
- 支持按AI提供商和模型类型细分统计

### 4. 配额告警机制
- 80%使用率触发WARNING级别告警
- 90%使用率触发CRITICAL级别告警  
- 支持扩展邮件、短信、系统通知等告警方式

### 5. 灵活的配置管理
- 通过ai_config表集中管理系统配置
- 支持租户级别的个性化配置
- 可动态开启/关闭Token统计和配额检查功能

## 数据库设计优势

### 1. 高性能索引设计
- ai_token_usage表: 按conversation_id、user_id、create_time建立索引
- ai_user_quota表: user_id+tenant唯一约束，status、reset_date索引
- ai_token_statistics表: 复合唯一索引确保统计准确性

### 2. 外键关联保证数据一致性
- ai_token_usage.conversation_id → ai_conversation.id
- ai_token_usage.model_source_id → ai_model_source.id

### 3. 时间戳设计
- 统一使用BIGINT类型存储毫秒时间戳
- 便于跨时区处理和高精度时间计算

## 总结

该AI模块的Token管理系统采用了完整的企业级设计:
- **数据层**: 8张核心表支撑完整的Token管理生命周期
- **应用层**: 事件驱动架构实现高性能异步处理  
- **业务层**: 完整的配额管理、统计分析、告警机制
- **扩展性**: 支持多租户、多模型、多维度统计分析

这是一个生产就绪的Token管理解决方案，能够满足企业级AI应用的成本控制和使用监管需求。