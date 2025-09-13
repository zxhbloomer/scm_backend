/**
 * 聊天记忆实体，用于存储多轮对话的上下文信息
 */
package com.xinyirun.scm.ai.memory;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("scm_ai_chat_memory")
public class ChatMemoryEntity extends BytedeskBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID，关联到thread_uid
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型：USER, ASSISTANT, SYSTEM, TOOL
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 消息时间戳，用于排序
     */
    @TableField("timestamp")
    private Long timestamp;

    /**
     * 额外信息，用于存储消息的扩展数据
     */
    @TableField("extra")
    private String extra;
}