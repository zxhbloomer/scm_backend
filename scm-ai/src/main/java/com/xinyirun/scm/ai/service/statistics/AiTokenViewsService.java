package com.xinyirun.scm.ai.service.statistics;

import com.xinyirun.scm.ai.bean.vo.statistics.*;
import com.xinyirun.scm.ai.mapper.statistics.AiTokenViewsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI Token统计视图查询服务
 * 提供基于数据库的统计查询业务逻辑
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Service
@Slf4j
public class AiTokenViewsService {

    @Resource
    private AiTokenViewsMapper aiTokenViewsMapper;

    /**
     * 查询用户Token使用汇总
     */
    public List<UserTokenSummaryVo> getUserTokenSummary(String userId, Integer limit) {
        try {
            List<Map<String, Object>> results = aiTokenViewsMapper.selectUserTokenSummary(userId, limit);
            return results.stream()
                    .map(this::mapToUserTokenSummary)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户Token使用汇总失败, userId: {}, limit: {}", userId, limit, e);
            throw new RuntimeException("查询用户Token使用汇总失败", e);
        }
    }

    /**
     * 查询模型使用统计
     */
    public List<ModelUsageStatsVo> getModelUsageStats(String aiProvider, Integer limit) {
        try {
            List<Map<String, Object>> results = aiTokenViewsMapper.selectModelUsageStats(aiProvider, limit);
            return results.stream()
                    .map(this::mapToModelUsageStats)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询模型使用统计失败, aiProvider: {}, limit: {}", aiProvider, limit, e);
            throw new RuntimeException("查询模型使用统计失败", e);
        }
    }

    /**
     * 查询Token使用趋势
     */
    public List<TokenUsageTrendVo> getTokenUsageTrend(LocalDate startDate, LocalDate endDate) {
        try {
            List<Map<String, Object>> results = aiTokenViewsMapper.selectTokenUsageTrend(startDate, endDate);
            return results.stream()
                    .map(this::mapToTokenUsageTrend)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询Token使用趋势失败, startDate: {}, endDate: {}", startDate, endDate, e);
            throw new RuntimeException("查询Token使用趋势失败", e);
        }
    }

    /**
     * 查询用户排行榜
     */
    public List<UserRankingVo> getUserRanking(LocalDate startDate, LocalDate endDate, Integer topN) {
        try {
            List<Map<String, Object>> results = aiTokenViewsMapper.selectUserRanking(startDate, endDate, topN);
            return results.stream()
                    .map(this::mapToUserRanking)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户排行榜失败, startDate: {}, endDate: {}, topN: {}", startDate, endDate, topN, e);
            throw new RuntimeException("查询用户排行榜失败", e);
        }
    }

    /**
     * 查询日期统计汇总
     */
    public List<DailyTokenSummaryVo> getDailyTokenSummary(LocalDate startDate, LocalDate endDate, Integer limit) {
        try {
            List<Map<String, Object>> results = aiTokenViewsMapper.selectDailyTokenSummary(startDate, endDate, limit);
            return results.stream()
                    .map(this::mapToDailyTokenSummary)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询日期统计汇总失败, startDate: {}, endDate: {}, limit: {}", startDate, endDate, limit, e);
            throw new RuntimeException("查询日期统计汇总失败", e);
        }
    }

    // 映射方法
    private UserTokenSummaryVo mapToUserTokenSummary(Map<String, Object> row) {
        UserTokenSummaryVo vo = new UserTokenSummaryVo();
        vo.setUser_id((String) row.get("user_id"));
        vo.setTotal_requests(((Number) row.get("total_requests")).longValue());
        vo.setSuccess_requests(((Number) row.get("success_requests")).longValue());
        vo.setTotal_tokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotal_cost((BigDecimal) row.get("total_cost"));
        vo.setAvg_response_time((BigDecimal) row.get("avg_response_time"));
        vo.setFirst_usage_time((LocalDateTime) row.get("first_usage_time"));
        vo.setLast_usage_time((LocalDateTime) row.get("last_usage_time"));
        return vo;
    }

    private ModelUsageStatsVo mapToModelUsageStats(Map<String, Object> row) {
        ModelUsageStatsVo vo = new ModelUsageStatsVo();
        vo.setAi_provider((String) row.get("ai_provider"));
        vo.setAi_model_type((String) row.get("ai_model_type"));
        vo.setTotal_requests(((Number) row.get("total_requests")).longValue());
        vo.setSuccess_requests(((Number) row.get("success_requests")).longValue());
        vo.setTotal_tokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotal_cost((BigDecimal) row.get("total_cost"));
        vo.setAvg_response_time((BigDecimal) row.get("avg_response_time"));
        vo.setUnique_users(((Number) row.get("unique_users")).longValue());
        return vo;
    }

    private DailyTokenSummaryVo mapToDailyTokenSummary(Map<String, Object> row) {
        DailyTokenSummaryVo vo = new DailyTokenSummaryVo();
        vo.setUsage_date((LocalDate) row.get("usage_date"));
        vo.setTotal_requests(((Number) row.get("total_requests")).longValue());
        vo.setSuccess_requests(((Number) row.get("success_requests")).longValue());
        vo.setTotal_tokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotal_cost((BigDecimal) row.get("total_cost"));
        vo.setUnique_users(((Number) row.get("unique_users")).longValue());
        vo.setUnique_models(((Number) row.get("unique_models")).longValue());
        vo.setAvg_response_time((BigDecimal) row.get("avg_response_time"));
        return vo;
    }

    private TokenUsageTrendVo mapToTokenUsageTrend(Map<String, Object> row) {
        TokenUsageTrendVo vo = new TokenUsageTrendVo();
        vo.setUsage_date((LocalDate) row.get("usage_date"));
        vo.setRequest_count(((Number) row.get("request_count")).longValue());
        vo.setTotal_tokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotal_cost((BigDecimal) row.get("total_cost"));
        vo.setActive_users(((Number) row.get("active_users")).longValue());
        return vo;
    }

    private UserRankingVo mapToUserRanking(Map<String, Object> row) {
        UserRankingVo vo = new UserRankingVo();
        vo.setUser_id((String) row.get("user_id"));
        vo.setRequest_count(((Number) row.get("request_count")).longValue());
        vo.setTotal_tokens(((Number) row.get("total_tokens")).longValue());
        vo.setTotal_cost((BigDecimal) row.get("total_cost"));
        vo.setAvg_response_time((BigDecimal) row.get("avg_response_time"));
        return vo;
    }

}