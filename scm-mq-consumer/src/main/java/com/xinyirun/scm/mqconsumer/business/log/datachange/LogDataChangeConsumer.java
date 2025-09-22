package com.xinyirun.scm.mqconsumer.business.log.datachange;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeOperateClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeDetailClickHouseService;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeMainClickHouseService;
import com.xinyirun.scm.clickhouse.service.datachange.SLogDataChangeOperateClickHouseService;
import com.xinyirun.scm.clickhouse.service.mq.SLogMqConsumerClickHouseService;
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


//    @Autowired
//    private LogChangeMainMongoService logChangeMainMongoService;
    @Autowired
    private SLogDataChangeMainClickHouseService  sLogDataChangeMainClickHouseService;

//    @Autowired
//    private LogChangeMongoService logChangeMongoService;
    @Autowired
    private SLogDataChangeDetailClickHouseService sLogDataChangeDetailClickHouseService;

//    @Autowired
//    private LogChangeOperateMongoService logChangeOperateMongoService;
    @Autowired
    private SLogDataChangeOperateClickHouseService sLogDataChangeOperateClickHouseService;

//    @Autowired
//    private ISLogMqConsumerService consumerService;
    @Autowired
    private SLogMqConsumerClickHouseService consumerService;

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
            if (messageContext instanceof  SLogDataChangeMainClickHouseVo) {
                // 保存数据变更日志信息---main表
                SLogDataChangeMainClickHouseVo vo = (SLogDataChangeMainClickHouseVo) messageContext;
                vo.setTenant_code(mqSenderAo.getTenant_code());
                // 保存日志信息
                sLogDataChangeMainClickHouseService.insert(vo);
            }
            if (messageContext instanceof SDataChangeLogVo) {
                // 保存数据变更日志信息---数据表
                SDataChangeLogVo vo = (SDataChangeLogVo) messageContext;
                vo.setTenant_code(mqSenderAo.getTenant_code());
                // 保存日志信息
                sLogDataChangeDetailClickHouseService.insert(vo);
            }
            if (messageContext instanceof SLogDataChangeOperateClickHouseVo) {
                // 保存数据变更日志信息---操作表
                SLogDataChangeOperateClickHouseVo vo = (SLogDataChangeOperateClickHouseVo) messageContext;
                vo.setTenant_code(mqSenderAo.getTenant_code());
                // 保存日志信息
                sLogDataChangeOperateClickHouseService.insert(vo);
            }
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
            // 更新异常 保存日志
            SLogMqConsumerClickHouseVo vo = new SLogMqConsumerClickHouseVo();
            vo.setMessage_id(message_id);
            vo.setConsumer_c_time(LocalDateTime.now());
            vo.setConsumer_exception(e.getMessage());
            vo.setConsumer_status(0);
            vo.setType("NG");
            vo.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(vo, headers, mqSenderAo);
            log.error("------数据变更消费者消费：error-----");
            log.error(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            channel.basicAck(deliveryTag, false);
            log.debug("------消费者消费：end-----");
        }
    }
}
