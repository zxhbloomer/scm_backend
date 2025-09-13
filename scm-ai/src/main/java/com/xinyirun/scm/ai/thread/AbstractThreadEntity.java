/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-15 17:09:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.xinyirun.scm.ai.thread;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import com.xinyirun.scm.ai.constant.BytedeskConsts;
import com.xinyirun.scm.ai.constant.TypeConsts;
import com.xinyirun.scm.ai.core.enums.ChannelEnum;
import com.xinyirun.scm.ai.thread.enums.ThreadTypeEnum;
import com.xinyirun.scm.ai.thread.enums.ThreadProcessStatusEnum;
import com.xinyirun.scm.ai.thread.enums.ThreadTransferStatusEnum;
import com.xinyirun.scm.ai.converter.JsonListConverter;
import com.xinyirun.scm.ai.converter.StringListConverter;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.ToString;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class AbstractThreadEntity extends BytedeskBaseEntity {
    
    private static final long serialVersionUID = 1L;

    /**
     * @{TopicUtils}
     */
    @NotBlank
    @TableField("thread_topic")
    private String topic;

    @Builder.Default
    @TableField("content")
    private String content = BytedeskConsts.EMPTY_STRING;

    /**
     * @{ThreadTypeConsts}
     */
    @Builder.Default
    @TableField("thread_type")
    private String type = ThreadTypeEnum.WORKGROUP.name();

    // process status
    @Builder.Default
    @TableField("thread_status")
    private String status = ThreadProcessStatusEnum.NEW.name();

    // transfer status
    @Builder.Default
    @TableField("thread_transfer_status")
    private String transferStatus = ThreadTransferStatusEnum.NONE.name();

    // 星标
    @Builder.Default
    @TableField("thread_star")
    private Integer star = 0;

    // 置顶
    @Builder.Default
    @TableField("is_top")
    private Boolean top = false;

    // 未读
    @Builder.Default
    @TableField("is_unread")
    private Boolean unread = false;

    // 免打扰
    @Builder.Default
    @TableField("is_mute")
    private Boolean mute = false;

    // 不在会话列表显示
    @Builder.Default
    @TableField("is_hide")
    private Boolean hide = false;
    
    // 类似微信折叠会话
    @Builder.Default
    @TableField("is_fold")
    private Boolean fold = false;

    // 自动关闭
    @Builder.Default
    @TableField("is_auto_close")
    private Boolean autoClose = false;

    // 备注
    @TableField("thread_note")
    private String note;

    // 标签
    @Builder.Default
    @TableField("tag_list")
    private String tagList = BytedeskConsts.EMPTY_JSON_STRING;

    @Builder.Default
    @TableField("channel")
    private String channel = ChannelEnum.WEB.name();

    @Builder.Default
    @TableField("thread_extra")   
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 在客服会话中，存储访客信息
     * 在同事会话中，存储同事信息
     * 在用户私聊中，存储对方用户信息
     * 机器人会话中，存储访客信息
     * 群组会话中，存储群组信息
     * 注意：h2 db 不能使用 user, 所以重定义为 thread_user
     * @{UserProtobuf}
     */
    @Builder.Default
    @TableField("thread_user")
    private String user = BytedeskConsts.EMPTY_JSON_STRING;

    /**
     * 一对一客服对话中，存储客服信息
     * @{UserProtobuf}
     */
    @Builder.Default
    @TableField("agent")
    private String agent = BytedeskConsts.EMPTY_JSON_STRING;

    // 机器人对话中，存储机器人信息
    @Builder.Default
    @TableField("robot")
    private String robot = BytedeskConsts.EMPTY_JSON_STRING;

    // 技能组客服对话中，存储技能组信息
    @Builder.Default
    @TableField("workgroup")
    private String workgroup = BytedeskConsts.EMPTY_JSON_STRING;

    // 存放被转接客服，存放多个 UserProtobuf 实体转换成的 JSON
    @Builder.Default
    @TableField("transfer")
    private String transfer = BytedeskConsts.EMPTY_JSON_STRING;

    // 邀请多个客服参与会话，存放多个 UserProtobuf 实体转换成的 JSON
    @Builder.Default
    @TableField("invites")
    private String invites = BytedeskConsts.EMPTY_JSON_STRING;

    // 多个管理员监听会话, 存放多个 UserProtobuf 实体转换成的 JSON
    @Builder.Default
    @TableField("monitors")
    private String monitors = BytedeskConsts.EMPTY_JSON_STRING;

    // 存放多个 UserProtobuf 实体转换成的 JSON
    @Builder.Default
    @TableField("assistants")
    private String assistants = BytedeskConsts.EMPTY_JSON_STRING;

    // 存放多个 UserProtobuf 实体转换成的 JSON
    @Builder.Default
    @TableField("ticketors")
    private String ticketors = BytedeskConsts.EMPTY_JSON_STRING;

    // 流程实例ID
    @TableField("process_instance_id")
    private String processInstanceId;

    // 流程定义实体UID
    @TableField("process_entity_uid")
    private String processEntityUid;

    // belongs to user - owner uid
    @TableField("owner_uid")
    private String ownerUid;    

}