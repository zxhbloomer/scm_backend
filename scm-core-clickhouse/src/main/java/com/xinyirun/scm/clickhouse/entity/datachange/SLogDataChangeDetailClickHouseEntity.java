package com.xinyirun.scm.clickhouse.entity.datachange;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据变更详细日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_log_data_change_detail
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogDataChangeDetailClickHouseEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -1327871147941985399L;
    /**
     * 主键ID，自动生成UUID
     * ClickHouse类型：UUID DEFAULT generateUUIDv4()
     */
    private String id;

    /**
     * 操作业务名：entity的注解名称
     * ClickHouse类型：Nullable(String)
     */
    private String name;

    /**
     * 数据操作类型：UPDATE|INSERT|DELETE
     * ClickHouse类型：LowCardinality(String)
     */
    private String type;

    /**
     * SQL命令类型
     * ClickHouse类型：LowCardinality(String)
     */
    private String sql_command_type;

    /**
     * 数据库表名
     * ClickHouse类型：String
     */
    private String table_name;

    /**
     * 对应的实体类名
     * ClickHouse类型：Nullable(String)
     */
    private String entity_name;

    /**
     * 单号
     * ClickHouse类型：Nullable(String)
     */
    private String order_code;

    /**
     * 调用策略模式的数据变更类名
     * ClickHouse类型：Nullable(String)
     */
    private String class_name;

    /**
     * 具体的变更前后数据，JSON格式存储
     * ClickHouse类型：Nullable(String)
     */
    private String details;

    /**
     * 数据库表对应的ID
     * ClickHouse类型：Nullable(UInt32)
     */
    private Integer table_id;

    /**
     * 创建人ID
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long c_id;

    /**
     * 修改人ID
     * ClickHouse类型：Nullable(UInt64)
     */
    private Long u_id;

    /**
     * 创建时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime u_time;

    /**
     * 创建人名称
     * ClickHouse类型：Nullable(String)
     */
    private String c_name;

    /**
     * 修改人名称
     * ClickHouse类型：Nullable(String)
     */
    private String u_name;

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