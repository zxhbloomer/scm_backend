package com.xinyirun.scm.clickhouse.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * 系统日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_log_sys
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 * @updated 2025-01-18 - 适配ClickHouse Client v2
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogSysClickHouseEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 5520532239510860027L;
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
     * 操作说明描述
     * ClickHouse类型：String
     */
    private String operation;

    /**
     * 操作耗时（毫秒）
     * ClickHouse类型：UInt64
     */
    private Long time;

    /**
     * 调用的类名
     * ClickHouse类型：String
     */
    private String class_name;

    /**
     * 调用的方法名
     * ClickHouse类型：String
     */
    private String class_method;

    /**
     * HTTP请求方法（GET/POST等）
     * ClickHouse类型：LowCardinality(String)
     */
    private String http_method;

    /**
     * 请求参数（JSON格式）
     * ClickHouse类型：String
     */
    private String params;

    /**
     * Session信息（JSON格式）
     * ClickHouse类型：String
     */
    private String session;

    /**
     * 请求URL地址
     * ClickHouse类型：String
     */
    private String url;

    /**
     * 客户端IP地址
     * ClickHouse类型：String
     */
    private String ip;

    /**
     * 异常信息详情
     * ClickHouse类型：String
     */
    private String exception;

    /**
     * 创建时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime c_time;

    /**
     * 请求唯一标识ID
     * ClickHouse类型：String
     */
    private String request_id;

    /**
     * 接口返回信息
     * ClickHouse类型：String
     */
    private String result;

    /**
     * 租户代码
     * ClickHouse类型：LowCardinality(String)
     */
    private String tenant_code;

}