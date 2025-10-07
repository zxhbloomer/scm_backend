package com.xinyirun.scm.ai.repository.neo4j;

import com.xinyirun.scm.ai.bean.entity.rag.neo4j.KnowledgeBaseSegmentNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Neo4j 知识库文本段 Repository
 *
 * <p>用于管理知识库文本段节点及其关系</p>
 * <p>注意：所有查询必须包含 tenant_id 过滤，确保多租户隔离</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Repository
public interface KnowledgeBaseSegmentRepository extends Neo4jRepository<KnowledgeBaseSegmentNode, Long> {

    /**
     * 根据 segment_uuid 查询文本段节点（包含租户隔离）
     *
     * @param segment_uuid 文本段UUID
     * @param tenant_id 租户ID
     * @return 文本段节点
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {segment_uuid: $segment_uuid, tenant_id: $tenant_id})
            RETURN s
            """)
    Optional<KnowledgeBaseSegmentNode> findBySegmentUuidAndTenantId(
            @Param("segment_uuid") String segment_uuid,
            @Param("tenant_id") String tenant_id);

    /**
     * 查询知识库的所有文本段节点
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_id 租户ID
     * @return 文本段节点列表
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {kb_uuid: $kb_uuid, tenant_id: $tenant_id})
            RETURN s
            ORDER BY s.segment_index
            """)
    List<KnowledgeBaseSegmentNode> findByKbUuidAndTenantId(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_id") String tenant_id);

    /**
     * 查询文档的所有文本段节点
     *
     * @param kb_item_uuid 文档UUID
     * @param tenant_id 租户ID
     * @return 文本段节点列表（按索引排序）
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {kb_item_uuid: $kb_item_uuid, tenant_id: $tenant_id})
            RETURN s
            ORDER BY s.segment_index
            """)
    List<KnowledgeBaseSegmentNode> findByItemUuidAndTenantId(
            @Param("kb_item_uuid") String kb_item_uuid,
            @Param("tenant_id") String tenant_id);

    /**
     * 查询文本段提及的所有实体（MENTIONS 关系）
     *
     * @param segment_uuid 文本段UUID
     * @param tenant_id 租户ID
     * @return MENTIONS关系的Cypher查询结果（包含实体和置信度）
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {segment_uuid: $segment_uuid, tenant_id: $tenant_id})
                  -[r:MENTIONS]->(e:Entity)
            WHERE e.tenant_id = $tenant_id
            RETURN e, r.confidence AS confidence, r.mention_context AS mention_context
            ORDER BY r.confidence DESC
            """)
    List<Object[]> findMentionedEntities(
            @Param("segment_uuid") String segment_uuid,
            @Param("tenant_id") String tenant_id);

    /**
     * 查询相似文本段（SIMILAR_TO 关系）
     *
     * @param segment_uuid 文本段UUID
     * @param tenant_id 租户ID
     * @param minSimilarity 最小相似度阈值
     * @param limit 返回数量限制
     * @return 相似文本段列表（按相似度降序）
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {segment_uuid: $segment_uuid, tenant_id: $tenant_id})
                  -[r:SIMILAR_TO]->(similar:KnowledgeBaseSegment)
            WHERE similar.tenant_id = $tenant_id
              AND r.similarity_score >= $minSimilarity
            RETURN similar, r.similarity_score AS similarity_score
            ORDER BY r.similarity_score DESC
            LIMIT $limit
            """)
    List<Object[]> findSimilarSegments(
            @Param("segment_uuid") String segment_uuid,
            @Param("tenant_id") String tenant_id,
            @Param("minSimilarity") Float minSimilarity,
            @Param("limit") Integer limit);

    /**
     * 创建文本段与实体的 MENTIONS 关系
     *
     * @param segment_uuid 文本段UUID
     * @param entity_uuid 实体UUID
     * @param tenant_id 租户ID
     * @param confidence 置信度
     * @param mention_context 提及上下文
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {segment_uuid: $segment_uuid, tenant_id: $tenant_id}),
                  (e:Entity {entity_uuid: $entity_uuid, tenant_id: $tenant_id})
            MERGE (s)-[r:MENTIONS]->(e)
            SET r.confidence = $confidence,
                r.mention_context = $mention_context,
                r.create_time = datetime()
            """)
    void createMentionsRelationship(
            @Param("segment_uuid") String segment_uuid,
            @Param("entity_uuid") String entity_uuid,
            @Param("tenant_id") String tenant_id,
            @Param("confidence") Float confidence,
            @Param("mention_context") String mention_context);

    /**
     * 创建文本段之间的 SIMILAR_TO 关系
     *
     * @param segment_uuid1 文本段1 UUID
     * @param segment_uuid2 文本段2 UUID
     * @param tenant_id 租户ID
     * @param similarity_score 相似度分数
     */
    @Query("""
            MATCH (s1:KnowledgeBaseSegment {segment_uuid: $segment_uuid1, tenant_id: $tenant_id}),
                  (s2:KnowledgeBaseSegment {segment_uuid: $segment_uuid2, tenant_id: $tenant_id})
            MERGE (s1)-[r:SIMILAR_TO]->(s2)
            SET r.similarity_score = $similarity_score,
                r.create_time = datetime()
            """)
    void createSimilarToRelationship(
            @Param("segment_uuid1") String segment_uuid1,
            @Param("segment_uuid2") String segment_uuid2,
            @Param("tenant_id") String tenant_id,
            @Param("similarity_score") Float similarity_score);

    /**
     * 删除文档的所有文本段节点（级联删除关系）
     *
     * @param kb_item_uuid 文档UUID
     * @param tenant_id 租户ID
     * @return 删除的节点数量
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {kb_item_uuid: $kb_item_uuid, tenant_id: $tenant_id})
            DETACH DELETE s
            RETURN count(s)
            """)
    Integer deleteByItemUuidAndTenantId(
            @Param("kb_item_uuid") String kb_item_uuid,
            @Param("tenant_id") String tenant_id);

    /**
     * 删除知识库的所有文本段节点（级联删除关系）
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_id 租户ID
     * @return 删除的节点数量
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {kb_uuid: $kb_uuid, tenant_id: $tenant_id})
            DETACH DELETE s
            RETURN count(s)
            """)
    Integer deleteByKbUuidAndTenantId(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_id") String tenant_id);

    /**
     * 统计知识库的文本段数量
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_id 租户ID
     * @return 文本段数量
     */
    @Query("""
            MATCH (s:KnowledgeBaseSegment {kb_uuid: $kb_uuid, tenant_id: $tenant_id})
            RETURN count(s)
            """)
    Long countByKbUuidAndTenantId(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_id") String tenant_id);
}
