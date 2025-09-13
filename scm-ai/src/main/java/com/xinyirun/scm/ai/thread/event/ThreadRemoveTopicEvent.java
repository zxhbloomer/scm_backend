/**
 * Thread移除主题事件，当会话主题被移除时触发
 */
package com.xinyirun.scm.ai.thread.event;

import org.springframework.context.ApplicationEvent;

import com.xinyirun.scm.ai.thread.entity.ThreadEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ThreadRemoveTopicEvent extends ApplicationEvent {

    private ThreadEntity thread;

    public ThreadRemoveTopicEvent(Object source, ThreadEntity thread) {
        super(source);
        this.thread = thread;
    }

}