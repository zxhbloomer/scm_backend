/**
 * 聊天记忆类型枚举
 */
package com.xinyirun.scm.ai.memory;

public enum ChatMemoryType {
    
    /**
     * 用户消息
     */
    USER("用户消息"),
    
    /**
     * 助手/机器人回复
     */
    ASSISTANT("助手回复"),
    
    /**
     * 系统消息
     */
    SYSTEM("系统消息"),
    
    /**
     * 工具调用结果
     */
    TOOL("工具调用");

    private final String description;

    ChatMemoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据字符串获取枚举值
     */
    public static ChatMemoryType fromValue(String value) {
        for (ChatMemoryType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return USER; // 默认返回用户类型
    }

    /**
     * 判断是否为用户消息
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * 判断是否为助手消息
     */
    public boolean isAssistant() {
        return this == ASSISTANT;
    }

    /**
     * 判断是否为系统消息
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 判断是否为工具消息
     */
    public boolean isTool() {
        return this == TOOL;
    }
}