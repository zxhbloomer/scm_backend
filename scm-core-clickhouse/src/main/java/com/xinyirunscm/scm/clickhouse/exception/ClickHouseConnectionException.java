package com.xinyirunscm.scm.clickhouse.exception;

/**
 * ClickHouse 连接异常
 * 
 * @author SCM System
 * @since 1.0.39
 */
public class ClickHouseConnectionException extends ClickHouseException {

    public ClickHouseConnectionException(String message) {
        super("CONNECTION_ERROR", message);
    }

    public ClickHouseConnectionException(String message, Throwable cause) {
        super("CONNECTION_ERROR", message, cause);
    }

    public ClickHouseConnectionException(String message, Object... params) {
        super("CONNECTION_ERROR", message, params);
    }
}