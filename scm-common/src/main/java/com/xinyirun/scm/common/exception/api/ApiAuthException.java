package com.xinyirun.scm.common.exception.api;

import com.xinyirun.scm.common.enums.api.ApiResultEnum;

/**
 * api认证失败
 * 
 * @author
 */
public class ApiAuthException extends RuntimeException {

    private static final long serialVersionUID = -3462053098594283837L;

    private String message;
    private ApiResultEnum enumData;

    public ApiAuthException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
    }

    public ApiAuthException(String message) {
        this.message = message;
    }
    public ApiAuthException(ApiResultEnum enumData) {
        this.enumData = enumData;
        this.message = enumData.getMsg();
    }

    public ApiAuthException(String message, Throwable e) {
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
