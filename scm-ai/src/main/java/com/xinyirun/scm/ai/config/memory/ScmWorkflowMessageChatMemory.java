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
 * Workflow领域专用ChatMemory实现（只读模式）
 *
 * 负责Workflow领域的历史对话读取功能:
 * - 查询 ai_workflow_conversation_content 表
 * - 支持最多10条历史消息记忆
 * - conversationId格式: tenantCode::workflowUuid::userId (3段)
 *
 * 注意：
 * - 对话保存功能由 WorkflowConversationAdvisor 负责
 * - 此类只负责读取历史对话（get方法）
 * - add()方法为空实现（不保存），避免重复保存
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
        // 空实现：对话保存由 WorkflowConversationAdvisor 负责
        // 避免通过 MessageChatMemoryAdvisor 重复保存
        log.debug("Workflow领域对话保存已由WorkflowConversationAdvisor接管，跳过add()调用");
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
                            default -> throw new IllegalArgumentException("未知消息类型: " + type);
                        };
                    }).collect(Collectors.toList());

//            if (!messages.isEmpty()) {
//                messages.add(0, new SystemMessage(
//                    "以下是你和用户的历史对话记录，你必须基于这些历史对话来回答用户的新问题。" +
//                    "请确保你的回答体现出你记得之前的对话内容。"
//                ));
//            }

//            if (!messages.isEmpty()) {
//                messages.add(0, new SystemMessage(
//                    "以下是你和用户的历史对话记录，作为你的重要参考。" +
//                    "请确保你的回答问题时很自然的把历史对话进行参考，不要生硬。"
//                ));
//            }


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
