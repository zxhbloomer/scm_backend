package com.xinyirun.scm.ai.common.exception;

/**
 * AI会话相关异常
 *
 * 用于处理AI会话业务逻辑中的异常情况
 * 包括会话创建失败、会话内容处理失败等
 *
 * @author zxh
 * @since 1.0.0
 */
public class AiConversationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public AiConversationException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause 原因异常
     */
    public AiConversationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public AiConversationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param cause 原因异常
     */
    public AiConversationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误代码
     *
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误代码
     *
     * @param errorCode 错误代码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}