package com.xinyirun.scm.ai.bean.entity.search;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI搜索记录实体类
 * 对应数据表：ai_search_record
 *
 * 功能说明：存储AI搜索历史，包括搜索问题、搜索引擎响应、LLM回答等信息
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_search_record", autoResultMap = true)
public class AiSearchRecordEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 搜索UUID(业务主键)
     */
    @TableField("search_uuid")
    private String searchUuid;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * AI模型ID
     */
    @TableField("ai_model_id")
    private Long aiModelId;

    /**
     * 搜索问题
     */
    @TableField("question")
    private String question;

    /**
     * 搜索引擎响应(JSON格式)
     */
    @TableField(value = "search_engine_response", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> searchEngineResponse;

    /**
     * LLM提示词
     */
    @TableField("prompt")
    private String prompt;

    /**
     * LLM回答
     */
    @TableField("answer")
    private String answer;

    /**
     * 提示词token数
     */
    @TableField("prompt_tokens")
    private Integer promptTokens;

    /**
     * 回答token数
     */
    @TableField("answer_tokens")
    private Integer answerTokens;

    /**
     * 总token数
     */
    @TableField("total_tokens")
    private Integer totalTokens;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本(乐观锁)
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
