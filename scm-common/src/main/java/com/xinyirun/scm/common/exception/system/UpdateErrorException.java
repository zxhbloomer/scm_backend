package com.xinyirun.scm.common.exception.system;
import org.springframework.security.core.AuthenticationException;

/**
 * 更新出错异常
 */
public class UpdateErrorException extends AuthenticationException {

    private static final long serialVersionUID = 5022575393500654458L;

    public UpdateErrorException(String message) {
        super(message);
    }
}