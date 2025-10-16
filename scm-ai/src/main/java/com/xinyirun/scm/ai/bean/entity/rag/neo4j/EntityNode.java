package com.xinyirun.scm.ai.bean.entity.rag.neo4j;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Neo4j 实体节点实体类
 *
 * <p>对应 Neo4j 节点标签：Entity</p>
 * <p>注意：属性命名使用 snake_case 与 Neo4j 属性一致</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Data
@Node("Entity")
public class EntityNode {

    /**
     * Neo4j 内部ID（自动生成）
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 业务UUID（32字符无连字符）
     * 统一使用snake_case命名，与Neo4j属性名一致
     */
    @Property("entity_uuid")
    private String entityUuid;

    /**
     * 实体名称（如"ABC供应商"）
     * 统一使用snake_case命名，与Neo4j属性名一致
     */
    @Property("entity_name")
    private String entityName;

    /**
     * 实体类型（supplier, product, contract, purchase_order等）
     * 统一使用snake_case命名，与Neo4j属性名一致
     */
    @Property("entity_type")
    private String entityType;

    /**
     * 实体元数据（JSON字符串格式）
     */
    @Property("entity_metadata")
    private String entityMetadata;

    /**
     * 所属知识库UUID
     */
    @Property("kb_uuid")
    private String kbUuid;

    /**
     * 所属知识项UUID（用于按文档查询图谱）
     */
    @Property("kb_item_uuid")
    private String kbItemUuid;

    /**
     * 租户编码（多租户隔离）
     */
    @Property("tenant_code")
    private String tenantCode;

    /**
     * 创建时间
     */
    @Property("create_time")
    private LocalDateTime createTime;

    /**
     * 关系：实体之间的关联关系列表
     */
    @Relationship(type = "RELATED_TO", direction = Relationship.Direction.OUTGOING)
    private List<RelatedToRelationship> relatedEntities;

    /**
     * RELATED_TO 关系属性类
     */
    @Data
    @RelationshipProperties
    public static class RelatedToRelationship {
        @Id
        @GeneratedValue
        private Long id;

        /**
         * 关系类型（supplies, belongs_to, signed, purchased等）
         */
        @Property("relation_type")
        private String relationType;

        /**
         * 关系强度（0.0-1.0）
         */
        @Property("strength")
        private Float strength;

        /**
         * 关系元数据（JSON字符串）
         */
        @Property("metadata")
        private String metadata;

        /**
         * 所属知识项UUID（用于按文档查询关系边）
         */
        @Property("kb_item_uuid")
        private String kbItemUuid;

        /**
         * 租户编码（关系级别的租户隔离）
         */
        @Property("tenant_code")
        private String tenantCode;

        /**
         * 创建时间
         */
        @Property("create_time")
        private LocalDateTime createTime;

        /**
         * 目标实体节点
         */
        @TargetNode
        private EntityNode targetEntity;
    }
}
