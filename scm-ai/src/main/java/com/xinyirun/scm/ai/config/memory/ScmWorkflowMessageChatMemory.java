package com.xinyirun.scm.ai.config.memory;

import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowConversationContentVo;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowConversationContentService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Workflow领域专用ChatMemory实现
 *
 * 负责Workflow领域的多轮对话记忆功能:
 * - 查询 ai_workflow_conversation_content 表
 * - 支持最多10条历史消息记忆
 * - conversationId格式: tenantCode::workflowUuid::userId (3段)
 *
 * 注意：此类在Reactor响应式流中被调用，不应使用@Transactional
 * 底层的查询方法已声明为NOT_SUPPORTED，不需要事务管理
 *
 * @author SCM-AI开发团队
 * @since 2025-01-08
 */
@Slf4j
@Component("workflowMessageChatMemory")
public class ScmWorkflowMessageChatMemory implements ChatMemory {

    /**
     * 默认记忆10条消息
     */
    private static final int DEFAULT_MAX_MESSAGES = 10;

    /**
     * 租户与会话ID的分隔符
     */
    private static final String TENANT_SEPARATOR = "::";

    @Resource
    @Lazy
    private AiWorkflowConversationContentService aiWorkflowConversationContentService;

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        try {
            String tenantId = parseTenantId(conversationId);
            DataSourceHelper.use(tenantId);

            for (Message msg : messages) {
                String messageType = msg.getMessageType().getValue();
                String content = extractMessageContent(msg);

                aiWorkflowConversationContentService.saveMessage(
                        conversationId,
                        messageType,
                        content,
                        null,
                        null,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            log.error("保存消息失败 - conversationId: {}", conversationId, e);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 从Message对象中提取文本内容
     */
    private String extractMessageContent(Message msg) {
        if (msg instanceof UserMessage) {
            return ((UserMessage) msg).getText();
        } else if (msg instanceof AssistantMessage) {
            return ((AssistantMessage) msg).getText();
        } else if (msg instanceof SystemMessage) {
            return ((SystemMessage) msg).getText();
        }
        return "";
    }

    @Override
    public List<Message> get(String conversationId) {
        try {
            log.debug("Workflow领域查询对话历史, conversationId: {}", conversationId);

            String tenantId = parseTenantId(conversationId);
            DataSourceHelper.use(tenantId);

            List<AiWorkflowConversationContentVo> contents =
                aiWorkflowConversationContentService.getConversationHistory(conversationId, DEFAULT_MAX_MESSAGES);

            Collections.reverse(contents);

            List<Message> messages = contents.stream()
                    .map(conversationContent -> {
                        MessageType type = MessageType.fromValue(conversationContent.getType());
                        String content = conversationContent.getContent();
                        return switch (type) {
                            case USER -> new UserMessage(content);
                            case ASSISTANT -> new AssistantMessage(content);
                            case SYSTEM -> new SystemMessage(content);
                            case TOOL -> new ToolResponseMessage(List.of());
                        };
                    }).collect(Collectors.toList());

            if (!messages.isEmpty()) {
                messages.add(0, new SystemMessage(
                    "以下是你和用户的历史对话记录，你必须基于这些历史对话来回答用户的新问题。" +
                    "请确保你的回答体现出你记得之前的对话内容。"
                ));
            }

            return messages;
        } finally {
            DataSourceHelper.close();
        }
    }

    @Override
    public void clear(String conversationId) {
        // do nothing
    }

    /**
     * 从conversationId解析租户ID
     * 格式：tenantCode::workflowUuid::userId
     */
    private String parseTenantId(String conversationId) {
        return conversationId.split(TENANT_SEPARATOR, 2)[0];
    }
}
