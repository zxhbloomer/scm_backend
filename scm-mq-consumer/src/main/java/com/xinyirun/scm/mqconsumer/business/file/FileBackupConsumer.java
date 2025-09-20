package com.xinyirun.scm.mqconsumer.business.file;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.sys.file.BackupFileVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import com.xinyirunscm.scm.clickhouse.service.mq.SLogMqConsumerClickHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: Wqf
 * @Description: 文件备份消费者
 * @CreateTime : 2023/4/27 14:10
 */

@Component
@Slf4j
public class FileBackupConsumer extends BaseMqConsumer {

    @Autowired
    private SFileInfoMapper mapper;

//    @Autowired
//    private ISLogMqConsumerService consumerService;
    @Autowired
    private SLogMqConsumerClickHouseService consumerService;

    @Autowired
    private RestTemplate restTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQEnum.MqInfo.FILE_BACKUP_QUEUE.queueCode, durable = "true"),
                    exchange = @Exchange(name = MQEnum.MqInfo.FILE_BACKUP_QUEUE.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.FILE_BACKUP_QUEUE.routing_key
            )
    )
    @RabbitHandler
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
            SBackupLogVo item = (SBackupLogVo) messageContext;

            BackupFileVo backupFileVo = new BackupFileVo();
            backupFileVo.setItems(Lists.newArrayList(item));
            backupFileVo.setApp_key(getApp_key());

            String fileUrl = getFileUrl(item.getUri(), item.getUrl());
            ResponseEntity<String> response = restTemplate.postForEntity(fileUrl, backupFileVo, String.class);

            log.debug(response.getBody());

            BackupFileVo vo = JSON.parseObject(response.getBody(), BackupFileVo.class);
            SBackupLogVo sBackupLogVo = vo.getItems().get(0);


            SFileInfoEntity sFileInfoEntity = mapper.selectById(item.getId());
            sFileInfoEntity.setStatus(sBackupLogVo.getStatus());
            sFileInfoEntity.setRemark(sBackupLogVo.getRemark());
            sFileInfoEntity.setUrl(sBackupLogVo.getTarget_file_url());
            sFileInfoEntity.setBackup_time(sBackupLogVo.getBackup_time());
            sFileInfoEntity.setType(SystemConstants.FILE_TYPE.ALI_OSS);
            mapper.update(sFileInfoEntity);
            // 更新消费者时间
//            logEntity.setConsumer_status(true);
//            logEntity.setType("OK");
//            logEntity.setMq_data(JSONObject.toJSONString(messageContext));

        } catch (Exception e) {
            // 保存日志
            SLogMqConsumerClickHouseVo vo = new SLogMqConsumerClickHouseVo();
            vo.setMessage_id(message_id);
            vo.setConsumer_c_time(LocalDateTime.now());
            vo.setConsumer_exception(e.getMessage());
            vo.setConsumer_status(0);
            vo.setType("NG");
            vo.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(vo, headers, mqSenderAo);
            log.error("onMessage error", e);
            log.error("------api消费者消费：error-----");
            log.error(e.getMessage());
            throw new MessageConsumerQueueException(e);
        } finally {
            channel.basicAck(deliveryTag, false);
            log.debug("------消费者消费：end-----");
        }
    }
}
