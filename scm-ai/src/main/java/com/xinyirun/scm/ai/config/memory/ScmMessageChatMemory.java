package com.xinyirun.scm.ai.config.memory;

import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.core.service.chat.AiConversationService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用 MessageWindowChatMemory 只能记忆和持久化 max_messages 条消息
 * 该自定义类，能持久化所有消息，并且设置 ai 记忆消息的条数
 *
 * 注意：此类在Reactor响应式流中被调用，不应使用@Transactional
 * 底层的查询方法已声明为NOT_SUPPORTED，不需要事务管理
 */
@Slf4j
@Component
public class ScmMessageChatMemory implements ChatMemory {

    /**
     * 默认记忆10条消息
     */
    private static final int DEFAULT_MAX_MESSAGES = 10;

    /**
     * 租户与会话ID的分隔符
     */
    private static final String TENANT_SEPARATOR = "::";

    /**
     * 当前线程的租户ID存储 - 使用NamedThreadLocal与dynamic-datasource保持一致
     * 注意：优化后主要从conversationId中解析租户信息，此ThreadLocal作为降级方案
     */
    private static final ThreadLocal<String> CURRENT_TENANT = new NamedThreadLocal<>("tenant-context");


    @Resource
    @Lazy
    private AiConversationService aiConversationService;

    @Override
    public void add(String conversationId, List<Message> messages) {
        // 这里不处理，手动保存原始的提示词
    }

    @Override
    public List<Message> get(String conversationId) {
        try {
            log.debug("聊天的conversationId {}", conversationId);

            // 从conversationId解析租户ID
            String tenantId = parseTenantId(conversationId);

            // 设置数据源
            DataSourceHelper.use(tenantId);

            // 查询对话历史
            List<AiConversationContentVo> contents = aiConversationService.getConversationHistory(
                conversationId, DEFAULT_MAX_MESSAGES);

            // 反转为时间升序（老消息在前）
            Collections.reverse(contents);

            return contents.stream()
                    .map(conversationContent -> {
                        MessageType type = MessageType.fromValue(conversationContent.getType());
                        String content = conversationContent.getContent();
                        Message message = switch (type) {
                            case USER -> new UserMessage(content);
                            case ASSISTANT -> new AssistantMessage(content);
                            case SYSTEM -> new SystemMessage(content);
                            // The content is always stored empty for ToolResponseMessages.
                            // If we want to capture the actual content, we need to extend
                            // AddBatchPreparedStatement to support it.
                            case TOOL -> new ToolResponseMessage(List.of());
                        };
                        return message;
                    }).collect(Collectors.toList());
        } finally {
            // 确保清理数据源连接
            DataSourceHelper.close();
        }
    }

    @Override
    public void clear(String conversationId) {
        // do nothing
    }

    /**
     * 从conversationId解析租户ID
     * 格式：tenantId::conversationId
     */
    private String parseTenantId(String conversationId) {
        return conversationId.split(TENANT_SEPARATOR, 2)[0];
    }
}