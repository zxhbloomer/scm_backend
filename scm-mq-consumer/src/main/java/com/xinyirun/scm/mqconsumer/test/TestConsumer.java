package com.xinyirun.scm.mqconsumer.test;

import com.alibaba.fastjson2.JSON;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.log.mq.SLogMqEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.core.system.service.log.mq.ISLogMqService;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @ClassName: PlatformTaskReceiver
 * @Description: Rabbit mq 租户消费者
 * @Author: zxh
 * @date: 2019/10/17
 * @Version: 1.0
 */

@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "enable", havingValue = "true")
@Component
@Slf4j
public class TestConsumer {

    @Autowired
    ISLogMqService service;

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
            value = @Queue(value = MQEnum.MqInfo.TEST.queueCode, durable = "true"),
            exchange = @Exchange(name=MQEnum.MqInfo.TEST.exchange, durable = "true", type = "topic"),
            key = MQEnum.MqInfo.TEST.routing_key
        )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) {
        log.debug("------消费者消费：start----- ");
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);

        SLogMqEntity entity = service.selectByMessageId(message_id);
        entity.setConsumer_status(true);
        try {
            MqSenderAo mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);
            log.debug(JSON.toJSONString(mqSenderAo));
            log.debug("MESSAGE_ID是【{}】;TenantDisableConsumer.onMessage方法中从【{}】接收到消息：【{}】",
                    message_id,
                    MqSenderEnum.TEST_MQ.getContent(),
                    messageContext);

            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

            boolean multiple = false;
            channel.basicAck(deliveryTag, multiple);
        } catch (Exception e) {
            // 更新异常
            entity.setConsumer_exception(LocalDateTime.now().toString() + "---" + e.getMessage());
            entity.setConsumer_status(false);
            entity.setType("NG");
            service.update(entity);
            log.error("onMessage error", e);
            log.debug("------消费者消费：error-----");
            log.debug(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            // 更新消费者时间
            entity.setConsumer_exception("");
            entity.setConsumer_status(true);
            entity.setConsumer_c_time(LocalDateTime.now());
            entity.setType("OK");
            service.update(entity);
            log.debug("------消费者消费：end-----");
        }
    }
}
