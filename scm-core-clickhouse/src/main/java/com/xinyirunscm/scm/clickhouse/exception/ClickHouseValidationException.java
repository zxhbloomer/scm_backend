package com.xinyirunscm.scm.clickhouse.exception;

/**
 * ClickHouse 输入验证异常
 * 
 * @author SCM System
 * @since 1.0.39
 */
public class ClickHouseValidationException extends ClickHouseException {

    public ClickHouseValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }

    public ClickHouseValidationException(String message, Object... params) {
        super("VALIDATION_ERROR", message, params);
    }

    public ClickHouseValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
    }
}