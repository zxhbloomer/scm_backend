/**
 * 聊天记忆项，用于传输和构建聊天记忆数据
 */
package com.xinyirun.scm.ai.memory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemoryItem {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private ChatMemoryType type;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 额外信息
     */
    private String extra;

    /**
     * 创建用户消息记忆项
     */
    public static ChatMemoryItem createUserMessage(String content) {
        return ChatMemoryItem.builder()
                .content(content)
                .type(ChatMemoryType.USER)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建助手消息记忆项
     */
    public static ChatMemoryItem createAssistantMessage(String content) {
        return ChatMemoryItem.builder()
                .content(content)
                .type(ChatMemoryType.ASSISTANT)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建系统消息记忆项
     */
    public static ChatMemoryItem createSystemMessage(String content) {
        return ChatMemoryItem.builder()
                .content(content)
                .type(ChatMemoryType.SYSTEM)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建工具消息记忆项
     */
    public static ChatMemoryItem createToolMessage(String content) {
        return ChatMemoryItem.builder()
                .content(content)
                .type(ChatMemoryType.TOOL)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 设置额外信息
     */
    public ChatMemoryItem withExtra(String extra) {
        this.extra = extra;
        return this;
    }

    /**
     * 设置时间戳
     */
    public ChatMemoryItem withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}