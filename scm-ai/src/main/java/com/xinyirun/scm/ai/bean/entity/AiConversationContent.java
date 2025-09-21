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
 * AI会话内容实体类
 *
 * 对应数据库表 ai_conversation_content
 * 用于存储AI会话中的具体消息内容
 *
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_conversation_content")
public class AiConversationContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 内容唯一标识符
     */
    @TableId("id")
    private String id;

    /**
     * 会话ID
     * 关联到ai_conversation表的外键
     */
    @TableField("conversation_id")
    private String conversation_id;

    /**
     * 内容类型
     * USER - 用户消息
     * AI - AI回复
     * SYSTEM - 系统消息
     */
    @TableField("type")
    private String type;

    /**
     * 消息内容
     * 存储用户输入或AI回复的具体内容
     */
    @TableField("content")
    private String content;

    /**
     * 创建时间
     * 消息创建的时间戳（毫秒）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Long create_time;

    // ==================== 非数据库字段（业务扩展） ====================

    /**
     * 处理状态（扩展字段，不存储到数据库）
     * PENDING - 处理中
     * SUCCESS - 处理成功
     * FAILED - 处理失败
     * TIMEOUT - 处理超时
     */
    @TableField(exist = false)
    private String status;

    /**
     * 错误消息（扩展字段，不存储到数据库）
     * 当处理失败时记录错误详情
     */
    @TableField(exist = false)
    private String error_message;

    /**
     * AI模型信息（扩展字段，不存储到数据库）
     * 记录使用的AI模型相关信息
     */
    @TableField(exist = false)
    private String ai_model;

    /**
     * AI提供商（扩展字段，不存储到数据库）
     * 记录AI模型的提供商
     */
    @TableField(exist = false)
    private String ai_provider;

    /**
     * Token消耗（扩展字段，不存储到数据库）
     * 记录本次对话消耗的token数量
     */
    @TableField(exist = false)
    private Integer token_usage;

    /**
     * 处理耗时（扩展字段，不存储到数据库）
     * 记录AI响应时间，单位：毫秒
     */
    @TableField(exist = false)
    private Long processing_time;

    /**
     * 创建用户（扩展字段，不存储到数据库）
     * 记录创建该内容的用户
     */
    @TableField(exist = false)
    private String create_user;

    /**
     * 租户ID（扩展字段，不存储到数据库）
     * 用于多租户数据隔离
     */
    @TableField(exist = false)
    private String tenant_id;

    /**
     * 是否包含敏感信息（扩展字段，不存储到数据库）
     * 标识内容是否包含敏感信息
     */
    @TableField(exist = false)
    private Boolean has_sensitive_info;

    /**
     * 内容摘要（扩展字段，不存储到数据库）
     * 用于快速展示内容概要
     */
    @TableField(exist = false)
    private String content_summary;

    /**
     * 消息内容JSON（扩展字段，不存储到数据库）
     * 存储结构化的消息内容
     */
    @TableField(exist = false)
    private String message_content;

    /**
     * Token使用量（兼容字段，不存储到数据库）
     * 与token_usage字段保持兼容
     */
    @TableField(exist = false)
    private Integer token_used;

    /**
     * 响应时间（兼容字段，不存储到数据库）
     * 与processing_time字段保持兼容
     */
    @TableField(exist = false)
    private Long response_time;

    /**
     * 消息类型（兼容字段，不存储到数据库）
     * 与type字段保持兼容
     */
    @TableField(exist = false)
    private Integer message_type;

    /**
     * 删除标记（扩展字段，不存储到数据库）
     * 0-未删除，1-已删除
     */
    @TableField(exist = false)
    private Integer is_deleted;

    // ==================== 常量定义 ====================

    /**
     * 内容类型：用户消息
     */
    public static final String TYPE_USER = "USER";

    /**
     * 内容类型：AI回复
     */
    public static final String TYPE_AI = "AI";

    /**
     * 内容类型：系统消息
     */
    public static final String TYPE_SYSTEM = "SYSTEM";

    /**
     * 处理状态：处理中
     */
    public static final String STATUS_PENDING = "PENDING";

    /**
     * 处理状态：处理成功
     */
    public static final String STATUS_SUCCESS = "SUCCESS";

    /**
     * 处理状态：处理失败
     */
    public static final String STATUS_FAILED = "FAILED";

    /**
     * 处理状态：处理超时
     */
    public static final String STATUS_TIMEOUT = "TIMEOUT";

    // ==================== 业务方法 ====================

    /**
     * 检查是否为用户消息
     *
     * @return true如果是用户消息
     */
    public boolean isUserMessage() {
        return TYPE_USER.equals(this.type);
    }

    /**
     * 检查是否为AI回复
     *
     * @return true如果是AI回复
     */
    public boolean isAiMessage() {
        return TYPE_AI.equals(this.type);
    }

    /**
     * 检查是否为系统消息
     *
     * @return true如果是系统消息
     */
    public boolean isSystemMessage() {
        return TYPE_SYSTEM.equals(this.type);
    }

    /**
     * 检查处理是否成功
     *
     * @return true如果处理成功
     */
    public boolean isProcessingSuccess() {
        return STATUS_SUCCESS.equals(this.status);
    }

    /**
     * 检查是否正在处理
     *
     * @return true如果正在处理
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(this.status);
    }

    /**
     * 检查处理是否失败
     *
     * @return true如果处理失败
     */
    public boolean isFailed() {
        return STATUS_FAILED.equals(this.status) || STATUS_TIMEOUT.equals(this.status);
    }

    /**
     * 获取内容长度
     *
     * @return 内容字符长度
     */
    public int getContentLength() {
        return content != null ? content.length() : 0;
    }

    /**
     * 获取内容摘要
     * 用于快速显示内容概要
     *
     * @param maxLength 最大长度
     * @return 内容摘要
     */
    public String getContentSummary(int maxLength) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        if (maxLength <= 0) {
            maxLength = 100;
        }

        String cleanedContent = content.trim();
        if (cleanedContent.length() <= maxLength) {
            return cleanedContent;
        }

        return cleanedContent.substring(0, maxLength - 3) + "...";
    }

    /**
     * 获取类型显示名称
     *
     * @return 类型的中文显示名称
     */
    public String getTypeDisplayName() {
        switch (this.type) {
            case TYPE_USER:
                return "用户";
            case TYPE_AI:
                return "AI";
            case TYPE_SYSTEM:
                return "系统";
            default:
                return "未知";
        }
    }

    /**
     * 设置默认值
     * 用于创建新内容时初始化默认值
     */
    public void setDefaults() {
        if (this.status == null) {
            this.status = STATUS_PENDING;
        }

        if (this.create_time == null) {
            this.create_time = System.currentTimeMillis();
        }

        if (this.has_sensitive_info == null) {
            this.has_sensitive_info = false;
        }
    }

    /**
     * 检查内容是否有效
     *
     * @return true如果内容有效
     */
    public boolean isValidContent() {
        return content != null && !content.trim().isEmpty();
    }

    /**
     * 构建调试信息
     *
     * @return 调试信息字符串
     */
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("AiConversationContent{");
        info.append("id='").append(id).append('\'');
        info.append(", conversation_id='").append(conversation_id).append('\'');
        info.append(", type='").append(type).append('\'');
        info.append(", status='").append(status).append('\'');
        info.append(", content_length=").append(getContentLength());
        info.append(", create_time=").append(create_time);

        if (token_usage != null) {
            info.append(", token_usage=").append(token_usage);
        }

        if (processing_time != null) {
            info.append(", processing_time=").append(processing_time).append("ms");
        }

        info.append('}');
        return info.toString();
    }
}