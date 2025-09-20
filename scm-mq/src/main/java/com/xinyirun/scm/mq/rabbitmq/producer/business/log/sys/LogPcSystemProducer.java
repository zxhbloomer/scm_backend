package com.xinyirun.scm.mq.rabbitmq.producer.business.log.sys;

import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogSysClickHouseVo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Wang Qianfeng
 * @Description 接口日志
 * @date 2023/3/1 10:29
 */
@Component
@Slf4j
public class LogPcSystemProducer {

    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 发送系统日志消息
     */
    @Async("logExecutor")
    public void mqSendMq(SLogSysClickHouseVo data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_PC_SYSTEM_QUEUE);
        ao.setTenant_code(data.getTenant_code());
        // 异步发送消息
        mqProducer.send(ao, MQEnum.MQ_LOG_PC_SYSTEM_QUEUE);
    }
}
