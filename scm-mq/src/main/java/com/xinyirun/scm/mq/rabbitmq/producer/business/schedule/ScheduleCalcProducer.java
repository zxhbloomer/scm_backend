package com.xinyirun.scm.mq.rabbitmq.producer.business.schedule;

import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.business.schedule.AppScheduleSendMqData;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wang Qianfeng
 * @Description 计算 物流订单物流数量生产者
 * @date 2023/3/1 15:56
 */
@Component
public class ScheduleCalcProducer {

    @Autowired
    private ScmMqProducer mqProducer;

    public void mqSendMq(AppScheduleSendMqData data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_SCHEDULE_CALC_QTY_QUEUE);
        // 发送消息
        mqProducer.send(ao, MQEnum.MQ_SCHEDULE_CALC_QTY_QUEUE);
    }
}
