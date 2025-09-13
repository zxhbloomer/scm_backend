/**
 * 对话上下文，包含会话的完整上下文信息
 */
package com.xinyirun.scm.ai.memory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 记忆列表
     */
    private List<ChatMemoryEntity> memories;

    /**
     * 记忆总数
     */
    private Long totalMemoryCount;

    /**
     * 上下文元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 最后活动时间
     */
    private Long lastActivityTime;

    /**
     * 获取用户消息数量
     */
    public long getUserMessageCount() {
        if (memories == null) {
            return 0;
        }
        return memories.stream()
                .filter(memory -> ChatMemoryType.USER.name().equals(memory.getMessageType()))
                .count();
    }

    /**
     * 获取助手消息数量
     */
    public long getAssistantMessageCount() {
        if (memories == null) {
            return 0;
        }
        return memories.stream()
                .filter(memory -> ChatMemoryType.ASSISTANT.name().equals(memory.getMessageType()))
                .count();
    }

    /**
     * 获取最后一条用户消息
     */
    public ChatMemoryEntity getLastUserMessage() {
        if (memories == null) {
            return null;
        }
        return memories.stream()
                .filter(memory -> ChatMemoryType.USER.name().equals(memory.getMessageType()))
                .reduce((first, second) -> second)
                .orElse(null);
    }

    /**
     * 获取最后一条助手消息
     */
    public ChatMemoryEntity getLastAssistantMessage() {
        if (memories == null) {
            return null;
        }
        return memories.stream()
                .filter(memory -> ChatMemoryType.ASSISTANT.name().equals(memory.getMessageType()))
                .reduce((first, second) -> second)
                .orElse(null);
    }

    /**
     * 获取指定类型的消息列表
     */
    public List<ChatMemoryEntity> getMessagesByType(ChatMemoryType type) {
        if (memories == null) {
            return List.of();
        }
        return memories.stream()
                .filter(memory -> type.name().equals(memory.getMessageType()))
                .collect(Collectors.toList());
    }

    /**
     * 构建对话历史文本，用于AI推理
     */
    public String buildContextText() {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        for (ChatMemoryEntity memory : memories) {
            String role = mapTypeToRole(memory.getMessageType());
            context.append(role).append(": ").append(memory.getContent()).append("\n");
        }

        return context.toString();
    }

    /**
     * 构建简化的对话历史，只包含用户和助手消息
     */
    public String buildSimpleContextText() {
        if (memories == null || memories.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        for (ChatMemoryEntity memory : memories) {
            if (ChatMemoryType.USER.name().equals(memory.getMessageType()) ||
                ChatMemoryType.ASSISTANT.name().equals(memory.getMessageType())) {
                String role = mapTypeToRole(memory.getMessageType());
                context.append(role).append(": ").append(memory.getContent()).append("\n");
            }
        }

        return context.toString();
    }

    /**
     * 获取上下文摘要统计
     */
    public ContextSummary getContextSummary() {
        return ContextSummary.builder()
                .conversationId(conversationId)
                .totalMessages(memories != null ? memories.size() : 0)
                .userMessages(getUserMessageCount())
                .assistantMessages(getAssistantMessageCount())
                .lastActivityTime(lastActivityTime)
                .hasContext(memories != null && !memories.isEmpty())
                .build();
    }

    /**
     * 检查是否有有效的上下文
     */
    public boolean hasValidContext() {
        return memories != null && !memories.isEmpty();
    }

    /**
     * 添加元数据
     */
    public ConversationContext addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
        return this;
    }

    /**
     * 获取元数据
     */
    public Object getMetadata(String key) {
        return metadata != null ? metadata.get(key) : null;
    }

    /**
     * 映射消息类型到角色名称
     */
    private String mapTypeToRole(String messageType) {
        return switch (ChatMemoryType.fromValue(messageType)) {
            case USER -> "用户";
            case ASSISTANT -> "助手";
            case SYSTEM -> "系统";
            case TOOL -> "工具";
        };
    }

    /**
     * 上下文摘要内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextSummary {
        private String conversationId;
        private Integer totalMessages;
        private Long userMessages;
        private Long assistantMessages;
        private Long lastActivityTime;
        private Boolean hasContext;
    }
}