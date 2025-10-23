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
 * AI搜索向量元数据实体类
 * 对应数据表：ai_search_embedding
 *
 * 功能说明：存储搜索相关的向量元数据，实际向量数据存储在Elasticsearch中
 * 注意：scm-ai架构使用Elasticsearch存储向量,此表仅用于元数据关联
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_search_embedding", autoResultMap = true)
public class AiSearchEmbeddingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 向量ID(对应Elasticsearch文档ID)
     */
    @TableField("embedding_id")
    private String embeddingId;

    /**
     * 搜索记录ID
     */
    @TableField("search_record_id")
    private Long searchRecordId;

    /**
     * 文本内容
     */
    @TableField("text")
    private String text;

    /**
     * 元数据(JSON格式)
     */
    @TableField(value = "metadata", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * Elasticsearch索引名称
     */
    @TableField("es_index_name")
    private String esIndexName;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Integer isDeleted;

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
