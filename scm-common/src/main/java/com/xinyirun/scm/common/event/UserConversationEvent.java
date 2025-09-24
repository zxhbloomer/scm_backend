package com.xinyirun.scm.common.event;

import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户AI会话事件
 *
 * @author SCM-AI模块
 */
public class UserConversationEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -4543998018704250379L;

    private final Long userId;
    private final String convUuid;
    private final String userName;
    private final String tenant;
    private final LocalDateTime createTime;

    public UserConversationEvent(Object source, Long userId, String convUuid, String userName, LocalDateTime createTime, String tenant) {
        super(source);
        this.userId = userId;
        this.convUuid = convUuid;
        this.userName = userName;
        this.createTime = createTime;
        this.tenant = tenant;
    }

    public Long getUserId() {
        return userId;
    }

    public String getConvUuid() {
        return convUuid;
    }

    public String getUserName() {
        return userName;
    }
    public String getTenant() {
        return tenant;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
}