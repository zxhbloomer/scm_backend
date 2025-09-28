package com.xinyirun.scm.ai.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI会话表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiConversationMapper extends BaseMapper<AiConversationEntity> {

    /**
     * 批量插入会话记录
     */
    @Insert("<script>" +
            "INSERT INTO ai_conversation (id, title, create_time, create_user, tenant, ai_config_id) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.title}, #{item.createTime}, #{item.createUser}, #{item.tenant}, #{item.aiConfigId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<AiConversationEntity> list);

    /**
     * 根据用户查询会话列表
     */
    @Select("SELECT id, title, create_time, create_user, tenant, ai_config_id " +
            "FROM ai_conversation " +
            "WHERE create_user = #{createUser} AND tenant = #{tenant} " +
            "ORDER BY create_time DESC")
    List<AiConversationEntity> selectByCreateUser(@Param("createUser") String createUser, @Param("tenant") String tenant);

    /**
     * 根据租户查询会话列表
     */
    @Select("SELECT id, title, create_time, create_user, tenant, ai_config_id " +
            "FROM ai_conversation " +
            "WHERE tenant = #{tenant} " +
            "ORDER BY create_time DESC")
    List<AiConversationEntity> selectByTenant(@Param("tenant") String tenant);

    /**
     * 根据标题模糊查询会话
     */
    @Select("SELECT id, title, create_time, create_user, tenant, ai_config_id " +
            "FROM ai_conversation " +
            "WHERE title LIKE CONCAT('%', #{title}, '%') AND tenant = #{tenant} " +
            "ORDER BY create_time DESC")
    List<AiConversationEntity> selectByTitleLike(@Param("title") String title, @Param("tenant") String tenant);

    /**
     * 根据用户和时间范围查询会话
     */
    @Select("SELECT id, title, create_time, create_user, tenant, ai_config_id " +
            "FROM ai_conversation " +
            "WHERE create_user = #{createUser} AND tenant = #{tenant} " +
            "AND create_time >= #{startTime} AND create_time <= #{endTime} " +
            "ORDER BY create_time DESC")
    List<AiConversationEntity> selectByUserAndTimeRange(@Param("createUser") String createUser,
                                                       @Param("tenant") String tenant,
                                                       @Param("startTime") Long startTime,
                                                       @Param("endTime") Long endTime);

    /**
     * 统计用户会话数量
     */
    @Select("SELECT COUNT(*) FROM ai_conversation " +
            "WHERE create_user = #{createUser} AND tenant = #{tenant}")
    long countByUser(@Param("createUser") String createUser, @Param("tenant") String tenant);

    /**
     * 删除指定时间之前的会话
     */
    @Delete("DELETE FROM ai_conversation " +
            "WHERE create_time < #{beforeTime} AND tenant = #{tenant}")
    int deleteByCreateTimeBefore(@Param("beforeTime") Long beforeTime, @Param("tenant") String tenant);
}