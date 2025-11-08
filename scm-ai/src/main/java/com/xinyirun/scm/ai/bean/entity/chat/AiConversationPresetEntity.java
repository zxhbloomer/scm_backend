package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话预设实体类
 * 对应数据表：ai_conversation_preset
 *
 * 功能说明：存储AI角色预设（系统预设和用户自定义）
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_conversation_preset")
public class AiConversationPresetEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 业务主键UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 预设标题
     */
    @TableField("title")
    private String title;

    /**
     * 预设描述
     */
    @TableField("remark")
    private String remark;

    /**
     * AI系统提示词
     * 类似ChatGPT的Custom Instructions
     */
    @TableField("ai_system_message")
    private String aiSystemMessage;

    /**
     * 是否公开
     * 1-公开，0-私有
     */
    @TableField("is_public")
    private Integer isPublic;

    /**
     * 分类
     * 如：编程、写作、翻译等
     */
    @TableField("category")
    private String category;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 使用次数统计
     */
    @TableField("use_count")
    private Integer useCount;

    /**
     * 创建者类型
     * SYSTEM-系统预设，USER-用户创建
     */
    @TableField("creator_type")
    private String creatorType;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 数据版本（乐观锁）
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 创建人用户名
     */
    @TableField("create_user")
    private String createUser;
}
