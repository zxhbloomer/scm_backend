package com.xinyirun.scm.bean.system.ao.mqsender;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @ClassName: MqMessageAo
 * @Description: mq的消息体
 * @Author: zxh
 * @date: 2019/10/16
 * @Version: 1.0
 */
@Data
@Builder
public class MqMessageAo implements Serializable {

    private static final long serialVersionUID = 7703914218244201636L;

    @Tolerate
    MqMessageAo(){}

    /**
     * messagebean类
     */
    private String messageBeanClass;

    /**
     * message内容：json
     */
    private String parameterJson;
}
