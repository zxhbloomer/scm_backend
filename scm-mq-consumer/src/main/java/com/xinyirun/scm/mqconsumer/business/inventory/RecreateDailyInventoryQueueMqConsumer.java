package com.xinyirun.scm.mqconsumer.business.inventory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDailyInventoryVo;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.common.utils.DateUtils;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyInventoryNewV2Service;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyProductV2Service;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import com.xinyirun.scm.clickhouse.service.mq.SLogMqConsumerClickHouseService;
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
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 数据同步错误消费者
 */

@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "enable", havingValue = "true")
@Component
@Slf4j
public class RecreateDailyInventoryQueueMqConsumer extends BaseMqConsumer {

    // 每日库存
    @Autowired
    private ISBDailyInventoryNewV2Service isbDailyInventoryService;
    //    @Autowired
//    private ISLogMqConsumerService consumerService;
    @Autowired
    private SLogMqConsumerClickHouseService consumerService;

    @Autowired
    private ISBDailyProductV2Service dailyProductV2Service;

    @Autowired
    private IMWarehouseService warehouseService;

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
            value = @Queue(value = MQEnum.MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.queueCode, durable = "true"),
            exchange = @Exchange(name=MQEnum.MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.exchange, durable = "true", type = "topic"),
            key = MQEnum.MqInfo.MQ_RECREATE_DAILY_INVENTORY_QUEUE.routing_key
        )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException{
        log.debug("------消费者消费：start----- ");
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        MqSenderAo mqSenderAo = new MqSenderAo();
        try {
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            // 设置租户数据库
            setTenantDataSource(mqSenderAo);

            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);


            BDailyInventoryVo vo = (BDailyInventoryVo) messageContext;
            isbDailyInventoryService.reCreateDailyInventoryAll(BDailyInventoryVo.class.getName(), JSON.toJSONString(vo));

            if (null != vo.getDt()) {
                // 仓库类型是 加工仓的
                MWarehouseEntity warehouse = warehouseService.getById(vo.getWarehouse_id());
                if (!DictConstant.DICT_M_WAREHOUSE_TYPE_WD.equals(warehouse.getWarehouse_type())) {
                    return;
                }
                // 更新每日库存
                BProductDailyVo param = new BProductDailyVo();
                param.setInit_time(vo.getDt().format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD)));
                param.setWarehouse_id(vo.getWarehouse_id());
                param.setType("2");
                dailyProductV2Service.recreate(param);
            }

            // 更新消费者时间
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));

        } catch (Exception e) {
            // 更新异常 保存日志
            SLogMqConsumerClickHouseVo vo = new SLogMqConsumerClickHouseVo();
            vo.setMessage_id(message_id);
            vo.setConsumer_c_time(LocalDateTime.now());
            vo.setConsumer_exception(e.getMessage());
            vo.setConsumer_status(0);
            vo.setType("NG");
            vo.setMq_data(JSONObject.toJSONString(messageDataObject));
            // 新增 mq 消费者日志
            consumerService.insert(vo, headers, mqSenderAo);
            log.error("onMessage error", e);
            log.debug("------消费者消费：error-----");
            log.debug(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            // 手动确认
            boolean multiple = false;
            channel.basicAck(deliveryTag, multiple);

            log.debug("------消费者消费：end-----");
        }
    }
}
