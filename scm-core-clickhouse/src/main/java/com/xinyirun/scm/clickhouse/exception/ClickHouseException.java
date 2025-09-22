package com.xinyirun.scm.clickhouse.exception;

/**
 * ClickHouse 基础异常类
 * 
 * @author SCM System
 * @since 1.0.39
 */
public class ClickHouseException extends RuntimeException {

    private final String errorCode;
    private final Object[] params;

    public ClickHouseException(String message) {
        super(message);
        this.errorCode = "CLICKHOUSE_ERROR";
        this.params = null;
    }

    public ClickHouseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.params = null;
    }

    public ClickHouseException(String errorCode, String message, Object... params) {
        super(message);
        this.errorCode = errorCode;
        this.params = params;
    }

    public ClickHouseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CLICKHOUSE_ERROR";
        this.params = null;
    }

    public ClickHouseException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.params = null;
    }

    public ClickHouseException(String errorCode, String message, Throwable cause, Object... params) {
        super(message, cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParams() {
        return params;
    }
}