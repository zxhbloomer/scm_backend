package com.xinyirun.scm.common.exception.system;

/**
 * 业务异常
 * 
 * @author
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 5479579033115929083L;

    private String message;

    public BusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
