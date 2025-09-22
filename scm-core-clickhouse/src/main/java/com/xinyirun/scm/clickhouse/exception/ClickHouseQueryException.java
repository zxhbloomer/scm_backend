package com.xinyirun.scm.clickhouse.exception;

import java.io.Serial;

/**
 * ClickHouse 查询执行异常
 * 
 * @author SCM System
 * @since 1.0.39
 */
public class ClickHouseQueryException extends ClickHouseException {

    @Serial
    private static final long serialVersionUID = -766553688814057392L;

    private final String sql;

    public ClickHouseQueryException(String message, String sql) {
        super("QUERY_ERROR", message);
        this.sql = sql;
    }

    public ClickHouseQueryException(String message, String sql, Throwable cause) {
        super("QUERY_ERROR", message, cause);
        this.sql = sql;
    }

    public ClickHouseQueryException(String message, String sql, Object... params) {
        super("QUERY_ERROR", message, params);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}