package com.xinyirunscm.scm.clickhouse.entity.datachange;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据变更主日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_log_data_change_main
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogDataChangeMainClickHouseEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8750656382463150868L;
    /**
     * 主键ID，自动生成UUID
     * ClickHouse类型：UUID DEFAULT generateUUIDv4()
     */
    private String id;

    /**
     * 单号类型
     * ClickHouse类型：LowCardinality(String)
     */
    private String order_type;

    /**
     * 单号
     * ClickHouse类型：String
     */
    private String order_code;

    /**
     * 名称
     * ClickHouse类型：Nullable(String)
     */
    private String name;

    /**
     * 创建时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime c_time;

    /**
     * 最后更新时间
     * ClickHouse类型：Nullable(DateTime)
     */
    private LocalDateTime u_time;

    /**
     * 更新人名称
     * ClickHouse类型：Nullable(String)
     */
    private String u_name;

    /**
     * 更新人ID
     * ClickHouse类型：Nullable(String)
     */
    private String u_id;

    /**
     * 请求ID
     * ClickHouse类型：String
     */
    private String request_id;

    /**
     * 租户代码
     * ClickHouse类型：LowCardinality(String)
     */
    private String tenant_code;

}