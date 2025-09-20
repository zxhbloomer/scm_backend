package com.xinyirun.scm.bean.system.ao.mqsender.builder;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.system.ao.mqsender.MqMessageAo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;

/**
 * @ClassName: MqSenderAoBuilder
 * @Author: zxh
 * @date: 2019/10/17
 * @Version: 1.0
 */
public class MqSenderAoBuilder {

    /**
     * 构筑mq的bean
     * @param messageData    消息数据
     * @param mqSenderEnum   消息code，消息名称
     * @return
     */
    public static MqSenderAo buildMqSenderAo(Object messageData, MqSenderEnum mqSenderEnum){
        MqSenderAo mqSenderAo = MqSenderAo.builder()
            .mqMessageAo(
                MqMessageAo.builder()
                    .messageBeanClass(messageData.getClass().getName())
                    .parameterJson(JSON.toJSONString(messageData))
                    .build()
            )
            .key(UuidUtil.randomUUID())
            .type(mqSenderEnum.getCode().toString())
            .name(mqSenderEnum.getName())
//            .tenant_code(DataSourceHelper.getCurrentDataSourceName())
            .build();
        return mqSenderAo;
    }
}
