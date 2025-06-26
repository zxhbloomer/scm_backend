package com.xinyirun.scm.common.exception.api;

import com.xinyirun.scm.common.enums.api.ApiResultEnum;

/**
 * 业务异常
 * 
 * @author
 */
public class ApiBusinessException extends RuntimeException {

    private static final long serialVersionUID = 4369986987335685075L;

    private String message;
    private ApiResultEnum enumData;

    public ApiBusinessException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public ApiBusinessException(String message) {
        this.message = message;
    }

    public ApiBusinessException(ApiResultEnum enumData) {
        this.enumData = enumData;
        this.message = enumData.getMsg();
    }

    public ApiBusinessException(ApiResultEnum enumData, String msg) {
        this.enumData = enumData;
        this.message = msg;
    }

    public ApiBusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
    public ApiResultEnum getEnumData(){
        return enumData;
    }
}
