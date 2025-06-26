package com.xinyirun.scm.mqconsumer.business.log.quartz;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.log.mq.SLogMqEntity;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.quartz.SJobLogMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.core.system.service.log.mq.ISLogMqService;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqConsumerService;
import com.xinyirun.scm.mongodb.service.log.quartz.LogQuartzMongoService;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 数据同步错误消费者
 */

@Component
@Slf4j
public class LogQuartzConsumer extends BaseMqConsumer {

    @Autowired
    private ISLogMqService service;

    @Autowired
    private LogQuartzMongoService mongoService;


    @Autowired
    private ISLogMqConsumerService consumerService;

    /**
     * 如果有消息过来，在消费的时候调用这个方法
     */
    /**
     * 配置监听的哪一个队列，同时在没有queue和exchange的情况下会去创建并建立绑定关系
     * @param messageDataObject
     * @param headers
     * @param channel
     * @throws IOException
     */
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = MQEnum.MqInfo.LOG_QUARTZ_QUEUE.queueCode, durable = "true"),
            exchange = @Exchange(name=MQEnum.MqInfo.LOG_QUARTZ_QUEUE.exchange, durable = "true", type = "topic"),
            key = MQEnum.MqInfo.LOG_QUARTZ_QUEUE.routing_key
        )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        log.debug("------消费者消费：start----- ");
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        MqSenderAo mqSenderAo = new MqSenderAo();

        try {
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            // 设置租户数据库
            setTenantDataSource(mqSenderAo);

            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);
            // 获取日志信息
            SJobLogMongoEntity vo = (SJobLogMongoEntity) messageContext;
            mongoService.save(vo);
            // 更新消费者状态
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));

        } catch (Exception e) {
            // 保存日志
            SLogMqConsumerMongoEntity logEntity = new SLogMqConsumerMongoEntity();
            logEntity.setMessage_id(message_id);
            logEntity.setConsumer_c_time(LocalDateTime.now());
            // 更新异常
            logEntity.setConsumer_exception(e.getMessage());
            logEntity.setConsumer_status(false);
            logEntity.setType("NG");
            logEntity.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(logEntity, headers, mqSenderAo);
            log.error("onMessage error", e);
            log.error("------quartz消费者消费：error-----");
            log.error(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            channel.basicAck(deliveryTag, false);
            log.debug("------消费者消费：end-----");
        }
    }

    /**
     * 如果没有日志, 新增保存
     * @param entity
     * @param messageDataObject
     * @param messageId
     */
    private void insertErrorMqLog(SLogMqEntity entity, Message messageDataObject, String messageId) {
        entity.setMessage_id(messageId);
        entity.setError(JSONObject.toJSONString(messageDataObject));
        service.save(entity);
    }
}
