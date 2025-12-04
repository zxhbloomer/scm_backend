package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI知识库实体类
 *
 * <p>对应数据库表：ai_knowledge_base</p>
 * <p>注意：字段命名使用 snake_case 与数据库一致</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Data
@TableName("ai_knowledge_base")
public class AiKnowledgeBaseEntity {

    /**
     * 主键ID（UUID字符串）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 知识库UUID（业务主键，32字符无连字符）
     */
    @TableField("kb_uuid")
    private String kbUuid;

    /**
     * 知识库标题
     */
    @TableField("title")
    private String title;

    /**
     * 备注说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否公开（0-私有，1-公开）
     */
    @TableField("is_public")
    private Integer isPublic;

    /**
     * 是否严格模式(0-非严格,1-严格)
     */
    @TableField("is_strict")
    private Integer isStrict;

    /**
     * 文档切块重叠数量
     */
    @TableField("ingest_max_overlap")
    private Integer ingestMaxOverlap;

    /**
     * 索引时使用的LLM模型名称
     * @deprecated 已废弃，关联的 ai_model_source 表已废弃，请使用 ai_config 和 ai_model_config 表的新配置逻辑
     */
    @Deprecated
    @TableField("ingest_model_name")
    private String ingestModelName;

    /**
     * 索引时使用的LLM模型ID
     * @deprecated 已废弃，关联的 ai_model_source 表已废弃，请使用 ai_config 和 ai_model_config 表的新配置逻辑
     */
    @Deprecated
    @TableField("ingest_model_id")
    private String ingestModelId;

    /**
     * Token估计器
     */
    @TableField("ingest_token_estimator")
    private String ingestTokenEstimator;

    /**
     * 向量化模型
     */
    @TableField("ingest_embedding_model")
    private String ingestEmbeddingModel;

    /**
     * 检索最大结果数
     */
    @TableField("retrieve_max_results")
    private Integer retrieveMaxResults;

    /**
     * 检索最小分数
     */
    @TableField("retrieve_min_score")
    private java.math.BigDecimal retrieveMinScore;

    /**
     * LLM温度参数
     */
    @TableField("query_llm_temperature")
    private java.math.BigDecimal queryLlmTemperature;

    /**
     * 系统消息模板
     */
    @TableField("query_system_message")
    private String querySystemMessage;

    /**
     * 点赞数
     */
    @TableField("star_count")
    private Integer starCount;

    /**
     * 向量数量
     */
    @TableField("embedding_count")
    private Integer embeddingCount;

    /**
     * 知识图谱实体数量
     */
    @TableField("entity_count")
    private Integer entityCount;

    /**
     * 知识图谱关系数量
     */
    @TableField("relation_count")
    private Integer relationCount;

    /**
     * 所有者ID
     */
    @TableField("owner_id")
    private String ownerId;

    /**
     * 所有者名称
     */
    @TableField("owner_name")
    private String ownerName;

    /**
     * 文档项数量（统计字段）
     */
    @TableField("item_count")
    private Integer itemCount;

    /**
     * 是否临时知识库（0-否，1-是）
     */
    @TableField("is_temp")
    private Integer is_temp;

    /**
     * 过期时间（仅临时知识库有值）
     */
    @TableField("expire_time")
    private LocalDateTime expire_time;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;
}
