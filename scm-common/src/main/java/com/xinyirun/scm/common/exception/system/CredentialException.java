package com.xinyirun.scm.common.exception.system;

import org.springframework.security.core.AuthenticationException;

/**
 * 没有权限异常
 */
public class CredentialException extends AuthenticationException {

    private static final long serialVersionUID = -920087729589688230L;

    public CredentialException(String message) {
        super(message);
    }
}
