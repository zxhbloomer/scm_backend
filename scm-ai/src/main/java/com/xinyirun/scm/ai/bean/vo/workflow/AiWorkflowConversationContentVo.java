package com.xinyirun.scm.ai.bean.vo.workflow;

import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI工作流对话内容业务视图对象
 *
 * 用于业务逻辑处理的工作流对话内容数据传输对象
 * 简化版VO，专注于工作流对话记录的基本信息
 *
 * @author SCM-AI开发团队
 * @since 2025-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiWorkflowConversationContentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private String id;

    /**
     * 消息ID（业务主键）
     * 用于引用表关联
     */
    private String message_id;

    /**
     * 对话ID
     * 格式：tenantCode::workflowUuid::userId
     */
    private String conversation_id;

    /**
     * 内容类型
     * USER - 用户消息
     * ASSISTANT - AI回复
     * SYSTEM - 系统消息
     * TOOL - 工具调用
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * AI模型源ID
     */
    private String model_source_id;

    /**
     * AI提供商名称
     */
    private String provider_name;

    /**
     * 基础模型名称
     */
    private String base_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 检查是否为用户消息
     */
    public boolean isUserMessage() {
        return AiMessageTypeConstant.MESSAGE_TYPE_USER.equals(this.type);
    }

    /**
     * 检查是否为AI回复
     */
    public boolean isAssistantMessage() {
        return AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT.equals(this.type);
    }

    /**
     * 检查是否为系统消息
     */
    public boolean isSystemMessage() {
        return AiMessageTypeConstant.MESSAGE_TYPE_SYSTEM.equals(this.type);
    }

    /**
     * 检查是否为工具消息
     */
    public boolean isToolMessage() {
        return AiMessageTypeConstant.MESSAGE_TYPE_TOOL.equals(this.type);
    }

    /**
     * 获取简短摘要
     */
    public String getBriefSummary(int maxLength) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        if (maxLength <= 0) {
            maxLength = 50;
        }

        String cleanedContent = content.trim();
        if (cleanedContent.length() <= maxLength) {
            return cleanedContent;
        }

        return cleanedContent.substring(0, maxLength - 3) + "...";
    }

}
