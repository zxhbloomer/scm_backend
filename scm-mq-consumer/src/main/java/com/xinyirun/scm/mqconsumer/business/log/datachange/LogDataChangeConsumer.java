package com.xinyirun.scm.mqconsumer.business.log.datachange;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeMainMongoService;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeMongoService;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeOperateMongoService;
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
public class LogDataChangeConsumer extends BaseMqConsumer {


    @Autowired
    private LogChangeMainMongoService logChangeMainMongoService;

    @Autowired
    private LogChangeMongoService logChangeMongoService;

    @Autowired
    private LogChangeOperateMongoService logChangeOperateMongoService;

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
                    value = @Queue(value = MQEnum.MqInfo.LOG_DATA_CHANGE_QUEUE.queueCode, durable = "true"),
                    exchange = @Exchange(name=MQEnum.MqInfo.LOG_DATA_CHANGE_QUEUE.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.LOG_DATA_CHANGE_QUEUE.routing_key
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
            if (messageContext instanceof  SLogDataChangeMainMongoEntity) {
                // 保存数据变更日志信息---main表
                SLogDataChangeMainMongoEntity vo = (SLogDataChangeMainMongoEntity) messageContext;
                // 保存日志信息
                logChangeMainMongoService.save(vo);
            }
            if (messageContext instanceof SDataChangeLogVo) {
                // 保存数据变更日志信息---数据表
                SDataChangeLogVo vo = (SDataChangeLogVo) messageContext;
                // 保存日志信息
                logChangeMongoService.save(vo);
            }
            if (messageContext instanceof SLogDataChangeOperateMongoEntity) {
                // 保存数据变更日志信息---操作表
                SLogDataChangeOperateMongoEntity vo = (SLogDataChangeOperateMongoEntity) messageContext;
                // 保存日志信息
                logChangeOperateMongoService.save(vo);
            }
        } catch (Exception e) {
            // 更新异常 保存日志
            SLogMqConsumerMongoEntity logEntity = new SLogMqConsumerMongoEntity();
            logEntity.setMessage_id(message_id);
            logEntity.setConsumer_c_time(LocalDateTime.now());
            logEntity.setConsumer_exception(e.getMessage());
            logEntity.setConsumer_status(false);
            logEntity.setType("NG");
            logEntity.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(logEntity, headers, mqSenderAo);
            log.error("------数据变更消费者消费：error-----");
            log.error(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            channel.basicAck(deliveryTag, false);
            log.debug("------消费者消费：end-----");
        }
    }
}
