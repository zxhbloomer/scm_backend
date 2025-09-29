package com.xinyirun.scm.ai.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenStatisticsEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI Token统计表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiTokenStatisticsMapper extends BaseMapper<AiTokenStatisticsEntity> {

    /**
     * 批量插入Token统计记录
     */
    @Insert("""
    <script>
        INSERT INTO ai_token_statistics (
            id,
            user_id,
            model_source_id,
            total_prompt_tokens,
            total_completion_tokens,
            total_tokens,
            statistics_date,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        )
        VALUES
        <foreach collection='list' item='item' separator=','>
            (
                #{item.id},
                #{item.user_id},
                #{item.model_source_id},
                #{item.total_prompt_tokens},
                #{item.total_completion_tokens},
                #{item.total_tokens},
                #{item.statistics_date},
                #{item.c_time},
                #{item.u_time},
                #{item.c_id},
                #{item.u_id},
                #{item.dbversion}
            )
        </foreach>
    </script>
    """)
    int batchInsert(@Param("list") List<AiTokenStatisticsEntity> list);

    /**
     * 根据统计日期查询记录
     */
    @Select("""
        SELECT
            id,
            user_id,
            model_source_id,
            total_prompt_tokens,
            total_completion_tokens,
            total_tokens,
            statistics_date,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_token_statistics
        WHERE statistics_date = #{statisticsDate}
        ORDER BY c_time DESC
        """)
    List<AiTokenStatisticsEntity> selectByStatisticsDate(@Param("statisticsDate") java.time.LocalDateTime statisticsDate);

    /**
     * 根据用户和日期查询统计
     */
    @Select("""
        SELECT
            id,
            user_id,
            model_source_id,
            total_prompt_tokens,
            total_completion_tokens,
            total_tokens,
            statistics_date,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_token_statistics
        WHERE user_id = #{userId}
            AND DATE(statistics_date) = DATE(#{statisticsDate})
        """)
    List<AiTokenStatisticsEntity> selectByUserAndDate(@Param("userId") String userId,
                                                     @Param("statisticsDate") java.time.LocalDateTime statisticsDate);

    /**
     * 根据租户和日期范围查询统计
     */
    @Select("""
        SELECT
            id,
            user_id,
            model_source_id,
            total_prompt_tokens,
            total_completion_tokens,
            total_tokens,
            statistics_date,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_token_statistics
        WHERE statistics_date >= #{startDate}
            AND statistics_date <= #{endDate}
        ORDER BY statistics_date DESC
        """)
    List<AiTokenStatisticsEntity> selectByTenantAndDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                                                            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * 根据模型源查询统计
     */
    @Select("""
        SELECT
            id,
            user_id,
            model_source_id,
            total_prompt_tokens,
            total_completion_tokens,
            total_tokens,
            statistics_date,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_token_statistics
        WHERE model_source_id = #{modelSourceId}
        ORDER BY statistics_date DESC
        """)
    List<AiTokenStatisticsEntity> selectByModelSourceId(@Param("modelSourceId") Integer modelSourceId);

    /**
     * 统计租户指定日期的Token总量
     */
    @Select("""
        SELECT COALESCE(SUM(total_tokens), 0)
        FROM ai_token_statistics
        WHERE DATE(statistics_date) = DATE(#{statisticsDate})
        """)
    Long sumTokensByTenantAndDate(@Param("statisticsDate") java.time.LocalDateTime statisticsDate);

    /**
     * 统计用户指定日期的Token总量
     */
    @Select("""
        SELECT COALESCE(SUM(total_tokens), 0)
        FROM ai_token_statistics
        WHERE user_id = #{userId}
            AND DATE(statistics_date) = DATE(#{statisticsDate})
        """)
    Long sumTokensByUserAndDate(@Param("userId") String userId,
                               @Param("statisticsDate") java.time.LocalDateTime statisticsDate);

    /**
     * 查询用户月度统计排行
     */
    @Select("""
        SELECT
            user_id,
            SUM(total_prompt_tokens) as total_prompt_tokens,
            SUM(total_completion_tokens) as total_completion_tokens,
            SUM(total_tokens) as total_tokens
        FROM ai_token_statistics
        WHERE YEAR(statistics_date) = #{year}
            AND MONTH(statistics_date) = #{month}
        GROUP BY user_id
        ORDER BY total_tokens DESC
        LIMIT #{limit}
        """)
    List<AiTokenStatisticsEntity> selectMonthlyUserRanking(@Param("year") Integer year,
                                                          @Param("month") Integer month,
                                                          @Param("limit") Integer limit);

    /**
     * 查询模型源使用排行
     */
    @Select("""
        SELECT
            model_source_id,
            SUM(total_prompt_tokens) as total_prompt_tokens,
            SUM(total_completion_tokens) as total_completion_tokens,
            SUM(total_tokens) as total_tokens
        FROM ai_token_statistics
        WHERE statistics_date >= #{startDate}
            AND statistics_date <= #{endDate}
        GROUP BY model_source_id
        ORDER BY total_tokens DESC
        LIMIT #{limit}
        """)
    List<AiTokenStatisticsEntity> selectModelUsageRanking(@Param("startDate") java.time.LocalDateTime startDate,
                                                         @Param("endDate") java.time.LocalDateTime endDate,
                                                         @Param("limit") Integer limit);

    /**
     * 删除指定日期之前的统计记录
     */
    @Delete("""
        DELETE FROM ai_token_statistics
        WHERE statistics_date < #{beforeDate}
        """)
    int deleteByStatisticsDateBefore(@Param("beforeDate") java.time.LocalDateTime beforeDate);

    /**
     * 统计用户当日Token使用量
     */
    @Select("""
        SELECT COALESCE(SUM(total_tokens), 0)
        FROM ai_token_statistics
        WHERE user_id = #{userId}
            AND DATE(statistics_date) = CURDATE()
        """)
    Long sumDailyTokensByUser(@Param("userId") String userId);

    /**
     * 统计用户当月Token使用量
     */
    @Select("""
        SELECT COALESCE(SUM(total_tokens), 0)
        FROM ai_token_statistics
        WHERE user_id = #{userId}
            AND YEAR(statistics_date) = YEAR(CURDATE())
            AND MONTH(statistics_date) = MONTH(CURDATE())
        """)
    Long sumMonthlyTokensByUser(@Param("userId") String userId);
}