package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI Token统计视图查询控制器
 * 提供基于数据库视图的快速查询接口，专用于报表和分析功能
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Tag(name = "AI Token统计视图")
@RestController
@RequestMapping("/api/v1/ai/statistics/views")
@Slf4j
public class AiTokenViewsController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询用户Token使用汇总视图
     */
    @Operation(summary = "查询用户Token使用汇总")
    @GetMapping("/user-summary")
    public ResponseEntity<List<UserTokenSummaryVo>> getUserTokenSummary(
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
            List<UserTokenSummaryVo> summaries = results.stream()
                    .map(this::mapToUserTokenSummary)
                    .toList();

            return ResponseEntity.ok(summaries);

        } catch (Exception e) {
            log.error("查询用户Token使用汇总失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 查询模型使用统计视图
     */
    @Operation(summary = "查询模型使用统计")
    @GetMapping("/model-stats")
    public ResponseEntity<List<ModelUsageStatsVo>> getModelUsageStats(
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
            List<ModelUsageStatsVo> stats = results.stream()
                    .map(this::mapToModelUsageStats)
                    .toList();

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("查询模型使用统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 查询日期统计汇总视图
     */
    @Operation(summary = "查询日期统计汇总")
    @GetMapping("/daily-summary")
    public ResponseEntity<List<DailyTokenSummaryVo>> getDailyTokenSummary(
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
            List<DailyTokenSummaryVo> summaries = results.stream()
                    .map(this::mapToDailyTokenSummary)
                    .toList();

            return ResponseEntity.ok(summaries);

        } catch (Exception e) {
            log.error("查询日期统计汇总失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取Token使用趋势数据
     */
    @Operation(summary = "获取Token使用趋势")
    @GetMapping("/usage-trend")
    public ResponseEntity<List<TokenUsageTrendVo>> getTokenUsageTrend(
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

            List<TokenUsageTrendVo> trends = results.stream()
                    .map(this::mapToTokenUsageTrend)
                    .toList();

            return ResponseEntity.ok(trends);

        } catch (Exception e) {
            log.error("获取Token使用趋势失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取用户排行榜
     */
    @Operation(summary = "获取用户Token使用排行榜")
    @GetMapping("/user-ranking")
    public ResponseEntity<List<UserRankingVo>> getUserRanking(
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
            List<UserRankingVo> rankings = results.stream()
                    .map(this::mapToUserRanking)
                    .toList();

            return ResponseEntity.ok(rankings);

        } catch (Exception e) {
            log.error("获取用户排行榜失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 映射方法
    private UserTokenSummaryVo mapToUserTokenSummary(Map<String, Object> row) {
        UserTokenSummaryVo vo = new UserTokenSummaryVo();
        vo.setUserId((String) row.get("user_id"));
        vo.setTenant((String) row.get("tenant"));
        vo.setTotalRequests(((Number) row.get("total_requests")).longValue());
        vo.setSuccessRequests(((Number) row.get("success_requests")).longValue());
        vo.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotalCost((BigDecimal) row.get("total_cost"));
        vo.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        vo.setFirstUsageTime((Long) row.get("first_usage_time"));
        vo.setLastUsageTime((Long) row.get("last_usage_time"));
        return vo;
    }

    private ModelUsageStatsVo mapToModelUsageStats(Map<String, Object> row) {
        ModelUsageStatsVo vo = new ModelUsageStatsVo();
        vo.setAiProvider((String) row.get("ai_provider"));
        vo.setAiModelType((String) row.get("ai_model_type"));
        vo.setTenant((String) row.get("tenant"));
        vo.setTotalRequests(((Number) row.get("total_requests")).longValue());
        vo.setSuccessRequests(((Number) row.get("success_requests")).longValue());
        vo.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotalCost((BigDecimal) row.get("total_cost"));
        vo.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        vo.setUniqueUsers(((Number) row.get("unique_users")).longValue());
        return vo;
    }

    private DailyTokenSummaryVo mapToDailyTokenSummary(Map<String, Object> row) {
        DailyTokenSummaryVo vo = new DailyTokenSummaryVo();
        vo.setUsageDate((LocalDate) row.get("usage_date"));
        vo.setTenant((String) row.get("tenant"));
        vo.setTotalRequests(((Number) row.get("total_requests")).longValue());
        vo.setSuccessRequests(((Number) row.get("success_requests")).longValue());
        vo.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotalCost((BigDecimal) row.get("total_cost"));
        vo.setUniqueUsers(((Number) row.get("unique_users")).longValue());
        vo.setUniqueModels(((Number) row.get("unique_models")).longValue());
        vo.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        return vo;
    }

    private TokenUsageTrendVo mapToTokenUsageTrend(Map<String, Object> row) {
        TokenUsageTrendVo vo = new TokenUsageTrendVo();
        vo.setUsageDate((LocalDate) row.get("usage_date"));
        vo.setRequestCount(((Number) row.get("request_count")).longValue());
        vo.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotalCost((BigDecimal) row.get("total_cost"));
        vo.setActiveUsers(((Number) row.get("active_users")).longValue());
        return vo;
    }

    private UserRankingVo mapToUserRanking(Map<String, Object> row) {
        UserRankingVo vo = new UserRankingVo();
        vo.setUserId((String) row.get("user_id"));
        vo.setTenant((String) row.get("tenant"));
        vo.setRequestCount(((Number) row.get("request_count")).longValue());
        vo.setTotalTokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotalCost((BigDecimal) row.get("total_cost"));
        vo.setAvgResponseTime((BigDecimal) row.get("avg_response_time"));
        return vo;
    }

    // 响应VO类

    @Data
    public static class UserTokenSummaryVo {
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
    public static class ModelUsageStatsVo {
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
    public static class DailyTokenSummaryVo {
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
    public static class TokenUsageTrendVo {
        private LocalDate usageDate;
        private Long requestCount;
        private Long totalTokens;
        private BigDecimal totalCost;
        private Long activeUsers;
    }

    @Data
    public static class UserRankingVo {
        private String userId;
        private String tenant;
        private Long requestCount;
        private Long totalTokens;
        private BigDecimal totalCost;
        private BigDecimal avgResponseTime;
    }
}