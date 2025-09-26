package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.ai.common.util.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI Token统计视图查询控制器
 * 提供基于数据库视图的快速查询接口
 *
 * @author Claude AI Assistant
 * @createTime 2025-09-25
 */
@Tag(name = "AI Token统计视图", description = "基于数据库视图的快速统计查询")
@RestController
@RequestMapping("/ai/token/views")
@Slf4j
public class AiTokenViewsController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询用户Token使用汇总视图
     */
    @Operation(summary = "查询用户Token使用汇总", description = "基于v_user_token_summary视图查询用户Token使用统计")
    @GetMapping("/user-summary")
    public ApiResponse<List<UserTokenSummaryView>> getUserTokenSummary(
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId,
            @Parameter(description = "租户ID") @RequestParam(required = false) String tenant,
            @Parameter(description = "限制返回条数") @RequestParam(defaultValue = "100") Integer limit) {

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM v_user_token_summary WHERE 1=1");

            if (userId != null && !userId.isEmpty()) {
                sql.append(" AND user_id = '").append(userId).append("'");
            }

            if (tenant != null && !tenant.isEmpty()) {
                sql.append(" AND tenant = '").append(tenant).append("'");
            }

            sql.append(" ORDER BY total_tokens DESC LIMIT ").append(limit);

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString());
            List<UserTokenSummaryView> summaries = results.stream()
                    .map(this::mapToUserTokenSummary)
                    .toList();

            return ApiResponse.success(summaries);

        } catch (Exception e) {
            LogUtils.error("查询用户Token使用汇总失败", e);
            return ApiResponse.error("查询用户Token使用汇总失败: " + e.getMessage());
        }
    }

    /**
     * 查询模型使用统计视图
     */
    @Operation(summary = "查询模型使用统计", description = "基于v_model_usage_stats视图查询AI模型使用统计")
    @GetMapping("/model-stats")
    public ApiResponse<List<ModelUsageStatsView>> getModelUsageStats(
            @Parameter(description = "AI提供商") @RequestParam(required = false) String aiProvider,
            @Parameter(description = "租户ID") @RequestParam(required = false) String tenant,
            @Parameter(description = "限制返回条数") @RequestParam(defaultValue = "50") Integer limit) {

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM v_model_usage_stats WHERE 1=1");

            if (aiProvider != null && !aiProvider.isEmpty()) {
                sql.append(" AND ai_provider = '").append(aiProvider).append("'");
            }

            if (tenant != null && !tenant.isEmpty()) {
                sql.append(" AND tenant = '").append(tenant).append("'");
            }

            sql.append(" ORDER BY total_tokens DESC LIMIT ").append(limit);

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString());
            List<ModelUsageStatsView> stats = results.stream()
                    .map(this::mapToModelUsageStats)
                    .toList();

            return ApiResponse.success(stats);

        } catch (Exception e) {
            LogUtils.error("查询模型使用统计失败", e);
            return ApiResponse.error("查询模型使用统计失败: " + e.getMessage());
        }
    }

    /**
     * 查询日期统计汇总视图
     */
    @Operation(summary = "查询日期统计汇总", description = "基于v_daily_token_summary视图查询每日Token使用汇总")
    @GetMapping("/daily-summary")
    public ApiResponse<List<DailyTokenSummaryView>> getDailyTokenSummary(
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate,
            @Parameter(description = "租户ID") @RequestParam(required = false) String tenant,
            @Parameter(description = "限制返回条数") @RequestParam(defaultValue = "30") Integer limit) {

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM v_daily_token_summary WHERE 1=1");

            if (startDate != null && !startDate.isEmpty()) {
                sql.append(" AND usage_date >= '").append(startDate).append("'");
            }

            if (endDate != null && !endDate.isEmpty()) {
                sql.append(" AND usage_date <= '").append(endDate).append("'");
            }

            if (tenant != null && !tenant.isEmpty()) {
                sql.append(" AND tenant = '").append(tenant).append("'");
            }

            sql.append(" ORDER BY usage_date DESC LIMIT ").append(limit);

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString());
            List<DailyTokenSummaryView> summaries = results.stream()
                    .map(this::mapToDailyTokenSummary)
                    .toList();

            return ApiResponse.success(summaries);

        } catch (Exception e) {
            LogUtils.error("查询日期统计汇总失败", e);
            return ApiResponse.error("查询日期统计汇总失败: " + e.getMessage());
        }
    }

    /**
     * 获取Token使用趋势数据
     */
    @Operation(summary = "获取Token使用趋势", description = "获取指定时间范围内的Token使用趋势数据")
    @GetMapping("/usage-trend")
    public ApiResponse<List<TokenUsageTrend>> getTokenUsageTrend(
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam String startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam String endDate,
            @Parameter(description = "租户ID") @RequestParam(required = false) String tenant) {

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("    DATE(FROM_UNIXTIME(create_time/1000)) as usage_date,");
            sql.append("    COUNT(*) as request_count,");
            sql.append("    SUM(total_tokens) as total_tokens,");
            sql.append("    SUM(total_cost) as total_cost,");
            sql.append("    COUNT(DISTINCT user_id) as active_users ");
            sql.append("FROM ai_token_usage ");
            sql.append("WHERE DATE(FROM_UNIXTIME(create_time/1000)) BETWEEN ? AND ? ");

            if (tenant != null && !tenant.isEmpty()) {
                sql.append(" AND tenant = '").append(tenant).append("' ");
            }

            sql.append("GROUP BY DATE(FROM_UNIXTIME(create_time/1000)) ");
            sql.append("ORDER BY usage_date");

            List<Map<String, Object>> results = jdbcTemplate.queryForList(
                sql.toString(), startDate, endDate);

            List<TokenUsageTrend> trends = results.stream()
                    .map(this::mapToTokenUsageTrend)
                    .toList();

            return ApiResponse.success(trends);

        } catch (Exception e) {
            LogUtils.error("获取Token使用趋势失败", e);
            return ApiResponse.error("获取Token使用趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户排行榜
     */
    @Operation(summary = "获取用户Token使用排行榜", description = "获取Token使用量最多的用户排行榜")
    @GetMapping("/user-ranking")
    public ApiResponse<List<UserRanking>> getUserRanking(
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate,
            @Parameter(description = "租户ID") @RequestParam(required = false) String tenant,
            @Parameter(description = "排行榜数量") @RequestParam(defaultValue = "10") Integer topN) {

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("    user_id,");
            sql.append("    tenant,");
            sql.append("    COUNT(*) as request_count,");
            sql.append("    SUM(total_tokens) as total_tokens,");
            sql.append("    SUM(total_cost) as total_cost,");
            sql.append("    AVG(response_time_ms) as avg_response_time ");
            sql.append("FROM ai_token_usage ");
            sql.append("WHERE 1=1 ");

            if (startDate != null && !startDate.isEmpty()) {
                sql.append(" AND DATE(FROM_UNIXTIME(create_time/1000)) >= '").append(startDate).append("' ");
            }

            if (endDate != null && !endDate.isEmpty()) {
                sql.append(" AND DATE(FROM_UNIXTIME(create_time/1000)) <= '").append(endDate).append("' ");
            }

            if (tenant != null && !tenant.isEmpty()) {
                sql.append(" AND tenant = '").append(tenant).append("' ");
            }

            sql.append("GROUP BY user_id, tenant ");
            sql.append("ORDER BY total_tokens DESC ");
            sql.append("LIMIT ").append(topN);

            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString());
            List<UserRanking> rankings = results.stream()
                    .map(this::mapToUserRanking)
                    .toList();

            return ApiResponse.success(rankings);

        } catch (Exception e) {
            LogUtils.error("获取用户排行榜失败", e);
            return ApiResponse.error("获取用户排行榜失败: " + e.getMessage());
        }
    }

    // 映射方法
    private UserTokenSummaryView mapToUserTokenSummary(Map<String, Object> row) {
        UserTokenSummaryView view = new UserTokenSummaryView();
        view.setUserId((String) row.get("user_id"));
        view.setTenant((String) row.get("tenant"));
        view.setTotalRequests(((Number) row.get("total_requests")).longValue());
        view.setSuccessRequests(((Number) row.get("success_requests")).longValue());
        view.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        view.setTotalCost((BigDecimal) row.get("total_cost"));
        view.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        view.setFirstUsageTime((Long) row.get("first_usage_time"));
        view.setLastUsageTime((Long) row.get("last_usage_time"));
        return view;
    }

    private ModelUsageStatsView mapToModelUsageStats(Map<String, Object> row) {
        ModelUsageStatsView view = new ModelUsageStatsView();
        view.setAiProvider((String) row.get("ai_provider"));
        view.setAiModelType((String) row.get("ai_model_type"));
        view.setTenant((String) row.get("tenant"));
        view.setTotalRequests(((Number) row.get("total_requests")).longValue());
        view.setSuccessRequests(((Number) row.get("success_requests")).longValue());
        view.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        view.setTotalCost((BigDecimal) row.get("total_cost"));
        view.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        view.setUniqueUsers(((Number) row.get("unique_users")).longValue());
        return view;
    }

    private DailyTokenSummaryView mapToDailyTokenSummary(Map<String, Object> row) {
        DailyTokenSummaryView view = new DailyTokenSummaryView();
        view.setUsageDate((LocalDate) row.get("usage_date"));
        view.setTenant((String) row.get("tenant"));
        view.setTotalRequests(((Number) row.get("total_requests")).longValue());
        view.setSuccessRequests(((Number) row.get("success_requests")).longValue());
        view.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        view.setTotalCost((BigDecimal) row.get("total_cost"));
        view.setUniqueUsers(((Number) row.get("unique_users")).longValue());
        view.setUniqueModels(((Number) row.get("unique_models")).longValue());
        view.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        return view;
    }

    private TokenUsageTrend mapToTokenUsageTrend(Map<String, Object> row) {
        TokenUsageTrend trend = new TokenUsageTrend();
        trend.setUsageDate((LocalDate) row.get("usage_date"));
        trend.setRequestCount(((Number) row.get("request_count")).longValue());
        trend.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        trend.setTotalCost((BigDecimal) row.get("total_cost"));
        trend.setActiveUsers(((Number) row.get("active_users")).longValue());
        return trend;
    }

    private UserRanking mapToUserRanking(Map<String, Object> row) {
        UserRanking ranking = new UserRanking();
        ranking.setUserId((String) row.get("user_id"));
        ranking.setTenant((String) row.get("tenant"));
        ranking.setRequestCount(((Number) row.get("request_count")).longValue());
        ranking.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        ranking.setTotalCost((BigDecimal) row.get("total_cost"));
        ranking.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        return ranking;
    }

    // 响应DTO类
    @Data
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> success(T data) {
            ApiResponse<T> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setData(data);
            return response;
        }

        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }

    @Data
    public static class UserTokenSummaryView {
        private String userId;
        private String tenant;
        private Long totalRequests;
        private Long successRequests;
        private Long totalTokens;
        private BigDecimal totalCost;
        private BigDecimal avgResponseTime;
        private Long firstUsageTime;
        private Long lastUsageTime;
    }

    @Data
    public static class ModelUsageStatsView {
        private String aiProvider;
        private String aiModelType;
        private String tenant;
        private Long totalRequests;
        private Long successRequests;
        private Long totalTokens;
        private BigDecimal totalCost;
        private BigDecimal avgResponseTime;
        private Long uniqueUsers;
    }

    @Data
    public static class DailyTokenSummaryView {
        private LocalDate usageDate;
        private String tenant;
        private Long totalRequests;
        private Long successRequests;
        private Long totalTokens;
        private BigDecimal totalCost;
        private Long uniqueUsers;
        private Long uniqueModels;
        private BigDecimal avgResponseTime;
    }

    @Data
    public static class TokenUsageTrend {
        private LocalDate usageDate;
        private Long requestCount;
        private Long totalTokens;
        private BigDecimal totalCost;
        private Long activeUsers;
    }

    @Data
    public static class UserRanking {
        private String userId;
        private String tenant;
        private Long requestCount;
        private Long totalTokens;
        private BigDecimal totalCost;
        private BigDecimal avgResponseTime;
    }
}