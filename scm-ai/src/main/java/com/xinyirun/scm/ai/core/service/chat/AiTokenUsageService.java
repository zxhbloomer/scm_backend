package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.domain.AiTokenUsage;
import com.xinyirun.scm.ai.bean.event.LlmTokenUsageEvent;
import com.xinyirun.scm.ai.common.util.LogUtils;
import com.xinyirun.scm.ai.core.mapper.chat.AiTokenUsageMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * AI Token使用服务
 * 负责Token使用记录的创建、查询和统计功能
 *
 * @author Claude AI Assistant
 * @createTime 2025-09-25
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiTokenUsageService {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private AiConfigService aiConfigService;

    @Resource
    private AiTokenUsageMapper aiTokenUsageMapper;

    /**
     * 记录Token使用情况（同步方式）
     *
     * @param conversationId 对话ID
     * @param modelSourceId 模型源ID
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     * @param success 是否成功
     * @param responseTime 响应时间
     * @return Token使用记录
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AiTokenUsage recordTokenUsage(String conversationId, String modelSourceId, String userId,
                                        String tenant, String aiProvider, String aiModelType,
                                        Long promptTokens, Long completionTokens, Boolean success,
                                        Long responseTime) {
        try {
            // 检查是否启用Token统计
            if (!aiConfigService.isTokenStatisticsEnabled()) {
                LogUtils.debug("Token统计功能已禁用，跳过记录");
                return null;
            }

            // 成本计算已移除，只记录Token统计

            // 创建Token使用记录
            AiTokenUsage tokenUsage = new AiTokenUsage();
            tokenUsage.setId(UUID.randomUUID().toString().replace("-", ""));
            tokenUsage.setConversationId(conversationId);
            tokenUsage.setModelSourceId(modelSourceId);
            tokenUsage.setUserId(userId);
            tokenUsage.setTenant(tenant);
            tokenUsage.setAiProvider(aiProvider);
            tokenUsage.setAiModelType(aiModelType);
            tokenUsage.setPromptTokens(promptTokens != null ? promptTokens : 0L);
            tokenUsage.setCompletionTokens(completionTokens != null ? completionTokens : 0L);
            // total_tokens字段由数据库自动计算，不需要手动设置
            tokenUsage.setSuccess(success != null ? success : true);
            tokenUsage.setResponseTime(responseTime != null ? responseTime : 0L);
            tokenUsage.setCreateTime(System.currentTimeMillis());

            // 保存到数据库
            aiTokenUsageMapper.insertSelective(tokenUsage);

            LogUtils.info("Token使用记录已保存 - conversationId: {}, userId: {}, totalTokens: {}",
                    new Object[]{conversationId, userId, tokenUsage.getTotalTokens()});

            return tokenUsage;

        } catch (Exception e) {
            LogUtils.error("记录Token使用失败", e);
            throw e;
        }
    }

    /**
     * 异步记录Token使用情况（推荐方式）
     *
     * @param conversationId 对话ID
     * @param modelSourceId 模型源ID
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     * @param success 是否成功
     * @param responseTime 响应时间
     */
    public void recordTokenUsageAsync(String conversationId, String modelSourceId, String userId,
                                     String tenant, String aiProvider, String aiModelType,
                                     Long promptTokens, Long completionTokens, Boolean success,
                                     Long responseTime) {
        try {
            // 检查是否启用Token统计
            if (!aiConfigService.isTokenStatisticsEnabled()) {
                LogUtils.debug("Token统计功能已禁用，跳过记录");
                return;
            }

            // 发布Token使用事件（成本计算已移除）
            LlmTokenUsageEvent event = new LlmTokenUsageEvent(
                    this, conversationId, modelSourceId, userId, tenant, aiProvider, aiModelType,
                    promptTokens, completionTokens, success, responseTime, null, null);

            eventPublisher.publishEvent(event);

            LogUtils.debug("Token使用事件已发布 - conversationId: {}, userId: {}, totalTokens: {}",
                    new Object[]{conversationId, userId, event.getTotalTokens()});

        } catch (Exception e) {
            LogUtils.error("异步记录Token使用失败", e);
            // 异步记录失败不抛出异常，避免影响主业务流程
        }
    }

    /**
     * 根据Spring AI的Usage接口记录Token使用
     *
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     */
    public void recordFromSpringAiUsage(String conversationId, String userId, String tenant,
                                       String aiProvider, String aiModelType, Long promptTokens, Long completionTokens) {
        recordTokenUsageAsync(conversationId, null, userId, tenant, aiProvider, aiModelType,
                promptTokens, completionTokens, true, 0L);
    }

    /**
     * 查询用户今日Token使用量
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @return 今日使用量
     */
    public Long getTodayTokenUsageByUser(String userId, String tenant) {
        try {
            // TODO: 实现查询逻辑
            // 查询今日00:00:00到当前时间的Token使用总量
            return 0L;
        } catch (Exception e) {
            LogUtils.error("查询用户今日Token使用量失败 - userId: {}", userId, e);
            return 0L;
        }
    }

    /**
     * 查询用户本月Token使用量
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @return 本月使用量
     */
    public Long getMonthlyTokenUsageByUser(String userId, String tenant) {
        try {
            // TODO: 实现查询逻辑
            // 查询本月1日00:00:00到当前时间的Token使用总量
            return 0L;
        } catch (Exception e) {
            LogUtils.error("查询用户本月Token使用量失败 - userId: {}", userId, e);
            return 0L;
        }
    }

    /**
     * 查询对话的Token使用统计
     *
     * @param conversationId 对话ID
     * @return Token使用统计
     */
    public TokenUsageSummary getConversationTokenUsage(String conversationId) {
        try {
            // TODO: 实现查询逻辑
            TokenUsageSummary summary = new TokenUsageSummary();
            summary.setConversationId(conversationId);
            summary.setTotalRequests(0L);
            summary.setTotalPromptTokens(0L);
            summary.setTotalCompletionTokens(0L);
            summary.setTotalTokens(0L);
            summary.setAvgResponseTime(0L);
            return summary;
        } catch (Exception e) {
            LogUtils.error("查询对话Token使用统计失败 - conversationId: {}", conversationId, e);
            return new TokenUsageSummary();
        }
    }

    /**
     * 查询用户的Token使用统计
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @return Token使用统计
     */
    public UserTokenUsageSummary getUserTokenUsage(String userId, String tenant) {
        try {
            // TODO: 实现查询逻辑
            UserTokenUsageSummary summary = new UserTokenUsageSummary();
            summary.setUserId(userId);
            summary.setTenant(tenant);
            summary.setDailyUsed(getTodayTokenUsageByUser(userId, tenant));
            summary.setMonthlyUsed(getMonthlyTokenUsageByUser(userId, tenant));
            summary.setDailyLimit(aiConfigService.getDefaultDailyTokenLimit());
            summary.setMonthlyLimit(aiConfigService.getDefaultMonthlyTokenLimit());
            return summary;
        } catch (Exception e) {
            LogUtils.error("查询用户Token使用统计失败 - userId: {}", userId, e);
            return new UserTokenUsageSummary();
        }
    }


    /**
     * Token使用统计摘要
     */
    public static class TokenUsageSummary {
        private String conversationId;
        private Long totalRequests;
        private Long totalPromptTokens;
        private Long totalCompletionTokens;
        private Long totalTokens;
        private BigDecimal totalCost;
        private Long avgResponseTime;

        // Getters and Setters
        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }
        public Long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
        public Long getTotalPromptTokens() { return totalPromptTokens; }
        public void setTotalPromptTokens(Long totalPromptTokens) { this.totalPromptTokens = totalPromptTokens; }
        public Long getTotalCompletionTokens() { return totalCompletionTokens; }
        public void setTotalCompletionTokens(Long totalCompletionTokens) { this.totalCompletionTokens = totalCompletionTokens; }
        public Long getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Long totalTokens) { this.totalTokens = totalTokens; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        public Long getAvgResponseTime() { return avgResponseTime; }
        public void setAvgResponseTime(Long avgResponseTime) { this.avgResponseTime = avgResponseTime; }
    }

    /**
     * 用户Token使用统计摘要
     */
    public static class UserTokenUsageSummary {
        private String userId;
        private String tenant;
        private Long dailyUsed;
        private Long dailyLimit;
        private Long monthlyUsed;
        private Long monthlyLimit;
        private BigDecimal totalCost;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTenant() { return tenant; }
        public void setTenant(String tenant) { this.tenant = tenant; }
        public Long getDailyUsed() { return dailyUsed; }
        public void setDailyUsed(Long dailyUsed) { this.dailyUsed = dailyUsed; }
        public Long getDailyLimit() { return dailyLimit; }
        public void setDailyLimit(Long dailyLimit) { this.dailyLimit = dailyLimit; }
        public Long getMonthlyUsed() { return monthlyUsed; }
        public void setMonthlyUsed(Long monthlyUsed) { this.monthlyUsed = monthlyUsed; }
        public Long getMonthlyLimit() { return monthlyLimit; }
        public void setMonthlyLimit(Long monthlyLimit) { this.monthlyLimit = monthlyLimit; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

        public Double getDailyUsagePercent() {
            if (dailyLimit == null || dailyLimit == 0L || dailyUsed == null) {
                return 0.0;
            }
            return (double) dailyUsed / dailyLimit * 100;
        }

        public Double getMonthlyUsagePercent() {
            if (monthlyLimit == null || monthlyLimit == 0L || monthlyUsed == null) {
                return 0.0;
            }
            return (double) monthlyUsed / monthlyLimit * 100;
        }
    }
}