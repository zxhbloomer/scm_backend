package com.xinyirun.scm.mqconsumer.business.log.api;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.mongo.log.api.SLogApiMongoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.mongodb.bean.entity.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mongodb.service.log.api.LogApiMongoService;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqConsumerService;
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
public class LogApiConsumer extends BaseMqConsumer {

    @Autowired
    private ISLogMqConsumerService consumerService;

    @Autowired
    private LogApiMongoService mongoService;

    /**
     * 如果有消息过来，在消费的时候调用这个方法
     */
    /**
     * 配置监听的哪一个队列，同时在没有queue和exchange的情况下会去创建并建立绑定关系
     * @param messageDataObject
     * @param headers
     * @param channel
     * @param channel
     * @throws IOException
     */
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = MQEnum.MqInfo.LOG_API_QUEUE.queueCode, durable = "true"),
            exchange = @Exchange(name=MQEnum.MqInfo.LOG_API_QUEUE.exchange, durable = "true", type = "topic"),
            key = MQEnum.MqInfo.LOG_API_QUEUE.routing_key
        )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        log.debug("------消费者消费：start----- ");
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);

        MqSenderAo mqSenderAo = new MqSenderAo();

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            // 设置租户数据库
            setTenantDataSource(mqSenderAo);

            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);
            // 获取日志信息
            SLogApiMongoEntity vo = (SLogApiMongoEntity) messageContext;
            mongoService.save(vo);
            // 更新消费者时间
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));
            /**
             *  没有错误，更新mq消费者日志
             */
            SLogMqConsumerClickHouseVo consumerVo = new SLogMqConsumerClickHouseVo();
            consumerVo.setMessage_id(message_id);
            consumerVo.setConsumer_c_time(LocalDateTime.now());
            consumerVo.setConsumer_status(0);
            consumerVo.setType("OK");
            consumerVo.setTenant_code(mqSenderAo.getTenant_code());
            consumerVo.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(consumerVo, headers, mqSenderAo);
        } catch (Exception e) {
            // 保存日志
            SLogMqConsumerClickHouseVo vo = new SLogMqConsumerClickHouseVo();
            vo.setMessage_id(message_id);
            vo.setConsumer_c_time(LocalDateTime.now());
            vo.setConsumer_exception(e.getMessage());
            vo.setConsumer_status(0);
            vo.setType("NG");
            vo.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(vo, headers, mqSenderAo);
            log.error("onMessage error", e);
            log.error("------api消费者消费：error-----");
            log.error(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            channel.basicAck(deliveryTag, false);
            log.debug("------消费者消费：end-----");
        }
    }
}
