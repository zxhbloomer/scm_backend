package com.xinyirun.scm.ai.common.exception;

/**
 * AI业务异常类
 *
 * 用于处理AI模块业务逻辑中的异常情况
 *
 * @author zxh
 * @since 2025-09-21
 */
public class AiBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public AiBusinessException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 错误消息
     * @param cause   原因异常
     */
    public AiBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造方法
     *
     * @param errorCode 错误代码
     * @param message   错误消息
     */
    public AiBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造方法
     *
     * @param errorCode 错误代码
     * @param message   错误消息
     * @param cause     原因异常
     */
    public AiBusinessException(String errorCode, String message, Throwable cause) {
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