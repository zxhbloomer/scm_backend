package com.xinyirun.scm.ai.config;

import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.service.AiConversationContentService;
import com.xinyirun.scm.ai.service.AiConversationService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.core.NamedThreadLocal;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用 MessageWindowChatMemory 只能记忆和持久化 max_messages 条消息
 * 该自定义类，能持久化所有消息，并且设置 ai 记忆消息的条数
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class ScmMessageChatMemory implements ChatMemory {

    /**
     * 默认记忆10条消息
     */
    private static final int DEFAULT_MAX_MESSAGES = 10;

    /**
     * 租户ID参数键
     */
    public static final String TENANT_ID = "TENANT_ID";

    /**
     * 当前线程的租户ID存储 - 使用NamedThreadLocal与dynamic-datasource保持一致
     */
    private static final ThreadLocal<String> CURRENT_TENANT = new NamedThreadLocal<>("tenant-context");

    @Resource
    @Lazy
    private AiConversationContentService aiConversationContentService;

    @Resource
    @Lazy
    private AiConversationService aiConversationService;

    /**
     * 设置当前租户ID
     */
    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * 获取当前租户ID
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * 清理当前租户ID
     */
    public static void clearCurrentTenant() {
        CURRENT_TENANT.remove();
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        // 这里不处理，手动保存原始的提示词
    }

    @Override
    public List<Message> get(String conversationId) {
        try {
            // 获取当前租户ID
            String tenantId = getCurrentTenant();

            // 如果当前线程没有租户信息，尝试从conversationId中恢复
            if (tenantId == null || tenantId.isEmpty()) {
                tenantId = extractTenantFromConversationId(conversationId);
                if (tenantId != null && !tenantId.isEmpty()) {
                    setCurrentTenant(tenantId);
                }
            }

            // 关键修复：每次都重新设置DynamicDataSourceContextHolder
            // 因为Spring AI在不同线程中调用，ThreadLocal会丢失
            if (tenantId != null && !tenantId.isEmpty()) {
                DataSourceHelper.use(tenantId);
            }
            // 获取最近的几条聊天，进行记忆 - 通过Service层调用
            // 获取对话历史（SQL已跳过最新的用户消息，避免重复）
            List<AiConversationContentVo> contents = aiConversationService.getConversationHistory(conversationId, DEFAULT_MAX_MESSAGES);

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
            // 确保清理数据源连接和ThreadLocal
            DataSourceHelper.close();
            // 注意：这里不清理ThreadLocal，因为可能还需要在同一个线程中使用
            // ThreadLocal 的清理由调用方负责
        }
    }

    @Override
    public void clear(String conversationId) {
        // do nothing
    }

    /**
     * 从conversationId提取租户信息
     * 解决InheritableThreadLocal在异步线程中失效的问题
     */
    private String extractTenantFromConversationId(String conversationId) {
        // 由于conversationId无法直接确定租户信息，
        // 这里返回固定的租户ID作为降级方案
        // TODO: 根据实际业务需求调整租户获取逻辑
        return "scm_tenant_20250519_001";
    }
}