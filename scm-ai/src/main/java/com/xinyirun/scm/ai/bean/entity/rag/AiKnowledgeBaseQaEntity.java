package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * AI知识库问答记录实体类
 * 对应数据库表：ai_knowledge_base_qa
 *
 * @author zxh
 * @since 2025-10-12
 */
@Data
@TableName("ai_knowledge_base_qa")
public class AiKnowledgeBaseQaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * UUID（32位）
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 知识库ID
     */
    @TableField("kb_id")
    private String kbId;

    /**
     * 知识库UUID
     */
    @TableField("kb_uuid")
    private String kbUuid;

    /**
     * 用户问题
     */
    @TableField("question")
    private String question;

    /**
     * 实际发送给LLM的Prompt（包含上下文）
     */
    @TableField("prompt")
    private String prompt;

    /**
     * Prompt消耗的Token数
     */
    @TableField("prompt_tokens")
    private Integer promptTokens;

    /**
     * AI回答内容
     */
    @TableField("answer")
    private String answer;

    /**
     * Answer消耗的Token数
     */
    @TableField("answer_tokens")
    private Integer answerTokens;

    /**
     * 来源文件ID列表（逗号分隔）
     */
    @TableField("source_file_ids")
    private String sourceFileIds;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * AI模型ID
     */
    @TableField("ai_model_id")
    private String aiModelId;

    /**
     * 启用状态（1-启用，0-禁用）
     */
    @TableField("enable_status")
    private Integer enableStatus;

    /**
     * 创建时间（时间戳，毫秒）
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间（时间戳，毫秒）
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 删除标记（0-未删除，1-已删除）
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;

    /**
     * AI模型名称（避免关联查询ai_model_source表）
     */
    @TableField("ai_model_name")
    private String aiModelName;
}
