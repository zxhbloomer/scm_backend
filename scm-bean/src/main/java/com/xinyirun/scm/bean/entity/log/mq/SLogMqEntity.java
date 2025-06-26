package com.xinyirun.scm.bean.entity.log.mq;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
@TableName("s_log_mq")
public class SLogMqEntity implements Serializable {

    private static final long serialVersionUID = 4850292309675522117L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 异常"NG"，正常"OK"，进行中：“ING”
     */
    @TableField("type")
    private String type;

    /**
     * 消息体中的mq message id
     */
    @TableField("message_id")
    private String message_id;

    /**
     * mq的queue编号
     */
    @TableField("code")
    private String code;

    /**
     * mq的queue名称
     */
    @TableField("name")
    private String name;

    /**
     * mq的queue所对应的exchange名称
     */
    @TableField("exchange")
    private String exchange;

    /**
     * mq的queue所对应的routing_key名称
     */
    @TableField("routing_key")
    private String routing_key;

    /**
     * mq的消息体
     */
    @TableField("mq_data")
    private String mq_data;

    /**
     * 发送情况(0：未发送（false）、1：已发送（true）)
     */
    @TableField("producer_status")
    private Boolean producer_status;

    /**
     * 消费者生成时间
     */
    @TableField("producter_c_time")
    private LocalDateTime producter_c_time;

    /**
     * 异常信息
     */
    @TableField("producter_exception")
    private String producter_exception;

    /**
     * 执行情况(0：未接受（false）、1：已接受（true）)
     */
    @TableField("consumer_status")
    private Boolean consumer_status;

    /**
     * 消费者生成时间
     */
    @TableField("consumer_c_time")
    private LocalDateTime consumer_c_time;

    /**
     * 异常信息
     */
    @TableField("consumer_exception")
    private String consumer_exception;

    @TableField("product_name")
    private String product_name;

    @TableField("product_class_name")
    private String product_class_name;

    @TableField("product_method_name")
    private String product_method_name;

    @TableField("error")
    private String error;


}
