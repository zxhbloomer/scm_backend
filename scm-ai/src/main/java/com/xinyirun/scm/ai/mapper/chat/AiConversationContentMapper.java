package com.xinyirun.scm.ai.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI会话内容表 Mapper接口
 *
 * @author AI重构工具
 * @since 1.0.0
 */
@Repository
@Mapper
public interface AiConversationContentMapper extends BaseMapper<AiConversationContentEntity> {

    /**
     * 批量插入会话内容记录
     */
    @Insert("<script>" +
            "INSERT INTO ai_conversation_content (id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, content, model_source_id) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.id}, #{item.conversation_id}, #{item.type}, #{item.c_time}, #{item.u_time}, #{item.c_id}, #{item.u_id}, #{item.dbversion}, #{item.content}, #{item.model_source_id})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<AiConversationContentEntity> list);

    /**
     * 根据会话ID查询内容列表
     */
    @Select("SELECT id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, content, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY c_time ASC")
    List<AiConversationContentEntity> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID查询内容列表(不包含BLOB内容，用于列表显示)
     */
    @Select("SELECT id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY c_time ASC")
    List<AiConversationContentEntity> selectByConversationIdWithoutBlob(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID和类型查询内容
     */
    @Select("SELECT id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, content, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} AND type = #{type} " +
            "ORDER BY c_time ASC")
    List<AiConversationContentEntity> selectByConversationIdAndType(@Param("conversationId") String conversationId,
                                                                   @Param("type") String type);

    /**
     * 根据模型源ID查询内容列表
     */
    @Select("SELECT id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, content, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE model_source_id = #{modelSourceId} " +
            "ORDER BY c_time DESC")
    List<AiConversationContentEntity> selectByModelSourceId(@Param("modelSourceId") String modelSourceId);

    /**
     * 统计会话内容数量
     */
    @Select("SELECT COUNT(*) FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId}")
    long countByConversationId(@Param("conversationId") String conversationId);

    /**
     * 查询最新的会话内容
     */
    @Select("SELECT id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, content, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY c_time DESC " +
            "LIMIT 1")
    AiConversationContentEntity selectLatestByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据时间范围查询内容
     */
    @Select("SELECT id, conversation_id, type, c_time, u_time, c_id, u_id, dbversion, content, model_source_id " +
            "FROM ai_conversation_content " +
            "WHERE c_time >= #{startTime} AND c_time <= #{endTime} " +
            "ORDER BY c_time DESC")
    List<AiConversationContentEntity> selectByTimeRange(@Param("startTime") java.time.LocalDateTime startTime,
                                                       @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 删除指定会话的所有内容
     */
    @Delete("DELETE FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 删除指定时间之前的内容
     */
    @Delete("DELETE FROM ai_conversation_content " +
            "WHERE c_time < #{beforeTime}")
    int deleteByCreateTimeBefore(@Param("beforeTime") java.time.LocalDateTime beforeTime);
}