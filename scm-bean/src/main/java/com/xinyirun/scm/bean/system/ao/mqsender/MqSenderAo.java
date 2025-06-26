package com.xinyirun.scm.bean.system.ao.mqsender;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @ClassName: MqSenderAo
 * @Description: 消息队列Ao
 * @Author: zxh
 * @date: 2019/10/16
 * @Version: 1.0
 */
@Data
@Builder
public class MqSenderAo implements Serializable {

    private static final long serialVersionUID = 1145461000719110996L;

    @Tolerate
    public MqSenderAo(){}

    /**
     * 消息队列主键
     */
    private String key;

    /**
     * 类型
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 消息体
     */
    private MqMessageAo mqMessageAo;

    /**
     * 租户tenant_code
     */
    private String tenant_code;

}
