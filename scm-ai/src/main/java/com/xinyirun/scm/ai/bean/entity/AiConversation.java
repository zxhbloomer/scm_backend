package com.xinyirun.scm.ai.bean.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI会话实体类
 *
 * 对应数据库表 ai_conversation
 * 用于存储AI会话的基本信息
 *
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_conversation")
public class AiConversation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 会话唯一标识符
     */
    @TableId("id")
    private String id;

    /**
     * 会话标题
     * 会话的显示名称，用于用户界面展示
     */
    @TableField("title")
    private String title;

    /**
     * 创建用户
     * 创建该会话的用户标识
     */
    @TableField("create_user")
    private String create_user;

    /**
     * 创建时间
     * 会话创建的时间戳（毫秒）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long create_time;

    /**
     * 用户ID
     * 会话所属用户（兼容字段）
     */
    @TableField(exist = false)
    private Long user_id;

    /**
     * 模型提供商
     * AI模型提供商名称
     */
    @TableField(exist = false)
    private String model_provider;

    /**
     * 模型名称
     * 使用的AI模型名称
     */
    @TableField(exist = false)
    private String model_name;

    /**
     * 删除标记
     * 0-未删除，1-已删除
     */
    @TableField(exist = false)
    private Integer is_deleted;

    // ==================== 非数据库字段（业务扩展） ====================

    /**
     * 会话状态（扩展字段，不存储到数据库）
     * 用于业务逻辑中标识会话的当前状态
     */
    @TableField(exist = false)
    private String status;

    /**
     * 最后一条消息内容（扩展字段，不存储到数据库）
     * 用于列表展示时显示最后一条消息的摘要
     */
    @TableField(exist = false)
    private String last_content;

    /**
     * 最后更新时间（扩展字段，不存储到数据库）
     * 记录会话中最后一条消息的时间
     */
    @TableField(exist = false)
    private Long last_update_time;

    /**
     * 消息数量（扩展字段，不存储到数据库）
     * 统计该会话中包含的消息总数
     */
    @TableField(exist = false)
    private Integer message_count;

    /**
     * 创建用户名称（扩展字段，不存储到数据库）
     * 用于显示创建用户的友好名称
     */
    @TableField(exist = false)
    private String create_user_name;

    /**
     * 租户ID（扩展字段，不存储到数据库）
     * 用于多租户数据隔离
     */
    @TableField(exist = false)
    private String tenant_id;

    /**
     * 页面代码（扩展字段，不存储到数据库）
     * 标识会话所属的业务页面
     */
    @TableField(exist = false)
    private String page_code;

    // ==================== 常量定义 ====================

    /**
     * 会话状态：活跃
     */
    public static final String STATUS_ACTIVE = "ACTIVE";

    /**
     * 会话状态：已归档
     */
    public static final String STATUS_ARCHIVED = "ARCHIVED";

    /**
     * 会话状态：已删除
     */
    public static final String STATUS_DELETED = "DELETED";

    // ==================== 业务方法 ====================

    /**
     * 检查会话是否为活跃状态
     *
     * @return true如果会话是活跃状态
     */
    public boolean isActive() {
        return STATUS_ACTIVE.equals(this.status);
    }

    /**
     * 检查会话是否已归档
     *
     * @return true如果会话已归档
     */
    public boolean isArchived() {
        return STATUS_ARCHIVED.equals(this.status);
    }

    /**
     * 检查会话是否已删除
     *
     * @return true如果会话已删除
     */
    public boolean isDeleted() {
        return STATUS_DELETED.equals(this.status);
    }

    /**
     * 获取会话摘要信息
     * 用于快速显示会话的基本信息
     *
     * @return 会话摘要字符串
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("会话: ").append(title != null ? title : "未命名");

        if (message_count != null && message_count > 0) {
            summary.append(" (").append(message_count).append("条消息)");
        }

        if (last_content != null && !last_content.trim().isEmpty()) {
            String shortContent = last_content.length() > 50
                ? last_content.substring(0, 50) + "..."
                : last_content;
            summary.append(" - ").append(shortContent);
        }

        return summary.toString();
    }

    /**
     * 设置默认值
     * 用于创建新会话时初始化默认值
     */
    public void setDefaults() {
        if (this.status == null) {
            this.status = STATUS_ACTIVE;
        }

        if (this.message_count == null) {
            this.message_count = 0;
        }

        if (this.create_time == null) {
            this.create_time = System.currentTimeMillis();
        }
    }
}