package com.xinyirun.scm.ai.mapper.statistics;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI Token统计视图查询Mapper接口
 * 提供基于数据库的统计查询功能
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Repository
@Mapper
public interface AiTokenViewsMapper {

    /**
     * 查询用户Token使用汇总
     */
    @Select("""
    <script>
        SELECT
            user_id,
            COUNT(*) as total_requests,
            SUM(CASE WHEN success = 1 THEN 1 ELSE 0 END) as success_requests,
            SUM(total_tokens) as total_tokens,
            SUM(cost) as total_cost,
            AVG(response_time) as avg_response_time,
            MIN(c_time) as first_usage_time,
            MAX(c_time) as last_usage_time
        FROM ai_token_usage
        WHERE 1=1
        <if test='userId != null and userId != &quot;&quot;'>
            AND user_id = #{userId}
        </if>
        GROUP BY user_id
        ORDER BY total_tokens DESC
        LIMIT #{limit}
    </script>
    """)
    List<Map<String, Object>> selectUserTokenSummary(@Param("userId") String userId,
                                                     @Param("limit") Integer limit);

    /**
     * 查询模型使用统计
     */
    @Select("""
    <script>
        SELECT
            ai_provider,
            ai_model_type,
            COUNT(*) as total_requests,
            SUM(CASE WHEN success = 1 THEN 1 ELSE 0 END) as success_requests,
            SUM(total_tokens) as total_tokens,
            SUM(cost) as total_cost,
            AVG(response_time) as avg_response_time,
            COUNT(DISTINCT user_id) as unique_users
        FROM ai_token_usage
        WHERE 1=1
        <if test='aiProvider != null and aiProvider != &quot;&quot;'>
            AND ai_provider = #{aiProvider}
        </if>
        GROUP BY ai_provider, ai_model_type
        ORDER BY total_tokens DESC
        LIMIT #{limit}
    </script>
    """)
    List<Map<String, Object>> selectModelUsageStats(@Param("aiProvider") String aiProvider,
                                                    @Param("limit") Integer limit);

    /**
     * 查询Token使用趋势
     */
    @Select("""
        SELECT
            DATE(c_time) as usage_date,
            COUNT(*) as request_count,
            SUM(total_tokens) as total_tokens,
            SUM(cost) as total_cost,
            COUNT(DISTINCT user_id) as active_users
        FROM ai_token_usage
        WHERE DATE(c_time) BETWEEN #{startDate} AND #{endDate}
        GROUP BY DATE(c_time)
        ORDER BY usage_date
        """)
    List<Map<String, Object>> selectTokenUsageTrend(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * 查询用户排行榜
     */
    @Select("""
    <script>
        SELECT
            user_id,
            COUNT(*) as request_count,
            SUM(total_tokens) as total_tokens,
            SUM(cost) as total_cost,
            AVG(response_time) as avg_response_time
        FROM ai_token_usage
        WHERE 1=1
        <if test='startDate != null'>
            AND DATE(c_time) &gt;= #{startDate}
        </if>
        <if test='endDate != null'>
            AND DATE(c_time) &lt;= #{endDate}
        </if>
        GROUP BY user_id
        ORDER BY total_tokens DESC
        LIMIT #{topN}
    </script>
    """)
    List<Map<String, Object>> selectUserRanking(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("topN") Integer topN);

    /**
     * 查询日期统计汇总
     */
    @Select("""
    <script>
        SELECT
            DATE(c_time) as usage_date,
            COUNT(*) as total_requests,
            SUM(CASE WHEN success = 1 THEN 1 ELSE 0 END) as success_requests,
            SUM(total_tokens) as total_tokens,
            SUM(cost) as total_cost,
            COUNT(DISTINCT user_id) as unique_users,
            COUNT(DISTINCT ai_model_type) as unique_models,
            AVG(response_time) as avg_response_time
        FROM ai_token_usage
        WHERE 1=1
        <if test='startDate != null'>
            AND DATE(c_time) &gt;= #{startDate}
        </if>
        <if test='endDate != null'>
            AND DATE(c_time) &lt;= #{endDate}
        </if>
        GROUP BY DATE(c_time)
        ORDER BY usage_date DESC
        LIMIT #{limit}
    </script>
    """)
    List<Map<String, Object>> selectDailyTokenSummary(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate,
                                                      @Param("limit") Integer limit);
}