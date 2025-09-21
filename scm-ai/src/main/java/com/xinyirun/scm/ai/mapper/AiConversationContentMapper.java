package com.xinyirun.scm.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.entity.AiConversationContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话内容数据访问接口
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Mapper
public interface AiConversationContentMapper extends BaseMapper<AiConversationContent> {

    /**
     * 根据会话ID查询内容列表
     *
     * @param conversationId 会话ID
     * @return 内容列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND is_deleted = 0 ORDER BY c_time ASC")
    List<AiConversationContent> selectByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID分页查询内容
     *
     * @param page 分页参数
     * @param conversationId 会话ID
     * @return 分页结果
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND is_deleted = 0 ORDER BY c_time ASC")
    IPage<AiConversationContent> selectPageByConversationId(IPage<AiConversationContent> page,
                                                                  @Param("conversationId") String conversationId);

    /**
     * 根据会话ID和消息类型查询内容
     *
     * @param conversationId 会话ID
     * @param messageType 消息类型：1-用户消息，2-AI回复
     * @return 内容列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND message_type = #{messageType} AND is_deleted = 0 ORDER BY c_time ASC")
    List<AiConversationContent> selectByConversationIdAndType(@Param("conversationId") String conversationId,
                                                                    @Param("messageType") Integer messageType);

    /**
     * 获取会话的最后一条消息
     *
     * @param conversationId 会话ID
     * @return 最后一条消息
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND is_deleted = 0 ORDER BY c_time DESC LIMIT 1")
    AiConversationContent selectLastByConversationId(@Param("conversationId") String conversationId);

    /**
     * 获取会话的最近N条消息
     *
     * @param conversationId 会话ID
     * @param limit 限制数量
     * @return 最近消息列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND is_deleted = 0 ORDER BY c_time DESC LIMIT #{limit}")
    List<AiConversationContent> selectRecentByConversationId(@Param("conversationId") String conversationId,
                                                                   @Param("limit") Integer limit);

    /**
     * 统计会话消息总数
     *
     * @param conversationId 会话ID
     * @return 消息总数
     */
    @Select("SELECT COUNT(*) FROM ai_conversation_content WHERE conversation_id = #{conversationId} AND is_deleted = 0")
    Long countByConversationId(@Param("conversationId") String conversationId);

    /**
     * 根据用户ID查询所有消息内容
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    @Select("SELECT acc.* FROM ai_conversation_content acc " +
            "INNER JOIN ai_conversation ac ON acc.conversation_id = ac.id " +
            "WHERE ac.user_id = #{userId} AND acc.is_deleted = 0 AND ac.is_deleted = 0 " +
            "ORDER BY acc.c_time DESC")
    List<AiConversationContent> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据关键字搜索消息内容
     *
     * @param conversationId 会话ID
     * @param keyword 关键字
     * @return 消息列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} " +
            "AND (content LIKE CONCAT('%', #{keyword}, '%') OR message_content LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND is_deleted = 0 ORDER BY c_time ASC")
    List<AiConversationContent> searchByKeyword(@Param("conversationId") String conversationId,
                                                      @Param("keyword") String keyword);

    /**
     * 查询指定时间范围内的消息
     *
     * @param conversationId 会话ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息列表
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} " +
            "AND c_time BETWEEN #{startTime} AND #{endTime} AND is_deleted = 0 ORDER BY c_time ASC")
    List<AiConversationContent> selectByTimeRange(@Param("conversationId") String conversationId,
                                                        @Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 获取会话的第一条用户消息
     *
     * @param conversationId 会话ID
     * @return 第一条用户消息
     */
    @Select("SELECT * FROM ai_conversation_content WHERE conversation_id = #{conversationId} " +
            "AND message_type = 1 AND is_deleted = 0 ORDER BY c_time ASC LIMIT 1")
    AiConversationContent selectFirstUserMessage(@Param("conversationId") String conversationId);
}