package com.xinyirun.scm.mq.rabbitmq.producer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqProducerClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqProducerMongoVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.mq.MessageProductQueueException;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirunscm.scm.clickhouse.service.mq.SLogMqProducerClickHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 生产者
 *
 * @author zxh
 * @date 2019年 10月14日 21:46:52
 */
//@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "enable", havingValue = "true")
@Component
@Slf4j
public class ScmMqProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    /**
     * 考虑数据放到redis中，然后需要回调则考虑回调，没有就没有
     *
     * CorrelationData的id，使用redis的key
     *
     */
    @Autowired
//    @Qualifier("scm_RabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private MongoTemplate mongoTemplate;

    @Autowired
    private SLogMqProducerClickHouseService sLogMqProducerClickHouseService;

    /**
     * 消息发送
     * @param mqSenderAo
     * @param mqenum
     */
    public void send(MqSenderAo mqSenderAo, MQEnum mqenum) {

        log.debug("------生产者进入队列：start-----");

        String messageDataJson = JSON.toJSONString(mqSenderAo, JSONWriter.Feature.LargeObject);
//        String messageDataJson = JSON.toJSONString(mqSenderAo);

        // mongodb 数据库保存
//        SLogMqProducerMongoVo vo = new SLogMqProducerMongoVo();
        SLogMqProducerClickHouseVo vo = new SLogMqProducerClickHouseVo();
        try {
            // 新增数据
            /**
             * 保存mqSenderAo到redis，key为mqSenderAo.getKey()
             */
            redisUtil.putToMap(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, mqSenderAo.getKey(), messageDataJson);

            /**
             * 封装消息
             */
            Message message =
                    MessageBuilder.withBody(messageDataJson.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
                            .setContentEncoding("utf-8").setMessageId(mqSenderAo.getKey()).build();
            CorrelationData correlationData = new CorrelationData(mqSenderAo.getKey());

            // 添加生产状态
            vo.setProducer_status(1);
            vo.setType("OK");

            /**
             * 确认消息是否到达broker服务器
             */
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setConfirmCallback(this);
            rabbitTemplate.setReturnsCallback(this);
            rabbitTemplate.setExchange(mqenum.getExchange());
            rabbitTemplate.convertAndSend(mqenum.getExchange(), mqenum.getRouting_key(), message, correlationData);
            log.debug("------生产者进入队列：end-----");
        } catch (Exception e) {
            // 更新异常
            vo.setProducter_exception(LocalDateTime.now().toString() + "---" + e.getMessage());
            vo.setProducer_status(0);
            vo.setType("NG");
            log.debug("------生产者进入队列：error-----");
            log.debug(e.getMessage());
            throw new MessageProductQueueException(e);
        } finally {
            insertToDbService(mqSenderAo, mqenum, messageDataJson, vo);
            log.debug("------生产者进入队列：finally-----");
        }
    }

    /**
     * 消息发送：动态路由如：
     *
     * String orderId = "12345";
     * String routingKey = "orders.created." + orderId;
     *
     * @param mqSenderAo
     * @param mqenum
     */
    public void send(MqSenderAo mqSenderAo, MQEnum mqenum, String routingKey) {

        log.debug("------生产者进入队列：start-----");

        String messageDataJson = JSON.toJSONString(mqSenderAo);

        // mongodb 数据库保存
//        SLogMqProducerMongoVo vo = new SLogMqProducerMongoVo();
        SLogMqProducerClickHouseVo vo =  new SLogMqProducerClickHouseVo();

        try {
            // 新增数据
            /**
             * 保存mqSenderAo到redis，key为mqSenderAo.getKey()
             */
            redisUtil.putToMap(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, mqSenderAo.getKey(), messageDataJson);

            /**
             * 封装消息
             */
            Message message =
                    MessageBuilder.withBody(messageDataJson.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON)
                            .setContentEncoding("utf-8").setMessageId(mqSenderAo.getKey()).build();
            CorrelationData correlationData = new CorrelationData(mqSenderAo.getKey());

            // 添加生产状态
            vo.setProducer_status(1);
            vo.setType("OK");

            /**
             * 确认消息是否到达broker服务器
             */
            rabbitTemplate.setMandatory(true);
            rabbitTemplate.setConfirmCallback(this);
            rabbitTemplate.setReturnsCallback(this);
            rabbitTemplate.setExchange(mqenum.getExchange());
            /** 动态的routing key */
            rabbitTemplate.convertAndSend(mqenum.getExchange(), mqenum.getRouting_key()+routingKey, message, correlationData);
            log.debug("------生产者进入队列：end-----");
        } catch (Exception e) {
            // 更新异常
            vo.setProducter_exception(LocalDateTime.now().toString() + "---" + e.getMessage());
            vo.setProducer_status(0);
            vo.setType("NG");
            log.debug("------生产者进入队列：error-----");
            log.debug(e.getMessage());
            throw new MessageProductQueueException(e);
        } finally {
            insertToDbService(mqSenderAo, mqenum, messageDataJson, vo);
            log.debug("------生产者进入队列：finally-----");
        }
    }


    /**
     * 建立消息队列entity_bean
     * @param mqSenderAo
     */
    private SLogMqProducerClickHouseVo buildEntityBean(MqSenderAo mqSenderAo, MQEnum mqenum, String data, SLogMqProducerClickHouseVo vo){
        vo.setCode(mqSenderAo.getType());
        vo.setName(mqSenderAo.getName());
        vo.setExchange(mqenum.getExchange());
        vo.setRouting_key(mqenum.getRouting_key());
        vo.setMq_data(data);
        vo.setMessage_id(mqSenderAo.getKey());
        vo.setProducter_c_time(LocalDateTime.now());
        vo.setTenant_code(mqSenderAo.getTenant_code());
        return vo;
    }

    /**
     * 执行保存操作, 到 mongodb
     * @param mqSenderAo
     * @param mqenum
     * @param data
     */
    private void insertToDbService (MqSenderAo mqSenderAo, MQEnum mqenum, String data, SLogMqProducerClickHouseVo vo) {
        buildEntityBean(mqSenderAo, mqenum, data, vo);
//        SLogMqProducerMongoVo insertEntity = mongoTemplate.insert(vo);
        sLogMqProducerClickHouseService.insert(vo);
    }

    /**
     * 回调函数: confirm确认
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息id:" + correlationData.getId());
        if(correlationData != null) {
            // 处理返回
            if (ack) {
                log.info("------使用MQ消息确认：消息发送成功----");
                //                Object redisRtn = redisUtil.getFromMap(SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, correlationData.getId());
                // 删除redis
                redisUtil.removeFromMap(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, correlationData.getId());
            } else {
                log.error("------使用MQ消息确认：传送失败----");
                Object redisRtn = redisUtil.getFromMap(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, correlationData.getId());
                redisUtil.putToMap(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.REDIS_PREFIX.MQ_CONSUME_FAILT_PREFIX, correlationData.getId(), redisRtn);
                redisUtil.removeFromMap(DataSourceHelper.getCurrentDataSourceName() + "::" + SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, correlationData.getId());
            }
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("{}:消息未成功路由到队列",returned.getMessage().getMessageProperties().getMessageId());
    }

//    /**
//     * 回调函数: return返回
//     * @param message
//     * @param replyCode
//     * @param replyText
//     * @param exchange
//     * @param routingKey
//     */
//    @Override
//    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//        String messageData = Convert.str(message.getBody(), (Charset)null);
//        MqSenderAo mqSenderAo = JSONObject.parseObject(messageData, MqSenderAo.class);
//
//        Object redisRtn = redisUtil.getFromMap(SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, mqSenderAo.getKey());
//        redisUtil.putToMap(SystemConstants.REDIS_PREFIX.MQ_CONSUME_RETURN_PREFIX, mqSenderAo.getKey(), redisRtn);
//        redisUtil.removeFromMap(SystemConstants.REDIS_PREFIX.MQ_SEND_PREFIX, mqSenderAo.getKey());
//        System.out.println("消息被退回");
//        System.out.println("被退回的消息是 :" + messageData);
//        System.out.println("被退回的消息编码是 :" + replyCode);
//    }
}
