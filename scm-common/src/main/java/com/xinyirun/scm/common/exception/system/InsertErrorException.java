package com.xinyirun.scm.common.exception.system;
import org.springframework.security.core.AuthenticationException;

/**
 * 插入出错异常
 * @author zxh
 */
public class InsertErrorException extends AuthenticationException {

    private static final long serialVersionUID = 5022575393500654458L;

    public InsertErrorException(String message) {
        super(message);
    }
}