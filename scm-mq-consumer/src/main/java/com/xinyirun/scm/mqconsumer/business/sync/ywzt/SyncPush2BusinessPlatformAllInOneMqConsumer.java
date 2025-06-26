package com.xinyirun.scm.mqconsumer.business.sync.ywzt;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBInAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBInPlanAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiBMonitorAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutPlanAsyncVo;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanVo;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.RedisLockConstants;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.common.exception.system.SystemException;
import com.xinyirun.scm.common.utils.redis.RedisLockUtil;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据同步错误消费者
 */

@Component
@Slf4j
public class SyncPush2BusinessPlatformAllInOneMqConsumer extends BaseMqConsumer {


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IBSyncStatusErrorService syncStatusErrorService;

    @Autowired
    private ISLogMqConsumerService consumerService;

    /**
     * 如果有消息过来，在消费的时候调用这个方法
     */
    /**
     * 配置监听的哪一个队列，同时在没有queue和exchange的情况下会去创建并建立绑定关系
     *
     * @param messageDataObject
     * @param headers
     * @param channel
     * @throws IOException
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQEnum.MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.queueCode, durable = "true"),
                    exchange = @Exchange(name = MQEnum.MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MSG_QUEUE.routing_key
            )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        log.debug("------消费者消费：start----- ");
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);

        Set<Integer> ids = new HashSet<>();
        String type = "";
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        MqSenderAo mqSenderAo = new MqSenderAo();
        try {
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            // 设置租户数据库
            setTenantDataSource(mqSenderAo);

            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);
            /**
             * 1、判断类型执行对应业务
             */
            ApiBAllAsyncVo vo = (ApiBAllAsyncVo) messageContext;
            // 将 实体类转为 json
            switch (mqSenderAo.getType()) {
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN:
//                    syncIn(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN_PLAN:
//                    syncInPlan(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT:
//                    syncOut(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT_PLAN:
//                    syncOutPlan(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR:
//                    syncMonitor(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_DELIVERY:
//                    syncDelivery(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                case MqSenderEnum.MqSenderConstants.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_RECEIVE:
//                    syncReceive(vo, ids, type);
                    /**
                     * 启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     * TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库
                     */
                    throw new SystemException("TODO：启用多租户后，接口需要传参：租户ID，否则系统不知道同步给那个数据库");
                default:
                    break;
            }
            // 更新消费者时间
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));
        } catch (Exception e) {
            // 更新异常 保存日志
            SLogMqConsumerMongoEntity logEntity = new SLogMqConsumerMongoEntity();
            logEntity.setMessage_id(message_id);
            logEntity.setConsumer_c_time(LocalDateTime.now());
            logEntity.setConsumer_exception(e.getMessage());
            logEntity.setConsumer_status(false);
            logEntity.setType("NG");
            logEntity.setMq_data(JSONObject.toJSONString(messageDataObject));
            // 新增 mq 消费者日志
            consumerService.insert(logEntity, headers, mqSenderAo);
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

    private void syncMonitor(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncMonitor.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============监管任务start=============");
                ApiBMonitorAsyncVo asyncVo5 = new ApiBMonitorAsyncVo();
                String s5 = JSONObject.toJSONString(vo.getBeans());
                List<BMonitorVo> list5 = JSON.parseArray(s5, BMonitorVo.class);
                asyncVo5.setBeans(list5);
                ids = list5.stream().map(BMonitorVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR;
                asyncVo5.setApp_config_type(vo.getApp_config_type());
                String url5 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/monitor/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url5, asyncVo5, String.class);
                log.debug("=============监管任务end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncMonitor error", e);
            throw new SystemException("监管任务同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncMonitor.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }

    private void syncOutPlan(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncOutPlan.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============出库计划单信息start=============");
                ApiBOutPlanAsyncVo asyncVo4 = new ApiBOutPlanAsyncVo();
                String s4 = JSONObject.toJSONString(vo.getBeans());
                List<BOutPlanListVo> list4 = JSON.parseArray(s4, BOutPlanListVo.class);
                asyncVo4.setBeans(list4);
                ids = list4.stream().map(BOutPlanListVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN;
                asyncVo4.setApp_config_type(vo.getApp_config_type());
                String url4 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/outplan/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url4, asyncVo4, String.class);
                log.debug("=============出库单信息end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncOutPlan error", e);
            throw new SystemException("出库计划同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncOutPlan.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }

    private void syncOut(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncOut.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============出库单信息start=============");
                ApiBOutAsyncVo asyncVo3 = new ApiBOutAsyncVo();
                String s3 = JSONObject.toJSONString(vo.getBeans());
                List<BOutVo> list3 = JSON.parseArray(s3, BOutVo.class);
                asyncVo3.setBeans(list3);
                ids = list3.stream().map(BOutVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_OUT;
                asyncVo3.setApp_config_type(vo.getApp_config_type());
                String url3 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/out/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url3, asyncVo3, String.class);
                log.debug("=============出库单信息end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncOut error", e);
            throw new SystemException("出库单同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncOut.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }

    private void syncInPlan(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        // 为解决业务中台不能处理高并发问题,因存在多个消费者并发情况,所以此处增加分布式并发锁
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncInPlan.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============入库计划信息start=============");
                ApiBInPlanAsyncVo asyncVo2 = new ApiBInPlanAsyncVo();
                String s2 = JSONObject.toJSONString(vo.getBeans());
                List<BInPlanVo> list2 = JSON.parseArray(s2, BInPlanVo.class);
                asyncVo2.setBeans(list2);
                ids = list2.stream().map(BInPlanVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN;
                asyncVo2.setApp_config_type(vo.getApp_config_type());
                String url2 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/inplan/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url2, asyncVo2, String.class);
                log.debug("=============入库计划信息end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncInPlan error", e);
            throw new SystemException("入库计划同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncInPlan.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }

    private void syncIn(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncIn.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============同步入库信息start=============");
                ApiBInAsyncVo asyncVo1 = new ApiBInAsyncVo();
                asyncVo1.setApp_config_type(vo.getApp_config_type());
                String s1 = JSONObject.toJSONString(vo.getBeans());
                List<BInVo> list1 = JSON.parseArray(s1, BInVo.class);
                asyncVo1.setBeans(list1);
                ids = list1.stream().map(BInVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_IN;
                // 入库单
                String url1 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/in/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url1, asyncVo1, String.class);
                log.debug("=============同步入库信息end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncIn error", e);
            throw new SystemException("入库单同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncIn.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }

    private void syncDelivery(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncDelivery.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============同步入库信息start=============");
                ApiBInAsyncVo asyncVo1 = new ApiBInAsyncVo();
                asyncVo1.setApp_config_type(vo.getApp_config_type());
                String s1 = JSONObject.toJSONString(vo.getBeans());
                List<BInVo> list1 = JSON.parseArray(s1, BInVo.class);
                asyncVo1.setBeans(list1);
                ids = list1.stream().map(BInVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY;
                // 入库单
                String url1 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/delivery/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url1, asyncVo1, String.class);
                log.debug("=============同步入库信息end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncIn error", e);
            throw new SystemException("入库单同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncDelivery.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }


    private void syncReceive(ApiBAllAsyncVo vo, Set<Integer> ids, String type) {
        try {
            if (RedisLockUtil.tryGetDistributedLock(RedisLockConstants.SyncReceive.LOCK_KEY, String.valueOf(Thread.currentThread().getId()))) {
                log.debug("=============收货单信息start=============");
                ApiBOutAsyncVo asyncVo3 = new ApiBOutAsyncVo();
                String s3 = JSONObject.toJSONString(vo.getBeans());
                List<BOutVo> list3 = JSON.parseArray(s3, BOutVo.class);
                asyncVo3.setBeans(list3);
                ids = list3.stream().map(BOutVo::getId).collect(Collectors.toSet());
                type = DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE;
                asyncVo3.setApp_config_type(vo.getApp_config_type());
                String url3 = getBusinessCenterUrl("/wms/api/service/v1/steel/async/receive/execute", SystemConstants.APP_CODE.ZT);
                restTemplate.postForEntity(url3, asyncVo3, String.class);
                log.debug("=============收货单信息end=============");
            } else {
                throw new SystemException("分布式加锁失败");
            }
        } catch (Exception e) {
            log.error("syncOut error", e);
            throw new SystemException("收货单同步失败");
        } finally {
            // 更新同步日志状态为消费完成
            syncStatusErrorService.updateSyncErrorStatus(ids, type, "OK");
            RedisLockUtil.releaseDistributedLock(RedisLockConstants.SyncReceive.LOCK_KEY, String.valueOf(Thread.currentThread().getId()));
        }
    }
}
