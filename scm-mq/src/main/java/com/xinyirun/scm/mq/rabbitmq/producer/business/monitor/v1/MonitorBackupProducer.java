package com.xinyirun.scm.mq.rabbitmq.producer.business.monitor.v1;

import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/3/31 11:08
 */

@Component
public class MonitorBackupProducer {

    @Autowired
    private ScmMqProducer mqProducer;

    public void mqSendMq(BBkMonitorLogDetailVo data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_MONITOR_BACKUP_QUEUE);
        // 发送消息
        mqProducer.send(ao, MQEnum.MQ_MONITOR_BACKUP_QUEUE);
    }
}
