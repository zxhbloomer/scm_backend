package com.xinyirun.scm.ai.bean.entity.rag.elasticsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

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
@Document(indexName = "kb_embeddings")
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
     * Token数量（用于计费和统计）
     */
    @Field(type = FieldType.Integer)
    private Integer tokenCount;

    /**
     * 向量嵌入（1024维，硅基流动BAAI/bge-m3模型）
     */
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] embedding;

    /**
     * 相似度分数（查询时计算）
     */
    @Field(type = FieldType.Float)
    private Float similarityScore;

    /**
     * 租户编码（用于多租户过滤）
     */
    @Field(type = FieldType.Keyword)
    private String tenantCode;

    /**
     * 创建时间（时间戳，毫秒）
     */
    @Field(type = FieldType.Long)
    private Long createTime;
}
