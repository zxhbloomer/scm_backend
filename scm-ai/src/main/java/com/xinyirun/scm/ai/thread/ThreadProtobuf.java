/**
 * Thread协议缓冲区对象，用于数据传输和序列化，提供会话状态判断方法
 */
package com.xinyirun.scm.ai.thread;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.core.enums.ChannelEnum;
import com.xinyirun.scm.ai.rbac.user.UserProtobuf;
import com.xinyirun.scm.ai.thread.enums.ThreadProcessStatusEnum;
import com.xinyirun.scm.ai.thread.enums.ThreadTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ThreadProtobuf implements Serializable {

    private static final long serialVersionUID = 1L;

	private String uid;

    private String topic;

    private ThreadTypeEnum type;

    private ThreadProcessStatusEnum status;

    private UserProtobuf user;

    private ChannelEnum channel;

    private String extra;

    public static ThreadProtobuf fromJson(String json) {
        return JSON.parseObject(json, ThreadProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public Boolean isMember() {
        return ThreadTypeEnum.MEMBER.equals(getType());
    }

    public Boolean isNew() {
        return ThreadProcessStatusEnum.NEW.equals(getStatus());
    }

    // ROBOTING
    public Boolean isRoboting() {
        return isRobotType() && isChatting();
    }   

    // queuing
    public Boolean isQueuing() {
        return ThreadProcessStatusEnum.QUEUING.equals(getStatus());
    }

    // is offline
    public Boolean isOffline() {
        return ThreadProcessStatusEnum.OFFLINE.equals(getStatus());
    }

    public Boolean isChatting() {
        return ThreadProcessStatusEnum.CHATTING.equals(getStatus());
    }

    //
    public Boolean isClosed() {
        return ThreadProcessStatusEnum.CLOSED.equals(getStatus());
    }
    
    public Boolean isCustomerService() {
        return ThreadTypeEnum.AGENT.equals(getType())
                || ThreadTypeEnum.WORKGROUP.equals(getType())
                || ThreadTypeEnum.ROBOT.equals(getType())
                || ThreadTypeEnum.UNIFIED.equals(getType());
    }

    public Boolean isRobotType() {
        return ThreadTypeEnum.ROBOT.equals(getType());
    }

    public Boolean isWorkgroupType() {
        return ThreadTypeEnum.WORKGROUP.equals(getType());
    }

    public Boolean isAgentType() {
        return ThreadTypeEnum.AGENT.equals(getType());
    }

    public Boolean isUnifiedType() {
        return ThreadTypeEnum.UNIFIED.equals(getType());
    }

    public Boolean isWeChatMp() {
        return ChannelEnum.WECHAT_MP.equals(getChannel());
    }

    public Boolean isWeChatMini() {
        return ChannelEnum.WECHAT_MINI.equals(getChannel());
    }
}