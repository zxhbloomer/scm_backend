package com.xinyirun.scm.mq.rabbitmq.producer.detail;

import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: test
 * @Description: test的mq
 * @Author: zxh
 * @date: 2019/12/16
 * @Version: 1.0
 */
@Component
public class TestMqProducter {

    /**
     * 调用消息队列
     */
    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 调用mq
     *
     */
    public void mqSendAfterDataSave(BInVo data){
        // 初始化要发生mq的bean
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.TEST_MQ);
        // 发送消息
        mqProducer.send(ao, MQEnum.MQ_TEST);
    }

}
