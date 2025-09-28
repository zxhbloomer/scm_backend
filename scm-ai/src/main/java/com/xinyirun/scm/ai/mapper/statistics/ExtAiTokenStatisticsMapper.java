package com.xinyirun.scm.ai.mapper.statistics;

import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenStatisticsEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI Token统计扩展 Mapper接口
 *
 * 提供复杂的Token统计查询和分析功能
 * 包含聚合统计、趋势分析等高级查询
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Repository
@Mapper
public interface ExtAiTokenStatisticsMapper {

    /**
     * 按日期范围统计用户Token使用情况
     */
    @Select("SELECT " +
            "user_id, " +
            "DATE(statistics_date) as stat_date, " +
            "SUM(total_prompt_tokens) as total_input_tokens, " +
            "SUM(total_completion_tokens) as total_output_tokens, " +
            "SUM(total_tokens) as total_tokens " +
            "FROM ai_token_statistics " +
            "WHERE user_id = #{userId} AND tenant = #{tenant} " +
            "AND statistics_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY user_id, DATE(statistics_date) " +
            "ORDER BY stat_date DESC")
    List<Map<String, Object>> selectUserDailyStatistics(@Param("userId") String userId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate,
                                                        @Param("tenant") String tenant);

    /**
     * 按模型统计Token使用排行榜
     */
    @Select("SELECT " +
            "ats.model_source_id, " +
            "ams.model_name, " +
            "ams.provider, " +
            "SUM(ats.total_prompt_tokens) as total_input_tokens, " +
            "SUM(ats.total_completion_tokens) as total_output_tokens, " +
            "SUM(ats.total_tokens) as total_tokens, " +
            "COUNT(DISTINCT ats.user_id) as unique_users " +
            "FROM ai_token_statistics ats " +
            "LEFT JOIN ai_model_source ams ON ats.model_source_id = ams.id " +
            "WHERE ats.tenant = #{tenant} " +
            "AND ats.statistics_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY ats.model_source_id, ams.model_name, ams.provider " +
            "ORDER BY total_tokens DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectModelRanking(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate,
                                                 @Param("tenant") String tenant,
                                                 @Param("limit") Integer limit);

    /**
     * 按小时统计Token使用趋势
     */
    @Select("SELECT " +
            "DATE_FORMAT(statistics_date, '%Y-%m-%d %H:00:00') as stat_hour, " +
            "SUM(total_prompt_tokens) as total_input_tokens, " +
            "SUM(total_completion_tokens) as total_output_tokens, " +
            "SUM(total_tokens) as total_tokens, " +
            "COUNT(DISTINCT user_id) as unique_users " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} " +
            "AND statistics_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(statistics_date, '%Y-%m-%d %H:00:00') " +
            "ORDER BY stat_hour")
    List<Map<String, Object>> selectHourlyTrend(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("tenant") String tenant);

    /**
     * 用户Token使用TOP排行榜
     */
    @Select("SELECT " +
            "user_id, " +
            "SUM(total_prompt_tokens) as total_input_tokens, " +
            "SUM(total_completion_tokens) as total_output_tokens, " +
            "SUM(total_tokens) as total_tokens, " +
            "AVG(total_tokens) as avg_tokens_per_session, " +
            "COUNT(*) as session_count " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} " +
            "AND statistics_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY user_id " +
            "ORDER BY total_tokens DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectUserRanking(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("tenant") String tenant,
                                               @Param("limit") Integer limit);

    /**
     * 获取指定日期的聚合统计
     */
    @Select("SELECT " +
            "COUNT(DISTINCT user_id) as unique_users, " +
            "COUNT(DISTINCT model_source_id) as unique_models, " +
            "SUM(total_prompt_tokens) as total_input_tokens, " +
            "SUM(total_completion_tokens) as total_output_tokens, " +
            "SUM(total_tokens) as total_tokens, " +
            "AVG(total_tokens) as avg_tokens_per_session, " +
            "MAX(total_tokens) as max_tokens_in_session, " +
            "MIN(total_tokens) as min_tokens_in_session " +
            "FROM ai_token_statistics " +
            "WHERE DATE(statistics_date) = DATE(#{statisticsDate}) " +
            "AND tenant = #{tenant}")
    Map<String, Object> selectDailySummary(@Param("statisticsDate") LocalDateTime statisticsDate,
                                          @Param("tenant") String tenant);

    /**
     * 批量聚合统计数据（用于数据合并和清理）
     */
    @Select("SELECT " +
            "user_id, " +
            "model_source_id, " +
            "DATE(statistics_date) as stat_date, " +
            "SUM(total_prompt_tokens) as total_input_tokens, " +
            "SUM(total_completion_tokens) as total_output_tokens, " +
            "SUM(total_tokens) as total_tokens " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} " +
            "AND DATE(statistics_date) = DATE(#{statisticsDate}) " +
            "GROUP BY user_id, model_source_id, DATE(statistics_date)")
    List<Map<String, Object>> selectDailyAggregation(@Param("statisticsDate") LocalDateTime statisticsDate,
                                                     @Param("tenant") String tenant);

    /**
     * 删除指定日期之前的统计数据（数据清理）
     */
    @Delete("DELETE FROM ai_token_statistics " +
            "WHERE statistics_date < #{beforeDate} " +
            "AND tenant = #{tenant}")
    int deleteStatisticsBeforeDate(@Param("beforeDate") LocalDateTime beforeDate,
                                  @Param("tenant") String tenant);
}