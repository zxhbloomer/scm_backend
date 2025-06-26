package com.xinyirun.scm.mq.rabbitmq.producer.business.log.datachange;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 生产者：数据修改
 */
@Component
public class LogDataChangeProducer {

    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 生产者：数据修改，main
     * @param data
     */
    public void mqSendMq(SLogDataChangeMainMongoEntity data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_DATA_CHANGE_QUEUE);
        // 发送消息：动态code
        mqProducer.send(ao, MQEnum.MQ_LOG_DATA_CHANGE_QUEUE, data.getOrder_code());
    }

    /**
     * 生产者：数据修改，具体的数据
     * @param data
     */
    public void mqSendMq(SDataChangeLogVo data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_DATA_CHANGE_QUEUE);
        // 发送消息：动态code
        mqProducer.send(ao, MQEnum.MQ_LOG_DATA_CHANGE_QUEUE, data.getOrder_code());
    }

    /**
     * 生产者：数据修改，操作数据
     * @param data
     */
    public void mqSendMq(SLogDataChangeOperateMongoEntity data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_DATA_CHANGE_QUEUE);
        // 发送消息：动态code
        mqProducer.send(ao, MQEnum.MQ_LOG_DATA_CHANGE_QUEUE);
    }
}
