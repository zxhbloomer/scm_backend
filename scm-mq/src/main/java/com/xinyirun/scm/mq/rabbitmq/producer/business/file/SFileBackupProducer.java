package com.xinyirun.scm.mq.rabbitmq.producer.business.file;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/4/27 10:21
 */

import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 文件备份mq
 */
@Component
public class SFileBackupProducer {

    @Autowired
    private ScmMqProducer mqProducer;

    @Async("logExecutor")
    public void mqSendMq(SBackupLogVo data){
        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_FILE_BACKUP_QUEUE);
        // 发送消息
        mqProducer.send(ao, MQEnum.MQ_FILE_BACKUP_QUEUE);
    }
}
