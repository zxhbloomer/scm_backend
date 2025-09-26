package com.xinyirun.scm.ai.controller.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.core.service.chat.AiTokenUsageService;
import com.xinyirun.scm.ai.core.service.chat.AiUserQuotaService;
import com.xinyirun.scm.ai.core.service.chat.AiConfigService;
import com.xinyirun.scm.ai.common.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * AI Token统计控制器
 * 提供Token使用统计、配额查询和管理功能的REST API接口
 *
 * @author zxh
 * @createTime 2025-09-25
 */
@Tag(name = "AI Token统计")
@RestController
@RequestMapping(value = "/api/v1/ai/statistics")
public class AiTokenStatisticsController {

    @Resource
    private AiTokenUsageService aiTokenUsageService;

    @Resource
    private AiUserQuotaService aiUserQuotaService;

    @Resource
    private AiConfigService aiConfigService;

    /**
     * 获取当前用户的Token配额信息
     */
    @GetMapping(value = "/quota")
    @Operation(summary = "获取用户Token配额")
    public AiUserQuotaService.UserQuotaInfo getUserQuota() {
        String userId = SessionUtils.getUserId();
        String tenant = getCurrentTenant();
        return aiUserQuotaService.getUserQuotaInfo(userId, tenant);
    }

    /**
     * 获取指定用户的Token配额信息
     */
    @GetMapping(value = "/quota/{userId}")
    @Operation(summary = "获取指定用户Token配额")
    public AiUserQuotaService.UserQuotaInfo getUserQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId) {
        String tenant = getCurrentTenant();
        return aiUserQuotaService.getUserQuotaInfo(userId, tenant);
    }

    /**
     * 设置用户Token配额
     */
    @PostMapping(value = "/quota/{userId}")
    @Operation(summary = "设置用户Token配额")
    public void setUserQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "日限额")
            @RequestParam(required = false) Long dailyLimit,
            @Parameter(description = "月限额")
            @RequestParam(required = false) Long monthlyLimit) {

        String tenant = getCurrentTenant();

        // 如果没有提供限额，使用默认值
        if (dailyLimit == null) {
            dailyLimit = aiConfigService.getDefaultDailyTokenLimit();
        }
        if (monthlyLimit == null) {
            monthlyLimit = aiConfigService.getDefaultMonthlyTokenLimit();
        }

        aiUserQuotaService.setUserQuota(userId, tenant, dailyLimit, monthlyLimit);
    }

    /**
     * 重置用户日配额
     */
    @PostMapping(value = "/quota/{userId}/reset-daily")
    @Operation(summary = "重置用户日配额")
    public void resetUserDailyQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId) {
        String tenant = getCurrentTenant();
        aiUserQuotaService.resetUserDailyQuota(userId, tenant);
    }

    /**
     * 重置用户月配额
     */
    @PostMapping(value = "/quota/{userId}/reset-monthly")
    @Operation(summary = "重置用户月配额")
    public void resetUserMonthlyQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId) {
        String tenant = getCurrentTenant();
        aiUserQuotaService.resetUserMonthlyQuota(userId, tenant);
    }

    /**
     * 获取当前用户的Token使用统计
     */
    @GetMapping(value = "/usage")
    @Operation(summary = "获取用户Token使用统计")
    public AiTokenUsageService.UserTokenUsageSummary getUserTokenUsage() {
        String userId = SessionUtils.getUserId();
        String tenant = getCurrentTenant();
        return aiTokenUsageService.getUserTokenUsage(userId, tenant);
    }

    /**
     * 获取指定用户的Token使用统计
     */
    @GetMapping(value = "/usage/{userId}")
    @Operation(summary = "获取指定用户Token使用统计")
    public AiTokenUsageService.UserTokenUsageSummary getUserTokenUsage(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId) {
        String tenant = getCurrentTenant();
        return aiTokenUsageService.getUserTokenUsage(userId, tenant);
    }

    /**
     * 获取对话的Token使用统计
     */
    @GetMapping(value = "/conversation/{conversationId}")
    @Operation(summary = "获取对话Token使用统计")
    public AiTokenUsageService.TokenUsageSummary getConversationTokenUsage(
            @Parameter(description = "对话ID", required = true)
            @PathVariable String conversationId) {
        return aiTokenUsageService.getConversationTokenUsage(conversationId);
    }

    /**
     * 检查用户配额是否足够
     */
    @GetMapping(value = "/quota/check")
    @Operation(summary = "检查用户配额")
    public QuotaCheckResult checkUserQuota(
            @Parameter(description = "预估Token数")
            @RequestParam(defaultValue = "1000") Long estimatedTokens) {
        String userId = SessionUtils.getUserId();
        String tenant = getCurrentTenant();

        boolean hasEnoughQuota = aiUserQuotaService.checkUserQuota(userId, tenant, estimatedTokens);
        AiUserQuotaService.UserQuotaInfo quotaInfo = aiUserQuotaService.getUserQuotaInfo(userId, tenant);

        QuotaCheckResult result = new QuotaCheckResult();
        result.setHasEnoughQuota(hasEnoughQuota);
        result.setDailyUsed(quotaInfo.getDailyUsed());
        result.setDailyLimit(quotaInfo.getDailyLimit());
        result.setMonthlyUsed(quotaInfo.getMonthlyUsed());
        result.setMonthlyLimit(quotaInfo.getMonthlyLimit());
        result.setDailyUsagePercent(quotaInfo.getDailyUsagePercent());
        result.setMonthlyUsagePercent(quotaInfo.getMonthlyUsagePercent());

        return result;
    }

    /**
     * 获取Token统计配置
     */
    @GetMapping(value = "/config")
    @Operation(summary = "获取Token统计配置")
    public TokenStatisticsConfig getTokenStatisticsConfig() {
        TokenStatisticsConfig config = new TokenStatisticsConfig();
        config.setTokenStatisticsEnabled(aiConfigService.isTokenStatisticsEnabled());
        config.setTokenQuotaCheckEnabled(aiConfigService.isTokenQuotaCheckEnabled());
        config.setDefaultDailyLimit(aiConfigService.getDefaultDailyTokenLimit());
        config.setDefaultMonthlyLimit(aiConfigService.getDefaultMonthlyTokenLimit());
        return config;
    }

    /**
     * 清理配置缓存
     */
    @PostMapping(value = "/config/clear-cache")
    @Operation(summary = "清理配置缓存")
    public void clearConfigCache() {
        aiConfigService.clearConfigCache();
    }

    /**
     * 批量重置所有用户的日配额（管理员功能）
     */
    @PostMapping(value = "/admin/reset-all-daily-quota")
    @Operation(summary = "批量重置所有用户日配额")
    public void resetAllUsersDailyQuota() {
        aiUserQuotaService.resetAllUsersDailyQuota();
    }

    /**
     * 批量重置所有用户的月配额（管理员功能）
     */
    @PostMapping(value = "/admin/reset-all-monthly-quota")
    @Operation(summary = "批量重置所有用户月配额")
    public void resetAllUsersMonthlyQuota() {
        aiUserQuotaService.resetAllUsersMonthlyQuota();
    }

    /**
     * 获取当前租户ID
     * TODO: 实现获取当前租户的逻辑
     */
    private String getCurrentTenant() {
        // 这里需要根据实际的租户获取逻辑来实现
        // 可能从ThreadLocal、Session、请求头等获取
        return null;
    }

    /**
     * 配额检查结果
     */
    public static class QuotaCheckResult {
        private Boolean hasEnoughQuota;
        private Long dailyUsed;
        private Long dailyLimit;
        private Long monthlyUsed;
        private Long monthlyLimit;
        private Double dailyUsagePercent;
        private Double monthlyUsagePercent;

        // Getters and Setters
        public Boolean getHasEnoughQuota() { return hasEnoughQuota; }
        public void setHasEnoughQuota(Boolean hasEnoughQuota) { this.hasEnoughQuota = hasEnoughQuota; }
        public Long getDailyUsed() { return dailyUsed; }
        public void setDailyUsed(Long dailyUsed) { this.dailyUsed = dailyUsed; }
        public Long getDailyLimit() { return dailyLimit; }
        public void setDailyLimit(Long dailyLimit) { this.dailyLimit = dailyLimit; }
        public Long getMonthlyUsed() { return monthlyUsed; }
        public void setMonthlyUsed(Long monthlyUsed) { this.monthlyUsed = monthlyUsed; }
        public Long getMonthlyLimit() { return monthlyLimit; }
        public void setMonthlyLimit(Long monthlyLimit) { this.monthlyLimit = monthlyLimit; }
        public Double getDailyUsagePercent() { return dailyUsagePercent; }
        public void setDailyUsagePercent(Double dailyUsagePercent) { this.dailyUsagePercent = dailyUsagePercent; }
        public Double getMonthlyUsagePercent() { return monthlyUsagePercent; }
        public void setMonthlyUsagePercent(Double monthlyUsagePercent) { this.monthlyUsagePercent = monthlyUsagePercent; }
    }

    /**
     * Token统计配置信息
     */
    public static class TokenStatisticsConfig {
        private Boolean tokenStatisticsEnabled;
        private Boolean tokenQuotaCheckEnabled;
        private Long defaultDailyLimit;
        private Long defaultMonthlyLimit;

        // Getters and Setters
        public Boolean getTokenStatisticsEnabled() { return tokenStatisticsEnabled; }
        public void setTokenStatisticsEnabled(Boolean tokenStatisticsEnabled) { this.tokenStatisticsEnabled = tokenStatisticsEnabled; }
        public Boolean getTokenQuotaCheckEnabled() { return tokenQuotaCheckEnabled; }
        public void setTokenQuotaCheckEnabled(Boolean tokenQuotaCheckEnabled) { this.tokenQuotaCheckEnabled = tokenQuotaCheckEnabled; }
        public Long getDefaultDailyLimit() { return defaultDailyLimit; }
        public void setDefaultDailyLimit(Long defaultDailyLimit) { this.defaultDailyLimit = defaultDailyLimit; }
        public Long getDefaultMonthlyLimit() { return defaultMonthlyLimit; }
        public void setDefaultMonthlyLimit(Long defaultMonthlyLimit) { this.defaultMonthlyLimit = defaultMonthlyLimit; }
    }
}