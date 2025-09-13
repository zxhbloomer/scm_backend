/*
 * SCM AI Module - LLM Provider Type Enum  
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: AI提供商类型枚举
 */
package com.xinyirun.scm.ai.provider;

/**
 * AI提供商类型枚举
 * 定义系统支持的各种AI服务提供商
 * 
 * @author SCM-AI Module
 * @version 1.0.0
 * @since 2025-01-12
 */
public enum LlmProviderTypeEnum {
    
    OPENAI("openai", "OpenAI", "https://api.openai.com"),
    ZHIPUAI("zhipuai", "智谱AI", "https://open.bigmodel.cn/api/paas/v4"),
    DASHSCOPE("dashscope", "阿里云百炼", "https://dashscope.aliyuncs.com/api/v1"),
    DEEPSEEK("deepseek", "DeepSeek", "https://api.deepseek.com"),
    MINIMAX("minimax", "MiniMax", "https://api.minimax.chat/v1"),
    OLLAMA("ollama", "Ollama", "http://localhost:11434"),
    BAIDU("baidu", "百度文心", "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop"),
    TENCENT("tencent", "腾讯混元", "https://hunyuan.tencentcloudapi.com"),
    ANTHROPIC("anthropic", "Anthropic Claude", "https://api.anthropic.com"),
    GITEE("gitee", "Gitee AI", "https://ai.gitee.com/api/v1"),
    VOLCENGINE("volcengine", "火山方舟", "https://ark.cn-beijing.volces.com/api/v3"),
    SILICONFLOW("siliconflow", "硅基流动", "https://api.siliconflow.cn/v1"),
    OPENROUTER("openrouter", "OpenRouter", "https://openrouter.ai/api/v1"),
    CUSTOM("custom", "自定义", "");

    private final String type;
    private final String displayName;
    private final String defaultBaseUrl;

    LlmProviderTypeEnum(String type, String displayName, String defaultBaseUrl) {
        this.type = type;
        this.displayName = displayName;
        this.defaultBaseUrl = defaultBaseUrl;
    }

    public String getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultBaseUrl() {
        return defaultBaseUrl;
    }

    /**
     * 根据类型字符串获取枚举值
     * @param type 类型字符串
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果类型不存在
     */
    public static LlmProviderTypeEnum fromType(String type) {
        for (LlmProviderTypeEnum providerType : LlmProviderTypeEnum.values()) {
            if (providerType.getType().equalsIgnoreCase(type)) {
                return providerType;
            }
        }
        throw new IllegalArgumentException("未知的提供商类型: " + type);
    }
    
    /**
     * 检查是否为有效类型
     * @param type 类型字符串
     * @return 是否有效
     */
    public static boolean isValidType(String type) {
        try {
            fromType(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 检查是否为本地部署类型
     * @return 是否为本地部署
     */
    public boolean isLocalDeployment() {
        return this == OLLAMA || this == CUSTOM;
    }

    /**
     * 检查是否需要API Key
     * @return 是否需要API Key
     */
    public boolean requiresApiKey() {
        return this != OLLAMA && this != CUSTOM;
    }
}