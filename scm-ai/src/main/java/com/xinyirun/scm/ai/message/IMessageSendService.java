/**
 * 消息发送服务接口，定义消息发送的核心方法
 */
package com.xinyirun.scm.ai.message;

import com.xinyirun.scm.ai.message.protobuf.MessageProtobuf;

/**
 * 消息发送服务接口
 */
public interface IMessageSendService {

    /**
     * 发送JSON格式消息
     * @param json JSON格式的消息内容
     */
    void sendJsonMessage(String json);

    /**
     * 发送Protobuf格式消息
     * @param messageProtobuf Protobuf格式的消息对象
     */
    void sendProtobufMessage(MessageProtobuf messageProtobuf);
    
}