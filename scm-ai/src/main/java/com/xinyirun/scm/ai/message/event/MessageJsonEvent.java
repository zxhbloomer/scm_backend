/**
 * 消息JSON事件，用于消息的异步分发和处理
 */
package com.xinyirun.scm.ai.message.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 消息JSON事件
 */
@Getter
public class MessageJsonEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String messageJson;

    public MessageJsonEvent(Object source, String messageJson) {
        super(source);
        this.messageJson = messageJson;
    }
}