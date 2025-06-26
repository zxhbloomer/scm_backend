package com.xinyirun.scm.common.exception.system;
import org.springframework.security.core.AuthenticationException;

/**
 * 密码出错异常
 * @author zxh
 */
public class PasswordException extends AuthenticationException {

    private static final long serialVersionUID = 5022575393500654458L;

    public PasswordException(String message) {
        super(message);
    }
}