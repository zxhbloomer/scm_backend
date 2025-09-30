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
}