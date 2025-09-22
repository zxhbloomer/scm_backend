package com.xinyirun.scm.clickhouse.entity.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * MQ消费者日志表 - ClickHouse POJO实体类
 * 对应ClickHouse表：s_log_mq_consumer
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-01-18 - ClickHouse MQ Consumer Log Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogMqConsumerClickHouseEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 7200331783551619671L;

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
     * 执行情况：0未接受，1已接受
     * ClickHouse类型：UInt8
     */
    private Integer consumer_status;

    /**
     * 消费者生成时间
     * ClickHouse类型：DateTime
     */
    private LocalDateTime consumer_c_time;

    /**
     * 异常信息
     * ClickHouse类型：String
     */
    private String consumer_exception;

    /**
     * 租户代码
     * ClickHouse类型：LowCardinality(String)
     */
    private String tenant_code;

}