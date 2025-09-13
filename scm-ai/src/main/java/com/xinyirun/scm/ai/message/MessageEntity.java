/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 14:18:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.xinyirun.scm.ai.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.ai.rbac.user.UserProtobuf;
import com.xinyirun.scm.ai.thread.entity.ThreadEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Message entity for storing individual chat messages
 * Represents a single message within a conversation thread
 * 
 * Database Table: scm_ai_message
 * Purpose: Stores message content, sender information, and message metadata
 */
@Data
@SuperBuilder
@NoArgsConstructor // 添加无参构造函数
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("scm_ai_message")
public class MessageEntity extends AbstractMessageEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Associated conversation thread containing this message
     * Many-to-one relationship: multiple messages can belong to one thread
     */
    @TableField("thread_uid")
    private String threadUid;

    // 是否未读
    public Boolean isUnread() {
        if (MessageStatusEnum.SENDING.name().equals(getStatus()) 
            || MessageStatusEnum.SUCCESS.name().equals(getStatus())
            || MessageStatusEnum.DELIVERED.name().equals(getStatus())) {
            return true;
        }
        return false;
    }

    // 可以在这里添加 MessageEntity 特有的字段（如果有的话）
    public UserProtobuf getUserProtobuf() {
        return UserProtobuf.fromJson(getUser());
    }

    // 通过解析user字段中的type字段来判断 type=robot则为机器人，否则为访客
    public Boolean isFromRobot() {
        return getUserProtobuf().isRobot();
    }

    // 通过解析user字段中的type字段来判断 type=visitor则为访客，否则为客服
    public Boolean isFromVisitor() {
        return getUserProtobuf().isVisitor();
    }

    public Boolean isFromUser() {
        return getUserProtobuf().isUser();
    }

    public Boolean isFromMember() {
        return getUserProtobuf().isMember();
    }

    // 是否系统消息
    public Boolean isFromSystem() {
        return getUserProtobuf().isSystem();
    }

    // 是否客服消息
    public Boolean isFromAgent() {
        return getUserProtobuf().isAgent();
    }

    /**
     * 获取消息扩展信息
     */
    public MessageExtra getMessageExtra() {
        String extraJson = getExtra();
        if (extraJson != null && !extraJson.trim().isEmpty() && !extraJson.equals("{}")) {
            return MessageExtra.fromJson(extraJson);
        }
        return MessageExtra.builder().build();
    }

    /**
     * 设置消息扩展信息
     */
    public void setMessageExtra(MessageExtra messageExtra) {
        if (messageExtra != null) {
            setExtra(messageExtra.toJson());
        }
    }

    /**
     * 重写toString方法避免循环引用
     */
    @Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + getId() +
                ", uid='" + getUid() + '\'' +
                ", type='" + getType() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", userUid='" + getUserUid() + '\'' +
                '}';
    }
}