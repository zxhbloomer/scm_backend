package com.xinyirun.scm.common.exception.jwt;

import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import org.springframework.security.core.AuthenticationException;

/**
 * jwt认证失败
 * 
 * @author
 */
public class JWTAuthException extends AuthenticationException {

    private static final long serialVersionUID = 2587130333493061481L;

    private String message;
    private ApiResultEnum enumData;

    public JWTAuthException(Throwable cause) {
        super(cause.getMessage(), cause);
        this.message = cause.getMessage();
    }

    public JWTAuthException(String message) {
        super(message);
        this.message = message;
    }

    public JWTAuthException(String message, Throwable e) {
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
