package com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt;

import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wang Qianfeng
 * @Description 业务中台对接 api, mq生产者
 * @date 2023/2/22 10:52
 */
@Component
public class SyncPush2BusinessPlatformAllInOneMqProducter {

    /**
     * 调用消息队列
     */
    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 调用mq
     *
     */
    public void mqSendMq(ApiBAllAsyncVo data, String type){
        MqSenderAo ao = new MqSenderAo();
        // 返写sync_id到业务单据表
        switch (type) {
            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN:
                // 出库计划
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT_PLAN);
                // 发送消息
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_OUT:
                // 出库单
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_OUT);
                // 发送消息
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN:
                // 入库计划
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN_PLAN);
                // 发送消息
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_IN:
                // 入库单
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_IN);
                // 发送消息
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR:
                // 监管任务
                // 初始化要发生mq的bean
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_MONITOR);
                // 发送消息
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY:
                // 提货单
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_DELIVERY);
                // 发送消息
                break;
            case DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE:
                // 收货单
                ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_RECEIVE);
                // 发送消息
                break;
        }

        mqProducer.send(ao, MQEnum.MQ_SYNC_BUSINESS_PLATFORM_ALL_IN_ONE);
    }

}
