package com.xinyirun.scm.common.exception.system;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zxh
 * @date 2019/9/27
 */
public class SystemInvalidSessionStrategyException extends AuthenticationException {

    private static final long serialVersionUID = -1468881768452376477L;

    public SystemInvalidSessionStrategyException(String message) {
        super(message);
    }
}
