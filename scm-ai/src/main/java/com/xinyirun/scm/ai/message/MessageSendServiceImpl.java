/**
 * 消息发送服务实现，负责消息的发布和事件分发
 */
package com.xinyirun.scm.ai.message;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.xinyirun.scm.ai.message.protobuf.MessageProtobuf;
import com.xinyirun.scm.ai.message.event.MessageJsonEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements IMessageSendService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void sendJsonMessage(String json) {
        // log.debug("sendJsonMessage: {}", json);
        publishMessageJsonEvent(json);
    }

    @Override
    public void sendProtobufMessage(MessageProtobuf messageProtobuf) {
        String json = messageProtobuf.toJson(); 
        // log.debug("sendProtobufMessage: {}", json);
        publishMessageJsonEvent(json);
    }

    /**
     * 发布消息JSON事件
     * @param json 消息JSON字符串
     */
    private void publishMessageJsonEvent(String json) {
        // log.debug("publishMessageJsonEvent: {}", json);
        MessageJsonEvent event = new MessageJsonEvent(this, json);
        eventPublisher.publishEvent(event);
    }
    
}