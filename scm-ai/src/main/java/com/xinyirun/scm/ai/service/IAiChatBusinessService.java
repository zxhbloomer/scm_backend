package com.xinyirun.scm.ai.service;

import com.xinyirun.scm.ai.entity.AiConversationContent;
import com.xinyirun.scm.ai.entity.AiConversation;

/**
 * AI聊天业务聚合服务接口
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
public interface IAiChatBusinessService {

    /**
     * 发送消息并获取AI回复
     *
     * @param conversationId 会话ID（可为空，自动创建新会话）
     * @param message 用户消息
     * @param userId 用户ID
     * @param modelProvider 模型提供商（可为空，使用默认）
     * @param modelName 模型名称（可为空，使用默认）
     * @return AI回复内容
     */
    AiConversationContent sendMessage(String conversationId, String message, Long userId,
                                           String modelProvider, String modelName);

    /**
     * 创建新会话并发送第一条消息
     *
     * @param title 会话标题
     * @param message 用户消息
     * @param userId 用户ID
     * @param modelProvider 模型提供商
     * @param modelName 模型名称
     * @return AI回复内容
     */
    AiConversationContent startNewConversation(String title, String message, Long userId,
                                                     String modelProvider, String modelName);

    /**
     * 继续会话对话
     *
     * @param conversationId 会话ID
     * @param message 用户消息
     * @param userId 用户ID
     * @return AI回复内容
     */
    AiConversationContent continueConversation(String conversationId, String message, Long userId);

    /**
     * 重新生成AI回复
     *
     * @param conversationId 会话ID
     * @param messageId 要重新生成的消息ID
     * @param userId 用户ID
     * @return 新的AI回复内容
     */
    AiConversationContent regenerateResponse(String conversationId, Long messageId, Long userId);

    /**
     * 获取会话完整对话历史
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 会话实体（包含消息列表）
     */
    AiConversation getConversationHistory(String conversationId, Long userId);

    /**
     * 清空会话内容
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean clearConversation(String conversationId, Long userId);

    /**
     * 导出会话内容
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param format 导出格式：txt, json, markdown
     * @return 导出内容
     */
    String exportConversation(String conversationId, Long userId, String format);

    /**
     * 获取会话统计信息
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 统计信息JSON字符串
     */
    String getConversationStatistics(String conversationId, Long userId);

    /**
     * 验证用户是否有权限访问会话
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    Boolean validateConversationAccess(String conversationId, Long userId);

    /**
     * 自动生成会话标题
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 生成的标题
     */
    String generateConversationTitle(String conversationId, Long userId);

    /**
     * 获取推荐的后续问题
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 推荐问题列表JSON
     */
    String getSuggestedQuestions(String conversationId, Long userId);
}