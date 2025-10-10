package com.xinyirun.scm.ai.common.constant;

/**
 * AI模块常量定义
 *
 * @author zxh
 * @since 2025-09-21
 */
public final class AiConstant {

    private AiConstant() {}

    // ==================== 会话状态常量 ====================

    /**
     * 会话状态 - 活跃
     */
    public static final String CONVERSATION_STATUS_ACTIVE = "ACTIVE";

    /**
     * 会话状态 - 已归档
     */
    public static final String CONVERSATION_STATUS_ARCHIVED = "ARCHIVED";

    /**
     * 会话状态 - 已删除
     */
    public static final String CONVERSATION_STATUS_DELETED = "DELETED";

    // ==================== 处理状态常量 ====================

    /**
     * 处理状态 - 待处理
     */
    public static final Integer PROCESS_STATUS_PENDING = 0;

    /**
     * 处理状态 - 处理中
     */
    public static final Integer PROCESS_STATUS_PROCESSING = 1;

    /**
     * 处理状态 - 处理成功
     */
    public static final Integer PROCESS_STATUS_SUCCESS = 2;

    /**
     * 处理状态 - 处理失败
     */
    public static final Integer PROCESS_STATUS_FAILED = 3;

    // ==================== AI提供商常量 ====================

    /**
     * AI提供商 - OpenAI
     */
    public static final String AI_PROVIDER_OPENAI = "openai";

    /**
     * AI提供商 - Claude
     */
    public static final String AI_PROVIDER_CLAUDE = "claude";

    /**
     * AI提供商 - 智谱AI
     */
    public static final String AI_PROVIDER_ZHIPU = "zhipu";

    /**
     * AI提供商 - 百度千帆
     */
    public static final String AI_PROVIDER_QIANFAN = "qianfan";

    // ==================== 缓存相关常量 ====================

    /**
     * 缓存键前缀 - 会话
     */
    public static final String CACHE_KEY_CONVERSATION = "ai:conversation:";

    /**
     * 缓存键前缀 - 会话内容
     */
    public static final String CACHE_KEY_CONVERSATION_CONTENT = "ai:conversation:content:";

    /**
     * 缓存键前缀 - 用户会话列表
     */
    public static final String CACHE_KEY_USER_CONVERSATIONS = "ai:user:conversations:";

    // ==================== 业务限制常量 ====================

    /**
     * 会话标题最大长度
     */
    public static final int MAX_CONVERSATION_TITLE_LENGTH = 100;

    /**
     * 消息内容最大长度
     */
    public static final int MAX_MESSAGE_CONTENT_LENGTH = 10000;

    /**
     * 每页默认大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 每页最大大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 删除标记常量 ====================

    /**
     * 删除标记 - 未删除
     */
    public static final Integer NOT_DELETED = 0;

    /**
     * 删除标记 - 已删除
     */
    public static final Integer DELETED = 1;

    // ==================== 默认配置常量 ====================

    /**
     * 默认AI提供商
     */
    public static final String DEFAULT_MODEL_PROVIDER = "openai";

    /**
     * 默认AI模型名称
     */
    public static final String DEFAULT_MODEL_NAME = "gpt-3.5-turbo";

    // ==================== 错误代码常量 ====================

    /**
     * 错误代码 - 会话不存在
     */
    public static final String ERROR_CONVERSATION_NOT_FOUND = "CONVERSATION_NOT_FOUND";

    /**
     * 错误代码 - 无权限访问
     */
    public static final String ERROR_ACCESS_DENIED = "ACCESS_DENIED";

    /**
     * 错误代码 - AI服务不可用
     */
    public static final String ERROR_AI_SERVICE_UNAVAILABLE = "AI_SERVICE_UNAVAILABLE";

    /**
     * 错误代码 - 参数无效
     */
    public static final String ERROR_INVALID_PARAMETER = "INVALID_PARAMETER";

    // ==================== AI配置键常量（ai_config表config_key字段）====================

    /**
     * 嵌入模型提供商（siliconflow, ollama）
     */
    public static final String EMBEDDING_PROVIDER = "EMBEDDING_PROVIDER";

    /**
     * 嵌入模型名称
     */
    public static final String EMBEDDING_MODEL = "EMBEDDING_MODEL";

    /**
     * 嵌入模型向量维度
     */
    public static final String EMBEDDING_DIMENSIONS = "EMBEDDING_DIMENSIONS";

    // ==================== 硅基流动配置 ====================

    /**
     * 硅基流动 API Key
     */
    public static final String EMBEDDING_SILICONFLOW_API_KEY = "EMBEDDING_SILICONFLOW_API_KEY";

    /**
     * 硅基流动 API Base URL
     */
    public static final String EMBEDDING_SILICONFLOW_API_BASE = "EMBEDDING_SILICONFLOW_API_BASE";

    /**
     * 硅基流动嵌入模型名称
     */
    public static final String EMBEDDING_SILICONFLOW_MODEL = "EMBEDDING_SILICONFLOW_MODEL";

    // ==================== Ollama配置 ====================

    /**
     * Ollama Base URL
     */
    public static final String EMBEDDING_OLLAMA_BASE_URL = "EMBEDDING_OLLAMA_BASE_URL";

    /**
     * Ollama嵌入模型名称
     */
    public static final String EMBEDDING_OLLAMA_MODEL = "EMBEDDING_OLLAMA_MODEL";

    // ==================== RAG知识库LLM配置 ====================

    /**
     * RAG使用的LLM提供商（DeepSeek | Open AI | ZhiPu AI）
     */
    public static final String RAG_PROVIDER = "RAG_PROVIDER";

    /**
     * RAG DeepSeek API Key
     */
    public static final String RAG_DEEPSEEK_API_KEY = "RAG_DEEPSEEK_API_KEY";

    /**
     * RAG DeepSeek API Base URL
     */
    public static final String RAG_DEEPSEEK_API_BASE = "RAG_DEEPSEEK_API_BASE";

    /**
     * RAG DeepSeek 模型名称
     */
    public static final String RAG_DEEPSEEK_MODEL = "RAG_DEEPSEEK_MODEL";

    /**
     * RAG DeepSeek 温度
     */
    public static final String RAG_DEEPSEEK_TEMPERATURE = "RAG_DEEPSEEK_TEMPERATURE";

    /**
     * RAG DeepSeek 最大Token
     */
    public static final String RAG_DEEPSEEK_MAX_TOKENS = "RAG_DEEPSEEK_MAX_TOKENS";

    /**
     * RAG OpenAI API Key
     */
    public static final String RAG_OPENAI_API_KEY = "RAG_OPENAI_API_KEY";

    /**
     * RAG OpenAI API Base URL
     */
    public static final String RAG_OPENAI_API_BASE = "RAG_OPENAI_API_BASE";

    /**
     * RAG OpenAI 模型名称
     */
    public static final String RAG_OPENAI_MODEL = "RAG_OPENAI_MODEL";

    /**
     * RAG 智谱AI API Key
     */
    public static final String RAG_ZHIPUAI_API_KEY = "RAG_ZHIPUAI_API_KEY";

    /**
     * RAG 智谱AI API Base URL
     */
    public static final String RAG_ZHIPUAI_API_BASE = "RAG_ZHIPUAI_API_BASE";

    /**
     * RAG 智谱AI 模型名称
     */
    public static final String RAG_ZHIPUAI_MODEL = "RAG_ZHIPUAI_MODEL";

    // ==================== DeepSeek配置（聊天对话专用）====================

    /**
     * DeepSeek API Key
     */
    public static final String DEEPSEEK_API_KEY = "DEEPSEEK_API_KEY";

    /**
     * DeepSeek API Base URL
     */
    public static final String DEEPSEEK_API_BASE = "DEEPSEEK_API_BASE";

    /**
     * DeepSeek 聊天模型名称
     */
    public static final String DEEPSEEK_CHAT_MODEL = "DEEPSEEK_CHAT_MODEL";

    // ==================== 智谱AI配置 ====================

    /**
     * 智谱AI API Key
     */
    public static final String ZHIPUAI_API_KEY = "ZHIPUAI_API_KEY";

    /**
     * 智谱AI API Base URL
     */
    public static final String ZHIPUAI_API_BASE = "ZHIPUAI_API_BASE";

    /**
     * 智谱AI 聊天模型名称
     */
    public static final String ZHIPUAI_CHAT_MODEL = "ZHIPUAI_CHAT_MODEL";
}