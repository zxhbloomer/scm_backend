package com.xinyirun.scm.ai.thread.entity;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.ai.thread.enums.ThreadProcessStatusEnum;
import com.xinyirun.scm.ai.thread.enums.ThreadTypeEnum;
import com.xinyirun.scm.ai.core.enums.ChannelEnum;
import com.xinyirun.scm.ai.message.MessageEntity;
import com.xinyirun.scm.ai.rbac.user.UserProtobuf;
import com.xinyirun.scm.ai.rbac.user.UserTypeEnum;
import com.xinyirun.scm.ai.utils.ConvertUtils;
import com.xinyirun.scm.ai.thread.AbstractThreadEntity;
import com.xinyirun.scm.ai.thread.entity.ThreadProtobuf;
import com.xinyirun.scm.ai.thread.ThreadExtra;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = { "messages" })
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "messages"})
@TableName("scm_ai_thread")
public class ThreadEntity extends AbstractThreadEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<MessageEntity> messages = new ArrayList<>();

    public Boolean isNew() {
        return ThreadProcessStatusEnum.NEW.name().equals(getStatus());
    }

    public Boolean isRoboting() {
        return ThreadProcessStatusEnum.ROBOTING.name().equals(getStatus());
    }

    public Boolean isQueuing() {
        return ThreadProcessStatusEnum.QUEUING.name().equals(getStatus());
    }

    public Boolean isOffline() {
        return ThreadProcessStatusEnum.OFFLINE.name().equals(getStatus());
    }

    public Boolean isChatting() {
        return ThreadProcessStatusEnum.CHATTING.name().equals(getStatus());
    }

    public Boolean isTimeout() {
        return ThreadProcessStatusEnum.TIMEOUT.name().equals(getStatus());
    }

    public Boolean isClosed() {
        return ThreadProcessStatusEnum.CLOSED.name().equals(getStatus());
    }
    
    public Boolean isCustomerService() {
        return ThreadTypeEnum.AGENT.name().equals(getType())
                || ThreadTypeEnum.WORKGROUP.name().equals(getType())
                || ThreadTypeEnum.ROBOT.name().equals(getType())
                || ThreadTypeEnum.UNIFIED.name().equals(getType());
    }

    public Boolean isNoticeAccount() {
        return ThreadTypeEnum.CHANNEL.name().equals(getType());
    }

    public Boolean isAssistant() {
        return ThreadTypeEnum.ASSISTANT.name().equals(getType());
    }

    public Boolean isMember() {
        return ThreadTypeEnum.MEMBER.name().equals(getType());
    }

    public Boolean isGroup() {
        return ThreadTypeEnum.GROUP.name().equals(getType());
    }

    public Boolean isRobotType() {
        return ThreadTypeEnum.ROBOT.name().equals(getType());
    }

    public Boolean isWorkgroupType() {
        return ThreadTypeEnum.WORKGROUP.name().equals(getType());
    }

    public Boolean isAgentType() {
        return ThreadTypeEnum.AGENT.name().equals(getType());
    }

    public Boolean isUnifiedType() {
        return ThreadTypeEnum.UNIFIED.name().equals(getType());
    }

    public Boolean isWeChatMp() {
        return ChannelEnum.WECHAT_MP.name().equals(getChannel());
    }

    public Boolean isWeChatMini() {
        return ChannelEnum.WECHAT_MINI.name().equals(getChannel());
    }

    public ThreadEntity setRoboting() {
        setStatus(ThreadProcessStatusEnum.ROBOTING.name());
        return this;
    }

    public ThreadEntity setOffline() {
        setStatus(ThreadProcessStatusEnum.OFFLINE.name());
        return this;
    }

    public ThreadEntity setQueuing() {
        setStatus(ThreadProcessStatusEnum.QUEUING.name());
        return this;
    }

    public ThreadEntity setChatting() {
        setStatus(ThreadProcessStatusEnum.CHATTING.name());
        return this;
    }

    public ThreadEntity setTimeout() {
        setStatus(ThreadProcessStatusEnum.TIMEOUT.name());
        return this;
    }

    public ThreadEntity setClose() {
        setStatus(ThreadProcessStatusEnum.CLOSED.name());
        return this;
    }

    public ThreadProtobuf toProtobuf() {
        return ConvertUtils.convertToThreadProtobuf(this);
    }

    public UserProtobuf getUserProtobuf() {
        return JSON.parseObject(getUser(), UserProtobuf.class);
    }

    public UserProtobuf getAgentProtobuf() {
        return JSON.parseObject(getAgent(), UserProtobuf.class);
    }

    public UserProtobuf getRobotProtobuf() {
        return JSON.parseObject(getRobot(), UserProtobuf.class);
    }

    public UserProtobuf getWorkgroupProtobuf() {
        return JSON.parseObject(getWorkgroup(), UserProtobuf.class);
    }

    public UserProtobuf getTransferProtobuf() {
        return JSON.parseObject(getTransfer(), UserProtobuf.class);
    }

    public ThreadExtra getThreadExtra() {
        return JSON.parseObject(getExtra(), ThreadExtra.class);
    }

    public Boolean isRobotToAgent() {
        return getRobotProtobuf() != null && getAgentProtobuf() != null
                && UserTypeEnum.ROBOT.name().equals(getRobotProtobuf().getType())
                && UserTypeEnum.AGENT.name().equals(getAgentProtobuf().getType());
    }

    public Integer getAllMessageCount() {
        return messages.size();
    }

    public Integer getVisitorMessageCount() {
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromVisitor()) {
                count++;
            }
        }
        return count;
    }

    public Integer getAgentMessageCount() {
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromAgent()) {
                count++;
            }
        }
        return count;
    }

    public Integer getSystemMessageCount() {
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromSystem()) {
                count++;
            }
        }
        return count;
    }

    public Integer getRobotMessageCount() {
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromRobot()) {
                count++;
            }
        }
        return count;
    }

    public Integer getUnreadCount() {
        int count = 0;
        for (MessageEntity message : messages) {
            if (isCustomerService() && message.isFromVisitor() && message.isUnread()) {
                count++;
            } else if (isNoticeAccount() && message.isUnread()) {
                count++;
            } else if (isAssistant() && message.isUnread()) {
                count++;
            } else if (isMember() && message.isFromMember() && message.isUnread()) {
                return 0;
            } else if (isGroup() && message.isUnread()) {
                return 0;
            }
        }
        return count;
    }

    public Integer getVisitorUnreadCount() {
        int count = 0;
        for (MessageEntity message : messages) {
            if (message.isFromAgent() && message.isUnread()) {
                count++;
            }
        }
        return count;
    }

    public Boolean isValid() {
        return getVisitorMessageCount() > 0 && getAgentMessageCount() > 0;
    }

    @Override
    public String toString() {
        return "ThreadEntity{" +
                "id=" + getId() +
                ", uid='" + getUid() + '\'' +
                ", topic='" + getTopic() + '\'' +
                ", type='" + getType() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", createdAt=" + getCreatedAt() +
                '}';
    }

}