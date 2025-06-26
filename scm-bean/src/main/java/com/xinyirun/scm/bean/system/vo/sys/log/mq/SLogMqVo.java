package com.xinyirun.scm.bean.system.vo.sys.log.mq;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息队列日志
 * </p>
 *
 * @author zxh
 * @since 2019-10-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogMqVo implements Serializable {

    private static final long serialVersionUID = 5329100316323151273L;

    private Long id;

    /**
     * 异常"NG"，正常"OK"，进行中：“ING”
     */
    private String type;

    /**
     * 消息体中的mq message id
     */
    private String message_id;

    /**
     * mq的queue编号
     */
    private String code;

    /**
     * mq的queue名称
     */
    private String name;

    /**
     * mq的queue所对应的exchange名称
     */
    private String exchange;

    /**
     * mq的queue所对应的routing_key名称
     */
    private String routing_key;

    /**
     * mq的消息体
     */
    private String mq_data;

    /**
     * 发送情况(0：未发送（false）、1：已发送（true）)
     */
    private Boolean producer_status;

    /**
     * 消费者生成时间
     */
    private LocalDateTime producter_c_time;

    /**
     * 异常信息
     */
    private String producter_exception;

    /**
     * 执行情况(0：未接受（false）、1：已接受（true）)
     */
    private Boolean consumer_status;

    /**
     * 消费者生成时间
     */
    private LocalDateTime consumer_c_time;

    /**
     * 异常信息
     */
    private String consumer_exception;

    private String product_name;

    private String product_class_name;

    private String product_method_name;

}
