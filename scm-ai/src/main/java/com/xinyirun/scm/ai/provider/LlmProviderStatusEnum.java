/*
 * SCM AI Module - LLM Provider Status Enum
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商状态枚举
 */
package com.xinyirun.scm.ai.provider;

/**
 * AI提供商状态枚举
 * 定义AI提供商的运行状态
 */
public enum LlmProviderStatusEnum {
    
    /**
     * 开发环境状态
     */
    DEVELOPMENT("development", "开发环境"),
    
    /**
     * 生产环境状态
     */
    PRODUCTION("production", "生产环境"),
    
    /**
     * 测试环境状态
     */
    TEST("test", "测试环境"),
    
    /**
     * 维护状态
     */
    MAINTENANCE("maintenance", "维护中"),
    
    /**
     * 已停用
     */
    DISABLED("disabled", "已停用");

    private final String status;
    private final String description;

    LlmProviderStatusEnum(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态字符串获取枚举值
     * 
     * @param status 状态字符串
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果状态字符串无效
     */
    public static LlmProviderStatusEnum fromStatus(String status) {
        for (LlmProviderStatusEnum providerStatus : LlmProviderStatusEnum.values()) {
            if (providerStatus.getStatus().equalsIgnoreCase(status)) {
                return providerStatus;
            }
        }
        throw new IllegalArgumentException("未知的提供商状态: " + status);
    }

    /**
     * 检查是否为有效的提供商状态
     * 
     * @param status 状态字符串
     * @return 是否有效
     */
    public static boolean isValidStatus(String status) {
        try {
            fromStatus(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 是否为生产环境状态
     */
    public boolean isProduction() {
        return this == PRODUCTION;
    }

    /**
     * 是否为开发环境状态
     */
    public boolean isDevelopment() {
        return this == DEVELOPMENT;
    }

    /**
     * 是否为可用状态（非维护和停用）
     */
    public boolean isAvailable() {
        return this != MAINTENANCE && this != DISABLED;
    }
}