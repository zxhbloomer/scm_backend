package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 知识库问答记录实体类
 *
 * <p>对应数据库表：ai_knowledge_base_qa</p>
 * <p>对应 aideepin 实体：KnowledgeBaseQa</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Data
@TableName("ai_knowledge_base_qa")
public class AiKnowledgeBaseQaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 唯一标识符（32字符无连字符）
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 所属知识库ID
     * 关联：ai_knowledge_base.id
     */
    @TableField("kb_id")
    private String kbId;

    /**
     * 所属知识库UUID
     */
    @TableField("kb_uuid")
    private String kbUuid;

    /**
     * 用户的原始问题
     */
    @TableField("question")
    private String question;

    /**
     * 提供给LLM的提示词（包含RAG上下文）
     */
    @TableField("prompt")
    private String prompt;

    /**
     * 提示词消耗的token
     */
    @TableField("prompt_tokens")
    private Integer promptTokens;

    /**
     * 答案
     */
    @TableField("answer")
    private String answer;

    /**
     * 答案消耗的token
     */
    @TableField("answer_tokens")
    private Integer answerTokens;

    /**
     * 来源文档id,以逗号隔开
     */
    @TableField("source_file_ids")
    private String sourceFileIds;

    /**
     * 提问用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * AI模型ID
     */
    @TableField("ai_model_id")
    private String aiModelId;

    /**
     * 租户ID
     */
    @TableField("tenant_id")
    private Long tenantId;

    /**
     * 启用状态(0-禁用,1-启用)
     */
    @TableField("enable_status")
    private Integer enableStatus;

    /**
     * 创建时间（时间戳毫秒）
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间（时间戳毫秒）
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 是否删除（0-未删除, 1-已删除）
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建人
     */
    @TableField("create_user")
    private String createUser;
}
