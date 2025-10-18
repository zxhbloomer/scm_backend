package com.xinyirun.scm.ai.common.exception;

import java.io.Serial;

/**
 * AI业务异常类
 *
 * 用于处理AI模块业务逻辑中的异常情况
 *
 * @author zxh
 * @since 2025-09-21
 */
public class AiBusinessException extends RuntimeException {


    @Serial
    private static final long serialVersionUID = -3860248016291827847L;

    /**
     * 构造方法
     *
     * @param message 错误消息
     */
    public AiBusinessException(String message) {
        super(message);
    }

}