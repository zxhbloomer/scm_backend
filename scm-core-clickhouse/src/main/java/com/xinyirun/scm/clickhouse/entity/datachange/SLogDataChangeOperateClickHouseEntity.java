package com.xinyirun.scm.clickhouse.entity.datachange;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据变更操作日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_log_data_change_operate
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogDataChangeOperateClickHouseEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -946162600270680984L;

    /**
     * 主键ID，自动生成UUID
     * ClickHouse类型：UUID DEFAULT generateUUIDv4()
     */
    private String id;

    /**
     * 日志类型：异常"NG"，正常"OK"
     * ClickHouse类型：LowCardinality(String)
     */
    private String type;

    /**
     * 操作用户账号
     * ClickHouse类型：String
     */
    private String user_name;

    /**
     * 员工姓名
     * ClickHouse类型：String
     */
    private String staff_name;

    /**
     * 员工ID
     * ClickHouse类型：Nullable(String)
     */
    private String staff_id;

    /**
     * 操作说明描述
     * ClickHouse类型：Nullable(String)
     */
    private String operation;

    /**
     * 操作耗时毫秒
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long time;

    /**
     * 调用的类名
     * ClickHouse类型：Nullable(String)
     */
    private String class_name;

    /**
     * 调用的方法名
     * ClickHouse类型：Nullable(String)
     */
    private String class_method;

    /**
     * HTTP请求方法（GET/POST等）
     * ClickHouse类型：LowCardinality(String)
     */
    private String http_method;

    /**
     * 请求URL地址
     * ClickHouse类型：Nullable(String)
     */
    private String url;

    /**
     * 客户端IP地址
     * ClickHouse类型：Nullable(String)
     */
    private String ip;

    /**
     * 异常信息详情
     * ClickHouse类型：Nullable(String)
     */
    private String exception;

    /**
     * 操作时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime operate_time;

    /**
     * 页面名称
     * ClickHouse类型：Nullable(String)
     */
    private String page_name;

    /**
     * 请求唯一标识ID
     * ClickHouse类型：String
     */
    private String request_id;

    /**
     * 租户代码
     * ClickHouse类型：LowCardinality(String)
     */
    private String tenant_code;

}