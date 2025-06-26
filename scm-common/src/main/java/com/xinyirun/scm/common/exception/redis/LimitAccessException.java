package com.xinyirun.scm.common.exception.redis;

/**
 * 限流异常
 */
public class LimitAccessException extends RuntimeException {

    private static final long serialVersionUID = 5029434576501125401L;

    private String message;

    public LimitAccessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public LimitAccessException(String message) {
        this.message = message;
    }

    public LimitAccessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}