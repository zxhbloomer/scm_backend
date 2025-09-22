package com.xinyirun.scm.clickhouse.entity.quartz;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 定时任务调度日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_job_log
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-01-18 - 适配ClickHouse Client v2
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SJobLogClickHouseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 8920532239510860027L;

    /**
     * 主键ID，自动生成UUID
     * ClickHouse类型：UUID DEFAULT generateUUIDv4()
     */
    private String id;

    /**
     * 任务主键
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long job_id;

    /**
     * 任务名称
     * ClickHouse类型：String
     */
    private String job_name;

    /**
     * 任务组类型
     * ClickHouse类型：LowCardinality(String)
     */
    private String job_group_type;

    /**
     * 关联编号
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long job_serial_id;

    /**
     * 关联表名字
     * ClickHouse类型：Nullable(String)
     */
    private String job_serial_type;

    /**
     * 任务描述
     * ClickHouse类型：Nullable(String)
     */
    private String job_desc;

    /**
     * 任务简称
     * ClickHouse类型：Nullable(String)
     */
    private String job_simple_name;

    /**
     * Bean名称
     * ClickHouse类型：String
     */
    private String class_name;

    /**
     * 方法名称
     * ClickHouse类型：String
     */
    private String method_name;

    /**
     * 参数类型
     * ClickHouse类型：Nullable(String)
     */
    private String param_class;

    /**
     * 参数
     * ClickHouse类型：Nullable(String)
     */
    private String param_data;

    /**
     * 表达式
     * ClickHouse类型：Nullable(String)
     */
    private String cron_expression;

    /**
     * 是否并发执行（0允许 1禁止）
     * ClickHouse类型：Nullable(UInt8)
     */
    private Boolean concurrent;

    /**
     * 判断是否是cron表达式，还是simpletrigger
     * ClickHouse类型：Nullable(UInt8)
     */
    private Boolean is_cron;

    /**
     * 计划策略：0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     * ClickHouse类型：LowCardinality(String)
     */
    private String misfire_policy;

    /**
     * 是否是已经删除
     * ClickHouse类型：UInt8
     */
    private Boolean is_del;

    /**
     * 是否有效
     * ClickHouse类型：Nullable(UInt8)
     */
    private Boolean is_effected;

    /**
     * 首次执行时间
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime fire_time;

    /**
     * 计划首次执行时间
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime scheduled_fire_time;

    /**
     * 上次执行时间
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime prev_fire_time;

    /**
     * next_fire_time
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime next_fire_time;

    /**
     * 运行次数
     * ClickHouse类型：Nullable(UInt32)
     */
    private Integer run_times;

    /**
     * 执行情况
     * ClickHouse类型：Nullable(String)
     */
    private String msg;

    /**
     * 创建人ID
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long c_id;

    /**
     * 创建人名称
     * ClickHouse类型：Nullable(String)
     */
    private String c_name;

    /**
     * 创建时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime c_time;

    /**
     * 修改人ID
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long u_id;

    /**
     * 修改时间
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime u_time;

    /**
     * 租户代码
     * ClickHouse类型：LowCardinality(String)
     */
    private String tenant_code;
}