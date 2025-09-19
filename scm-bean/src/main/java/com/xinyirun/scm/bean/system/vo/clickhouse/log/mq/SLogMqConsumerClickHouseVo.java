package com.xinyirun.scm.bean.system.vo.clickhouse.log.mq;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * MQ消费者日志表 - ClickHouse VO
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogMqConsumerClickHouseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 9094026839776950904L;

    /**
     * 主键ID，自动生成UUID
     */
    private String id;

    /**
     * 日志类型：异常"NG"，正常"OK"
     */
    private String type;

    /**
     * 消息体中的MQ消息ID
     */
    private String message_id;

    /**
     * MQ队列编号
     */
    private String code;

    /**
     * MQ队列名称
     */
    private String name;

    /**
     * MQ队列所对应的交换机名称
     */
    private String exchange;

    /**
     * MQ队列所对应的路由键名称
     */
    private String routing_key;

    /**
     * MQ消息体内容
     */
    private String mq_data;

    /**
     * 执行情况：0未接受，1已接受
     */
    private Integer consumer_status;

    /**
     * 消费者生成时间
     */
    private LocalDateTime consumer_c_time;

    /**
     * 异常信息
     */
    private String consumer_exception;

    /**
     * 租户代码
     */
    private String tenant_code;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}