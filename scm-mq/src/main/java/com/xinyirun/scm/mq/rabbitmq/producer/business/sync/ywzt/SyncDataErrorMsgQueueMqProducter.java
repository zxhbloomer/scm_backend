package com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt;

import com.xinyirun.scm.bean.api.vo.business.in.ApiBInAsyncVo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据同步错误消息队列
 */
@Component
public class SyncDataErrorMsgQueueMqProducter {

    /**
     * 调用消息队列
     */
    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 调用mq
     *
     */
    public void mqSendMq(ApiBInAsyncVo data, String type){
        MqSenderAo ao;
        // 返写sync_id到业务单据表
        switch (type) {
            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN:
                // 出库计划
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_ERROR_MSG_QUEUE_OUT_PLAN_MQ);
                // 发送消息
                mqProducer.send(ao, MQEnum.MQ_SYNC_ERROR_MSG_QUEUE_OUT_PLAN);
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT:
                // 出库单
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_ERROR_MSG_QUEUE_OUT_MQ);
                // 发送消息
                mqProducer.send(ao, MQEnum.MQ_SYNC_ERROR_MSG_QUEUE_OUT);
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN:
                // 入库计划
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_ERROR_MSG_QUEUE_IN_PLAN_MQ);
                // 发送消息
                mqProducer.send(ao, MQEnum.MQ_SYNC_ERROR_MSG_QUEUE_IN_PLAN);
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_IN:
                // 入库单
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN);
                // 发送消息
                mqProducer.send(ao, MQEnum.MQ_SYNC_BUSINESS_PLATFORM_ALL_IN_ONE);
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR:
                // 监管任务
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR);
                // 发送消息
                mqProducer.send(ao, MQEnum.MQ_SYNC_ERROR_MSG_QUEUE_MONITOR);
                break;
        }
    }

}
