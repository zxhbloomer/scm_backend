package com.xinyirun.scm.mqconsumer.business.monitor.v2;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorBackupEntity;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v2.IBBkMonitorLogDetailV2Service;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v2.IBBkMonitorSyncLogV2Service;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorBackupV2Service;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v2.*;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqConsumerService;
import com.xinyirun.scm.mongodb.service.monitor.v2.IMonitorDataMongoV2Service;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/3/31 14:51
 */

@Component
@Slf4j
public class BMonitorRestoreV2Consumer extends BaseMqConsumer {

    @Autowired
    private ISLogMqConsumerService consumerService;

    @Autowired
    private IBMonitorRestoreV2Service monitorRestoreV2Service;

    @Autowired
    private IBMonitorInRestoreV2Service monitorInRestoreV2Service;

    @Autowired
    private IBMonitorOutRestoreV2Service monitorOutRestoreV2Service;

    @Autowired
    private IBMonitorDeliveryRestoreV2Service monitorDeliveryRestoreV2Service;

    @Autowired
    private IBMonitorUnloadRestoreV2Service monitorUnloadRestoreV2Service;

    @Autowired
    private IBReturnRelationRestoreV2Service returnRelationRestoreV2Service;

    @Autowired
    private IBBkMonitorLogDetailV2Service monitorLogDetailV2Service;

    @Autowired
    private IMonitorDataMongoV2Service monitorDataMongoV2Service;

    @Autowired
    private IBBkMonitorSyncLogV2Service bkMonitorSyncLogV2Service;

    @Autowired
    private IBMonitorBackupV2Service monitorBackupV2Service;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQEnum.MqInfo.MONITOR_RENEW_QUEUE_V2.queueCode, durable = "true"),
                    exchange = @Exchange(name = MQEnum.MqInfo.MONITOR_RENEW_QUEUE_V2.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.MONITOR_RENEW_QUEUE_V2.routing_key
            )
    )
    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
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

            try {
                save2Mysql(vo);
                monitorDataMongoV2Service.updateRestoreStatus(vo.getMonitor_id());
            } catch (Exception e) {
                // 如果恢复失败, 更新字段为可见
                monitorDataMongoV2Service.updateVisibilityStatusByMonitorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_MONGO_IS_SHOW_T);
                // 如果此处失败, 不再执行删除操作, 扔处异常
                monitorLogDetailV2Service.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
                bkMonitorSyncLogV2Service.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
                log.error("mongodb数据恢复失败!! {}", e.toString());
            }


            // 更新消费者时间
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));

            channel.basicAck(deliveryTag, false);

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
            log.debug("------消费者消费：end-----");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void save2Mysql(BBkMonitorLogDetailVo vo) {

        BMonitorDataMongoEntity mongoEntity = monitorDataMongoV2Service.getEntityByMonitorId(vo.getMonitor_id());
        if (null == mongoEntity) {
            throw new BusinessException("mongo 中查询不到数据， 恢复失败");
        }

//        b_monitor 表
        if (null != mongoEntity.getMonitor_json()) {
            monitorRestoreV2Service.saveOrUpdate(mongoEntity.getMonitor_json());
        } else {
            throw new BusinessException("b _monitor 表恢复失败");
        }

        // b_monitor_in 表
        if (null != vo.getMonitor_in_id()) {
            if (null != mongoEntity.getMonitor_in_json()) {
                monitorInRestoreV2Service.saveOrUpdate(mongoEntity.getMonitor_in_json());
            } else {
                throw new BusinessException("b b_monitor_in 表恢复失败");
            }
        }

        // b_monitor_delivery 表
        if (null != vo.getMonitor_delivery_id()) {
            if (null != mongoEntity.getMonitor_delivery_json()) {
                monitorDeliveryRestoreV2Service.saveOrUpdate(mongoEntity.getMonitor_delivery_json());
            } else {
                throw new BusinessException("b b_monitor_delivery 表恢复失败");
            }
        }

        // b_monitor_out 表
        if (null != vo.getMonitor_out_id()) {
            if (null != mongoEntity.getMonitor_out_json()) {
                monitorOutRestoreV2Service.saveOrUpdate(mongoEntity.getMonitor_out_json());
            } else {
                throw new BusinessException("b b_monitor_out 表恢复失败");
            }
        }

        // b_monitor_unload 表
        if (null != vo.getMonitor_unload_id()) {
            if (null != mongoEntity.getMonitor_unload_json()) {
                monitorUnloadRestoreV2Service.saveOrUpdate(mongoEntity.getMonitor_unload_json());
            } else {
                throw new BusinessException("b_monitor_unload 表恢复失败");
            }
        }

        // b_return_relation 表
        if (null != vo.getMonitor_id()) {
            if (null != mongoEntity.getReturn_relation_json()) {
                returnRelationRestoreV2Service.saveOrUpdate(mongoEntity.getReturn_relation_json());
            }
        }


        monitorBackupV2Service.remove(Wrappers.<BMonitorBackupEntity>lambdaQuery().eq(BMonitorBackupEntity::getCode, mongoEntity.getCode()));
        monitorLogDetailV2Service.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3);
        bkMonitorSyncLogV2Service.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3);
    }
}
