/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 10:21:32
 * @Description: 消息实体抽象基类，用于统一所有消息类型的字段结构
 */
package com.xinyirun.scm.ai.message;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import com.xinyirun.scm.ai.constant.BytedeskConsts;
import com.xinyirun.scm.ai.constant.TypeConsts;
import com.xinyirun.scm.ai.core.enums.ChannelEnum;

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
public abstract class AbstractMessageEntity extends BytedeskBaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    @TableField("message_type")
    private String type = MessageTypeEnum.TEXT.name();

    // 仅对一对一/客服/技能组聊天有效，表示对方是否已读。群聊无效
    @Builder.Default
    @TableField("status")
    private String status = MessageStatusEnum.SUCCESS.name();

    // 复杂类型可以使用json存储在此，通过type字段区分
    @TableField("content")
    private String content;

    // 额外信息，用于存储消息的额外信息
    @Builder.Default
    @TableField("extra")
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 消息来源，用于区分消息是来自web、android还是ios等
    @Builder.Default
    @TableField("channel")
    private String channel = ChannelEnum.WEB.name();

    /**
     * sender信息的JSON表示
     */
    @Builder.Default
    @TableField("message_user")
    private String user = BytedeskConsts.EMPTY_JSON_STRING;
    
}