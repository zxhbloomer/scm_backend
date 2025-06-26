package com.xinyirun.scm.mqconsumer.business.monitor.v1;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v1.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1.*;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v1.BMonitorDataDetailMongoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorLogDetailService;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorSyncLogService;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v1.*;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v1.*;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqConsumerService;
import com.xinyirun.scm.mongodb.service.monitor.v1.IMonitorDataMongoService;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
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
 * @Description: 备份消费者, 未使用 service 版本
 * @CreateTime : 2023/3/31 14:51
 */

@Component
@Slf4j
public class BMonitorBackupV1Consumer extends BaseMqConsumer {

    @Autowired
    private ISLogMqConsumerService consumerService;

    @Autowired
    private IBMonitorBackupBusinessService monitorBackupService;

    @Autowired
    private IBMonitorInBackupService monitorInBackupService;

    @Autowired
    private IBMonitorOutBackupService monitorOutBackupService;

    @Autowired
    private IBMonitorDeliveryBackupService monitorDeliveryBackupService;

    @Autowired
    private IBMonitorUnloadBackupService monitorUnloadBackupService;

    @Autowired
    private IBBkMonitorLogDetailService logDetailService;

    @Autowired
    private IBMonitorRestoreService monitorRestoreService;

    @Autowired
    private IBMonitorInRestoreService monitorInRestoreService;

    @Autowired
    private IBMonitorOutRestoreService monitorOutRestoreService;

    @Autowired
    private IBMonitorDeliveryRestoreService monitorDeliveryRestoreService;

    @Autowired
    private IBMonitorUnloadRestoreService monitorUnloadRestoreService;

    @Autowired
    private IMonitorDataMongoService monitorDataMongoService;

    @Autowired
    private IBBkMonitorSyncLogService syncLogService;


//    @RabbitListener(
//            bindings = @QueueBinding(
//                    value = @Queue(value = MQEnum.MqInfo.MONITOR_BACKUP_QUEUE.queueCode, durable = "true"),
//                    exchange = @Exchange(name=MQEnum.MqInfo.MONITOR_BACKUP_QUEUE.exchange, durable = "true", type = "topic"),
//                    key = MQEnum.MqInfo.MONITOR_BACKUP_QUEUE.routing_key
//            )
//    )
//    @RabbitHandler
//    @Transactional(rollbackFor = Exception.class)
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        log.debug("------消费者消费：start----- ");
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);
        // 保存消费者日志
        SLogMqConsumerMongoEntity logEntity = new SLogMqConsumerMongoEntity();
        logEntity.setMessage_id(message_id);
        logEntity.setConsumer_c_time(LocalDateTime.now());

        MqSenderAo mqSenderAo = new MqSenderAo();

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            // 设置租户数据库
            setTenantDataSource(mqSenderAo);

            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);
            // 获取日志信息
            BBkMonitorLogDetailVo vo = (BBkMonitorLogDetailVo) messageContext;

            // 添加事务锁
            monitorBackupService.selectForUpdate(vo);
            monitorInBackupService.selectForUpdate(vo);
            monitorUnloadBackupService.selectForUpdate(vo);
            monitorOutBackupService.selectForUpdate(vo);
            monitorDeliveryBackupService.selectForUpdate(vo);

            try {
                save2Mongo(vo);
            } catch(Exception e) {
                // 如果此处失败, 不再执行删除操作, 扔处异常
                logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
                log.error("数据保存mongodb失败!! {}", e.toString());
                syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
                throw new BusinessException(e.getMessage());
            }
            try {
                delete2Mysql(vo);
            } catch (Exception e) {
                log.error("删除数据失败!! {}", e.toString());
                logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
                syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
            }

            // 更新消费者时间
            logEntity.setConsumer_status(true);
            logEntity.setType("OK");
            logEntity.setMq_data(JSONObject.toJSONString(messageContext));

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            logEntity.setConsumer_exception(e.getMessage());
            logEntity.setConsumer_status(false);
            logEntity.setType("NG");
            logEntity.setMq_data(JSONObject.toJSONString(messageDataObject));
            log.error("onMessage error", e);
            log.error("------api消费者消费：error-----");
            log.error(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            consumerService.insert(logEntity, headers, mqSenderAo);
            log.debug("------消费者消费：end-----");
        }
    }

    private void save2Mongo(BBkMonitorLogDetailVo vo) {

        // 保存到 mongodb 中的 monitor 表
        BMonitorDataMongoEntity bMonitorDataMongoEntity = builderMonitorEntity(vo);

        if (null == bMonitorDataMongoEntity) {
            log.debug("当前监管任务已经备份过了, --> {}", JSONObject.toJSONString(vo));
            return;
        }

        // 保存到mongo
        monitorDataMongoService.saveAndFlush(bMonitorDataMongoEntity);


        // 更新 日志详情 状态
        logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_2);
        syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_2);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete2Mysql(BBkMonitorLogDetailVo vo) {

//        BBkMonitorLogDetailEntity detailEntity = logDetailService.getById(vo.getLog_detail_id());
//        if (detailEntity == null) {
//            // 如果为true， 说明有异常
//            throw new BusinessException("当前监管任务, 未备份到 mongo " + JSON.toJSONString(detailEntity));
//        }

        // 先判断是否备份成功
        BMonitorDataMongoEntity entity = monitorDataMongoService.getEntityByMonitorId(vo.getMonitor_id());

        if (null == entity || DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_T.equals(entity.getIs_restore())) {
            log.error("结果 --> {}", DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_T.equals(entity.getIs_restore()));
            throw new BusinessException("当前监管任务, 未备份到 mongo 或 已恢复到 mysql");
        }

        // b_monitor 表
        if (null != entity.getMonitor_json()) {
            monitorBackupService.removeById(vo.getMonitor_id());
        } else {
            throw new BusinessException("当前监管任务, 未备份到 mongo");
        }

        // b_monitor_in 表
        if (null != vo.getMonitor_in_id()) {
            if (null != entity.getMonitor_in_json()) {
                monitorInBackupService.removeById(vo.getMonitor_in_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_in 失败");
            }
        }

        // b_monitor_out 表
        if (null != vo.getMonitor_out_id()) {
            if (null != entity.getMonitor_out_json()) {
                monitorOutBackupService.removeById(vo.getMonitor_out_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_out 失败");
            }
        }

        // b_monitor_delivery 表
        if (null != vo.getMonitor_delivery_id()) {
            if (null != entity.getMonitor_delivery_json()) {
                monitorDeliveryBackupService.removeById(vo.getMonitor_delivery_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_delivery 失败");
            }
        }

        // b_monitor_unload 表
        if (null != vo.getMonitor_unload_id()) {
            if (null != entity.getMonitor_unload_json()) {
                monitorUnloadBackupService.removeById(vo.getMonitor_unload_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_unload 失败");
            }
        }


        // b_monitor 表
       /* BMonitorMongoEntity data = monitorMongoService.findById_(vo.getMonitor_id());
        if (null != data) {
            monitorBackupService.removeById(vo.getMonitor_id())
            ;
        } else {
            // 扔个异常试试看
            throw new RuntimeException("当前监管任务 b_monitor, mongo 中不存在");
        }

        // b_monitor_in 表
        if (null != vo.getMonitor_in_id()) {
            BMonitorInMongoEntity data1 = monitorInMongoService.findById_(vo.getMonitor_in_id());
            if (null != data1) {
                monitorInBackupService.removeById(vo.getMonitor_in_id());
            } else {
                // 异常
                throw new RuntimeException("当前监管任务 b_monitor_in, mongo 中不存在");
            }
        }

        // b_monitor_out 表
        if (null != vo.getMonitor_out_id()) {
            BMonitorOutMongoEntity data1 = monitorOutMongoService.findById_(vo.getMonitor_out_id());
            if (null != data1) {
                monitorOutBackupService.removeById(vo.getMonitor_out_id());
            } else {
                // 异常
                throw new RuntimeException("当前监管任务 b_monitor_out, mongo 中不存在");
            }
        }

        // b_monitor_delivery 表
        if (null != vo.getMonitor_delivery_id()) {
            BMonitorDeliveryMongoEntity data1 = monitorDeliveryMongoService.findById_(vo.getMonitor_delivery_id());
            if (null != data1) {
                monitorDeliveryBackupService.removeById(vo.getMonitor_delivery_id());
            } else {
                // 异常
                throw new RuntimeException("当前监管任务 b_monitor_delivery, mongo 中不存在");
            }
        }

        // b_monitor_unload 表
        if (null != vo.getMonitor_unload_id()) {
            BMonitorUnloadMongoEntity data1 = monitorUnloadMongoService.findById_(vo.getMonitor_unload_id());
            if (null != data1) {
                monitorUnloadBackupService.removeById(vo.getMonitor_unload_id());
            } else {
                // 异常
                throw new RuntimeException("当前监管任务 b_monitor_unload, mongo 中不存在");
            }
        }*/
        syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3);
        logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3);
    }

    private BMonitorDataMongoEntity builderMonitorEntity(BBkMonitorLogDetailVo vo) {

        // 查询 分页 页面 的数据
        BMonitorDataMongoEntity mongoEntity = monitorBackupService.selectPageById(vo.getMonitor_id());
        if (mongoEntity == null) {
//            throw new RuntimeException("备份失败, 查询不到数据");
            return null;
        }
        // 查询详情
        BMonitorDataDetailMongoVo detail = monitorBackupService.getDetail(vo.getMonitor_id());
        mongoEntity.setDetailVo(detail);

        // 查询 关联表数据, 以 json 格式存储数据库
        BMonitorRestoreEntity entity = monitorRestoreService.getById(vo.getMonitor_id());
        if (entity == null) {
            throw new RuntimeException("备份失败, 查询不到数据");
        }
        mongoEntity.setMonitor_json(entity);

        // b_monitor_in 表
        if (null != vo.getMonitor_in_id()) {
            BMonitorInRestoreEntity inEntity = monitorInRestoreService.getById(vo.getMonitor_in_id());
            mongoEntity.setMonitor_in_json(inEntity);
        }

        // b_monitor_out 表
        if (null != vo.getMonitor_out_id()) {
            BMonitorOutRestoreEntity outEntity = monitorOutRestoreService.getById(vo.getMonitor_out_id());
            mongoEntity.setMonitor_out_json(outEntity);
        }

        // b_monitor_delivery 表
        if (null != vo.getMonitor_delivery_id()) {
            BMonitorDeliveryRestoreEntity byId = monitorDeliveryRestoreService.getById(vo.getMonitor_delivery_id());
            mongoEntity.setMonitor_delivery_json(byId);
        }

        // b_monitor_unload 表
        if (null != vo.getMonitor_unload_id()) {
            BMonitorUnloadRestoreEntity byId = monitorUnloadRestoreService.getById(vo.getMonitor_unload_id());
            mongoEntity.setMonitor_unload_json(byId);
        }
        return mongoEntity;
    }
}
