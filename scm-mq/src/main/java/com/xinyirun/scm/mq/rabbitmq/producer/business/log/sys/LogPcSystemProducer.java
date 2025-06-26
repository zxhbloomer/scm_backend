package com.xinyirun.scm.mq.rabbitmq.producer.business.log.sys;

import com.xinyirun.scm.bean.entity.mongo.log.sys.SLogSysMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wang Qianfeng
 * @Description 接口日志
 * @date 2023/3/1 10:29
 */
@Component
public class LogPcSystemProducer {

    @Autowired
    private ScmMqProducer mqProducer;


    public void mqSendMq(SLogSysMongoEntity data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_PC_SYSTEM_QUEUE);
        // 发送消息
        mqProducer.send(ao, MQEnum.MQ_LOG_PC_SYSTEM_QUEUE);
    }
}
