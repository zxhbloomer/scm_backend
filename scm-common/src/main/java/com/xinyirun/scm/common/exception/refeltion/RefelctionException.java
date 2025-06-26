package com.xinyirun.scm.common.exception.refeltion;

/**
 * 业务异常
 * 
 * @author
 */
public class RefelctionException extends RuntimeException {

    private static final long serialVersionUID = -5479579033115929083L;

    private String message;

    public RefelctionException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public RefelctionException(String message) {
        this.message = message;
    }

    public RefelctionException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
