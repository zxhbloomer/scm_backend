/*
 * SCM AI Module - LLM Provider Entity
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商实体类，管理AI服务提供商的配置信息
 */
package com.xinyirun.scm.ai.provider;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * AI提供商实体类
 * 管理AI服务提供商的配置信息和连接参数
 * 
 * 数据表: scm_ai_provider
 * 用途: 存储AI提供商的基本信息、API配置和状态管理
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scm_ai_provider")
public class LlmProviderEntity extends BytedeskBaseEntity {

    /**
     * 提供商名称（唯一标识）
     */
    @TableField("name")
    private String name;

    /**
     * 提供商显示昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 提供商Logo URL
     * 例如: https://cdn.example.com/assets/images/llm/model/provider.png
     */
    @TableField("logo")
    private String logo;

    /**
     * 提供商描述信息
     */
    @TableField("description")
    private String description = "";

    /**
     * API基础URL
     * 例如: https://api.openai.com/v1
     */
    @TableField("base_url")
    private String baseUrl;

    /**
     * API密钥
     */
    @TableField("api_key")
    private String apiKey;

    /**
     * 提供商官方网站URL
     */
    @TableField("web_url")
    private String webUrl;

    /**
     * 关联的Coze Bot ID（如果适用）
     */
    @TableField("coze_bot_id")
    private String cozeBotId;

    /**
     * 提供商状态 (DEVELOPMENT, PRODUCTION, TEST等)
     */
    @TableField("status")
    private String status = LlmProviderStatusEnum.DEVELOPMENT.name();

    /**
     * 是否启用
     */
    @TableField("is_enabled")
    private Boolean enabled = true;

    /**
     * 是否允许租户调用系统API，默认不开启
     * 如果开启了，则租户默认调用系统API
     */
    @TableField("is_system_enabled")
    private Boolean systemEnabled = false;

    /**
     * 提供商类型（例如: OpenAI, Zhipu, Anthropic等）
     */
    @TableField("provider_type")
    private String providerType;

    /**
     * 提供商版本
     */
    @TableField("provider_version")
    private String providerVersion;

    /**
     * 配置参数（JSON格式）
     */
    @TableField("config_json")
    private String configJson;

    /**
     * 最大请求速率（每分钟）
     */
    @TableField("max_requests_per_minute")
    private Integer maxRequestsPerMinute;

    /**
     * 最大token数限制
     */
    @TableField("max_tokens")
    private Integer maxTokens;

    /**
     * 超时时间（毫秒）
     */
    @TableField("timeout_ms")
    private Integer timeoutMs;

    /**
     * 排序字段
     */
    @TableField("sort_order")
    private Integer sortOrder = 100;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    // =============== 业务方法 ===============

    /**
     * 获取状态枚举
     */
    public LlmProviderStatusEnum getStatusEnum() {
        return LlmProviderStatusEnum.fromStatus(this.status);
    }

    /**
     * 设置状态枚举
     */
    public void setStatusEnum(LlmProviderStatusEnum statusEnum) {
        this.status = statusEnum.name();
    }

    /**
     * 是否为生产环境状态
     */
    public boolean isProduction() {
        return LlmProviderStatusEnum.PRODUCTION.name().equals(this.status);
    }

    /**
     * 是否为开发环境状态
     */
    public boolean isDevelopment() {
        return LlmProviderStatusEnum.DEVELOPMENT.name().equals(this.status);
    }

    /**
     * 检查提供商是否可用
     */
    public boolean isAvailable() {
        if (!Boolean.TRUE.equals(this.enabled)) {
            return false;
        }
        
        LlmProviderStatusEnum statusEnum = getStatusEnum();
        return statusEnum.isAvailable();
    }

    /**
     * 检查配置是否完整
     */
    public boolean hasValidConfiguration() {
        return this.baseUrl != null && !this.baseUrl.trim().isEmpty() &&
               this.apiKey != null && !this.apiKey.trim().isEmpty();
    }

    /**
     * 获取masked的API Key（用于显示）
     */
    public String getMaskedApiKey() {
        if (this.apiKey == null || this.apiKey.length() < 8) {
            return "****";
        }
        String prefix = this.apiKey.substring(0, 4);
        String suffix = this.apiKey.substring(this.apiKey.length() - 4);
        return prefix + "****" + suffix;
    }

    /**
     * 是否支持流式响应
     */
    public boolean supportsStreaming() {
        // 可以根据providerType来判断
        return "OpenAI".equalsIgnoreCase(this.providerType) ||
               "Zhipu".equalsIgnoreCase(this.providerType) ||
               "Anthropic".equalsIgnoreCase(this.providerType);
    }
}