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
    @Insert("<script>" +
            "INSERT INTO ai_token_statistics (id, statistics_date, user_id, tenant, model_source_id, " +
            "total_prompt_tokens, total_completion_tokens, total_tokens, total_cost, conversation_count, create_time) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.statisticsDate}, #{item.userId}, #{item.tenant}, #{item.modelSourceId}, " +
            "#{item.totalInputTokens}, #{item.totalOutputTokens}, #{item.totalTokens}, #{item.totalCost}, " +
            "#{item.conversationCount}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<AiTokenStatisticsEntity> list);

    /**
     * 根据统计日期查询记录
     */
    @Select("SELECT id, statistics_date, user_id, tenant, model_source_id, total_prompt_tokens, total_completion_tokens, " +
            "total_tokens, total_cost, conversation_count, create_time " +
            "FROM ai_token_statistics " +
            "WHERE statistics_date = #{statisticsDate} " +
            "ORDER BY create_time DESC")
    List<AiTokenStatisticsEntity> selectByStatisticsDate(@Param("statisticsDate") String statisticsDate);

    /**
     * 根据用户和日期查询统计
     */
    @Select("SELECT id, statistics_date, user_id, tenant, model_source_id, total_prompt_tokens, total_completion_tokens, " +
            "total_tokens, total_cost, conversation_count, create_time " +
            "FROM ai_token_statistics " +
            "WHERE user_id = #{userId} AND tenant = #{tenant} AND statistics_date = #{statisticsDate}")
    List<AiTokenStatisticsEntity> selectByUserAndDate(@Param("userId") String userId,
                                                     @Param("tenant") String tenant,
                                                     @Param("statisticsDate") String statisticsDate);

    /**
     * 根据租户和日期范围查询统计
     */
    @Select("SELECT id, statistics_date, user_id, tenant, model_source_id, total_prompt_tokens, total_completion_tokens, " +
            "total_tokens, total_cost, conversation_count, create_time " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} " +
            "AND statistics_date >= #{startDate} AND statistics_date <= #{endDate} " +
            "ORDER BY statistics_date DESC")
    List<AiTokenStatisticsEntity> selectByTenantAndDateRange(@Param("tenant") String tenant,
                                                            @Param("startDate") String startDate,
                                                            @Param("endDate") String endDate);

    /**
     * 根据模型源查询统计
     */
    @Select("SELECT id, statistics_date, user_id, tenant, model_source_id, total_prompt_tokens, total_completion_tokens, " +
            "total_tokens, total_cost, conversation_count, create_time " +
            "FROM ai_token_statistics " +
            "WHERE model_source_id = #{modelSourceId} " +
            "ORDER BY statistics_date DESC")
    List<AiTokenStatisticsEntity> selectByModelSourceId(@Param("modelSourceId") String modelSourceId);

    /**
     * 统计租户指定日期的Token总量
     */
    @Select("SELECT COALESCE(SUM(total_tokens), 0) " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} AND statistics_date = #{statisticsDate}")
    Long sumTokensByTenantAndDate(@Param("tenant") String tenant, @Param("statisticsDate") String statisticsDate);

    /**
     * 统计用户指定日期的Token总量
     */
    @Select("SELECT COALESCE(SUM(total_tokens), 0) " +
            "FROM ai_token_statistics " +
            "WHERE user_id = #{userId} AND tenant = #{tenant} AND statistics_date = #{statisticsDate}")
    Long sumTokensByUserAndDate(@Param("userId") String userId,
                               @Param("tenant") String tenant,
                               @Param("statisticsDate") String statisticsDate);

    /**
     * 查询用户月度统计排行
     */
    @Select("SELECT user_id, tenant, " +
            "SUM(total_prompt_tokens) as total_prompt_tokens, " +
            "SUM(total_completion_tokens) as total_completion_tokens, " +
            "SUM(total_tokens) as total_tokens, " +
            "SUM(total_cost) as total_cost, " +
            "SUM(conversation_count) as conversation_count " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} " +
            "AND statistics_date LIKE CONCAT(#{yearMonth}, '%') " +
            "GROUP BY user_id, tenant " +
            "ORDER BY total_tokens DESC " +
            "LIMIT #{limit}")
    List<AiTokenStatisticsEntity> selectMonthlyUserRanking(@Param("tenant") String tenant,
                                                          @Param("yearMonth") String yearMonth,
                                                          @Param("limit") Integer limit);

    /**
     * 查询模型源使用排行
     */
    @Select("SELECT model_source_id, " +
            "SUM(total_prompt_tokens) as total_prompt_tokens, " +
            "SUM(total_completion_tokens) as total_completion_tokens, " +
            "SUM(total_tokens) as total_tokens, " +
            "SUM(total_cost) as total_cost, " +
            "SUM(conversation_count) as conversation_count " +
            "FROM ai_token_statistics " +
            "WHERE tenant = #{tenant} " +
            "AND statistics_date >= #{startDate} AND statistics_date <= #{endDate} " +
            "GROUP BY model_source_id " +
            "ORDER BY total_tokens DESC " +
            "LIMIT #{limit}")
    List<AiTokenStatisticsEntity> selectModelUsageRanking(@Param("tenant") String tenant,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate,
                                                         @Param("limit") Integer limit);

    /**
     * 删除指定日期之前的统计记录
     */
    @Delete("DELETE FROM ai_token_statistics " +
            "WHERE statistics_date < #{beforeDate}")
    int deleteByStatisticsDateBefore(@Param("beforeDate") String beforeDate);
}