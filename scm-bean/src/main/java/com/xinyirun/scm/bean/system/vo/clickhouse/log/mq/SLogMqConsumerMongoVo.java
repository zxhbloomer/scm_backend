package com.xinyirun.scm.bean.system.vo.clickhouse.log.mq;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/4/20 14:49
 */
@Data
public class SLogMqConsumerMongoVo implements Serializable {

    private static final long serialVersionUID = 8705613591648596031L;
    /**
     * 主键 id
     */
    private String id;

    /**
     * 类型, OK, ING
     */
    private String type;

    /**
     * 是否发送
     */
    private Boolean producer_status;

    /**
     * message_id
     */
    private String message_id;

    /**
     * 名称
     */
    private String name;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 队列
     */
    private String code;

    /**
     * routing_key
     */
    private String routing_key;

    /**
    * 消息体
    */
    private String mq_data;

    /**
     * 发送时间
     */
    private LocalDateTime consumer_c_time;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    /**
     * 发送时间起止
     */
    private LocalDateTime consumer_c_time_start
        ,consumer_c_time_end;

    /**
     * 异常信息
     */
    private String consumer_exception;


}
