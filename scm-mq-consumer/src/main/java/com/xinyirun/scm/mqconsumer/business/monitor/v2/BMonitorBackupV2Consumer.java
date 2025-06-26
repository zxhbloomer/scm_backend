package com.xinyirun.scm.mqconsumer.business.monitor.v2;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorConsumerV2Service;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
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
 * @Author: Wqf
 * @Description: 备份消费者, 使用 service 版本
 * @CreateTime : 2023/3/31 14:51
 */

@Component
@Slf4j
public class BMonitorBackupV2Consumer extends BaseMqConsumer {

    @Autowired
    private IBMonitorConsumerV2Service monitorConsumerService;

    @Autowired
    private ISLogMqConsumerService consumerService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQEnum.MqInfo.MONITOR_BACKUP_QUEUE_V2.queueCode, durable = "true"),
                    exchange = @Exchange(name=MQEnum.MqInfo.MONITOR_BACKUP_QUEUE_V2.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.MONITOR_BACKUP_QUEUE_V2.routing_key
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
            BBkMonitorLogDetailVo vo = (BBkMonitorLogDetailVo) messageContext;

            // 执行
            monitorConsumerService.exec(vo);

            // 更新消费者时间
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));

        } catch (Exception e) {
            // 保存消费者日志
            SLogMqConsumerMongoEntity logEntity = new SLogMqConsumerMongoEntity();
            logEntity.setMessage_id(message_id);
            logEntity.setConsumer_c_time(LocalDateTime.now());
            logEntity.setConsumer_exception(e.getMessage());
            logEntity.setConsumer_status(false);
            logEntity.setType("NG");
            logEntity.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(logEntity, headers, mqSenderAo);
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
