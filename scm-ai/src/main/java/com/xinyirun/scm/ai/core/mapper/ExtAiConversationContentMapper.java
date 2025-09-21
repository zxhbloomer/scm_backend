package com.xinyirun.scm.ai.core.mapper;

import com.xinyirun.scm.ai.bean.entity.AiConversationContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI会话内容扩展Mapper接口
 *
 * 提供更复杂的查询方法，特别是用于聊天记忆功能
 * 从MeterSphere迁移而来，适配scm-ai架构
 *
 * @Author: 迁移适配
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Mapper
public interface ExtAiConversationContentMapper {

    /**
     * 根据会话ID获取最近的N条消息（用于聊天记忆）
     *
     * 这个方法是聊天记忆功能的核心，用于获取对话上下文
     * 从MeterSphere的MsMessageChatMemory.java中迁移而来
     *
     * @param conversationId 会话ID
     * @param limit 限制数量
     * @return 最近的消息列表，按时间倒序
     */
    @Select("SELECT * FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<AiConversationContent> selectLastByConversationIdByLimit(@Param("conversationId") String conversationId,
                                                                 @Param("limit") Integer limit);

    /**
     * 获取会话的消息统计信息
     *
     * @param conversationId 会话ID
     * @return 统计信息（用户消息数、AI回复数、总消息数）
     */
    @Select("SELECT " +
            "  COUNT(CASE WHEN type = 'USER' THEN 1 END) as user_count, " +
            "  COUNT(CASE WHEN type = 'AI' THEN 1 END) as ai_count, " +
            "  COUNT(*) as total_count, " +
            "  MIN(create_time) as first_message_time, " +
            "  MAX(create_time) as last_message_time " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId}")
    ConversationStats getConversationStats(@Param("conversationId") String conversationId);

    /**
     * 获取用户在指定时间段内的消息统计
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户消息统计
     */
    @Select("SELECT " +
            "  COUNT(CASE WHEN acc.type = 'USER' THEN 1 END) as user_message_count, " +
            "  COUNT(CASE WHEN acc.type = 'AI' THEN 1 END) as ai_message_count, " +
            "  COUNT(DISTINCT acc.conversation_id) as conversation_count, " +
            "  COUNT(*) as total_message_count " +
            "FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.create_user = #{userId} " +
            "  AND acc.create_time BETWEEN #{startTime} AND #{endTime}")
    UserMessageStats getUserMessageStats(@Param("userId") String userId,
                                         @Param("startTime") Long startTime,
                                         @Param("endTime") Long endTime);

    /**
     * 获取会话中的关键词出现频率
     *
     * @param conversationId 会话ID
     * @param keywords 关键词列表（逗号分隔）
     * @return 关键词统计列表
     */
    @Select("SELECT " +
            "  #{keyword} as keyword, " +
            "  COUNT(*) as count " +
            "FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "  AND content LIKE CONCAT('%', #{keyword}, '%')")
    List<KeywordCount> getKeywordFrequency(@Param("conversationId") String conversationId,
                                          @Param("keyword") String keyword);

    /**
     * 根据消息长度范围查询内容
     *
     * @param conversationId 会话ID
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 符合长度要求的消息列表
     */
    @Select("SELECT * FROM ai_conversation_content " +
            "WHERE conversation_id = #{conversationId} " +
            "  AND CHAR_LENGTH(content) BETWEEN #{minLength} AND #{maxLength} " +
            "ORDER BY create_time ASC")
    List<AiConversationContent> selectByContentLength(@Param("conversationId") String conversationId,
                                                     @Param("minLength") Integer minLength,
                                                     @Param("maxLength") Integer maxLength);

    /**
     * 获取用户最活跃的时间段统计
     *
     * @param userId 用户ID
     * @param days 统计天数
     * @return 时间段活跃度统计
     */
    @Select("SELECT " +
            "  HOUR(FROM_UNIXTIME(acc.create_time/1000)) as hour_of_day, " +
            "  COUNT(*) as message_count " +
            "FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.create_user = #{userId} " +
            "  AND acc.create_time >= #{startTime} " +
            "GROUP BY HOUR(FROM_UNIXTIME(acc.create_time/1000)) " +
            "ORDER BY message_count DESC")
    List<HourlyActivity> getUserHourlyActivity(@Param("userId") String userId,
                                              @Param("startTime") Long startTime);

    /**
     * 清理指定时间之前的旧消息
     *
     * @param beforeTime 时间阈值
     * @return 删除的记录数
     */
    @Select("DELETE FROM ai_conversation_content WHERE create_time < #{beforeTime}")
    Integer cleanupOldMessages(@Param("beforeTime") Long beforeTime);

    /**
     * 获取会话的第一条和最后一条消息
     *
     * @param conversationId 会话ID
     * @return 首尾消息信息
     */
    @Select("(SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} ORDER BY create_time ASC LIMIT 1) " +
            "UNION ALL " +
            "(SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} ORDER BY create_time DESC LIMIT 1)")
    List<AiConversationContent> getFirstAndLastMessage(@Param("conversationId") String conversationId);

    /**
     * 会话统计信息DTO
     */
    class ConversationStats {
        private Long userCount;
        private Long aiCount;
        private Long totalCount;
        private Long firstMessageTime;
        private Long lastMessageTime;

        // Getters and Setters
        public Long getUserCount() { return userCount; }
        public void setUserCount(Long userCount) { this.userCount = userCount; }

        public Long getAiCount() { return aiCount; }
        public void setAiCount(Long aiCount) { this.aiCount = aiCount; }

        public Long getTotalCount() { return totalCount; }
        public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }

        public Long getFirstMessageTime() { return firstMessageTime; }
        public void setFirstMessageTime(Long firstMessageTime) { this.firstMessageTime = firstMessageTime; }

        public Long getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(Long lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    }

    /**
     * 用户消息统计DTO
     */
    class UserMessageStats {
        private Long userMessageCount;
        private Long aiMessageCount;
        private Long conversationCount;
        private Long totalMessageCount;

        // Getters and Setters
        public Long getUserMessageCount() { return userMessageCount; }
        public void setUserMessageCount(Long userMessageCount) { this.userMessageCount = userMessageCount; }

        public Long getAiMessageCount() { return aiMessageCount; }
        public void setAiMessageCount(Long aiMessageCount) { this.aiMessageCount = aiMessageCount; }

        public Long getConversationCount() { return conversationCount; }
        public void setConversationCount(Long conversationCount) { this.conversationCount = conversationCount; }

        public Long getTotalMessageCount() { return totalMessageCount; }
        public void setTotalMessageCount(Long totalMessageCount) { this.totalMessageCount = totalMessageCount; }
    }

    /**
     * 关键词统计DTO
     */
    class KeywordCount {
        private String keyword;
        private Long count;

        // Getters and Setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }

        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    /**
     * 小时活跃度统计DTO
     */
    class HourlyActivity {
        private Integer hourOfDay;
        private Long messageCount;

        // Getters and Setters
        public Integer getHourOfDay() { return hourOfDay; }
        public void setHourOfDay(Integer hourOfDay) { this.hourOfDay = hourOfDay; }

        public Long getMessageCount() { return messageCount; }
        public void setMessageCount(Long messageCount) { this.messageCount = messageCount; }
    }
}