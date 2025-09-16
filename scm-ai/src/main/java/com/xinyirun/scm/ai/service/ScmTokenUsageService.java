package com.xinyirun.scm.ai.service;

import com.xinyirun.scm.ai.entity.AiChatRecord;
import com.xinyirun.scm.ai.repository.AiChatRecordRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * SCM AI Token使用量统计和监控服务
 * 
 * 负责跟踪和分析AI模型的Token消耗情况，包括：
 * 1. 实时Token使用量统计
 * 2. 成本计算和预警
 * 3. 使用趋势分析
 * 4. 多厂商Token消耗对比
 * 5. 用户级别的使用量监控
 * 6. 预算控制和限制
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Slf4j
@Service
public class ScmTokenUsageService {

    @Autowired
    private AiChatRecordRepository chatRecordRepository;

    /**
     * AI厂商Token定价策略（每1000个Token的价格，单位：人民币分）
     * 注意：实际价格会变动，建议从配置文件或数据库读取
     */
    private static final Map<String, TokenPricing> TOKEN_PRICING = new HashMap<>();
    
    static {
        // OpenAI GPT-4 定价 (估算)
        TOKEN_PRICING.put("openai_gpt-4", new TokenPricing(0.6, 1.2));           // 输入0.006元，输出0.012元每1K tokens
        TOKEN_PRICING.put("openai_gpt-4o", new TokenPricing(0.3, 0.6));          // 输入0.003元，输出0.006元每1K tokens
        TOKEN_PRICING.put("openai_gpt-3.5-turbo", new TokenPricing(0.05, 0.15)); // 输入0.0005元，输出0.0015元每1K tokens
        
        // Anthropic Claude 定价 (估算)
        TOKEN_PRICING.put("anthropic_claude-3-opus", new TokenPricing(1.5, 7.5)); // 输入0.015元，输出0.075元每1K tokens
        TOKEN_PRICING.put("anthropic_claude-3-haiku", new TokenPricing(0.025, 0.125)); // 输入0.00025元，输出0.00125元每1K tokens
        
        // 国产大模型定价 (估算，通常更便宜)
        TOKEN_PRICING.put("zhipuai_glm-4", new TokenPricing(0.1, 0.1));          // 统一定价0.001元每1K tokens
        TOKEN_PRICING.put("zhipuai_glm-3-turbo", new TokenPricing(0.05, 0.05));  // 统一定价0.0005元每1K tokens
        TOKEN_PRICING.put("dashscope_qwen-turbo", new TokenPricing(0.03, 0.06)); // 输入0.0003元，输出0.0006元每1K tokens
        TOKEN_PRICING.put("dashscope_qwen-plus", new TokenPricing(0.2, 0.2));    // 统一定价0.002元每1K tokens
        
        // 默认定价 (兜底)
        TOKEN_PRICING.put("default", new TokenPricing(0.1, 0.2));
    }

    /**
     * 记录Token使用情况
     * 从ChatResponse的metadata中提取Usage信息
     * 
     * @param chatResponse AI模型响应
     * @param aiInfo AI模型信息
     * @param performanceStats 性能统计（会被更新）
     */
    public void recordTokenUsage(ChatResponseMetadata metadata, 
                               AiChatRecord.AiModelInfo aiInfo, 
                               AiChatRecord.PerformanceStats performanceStats) {
        
        if (metadata == null || metadata.getUsage() == null) {
            log.debug("ChatResponse metadata或usage为空，跳过Token统计");
            return;
        }
        
        Usage usage = metadata.getUsage();
        
        // 更新性能统计中的Token信息
        performanceStats.setInputTokens(usage.getPromptTokens());
        performanceStats.setOutputTokens(usage.getCompletionTokens());
        performanceStats.setTokenUsage(usage.getTotalTokens());
        
        // 计算成本
        BigDecimal cost = calculateTokenCost(aiInfo.getProvider(), aiInfo.getModelName(), 
                                           usage.getPromptTokens(), usage.getCompletionTokens());
        
        log.info("Token使用统计 - 厂商: {}, 模型: {}, 输入tokens: {}, 输出tokens: {}, 总计: {}, 成本: {}分", 
                aiInfo.getProvider(), aiInfo.getModelName(),
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(), 
                cost.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * 计算Token成本
     * 
     * @param provider AI厂商
     * @param model 模型名称
     * @param inputTokens 输入token数量
     * @param outputTokens 输出token数量
     * @return 成本（人民币分）
     */
    public BigDecimal calculateTokenCost(String provider, String model, int inputTokens, int outputTokens) {
        String pricingKey = provider + "_" + model;
        TokenPricing pricing = TOKEN_PRICING.getOrDefault(pricingKey, TOKEN_PRICING.get("default"));
        
        // 计算输入和输出token成本（每1000个token的价格）
        BigDecimal inputCost = BigDecimal.valueOf(inputTokens)
                .multiply(BigDecimal.valueOf(pricing.getInputPricePerThousand()))
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
        
        BigDecimal outputCost = BigDecimal.valueOf(outputTokens)
                .multiply(BigDecimal.valueOf(pricing.getOutputPricePerThousand()))
                .divide(BigDecimal.valueOf(1000), 4, RoundingMode.HALF_UP);
        
        return inputCost.add(outputCost);
    }

    /**
     * 获取租户的Token使用统计
     * 
     * @param tenantId 租户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Token使用统计
     */
    public TenantTokenUsageStats getTenantTokenUsage(String tenantId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        // 从MongoDB聚合查询Token使用情况
        // 这里简化实现，实际应该使用MongoDB的聚合管道
        var records = chatRecordRepository.findByTenantIdAndCreateTimeBetweenOrderByCreateTimeDesc(
                tenantId, startDateTime, endDateTime, null);
        
        TenantTokenUsageStats stats = new TenantTokenUsageStats();
        stats.setTenantId(tenantId);
        stats.setStartDate(startDate);
        stats.setEndDate(endDate);
        
        long totalInputTokens = 0;
        long totalOutputTokens = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, ProviderUsageStats> providerStats = new HashMap<>();
        Map<String, UserUsageStats> userStats = new HashMap<>();
        
        // 遍历记录计算统计信息
        for (AiChatRecord record : records.getContent()) {
            if (record.getPerformanceStats() != null && 
                record.getPerformanceStats().getTokenUsage() != null &&
                record.getPerformanceStats().getTokenUsage() > 0) {
                
                var perfStats = record.getPerformanceStats();
                totalInputTokens += perfStats.getInputTokens() != null ? perfStats.getInputTokens() : 0;
                totalOutputTokens += perfStats.getOutputTokens() != null ? perfStats.getOutputTokens() : 0;
                
                // 计算成本
                if (record.getAiInfo() != null) {
                    BigDecimal recordCost = calculateTokenCost(
                            record.getAiInfo().getProvider(),
                            record.getAiInfo().getModelName(),
                            perfStats.getInputTokens() != null ? perfStats.getInputTokens() : 0,
                            perfStats.getOutputTokens() != null ? perfStats.getOutputTokens() : 0
                    );
                    totalCost = totalCost.add(recordCost);
                    
                    // 按厂商统计
                    String provider = record.getAiInfo().getProvider();
                    providerStats.computeIfAbsent(provider, k -> new ProviderUsageStats())
                            .addUsage(perfStats.getInputTokens(), perfStats.getOutputTokens(), recordCost);
                }
                
                // 按用户统计
                String userId = record.getUserId().toString();
                userStats.computeIfAbsent(userId, k -> new UserUsageStats())
                        .addUsage(perfStats.getInputTokens(), perfStats.getOutputTokens());
            }
        }
        
        stats.setTotalInputTokens(totalInputTokens);
        stats.setTotalOutputTokens(totalOutputTokens);
        stats.setTotalTokens(totalInputTokens + totalOutputTokens);
        stats.setTotalCost(totalCost);
        stats.setProviderStats(providerStats);
        stats.setUserStats(userStats);
        stats.setRecordCount(records.getContent().size());
        
        return stats;
    }

    /**
     * 获取用户的Token使用统计
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户Token使用统计
     */
    public UserTokenUsageStats getUserTokenUsage(String tenantId, Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        // 查询用户的聊天记录
        var records = chatRecordRepository.findByTenantIdAndCreateTimeBetweenOrderByCreateTimeDesc(
                tenantId, startDateTime, endDateTime, null);
        
        UserTokenUsageStats stats = new UserTokenUsageStats();
        stats.setTenantId(tenantId);
        stats.setUserId(userId);
        stats.setStartDate(startDate);
        stats.setEndDate(endDate);
        
        long totalInputTokens = 0;
        long totalOutputTokens = 0;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, Long> pageUsage = new HashMap<>();
        
        // 只统计指定用户的记录
        for (AiChatRecord record : records.getContent()) {
            if (!record.getUserId().equals(userId)) {
                continue;
            }
            
            if (record.getPerformanceStats() != null && 
                record.getPerformanceStats().getTokenUsage() != null &&
                record.getPerformanceStats().getTokenUsage() > 0) {
                
                var perfStats = record.getPerformanceStats();
                totalInputTokens += perfStats.getInputTokens() != null ? perfStats.getInputTokens() : 0;
                totalOutputTokens += perfStats.getOutputTokens() != null ? perfStats.getOutputTokens() : 0;
                
                // 按页面统计
                pageUsage.merge(record.getPageCode(), 
                               (long) (perfStats.getTokenUsage() != null ? perfStats.getTokenUsage() : 0), 
                               Long::sum);
                
                // 计算成本
                if (record.getAiInfo() != null) {
                    BigDecimal recordCost = calculateTokenCost(
                            record.getAiInfo().getProvider(),
                            record.getAiInfo().getModelName(),
                            perfStats.getInputTokens() != null ? perfStats.getInputTokens() : 0,
                            perfStats.getOutputTokens() != null ? perfStats.getOutputTokens() : 0
                    );
                    totalCost = totalCost.add(recordCost);
                }
            }
        }
        
        stats.setTotalInputTokens(totalInputTokens);
        stats.setTotalOutputTokens(totalOutputTokens);
        stats.setTotalTokens(totalInputTokens + totalOutputTokens);
        stats.setTotalCost(totalCost);
        stats.setPageUsage(pageUsage);
        
        return stats;
    }

    /**
     * 检查用户是否超出Token使用限制
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param dailyLimit 每日限制
     * @param monthlyLimit 每月限制
     * @return 限制检查结果
     */
    public TokenLimitCheckResult checkTokenLimit(String tenantId, Long userId, String pageCode, 
                                               long dailyLimit, long monthlyLimit) {
        
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        
        // 查询今日和本月使用量
        UserTokenUsageStats dailyUsage = getUserTokenUsage(tenantId, userId, today, today);
        UserTokenUsageStats monthlyUsage = getUserTokenUsage(tenantId, userId, monthStart, today);
        
        TokenLimitCheckResult result = new TokenLimitCheckResult();
        result.setTenantId(tenantId);
        result.setUserId(userId);
        result.setPageCode(pageCode);
        
        // 检查每日限制
        if (dailyLimit > 0) {
            result.setDailyUsed(dailyUsage.getTotalTokens());
            result.setDailyLimit(dailyLimit);
            result.setDailyExceeded(dailyUsage.getTotalTokens() >= dailyLimit);
            result.setDailyRemaining(Math.max(0, dailyLimit - dailyUsage.getTotalTokens()));
        }
        
        // 检查每月限制
        if (monthlyLimit > 0) {
            result.setMonthlyUsed(monthlyUsage.getTotalTokens());
            result.setMonthlyLimit(monthlyLimit);
            result.setMonthlyExceeded(monthlyUsage.getTotalTokens() >= monthlyLimit);
            result.setMonthlyRemaining(Math.max(0, monthlyLimit - monthlyUsage.getTotalTokens()));
        }
        
        result.setLimitExceeded(result.isDailyExceeded() || result.isMonthlyExceeded());
        
        if (result.isLimitExceeded()) {
            log.warn("用户Token使用超限 - 租户: {}, 用户: {}, 页面: {}, 每日: {}/{}, 每月: {}/{}", 
                    tenantId, userId, pageCode, 
                    result.getDailyUsed(), result.getDailyLimit(),
                    result.getMonthlyUsed(), result.getMonthlyLimit());
        }
        
        return result;
    }

    /**
     * 获取实时Token使用率警报
     * 
     * @param tenantId 租户ID
     * @param warningThreshold 警告阈值（百分比，如80表示80%）
     * @param criticalThreshold 严重阈值（百分比，如95表示95%）
     * @return 警报列表
     */
    public TokenUsageAlert checkTokenUsageAlert(String tenantId, double warningThreshold, double criticalThreshold) {
        LocalDate today = LocalDate.now();
        TenantTokenUsageStats todayStats = getTenantTokenUsage(tenantId, today, today);
        
        TokenUsageAlert alert = new TokenUsageAlert();
        alert.setTenantId(tenantId);
        alert.setCheckTime(LocalDateTime.now());
        alert.setTotalTokensUsed(todayStats.getTotalTokens());
        alert.setTotalCost(todayStats.getTotalCost());
        
        // 这里可以根据实际业务设置预算限制
        // 示例：假设每日预算1000元（人民币分为单位则为100000分）
        long dailyBudgetInCents = 100000; // 1000元 = 100000分
        BigDecimal budgetUsagePercent = todayStats.getTotalCost()
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(dailyBudgetInCents), 2, RoundingMode.HALF_UP);
        
        alert.setBudgetUsagePercent(budgetUsagePercent.doubleValue());
        
        if (budgetUsagePercent.doubleValue() >= criticalThreshold) {
            alert.setAlertLevel(TokenUsageAlert.AlertLevel.CRITICAL);
            alert.setMessage("Token使用已达到严重阈值(" + criticalThreshold + "%)，请立即检查");
        } else if (budgetUsagePercent.doubleValue() >= warningThreshold) {
            alert.setAlertLevel(TokenUsageAlert.AlertLevel.WARNING);
            alert.setMessage("Token使用接近限制(" + warningThreshold + "%)，请注意控制");
        } else {
            alert.setAlertLevel(TokenUsageAlert.AlertLevel.NORMAL);
            alert.setMessage("Token使用正常");
        }
        
        return alert;
    }

    // ===== 内部数据类 =====

    /**
     * Token定价信息
     */
    @Data
    private static class TokenPricing {
        private double inputPricePerThousand;  // 输入token每千个的价格（人民币分）
        private double outputPricePerThousand; // 输出token每千个的价格（人民币分）
        
        public TokenPricing(double inputPrice, double outputPrice) {
            this.inputPricePerThousand = inputPrice;
            this.outputPricePerThousand = outputPrice;
        }
    }

    /**
     * 租户Token使用统计
     */
    @Data
    public static class TenantTokenUsageStats {
        private String tenantId;
        private LocalDate startDate;
        private LocalDate endDate;
        private long totalInputTokens;
        private long totalOutputTokens;
        private long totalTokens;
        private BigDecimal totalCost;
        private int recordCount;
        private Map<String, ProviderUsageStats> providerStats;
        private Map<String, UserUsageStats> userStats;
    }

    /**
     * 厂商使用统计
     */
    @Data
    public static class ProviderUsageStats {
        private long inputTokens;
        private long outputTokens;
        private long totalTokens;
        private BigDecimal cost;
        private int requestCount;
        
        public void addUsage(Integer inputTokens, Integer outputTokens, BigDecimal cost) {
            this.inputTokens += inputTokens != null ? inputTokens : 0;
            this.outputTokens += outputTokens != null ? outputTokens : 0;
            this.totalTokens = this.inputTokens + this.outputTokens;
            this.cost = this.cost != null ? this.cost.add(cost) : cost;
            this.requestCount++;
        }
    }

    /**
     * 用户使用统计
     */
    @Data
    public static class UserUsageStats {
        private long inputTokens;
        private long outputTokens;
        private long totalTokens;
        private int requestCount;
        
        public void addUsage(Integer inputTokens, Integer outputTokens) {
            this.inputTokens += inputTokens != null ? inputTokens : 0;
            this.outputTokens += outputTokens != null ? outputTokens : 0;
            this.totalTokens = this.inputTokens + this.outputTokens;
            this.requestCount++;
        }
    }

    /**
     * 用户Token使用详细统计
     */
    @Data
    public static class UserTokenUsageStats {
        private String tenantId;
        private Long userId;
        private LocalDate startDate;
        private LocalDate endDate;
        private long totalInputTokens;
        private long totalOutputTokens;
        private long totalTokens;
        private BigDecimal totalCost;
        private Map<String, Long> pageUsage; // 各页面使用量
    }

    /**
     * Token限制检查结果
     */
    @Data
    public static class TokenLimitCheckResult {
        private String tenantId;
        private Long userId;
        private String pageCode;
        
        // 每日限制
        private long dailyUsed;
        private long dailyLimit;
        private long dailyRemaining;
        private boolean dailyExceeded;
        
        // 每月限制  
        private long monthlyUsed;
        private long monthlyLimit;
        private long monthlyRemaining;
        private boolean monthlyExceeded;
        
        // 总体结果
        private boolean limitExceeded;
    }

    /**
     * Token使用警报
     */
    @Data
    public static class TokenUsageAlert {
        private String tenantId;
        private LocalDateTime checkTime;
        private long totalTokensUsed;
        private BigDecimal totalCost;
        private double budgetUsagePercent;
        private AlertLevel alertLevel;
        private String message;
        
        public enum AlertLevel {
            NORMAL,     // 正常
            WARNING,    // 警告
            CRITICAL    // 严重
        }
    }
}