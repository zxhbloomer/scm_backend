package com.xinyirun.scm.ai.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
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
            "WHERE user_id = #{userId} " +
            "AND DATE(c_time) = CURDATE()")
    Long getTodayTokenUsageByUser(@Param("userId") String userId);

    /**
     * 查询用户本月Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} " +
            "AND DATE_FORMAT(c_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long getMonthlyTokenUsageByUser(@Param("userId") String userId);

    /**
     * 查询对话Token使用统计
     */
    @Select("SELECT id, conversation_id, user_id, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, usage_time, c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_token_usage " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY c_time DESC")
    List<AiTokenUsageEntity> selectByConversationId(@Param("conversationId") Integer conversationId);

    /**
     * 批量插入Token使用记录
     */
    @Insert("<script>" +
            "INSERT INTO ai_token_usage (id, conversation_id, user_id, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, usage_time, c_time, u_time, c_id, u_id, dbversion) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.conversation_id}, #{item.user_id}, #{item.model_source_id}, " +
            "#{item.prompt_tokens}, #{item.completion_tokens}, #{item.total_tokens}, #{item.usage_time}, " +
            "#{item.c_time}, #{item.u_time}, #{item.c_id}, #{item.u_id}, #{item.dbversion})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<AiTokenUsageEntity> list);

    /**
     * 根据模型源查询Token使用记录
     */
    @Select("SELECT id, conversation_id, user_id, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, usage_time, c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_token_usage " +
            "WHERE model_source_id = #{modelSourceId} " +
            "ORDER BY c_time DESC")
    List<AiTokenUsageEntity> selectByModelSourceId(@Param("modelSourceId") Integer modelSourceId);

    /**
     * 查询用户指定时间范围内的Token使用记录
     */
    @Select("SELECT id, conversation_id, user_id, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, usage_time, c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} " +
            "AND c_time >= #{startTime} AND c_time <= #{endTime} " +
            "ORDER BY c_time DESC")
    List<AiTokenUsageEntity> selectByUserAndTimeRange(@Param("userId") String userId,
                                                     @Param("startTime") java.time.LocalDateTime startTime,
                                                     @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 统计租户今日Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE DATE(c_time) = CURDATE()")
    Long getTodayTokenUsageByTenant();

    /**
     * 统计租户本月Token使用量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE DATE_FORMAT(c_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long getMonthlyTokenUsageByTenant();

    /**
     * 查询高使用量用户排行
     */
    @Select("SELECT user_id, " +
            "COALESCE(SUM(prompt_tokens + completion_tokens), 0) as total_tokens " +
            "FROM ai_token_usage " +
            "WHERE DATE_FORMAT(c_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m') " +
            "GROUP BY user_id " +
            "ORDER BY total_tokens DESC " +
            "LIMIT #{limit}")
    List<AiTokenUsageEntity> selectTopUsageUsers(@Param("limit") Integer limit);

    /**
     * 删除指定时间之前的记录
     */
    @Delete("DELETE FROM ai_token_usage " +
            "WHERE c_time < #{beforeTime}")
    int deleteByCreateTimeBefore(@Param("beforeTime") java.time.LocalDateTime beforeTime);

    /**
     * 根据用户和租户查询Token使用记录
     */
    @Select("SELECT id, conversation_id, user_id, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, usage_time, c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId} " +
            "ORDER BY c_time DESC")
    List<AiTokenUsageEntity> selectByUserAndTenant(@Param("userId") String userId);

    /**
     * 统计用户Token使用总量
     */
    @Select("SELECT COALESCE(SUM(prompt_tokens + completion_tokens), 0) " +
            "FROM ai_token_usage " +
            "WHERE user_id = #{userId}")
    Long getTotalTokenUsageByUser(@Param("userId") String userId);

    /**
     * 根据使用时间查询Token使用记录
     */
    @Select("SELECT id, conversation_id, user_id, model_source_id, prompt_tokens, completion_tokens, " +
            "total_tokens, usage_time, c_time, u_time, c_id, u_id, dbversion " +
            "FROM ai_token_usage " +
            "WHERE usage_time >= #{startTime} AND usage_time <= #{endTime} " +
            "ORDER BY usage_time DESC")
    List<AiTokenUsageEntity> selectByUsageTimeRange(@Param("startTime") java.time.LocalDateTime startTime,
                                                   @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 统计模型源使用排行
     */
    @Select("SELECT model_source_id, " +
            "COUNT(*) as usage_count, " +
            "COALESCE(SUM(prompt_tokens + completion_tokens), 0) as total_tokens " +
            "FROM ai_token_usage " +
            "WHERE c_time >= #{startTime} AND c_time <= #{endTime} " +
            "GROUP BY model_source_id " +
            "ORDER BY total_tokens DESC " +
            "LIMIT #{limit}")
    List<AiTokenUsageEntity> selectModelUsageRanking(@Param("startTime") java.time.LocalDateTime startTime,
                                                    @Param("endTime") java.time.LocalDateTime endTime,
                                                    @Param("limit") Integer limit);
}