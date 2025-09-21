package com.xinyirun.scm.ai.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.entity.AiConversationContent;
import com.xinyirun.scm.ai.entity.AiConversation;
import com.xinyirun.scm.ai.service.IAiChatBusinessService;
import com.xinyirun.scm.ai.service.IAiConversationContentService;
import com.xinyirun.scm.ai.service.IAiConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AI聊天业务聚合服务实现
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Slf4j
@Service
public class AiChatBusinessServiceImpl implements IAiChatBusinessService {

    @Autowired
    private IAiConversationService conversationService;

    @Autowired
    private IAiConversationContentService contentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContent sendMessage(String conversationId, String message, Long userId,
                                                  String modelProvider, String modelName) {
        if (!StringUtils.hasText(message) || userId == null) {
            throw new AiBusinessException("消息内容和用户ID不能为空");
        }

        // 如果没有会话ID，创建新会话
        if (conversationId == null) {
            String title = generateTitleFromMessage(message);
            return startNewConversation(title, message, userId, modelProvider, modelName);
        }

        // 继续现有会话
        return continueConversation(conversationId, message, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContent startNewConversation(String title, String message, Long userId,
                                                           String modelProvider, String modelName) {
        if (!StringUtils.hasText(message) || userId == null) {
            throw new AiBusinessException("消息内容和用户ID不能为空");
        }

        // 创建新会话
        AiConversation conversation = conversationService.createConversation(title, userId, modelProvider, modelName);

        // 发送第一条消息
        return continueConversation(conversation.getId(), message, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContent continueConversation(String conversationId, String message, Long userId) {
        if (conversationId == null || !StringUtils.hasText(message) || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        try {
            // 1. 保存用户消息
            AiConversationContent userMessage = contentService.addUserMessage(conversationId, message, userId);

            // 2. 调用AI接口获取回复（这里需要集成具体的AI服务）
            String aiResponse = callAiService(conversationId, message);

            // 3. 保存AI回复
            AiConversationContent aiMessage = contentService.addAiMessage(
                    conversationId,
                    aiResponse,
                    buildMessageContent(userMessage, aiResponse),
                    calculateTokenUsage(message, aiResponse),
                    System.currentTimeMillis() // 简化的响应时间计算
            );

            log.info("会话消息处理完成, conversationId: {}, userMessageId: {}, aiMessageId: {}",
                    conversationId, userMessage.getId(), aiMessage.getId());

            return aiMessage;

        } catch (Exception e) {
            log.error("处理会话消息失败, conversationId: {}, message: {}", conversationId, message, e);
            throw new AiBusinessException("处理消息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContent regenerateResponse(String conversationId, Long messageId, Long userId) {
        if (conversationId == null || messageId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        // 获取原始用户消息
        AiConversationContent originalMessage = contentService.getById(messageId);
        if (originalMessage == null || !originalMessage.getConversation_id().equals(conversationId)) {
            throw new AiBusinessException("消息不存在或不属于该会话");
        }

        if (!AiConstant.MESSAGE_TYPE_USER.equals(originalMessage.getType())) {
            throw new AiBusinessException("只能重新生成用户消息的回复");
        }

        try {
            // 重新调用AI接口
            String aiResponse = callAiService(conversationId, originalMessage.getContent());

            // 保存新的AI回复
            AiConversationContent aiMessage = contentService.addAiMessage(
                    conversationId,
                    aiResponse,
                    buildMessageContent(originalMessage, aiResponse),
                    calculateTokenUsage(originalMessage.getContent(), aiResponse),
                    System.currentTimeMillis()
            );

            log.info("重新生成AI回复成功, conversationId: {}, originalMessageId: {}, newMessageId: {}",
                    conversationId, messageId, aiMessage.getId());

            return aiMessage;

        } catch (Exception e) {
            log.error("重新生成AI回复失败, conversationId: {}, messageId: {}", conversationId, messageId, e);
            throw new AiBusinessException("重新生成回复失败: " + e.getMessage());
        }
    }

    @Override
    public AiConversation getConversationHistory(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        // 获取会话基本信息
        AiConversation conversation = conversationService.getConversationDetail(conversationId, userId);

        // 获取会话内容（这里可以根据需要设置分页或限制）
        List<AiConversationContent> contents = contentService.getContentsByConversationId(conversationId);

        // 将内容设置到会话对象中（如果需要的话，可以扩展实体类增加contents字段）
        log.info("获取会话历史成功, conversationId: {}, messageCount: {}", conversationId, contents.size());

        return conversation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean clearConversation(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限操作该会话");
        }

        // 清空会话内容
        boolean result = contentService.deleteMessagesByConversationId(conversationId);
        if (result) {
            log.info("清空会话内容成功, conversationId: {}", conversationId);
        }

        return result;
    }

    @Override
    public String exportConversation(String conversationId, Long userId, String format) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        AiConversation conversation = conversationService.getConversationDetail(conversationId, userId);
        List<AiConversationContent> contents = contentService.getContentsByConversationId(conversationId);

        if (!StringUtils.hasText(format)) {
            format = "txt";
        }

        switch (format.toLowerCase()) {
            case "json":
                return exportAsJson(conversation, contents);
            case "markdown":
                return exportAsMarkdown(conversation, contents);
            default:
                return exportAsText(conversation, contents);
        }
    }

    @Override
    public String getConversationStatistics(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        JSONObject statistics = new JSONObject();
        statistics.put("conversationId", conversationId);
        statistics.put("messageCount", contentService.countMessagesByConversationId(conversationId));
        statistics.put("totalTokens", contentService.calculateTotalTokens(conversationId));
        statistics.put("averageResponseTime", contentService.calculateAverageResponseTime(conversationId));

        return statistics.toJSONString();
    }

    @Override
    public Boolean validateConversationAccess(String conversationId, Long userId) {
        return conversationService.checkConversationOwnership(conversationId, userId);
    }

    @Override
    public String generateConversationTitle(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        // 获取第一条用户消息
        AiConversationContent firstMessage = contentService.getFirstUserMessage(conversationId);
        if (firstMessage != null && StringUtils.hasText(firstMessage.getContent())) {
            return generateTitleFromMessage(firstMessage.getContent());
        }

        return "新对话 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }

    @Override
    public String getSuggestedQuestions(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 验证会话权限
        if (!validateConversationAccess(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        // 这里可以根据会话内容生成推荐问题，暂时返回默认推荐
        JSONArray suggestions = new JSONArray();
        suggestions.add("请详细解释一下");
        suggestions.add("有没有相关的例子？");
        suggestions.add("这个方案的优缺点是什么？");
        suggestions.add("还有其他解决方案吗？");

        return suggestions.toJSONString();
    }

    /**
     * 调用AI服务获取回复（需要根据实际AI服务实现）
     */
    private String callAiService(String conversationId, String message) {
        // TODO: 集成具体的AI服务（如OpenAI、Claude等）
        // 这里返回模拟回复
        return "这是AI的模拟回复：" + message;
    }

    /**
     * 从消息内容生成标题
     */
    private String generateTitleFromMessage(String message) {
        if (!StringUtils.hasText(message)) {
            return "新对话";
        }

        // 取前30个字符作为标题
        String title = message.length() > 30 ? message.substring(0, 30) + "..." : message;
        return title.replaceAll("\n", " ").trim();
    }

    /**
     * 构建消息内容JSON
     */
    private String buildMessageContent(AiConversationContent userMessage, String aiResponse) {
        JSONObject messageContent = new JSONObject();
        messageContent.put("userMessage", userMessage.getContent());
        messageContent.put("aiResponse", aiResponse);
        messageContent.put("timestamp", LocalDateTime.now().toString());
        return messageContent.toJSONString();
    }

    /**
     * 计算token使用量（简化实现）
     */
    private Integer calculateTokenUsage(String userMessage, String aiResponse) {
        // 简化的token计算，实际应该调用具体模型的token计算方法
        return (userMessage.length() + aiResponse.length()) / 4;
    }

    /**
     * 导出为JSON格式
     */
    private String exportAsJson(AiConversation conversation, List<AiConversationContent> contents) {
        JSONObject export = new JSONObject();
        export.put("conversation", conversation);
        export.put("messages", contents);
        return export.toJSONString();
    }

    /**
     * 导出为Markdown格式
     */
    private String exportAsMarkdown(AiConversation conversation, List<AiConversationContent> contents) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# ").append(conversation.getTitle()).append("\n\n");
        markdown.append("**会话时间**: ").append(conversation.getCreate_time()).append("\n\n");

        for (AiConversationContent content : contents) {
            if (AiConstant.MESSAGE_TYPE_USER.equals(content.getType())) {
                markdown.append("## 用户\n");
            } else {
                markdown.append("## AI助手\n");
            }
            markdown.append(content.getContent()).append("\n\n");
        }

        return markdown.toString();
    }

    /**
     * 导出为文本格式
     */
    private String exportAsText(AiConversation conversation, List<AiConversationContent> contents) {
        StringBuilder text = new StringBuilder();
        text.append("会话标题: ").append(conversation.getTitle()).append("\n");
        text.append("会话时间: ").append(conversation.getCreate_time()).append("\n");
        text.append("=".repeat(50)).append("\n\n");

        for (AiConversationContent content : contents) {
            String speaker = AiConstant.MESSAGE_TYPE_USER.equals(content.getType()) ? "用户" : "AI助手";
            text.append("[").append(speaker).append("] ");
            text.append(content.getCreate_time()).append("\n");
            text.append(content.getContent()).append("\n\n");
        }

        return text.toString();
    }
}