package com.xinyirun.scm.common.exception.app;

import com.xinyirun.scm.common.enums.app.AppResultEnum;

/**
 * 业务异常
 * 
 * @author
 */
public class AppBusinessException extends RuntimeException {

    private static final long serialVersionUID = 5479579033115929083L;

    private String message;
    private AppResultEnum enumData;

    public AppBusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public AppBusinessException(AppResultEnum enumData) {
        this.enumData = enumData;
        this.message = enumData.getMsg();
    }

    public AppBusinessException(AppResultEnum enumData, String msg) {
        this.enumData = enumData;
        this.message = msg;
    }

    public AppBusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public AppBusinessException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
