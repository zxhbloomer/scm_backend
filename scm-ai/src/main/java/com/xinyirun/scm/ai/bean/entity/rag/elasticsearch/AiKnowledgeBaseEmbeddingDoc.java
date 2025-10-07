package com.xinyirun.scm.ai.bean.entity.rag.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

/**
 * Elasticsearch 向量嵌入文档实体类
 *
 * <p>对应 Elasticsearch 索引：tenant_{tenant_id}_kb_embeddings</p>
 * <p>注意：字段命名使用 snake_case 与 Elasticsearch 映射一致</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Data
@Document(indexName = "#{@tenantIndexNameService.getKbEmbeddingsIndexName()}")
@Setting(settingPath = "elasticsearch/kb-embeddings-settings.json")
public class AiKnowledgeBaseEmbeddingDoc {

    /**
     * 文档ID（使用 segment_uuid 作为主键）
     */
    @Id
    private String id;

    /**
     * 所属知识库UUID
     */
    @Field(type = FieldType.Keyword)
    private String kbUuid;

    /**
     * 所属文档UUID
     */
    @Field(type = FieldType.Keyword)
    private String kbItemUuid;

    /**
     * 文本段UUID（业务主键）
     */
    @Field(type = FieldType.Keyword)
    private String segmentUuid;

    /**
     * 文本段索引（从0开始）
     */
    @Field(type = FieldType.Integer)
    private Integer segmentIndex;

    /**
     * 文本段内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String segmentText;

    /**
     * 向量嵌入（384维，all-minilm-l6-v2模型）
     */
    @Field(type = FieldType.Dense_Vector, dims = 384)
    private float[] embedding;

    /**
     * 相似度分数（查询时计算）
     */
    @Field(type = FieldType.Float)
    private Float similarityScore;

    /**
     * 租户ID（用于多租户过滤）
     */
    @Field(type = FieldType.Long)
    private Long tenantId;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createTime;
}
