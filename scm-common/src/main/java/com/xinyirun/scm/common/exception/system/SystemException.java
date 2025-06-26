package com.xinyirun.scm.common.exception.system;

/**
 * 系统异常
 * 
 * @author
 */
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 5479579133115929083L;
    private String message;

    public SystemException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public SystemException(String message) {
        this.message = message;
    }

    public SystemException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
