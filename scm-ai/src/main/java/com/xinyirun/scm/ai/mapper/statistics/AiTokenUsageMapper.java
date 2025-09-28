package com.xinyirun.scm.ai.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * AI Token使用记录表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiTokenUsageMapper extends BaseMapper<AiTokenUsageEntity> {

    /**
     * 查询用户今日Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} AND tenant = #{tenant} " +
            "AND DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE()")
    Long getTodayTokenUsageByUser(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 查询用户本月Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} AND tenant = #{tenant} " +
            "AND DATE_FORMAT(FROM_UNIXTIME(create_time/1000), '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long getMonthlyTokenUsageByUser(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 查询用户累计费用
     */
    @Select("SELECT COALESCE(SUM(cost), 0) " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} AND tenant = #{tenant}")
    BigDecimal getTotalCostByUser(@Param("userId") String userId, @Param("tenant") String tenant);

    /**
     * 查询对话Token使用统计
     */
    @Select("SELECT id, conversation_id, user_id, tenant, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, cost, create_time " +
            "FROM ai_token_usage " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY create_time DESC")
    List<AiTokenUsageEntity> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 批量插入Token使用记录
     */
    @Insert("<script>" +
            "INSERT INTO ai_token_usage (id, conversation_id, user_id, tenant, model_source_id, prompt_tokens, completion_tokens, total_tokens, cost, create_time) " +
            "VALUES " +
            "<foreach collection='records' item='record' separator=','>" +
            "(#{record.id}, #{record.conversationId}, #{record.userId}, #{record.tenant}, #{record.modelSourceId}, " +
            "#{record.inputTokens}, #{record.outputTokens}, #{record.totalTokens}, #{record.cost}, #{record.createTime})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("records") List<AiTokenUsageEntity> records);

    /**
     * 根据模型源查询Token使用记录
     */
    @Select("SELECT id, conversation_id, user_id, tenant, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, cost, create_time " +
            "FROM ai_token_usage " +
            "WHERE model_source_id = #{modelSourceId} " +
            "ORDER BY create_time DESC")
    List<AiTokenUsageEntity> selectByModelSourceId(@Param("modelSourceId") String modelSourceId);

    /**
     * 查询用户指定时间范围内的Token使用记录
     */
    @Select("SELECT id, conversation_id, user_id, tenant, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, cost, create_time " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} AND tenant = #{tenant} " +
            "AND create_time >= #{startTime} AND create_time <= #{endTime} " +
            "ORDER BY create_time DESC")
    List<AiTokenUsageEntity> selectByUserAndTimeRange(@Param("userId") String userId,
                                                     @Param("tenant") String tenant,
                                                     @Param("startTime") Long startTime,
                                                     @Param("endTime") Long endTime);

    /**
     * 统计租户今日Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE tenant = #{tenant} " +
            "AND DATE(FROM_UNIXTIME(create_time/1000)) = CURDATE()")
    Long getTodayTokenUsageByTenant(@Param("tenant") String tenant);

    /**
     * 统计租户本月Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE tenant = #{tenant} " +
            "AND DATE_FORMAT(FROM_UNIXTIME(create_time/1000), '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long getMonthlyTokenUsageByTenant(@Param("tenant") String tenant);

    /**
     * 查询高消费用户排行
     */
    @Select("SELECT user_id, COALESCE(SUM(cost), 0) as total_cost " +
            "FROM ai_token_usage " +
            "WHERE tenant = #{tenant} " +
            "AND DATE_FORMAT(FROM_UNIXTIME(create_time/1000), '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m') " +
            "GROUP BY user_id " +
            "ORDER BY total_cost DESC " +
            "LIMIT #{limit}")
    List<AiTokenUsageEntity> selectTopCostUsers(@Param("tenant") String tenant, @Param("limit") Integer limit);

    /**
     * 删除指定时间之前的记录
     */
    @Delete("DELETE FROM ai_token_usage " +
            "WHERE create_time < #{beforeTime}")
    int deleteByCreateTimeBefore(@Param("beforeTime") Long beforeTime);
}