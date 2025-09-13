/**
 * Thread关闭事件，当会话被关闭时触发
 */
package com.xinyirun.scm.ai.thread.event;

import org.springframework.context.ApplicationEvent;

import com.xinyirun.scm.ai.thread.entity.ThreadEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ThreadCloseEvent extends ApplicationEvent {

    private ThreadEntity thread;

    public ThreadCloseEvent(Object source, ThreadEntity thread) {
        super(source);
        this.thread = thread;
    }

}