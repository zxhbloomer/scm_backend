package com.xinyirunscm.scm.clickhouse.entity.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * MQ生产者日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_log_mq_producer
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-01-18 - ClickHouse MQ Producer Log Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogMqProducerClickHouseEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -2070363461236348789L;

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
     * 消息体中的MQ消息ID
     * ClickHouse类型：String
     */
    private String message_id;

    /**
     * MQ队列编号
     * ClickHouse类型：String
     */
    private String code;

    /**
     * MQ队列名称
     * ClickHouse类型：String
     */
    private String name;

    /**
     * MQ队列所对应的交换机名称
     * ClickHouse类型：String
     */
    private String exchange;

    /**
     * MQ队列所对应的路由键名称
     * ClickHouse类型：String
     */
    private String routing_key;

    /**
     * MQ消息体内容
     * ClickHouse类型：String
     */
    private String mq_data;

    /**
     * 发送情况：0未发送，1已发送
     * ClickHouse类型：UInt8
     */
    private Integer producer_status;

    /**
     * 生产者生成时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime producter_c_time;

    /**
     * 异常信息
     * ClickHouse类型：String
     */
    private String producter_exception;

    /**
     * 租户代码
     * ClickHouse类型：LowCardinality(String)
     */
    private String tenant_code;

}