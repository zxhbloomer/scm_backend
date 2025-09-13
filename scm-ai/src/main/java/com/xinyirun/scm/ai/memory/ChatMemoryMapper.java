/**
 * 聊天记忆数据访问接口
 */
package com.xinyirun.scm.ai.memory;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface ChatMemoryMapper extends BaseMapper<ChatMemoryEntity> {

    /**
     * 根据会话ID查询聊天记忆列表，按时间戳排序
     */
    @Select("SELECT * FROM scm_ai_chat_memory WHERE conversation_id = #{conversationId} AND deleted = 0 ORDER BY timestamp ASC")
    List<ChatMemoryEntity> findByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID和消息类型查询聊天记忆列表
     */
    @Select("SELECT * FROM scm_ai_chat_memory WHERE conversation_id = #{conversationId} AND message_type = #{messageType} AND deleted = 0 ORDER BY timestamp ASC")
    List<ChatMemoryEntity> findByConversationIdAndType(@Param("conversationId") String conversationId, @Param("messageType") String messageType);

    /**
     * 获取会话的最新N条记忆
     */
    @Select("SELECT * FROM scm_ai_chat_memory WHERE conversation_id = #{conversationId} AND deleted = 0 ORDER BY timestamp DESC LIMIT #{limit}")
    List<ChatMemoryEntity> findLatestByConversationId(@Param("conversationId") String conversationId, @Param("limit") int limit);

    /**
     * 根据会话ID删除所有记忆（软删除）
     */
    @Update("UPDATE scm_ai_chat_memory SET deleted = 1, updated_at = NOW() WHERE conversation_id = #{conversationId}")
    int deleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID物理删除所有记忆
     */
    @Delete("DELETE FROM scm_ai_chat_memory WHERE conversation_id = #{conversationId}")
    int hardDeleteByConversationId(@Param("conversationId") String conversationId);

    /**
     * 统计会话记忆数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_chat_memory WHERE conversation_id = #{conversationId} AND deleted = 0")
    Long countByConversationId(@Param("conversationId") String conversationId);

    /**
     * 获取所有会话ID列表
     */
    @Select("SELECT DISTINCT conversation_id FROM scm_ai_chat_memory WHERE deleted = 0")
    List<String> findAllConversationIds();

    /**
     * 根据时间范围查询聊天记忆
     */
    @Select("SELECT * FROM scm_ai_chat_memory WHERE conversation_id = #{conversationId} " +
            "AND timestamp >= #{startTime} AND timestamp <= #{endTime} AND deleted = 0 ORDER BY timestamp ASC")
    List<ChatMemoryEntity> findByConversationIdAndTimeRange(
        @Param("conversationId") String conversationId,
        @Param("startTime") Long startTime,
        @Param("endTime") Long endTime
    );

    /**
     * 清理过期的聊天记忆（超过指定天数）
     */
    @Update("UPDATE scm_ai_chat_memory SET deleted = 1, updated_at = NOW() " +
            "WHERE timestamp < #{expireTime}")
    int deleteExpiredMemories(@Param("expireTime") Long expireTime);
}