package com.xinyirun.scm.common.exception.whapp;

import com.xinyirun.scm.common.enums.app.AppResultEnum;

/**
 * 业务异常
 * 
 * @author
 */
public class WhAppBusinessException extends RuntimeException {

    private static final long serialVersionUID = -4696304692975587997L;

    private String message;
    private AppResultEnum enumData;

    public WhAppBusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public WhAppBusinessException(AppResultEnum enumData) {
        this.enumData = enumData;
        this.message = enumData.getMsg();
    }

    public WhAppBusinessException(AppResultEnum enumData, String msg) {
        this.enumData = enumData;
        this.message = msg;
    }

    public WhAppBusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public WhAppBusinessException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
