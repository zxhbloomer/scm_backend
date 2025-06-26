package com.xinyirun.scm.common.exception.autocode;

/**
 * 自动编号异常
 * 
 * @author
 */
public class AutoCodeException extends RuntimeException {

    private static final long serialVersionUID = -2317843243704714020L;

    private String message;

    public AutoCodeException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public AutoCodeException(String message) {
        this.message = message;
    }

    public AutoCodeException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
