package com.xinyirun.scm.ai.common.constant;

/**
 * AI会话相关常量定义
 *
 * 定义AI会话系统中使用的各种常量
 * 包括状态码、类型定义、配置参数等
 *
 * @author SCM-AI模块
 * @since 1.0.0
 */
public interface AiConversationConstants {

    // ==================== HTTP状态码 ====================
    /** {@code 200 OK} 成功 */
    Integer SC_OK_200 = 200;

    /** {@code 400 Bad Request} 请求参数错误 */
    Integer SC_BAD_REQUEST_400 = 400;

    /** {@code 500 Internal Server Error} 服务器内部错误 */
    Integer SC_INTERNAL_SERVER_ERROR_500 = 500;

    // ==================== 会话内容类型 ====================
    /** 用户消息类型 */
    String CONTENT_TYPE_USER = "USER";

    /** AI回复消息类型 */
    String CONTENT_TYPE_AI = "AI";

    /** 系统消息类型 */
    String CONTENT_TYPE_SYSTEM = "SYSTEM";

    // ==================== 会话状态 ====================
    /** 活跃状态 */
    String CONVERSATION_STATUS_ACTIVE = "ACTIVE";

    /** 已归档状态 */
    String CONVERSATION_STATUS_ARCHIVED = "ARCHIVED";

    /** 已删除状态 */
    String CONVERSATION_STATUS_DELETED = "DELETED";

    // ==================== 处理状态 ====================
    /** 处理中 */
    String PROCESS_STATUS_PENDING = "PENDING";

    /** 处理成功 */
    String PROCESS_STATUS_SUCCESS = "SUCCESS";

    /** 处理失败 */
    String PROCESS_STATUS_FAILED = "FAILED";

    /** 处理超时 */
    String PROCESS_STATUS_TIMEOUT = "TIMEOUT";

    // ==================== 错误代码 ====================
    /** 会话不存在 */
    String ERROR_CONVERSATION_NOT_FOUND = "CONVERSATION_NOT_FOUND";

    /** 会话内容不存在 */
    String ERROR_CONTENT_NOT_FOUND = "CONTENT_NOT_FOUND";

    /** 无效的会话内容 */
    String ERROR_INVALID_CONTENT = "INVALID_CONTENT";

    /** AI服务不可用 */
    String ERROR_AI_SERVICE_UNAVAILABLE = "AI_SERVICE_UNAVAILABLE";

    /** 内容包含敏感信息 */
    String ERROR_SENSITIVE_CONTENT = "SENSITIVE_CONTENT";

    /** 会话已达到最大长度 */
    String ERROR_CONVERSATION_TOO_LONG = "CONVERSATION_TOO_LONG";

    // ==================== 配置参数 ====================
    /** 默认会话标题最大长度 */
    Integer DEFAULT_TITLE_MAX_LENGTH = 100;

    /** 默认内容最大长度（20MB） */
    Integer DEFAULT_CONTENT_MAX_LENGTH = 20 * 1024 * 1024;

    /** 默认摘要长度 */
    Integer DEFAULT_SUMMARY_LENGTH = 200;

    /** 每个会话最大内容数量 */
    Integer MAX_CONTENT_PER_CONVERSATION = 1000;

    /** 默认分页大小 */
    Integer DEFAULT_PAGE_SIZE = 20;

    /** 最大分页大小 */
    Integer MAX_PAGE_SIZE = 100;

    // ==================== AI模型相关 ====================
    /** OpenAI模型提供商 */
    String AI_PROVIDER_OPENAI = "openai";

    /** Anthropic模型提供商 */
    String AI_PROVIDER_ANTHROPIC = "anthropic";

    /** 智谱AI模型提供商 */
    String AI_PROVIDER_ZHIPUAI = "zhipuai";

    /** 通义千问模型提供商 */
    String AI_PROVIDER_DASHSCOPE = "dashscope";

    /** DeepSeek模型提供商 */
    String AI_PROVIDER_DEEPSEEK = "deepseek";

    /** Ollama本地模型提供商 */
    String AI_PROVIDER_OLLAMA = "ollama";

    // ==================== 缓存键前缀 ====================
    /** 会话缓存键前缀 */
    String CACHE_KEY_CONVERSATION = "ai:conversation:";

    /** 会话内容缓存键前缀 */
    String CACHE_KEY_CONTENT = "ai:content:";

    /** 用户会话列表缓存键前缀 */
    String CACHE_KEY_USER_CONVERSATIONS = "ai:user:conversations:";

    /** AI模型状态缓存键前缀 */
    String CACHE_KEY_AI_MODEL_STATUS = "ai:model:status:";

    // ==================== WebSocket消息类型 ====================
    /** WebSocket连接消息 */
    String WS_MSG_TYPE_CONNECT = "CONNECT";

    /** WebSocket断开消息 */
    String WS_MSG_TYPE_DISCONNECT = "DISCONNECT";

    /** WebSocket聊天消息 */
    String WS_MSG_TYPE_CHAT = "CHAT";

    /** WebSocket状态更新消息 */
    String WS_MSG_TYPE_STATUS = "STATUS";

    /** WebSocket错误消息 */
    String WS_MSG_TYPE_ERROR = "ERROR";

    // ==================== 时间相关 ====================
    /** 会话超时时间（毫秒） - 1小时 */
    Long CONVERSATION_TIMEOUT_MS = 60 * 60 * 1000L;

    /** AI响应超时时间（毫秒） - 30秒 */
    Long AI_RESPONSE_TIMEOUT_MS = 30 * 1000L;

    /** 缓存过期时间（秒） - 24小时 */
    Long CACHE_EXPIRE_SECONDS = 24 * 60 * 60L;

    // ==================== 日志相关 ====================
    /** 日志类型：会话创建 */
    String LOG_TYPE_CONVERSATION_CREATE = "CONVERSATION_CREATE";

    /** 日志类型：会话更新 */
    String LOG_TYPE_CONVERSATION_UPDATE = "CONVERSATION_UPDATE";

    /** 日志类型：内容添加 */
    String LOG_TYPE_CONTENT_ADD = "CONTENT_ADD";

    /** 日志类型：AI响应 */
    String LOG_TYPE_AI_RESPONSE = "AI_RESPONSE";

    /** 日志类型：错误处理 */
    String LOG_TYPE_ERROR = "ERROR";

    // ==================== 权限相关 ====================
    /** 会话读权限 */
    String PERMISSION_CONVERSATION_READ = "ai:conversation:read";

    /** 会话写权限 */
    String PERMISSION_CONVERSATION_WRITE = "ai:conversation:write";

    /** 会话删除权限 */
    String PERMISSION_CONVERSATION_DELETE = "ai:conversation:delete";

    /** AI服务使用权限 */
    String PERMISSION_AI_SERVICE_USE = "ai:service:use";

    // ==================== 文件上传相关 ====================
    /** 允许的文件类型 */
    String[] ALLOWED_FILE_TYPES = {"txt", "pdf", "doc", "docx", "md", "json", "xml"};

    /** 最大文件大小（字节） - 10MB */
    Long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    /** 文件上传路径前缀 */
    String FILE_UPLOAD_PATH_PREFIX = "ai/conversations/";
}