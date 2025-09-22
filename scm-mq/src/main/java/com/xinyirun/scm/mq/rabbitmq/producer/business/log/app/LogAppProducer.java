//package com.xinyirun.scm.mq.rabbitmq.producer.business.log.app;
//
//import com.xinyirun.scm.bean.entity.mongo.log.app.SLogAppMongoEntity;
//import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
//import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
//import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
//import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
//import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author Wang Qianfeng
// * @Description TODO
// * @date 2023/3/1 15:56
// */
//@Component
//public class LogAppProducer {
//
//    @Autowired
//    private ScmMqProducer mqProducer;
//
//    public void mqSendMq(SLogAppMongoEntity data){
//        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_APP_QUEUE);
//        // 发送消息
//        mqProducer.send(ao, MQEnum.MQ_LOG_APP_QUEUE);
//    }
//}
