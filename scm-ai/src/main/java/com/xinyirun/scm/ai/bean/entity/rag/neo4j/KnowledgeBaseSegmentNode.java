package com.xinyirun.scm.ai.bean.entity.rag.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Neo4j 知识库文本段节点实体类
 *
 * <p>对应 Neo4j 节点标签：KnowledgeBaseSegment</p>
 * <p>注意：属性命名使用 snake_case 与 Neo4j 属性一致</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Data
@Node("KnowledgeBaseSegment")
public class KnowledgeBaseSegmentNode {

    /**
     * Neo4j 内部ID（自动生成）
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 业务UUID（32字符无连字符）
     */
    @Property("segment_uuid")
    private String segment_uuid;

    /**
     * 文本段内容
     */
    @Property("segment_text")
    private String segment_text;

    /**
     * 文本段索引（从0开始）
     */
    @Property("segment_index")
    private Integer segment_index;

    /**
     * 所属知识库UUID
     */
    @Property("kb_uuid")
    private String kb_uuid;

    /**
     * 所属文档UUID
     */
    @Property("kb_item_uuid")
    private String kb_item_uuid;

    /**
     * 租户ID（多租户隔离）
     */
    @Property("tenant_id")
    private String tenant_id;

    /**
     * 创建时间
     */
    @Property("create_time")
    private LocalDateTime create_time;

    /**
     * 关系：文本段提及的实体列表
     */
    @Relationship(type = "MENTIONS", direction = Relationship.Direction.OUTGOING)
    private List<MentionsRelationship> mentioned_entities;

    /**
     * 关系：相似的文本段列表
     */
    @Relationship(type = "SIMILAR_TO", direction = Relationship.Direction.OUTGOING)
    private List<SimilarToRelationship> similar_segments;

    /**
     * MENTIONS 关系属性类
     */
    @Data
    @RelationshipProperties
    public static class MentionsRelationship {
        @Id
        @GeneratedValue
        private Long id;

        /**
         * 提及置信度（0.0-1.0）
         */
        @Property("confidence")
        private Float confidence;

        /**
         * 提及上下文（前后20字符）
         */
        @Property("mention_context")
        private String mention_context;

        /**
         * 创建时间
         */
        @Property("create_time")
        private LocalDateTime create_time;

        /**
         * 目标实体节点
         */
        @TargetNode
        private EntityNode entity;
    }

    /**
     * SIMILAR_TO 关系属性类
     */
    @Data
    @RelationshipProperties
    public static class SimilarToRelationship {
        @Id
        @GeneratedValue
        private Long id;

        /**
         * 相似度分数（0.0-1.0）
         */
        @Property("similarity_score")
        private Float similarity_score;

        /**
         * 创建时间
         */
        @Property("create_time")
        private LocalDateTime create_time;

        /**
         * 目标文本段节点
         */
        @TargetNode
        private KnowledgeBaseSegmentNode target_segment;
    }
}
