package com.xinyirun.scm.common.exception.jwt;

import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import org.springframework.security.core.AuthenticationException;

/**
 * jwt认证失败
 * 
 * @author
 */
public class JWTAuthGetException extends AuthenticationException {

    private static final long serialVersionUID = -285019282515902229L;

    private String message;
    private ApiResultEnum enumData;

    public JWTAuthGetException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.message = cause.getMessage();
    }

    public JWTAuthGetException(String message) {
        super(message);
        this.message = message;
    }

    public JWTAuthGetException(String message, Throwable e) {
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
