package com.xinyirun.scm.mq.rabbitmq.producer.business.inventory;

import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 入/出库单作废后更新每日库存消息队列
 */
@Component
public class RecreateDailyInventoryQueueMqProducter {

    /**
     * 调用消息队列
     */
    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 调用mq
     *
     */
    public void mqSendMq(BDailyInventoryVo data){
        // 入/出库单作废
        // 初始化要发生mq的bean
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_RECREATE_DAILY_INVENTORY_QUEUE);
        // 发送消息
        mqProducer.send(ao, MQEnum.MQ_RECREATE_DAILY_INVENTORY_QUEUE);

    }

}
