package com.xinyirun.scm.bean.entity.mongo.log.mq;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息队列消费日志
 * </p>
 *
 * @author WQF
 * @since 2019-10-18
 */
@Data
@Document("s_log_mq_consumer")
public class SLogMqConsumerMongoEntity implements Serializable {

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

    /**
     * 租户code
     */
    private String tenant_code;
}
