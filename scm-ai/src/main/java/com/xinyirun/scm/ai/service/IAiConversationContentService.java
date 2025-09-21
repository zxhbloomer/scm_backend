package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.ai.entity.AiConversationContent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话内容服务接口
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
public interface IAiConversationContentService extends IService<AiConversationContent> {

    /**
     * 添加用户消息
     *
     * @param conversationId 会话ID
     * @param content 消息内容
     * @param userId 用户ID
     * @return 消息实体
     */
    AiConversationContent addUserMessage(String conversationId, String content, Long userId);

    /**
     * 添加AI回复消息
     *
     * @param conversationId 会话ID
     * @param content 回复内容
     * @param messageContent 消息内容（JSON格式）
     * @param tokenUsed 使用的token数量
     * @param responseTime 响应时间（毫秒）
     * @return 消息实体
     */
    AiConversationContent addAiMessage(String conversationId, String content, String messageContent,
                                             Integer tokenUsed, Long responseTime);

    /**
     * 根据会话ID查询内容列表
     *
     * @param conversationId 会话ID
     * @return 内容列表
     */
    List<AiConversationContent> getContentsByConversationId(String conversationId);

    /**
     * 根据会话ID分页查询内容
     *
     * @param conversationId 会话ID
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<AiConversationContent> getContentsByConversationId(String conversationId, Long current, Long size);

    /**
     * 根据会话ID和消息类型查询内容
     *
     * @param conversationId 会话ID
     * @param messageType 消息类型：1-用户消息，2-AI回复
     * @return 内容列表
     */
    List<AiConversationContent> getContentsByConversationIdAndType(String conversationId, Integer messageType);

    /**
     * 获取会话的最后一条消息
     *
     * @param conversationId 会话ID
     * @return 最后一条消息
     */
    AiConversationContent getLastMessage(String conversationId);

    /**
     * 获取会话的最近N条消息
     *
     * @param conversationId 会话ID
     * @param limit 限制数量
     * @return 最近消息列表
     */
    List<AiConversationContent> getRecentMessages(String conversationId, Integer limit);

    /**
     * 统计会话消息总数
     *
     * @param conversationId 会话ID
     * @return 消息总数
     */
    Long countMessagesByConversationId(String conversationId);

    /**
     * 根据用户ID查询所有消息内容
     *
     * @param userId 用户ID
     * @return 消息列表
     */
    List<AiConversationContent> getContentsByUserId(Long userId);

    /**
     * 根据关键字搜索消息内容
     *
     * @param conversationId 会话ID
     * @param keyword 关键字
     * @return 消息列表
     */
    List<AiConversationContent> searchMessages(String conversationId, String keyword);

    /**
     * 查询指定时间范围内的消息
     *
     * @param conversationId 会话ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 消息列表
     */
    List<AiConversationContent> getMessagesByTimeRange(String conversationId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除会话的所有消息（软删除）
     *
     * @param conversationId 会话ID
     * @return 是否成功
     */
    Boolean deleteMessagesByConversationId(String conversationId);

    /**
     * 获取会话的第一条用户消息
     *
     * @param conversationId 会话ID
     * @return 第一条用户消息
     */
    AiConversationContent getFirstUserMessage(String conversationId);

    /**
     * 计算会话总token使用量
     *
     * @param conversationId 会话ID
     * @return 总token数量
     */
    Integer calculateTotalTokens(String conversationId);

    /**
     * 计算会话平均响应时间
     *
     * @param conversationId 会话ID
     * @return 平均响应时间（毫秒）
     */
    Long calculateAverageResponseTime(String conversationId);
}