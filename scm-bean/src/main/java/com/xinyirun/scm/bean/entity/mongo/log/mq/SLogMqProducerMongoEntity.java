package com.xinyirun.scm.bean.entity.mongo.log.mq;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document("s_log_mq_producer")
public class SLogMqProducerMongoEntity implements Serializable {

    private static final long serialVersionUID = -622815795388635855L;

    @Id
    private String id;

    /**
     * 异常"NG"，正常"OK"
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
     * 租户code
     */
    private String tenant_code;
}
