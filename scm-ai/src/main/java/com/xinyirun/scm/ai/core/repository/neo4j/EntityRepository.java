package com.xinyirun.scm.ai.core.repository.neo4j;

import com.xinyirun.scm.ai.bean.entity.rag.neo4j.EntityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Neo4j 实体 Repository
 *
 * <p>用于管理实体节点及其关系</p>
 * <p>支持实体搜索、关系查询、路径查询等图数据库操作</p>
 * <p>注意：所有查询必须包含 tenant_code 过滤，确保多租户隔离</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Repository
public interface EntityRepository extends Neo4jRepository<EntityNode, Long> {

    /**
     * 根据 entity_uuid 查询实体节点（包含租户隔离）
     *
     * @param entity_uuid 实体UUID
     * @param tenant_code 租户ID
     * @return 实体节点
     */
    @Query("""
            MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code})
            RETURN e
            """)
    Optional<EntityNode> findByEntityUuidAndTenantId(
            @Param("entity_uuid") String entity_uuid,
            @Param("tenant_code") String tenant_code);

    /**
     * 关键词搜索实体（模糊匹配实体名称）
     *
     * <p>支持中文实体名称的模糊搜索</p>
     *
     * @param keyword 搜索关键词
     * @param tenant_code 租户ID
     * @param limit 返回数量限制
     * @return 实体节点列表
     */
    @Query("""
            MATCH (e:Entity)
            WHERE e.tenant_code = $tenant_code
              AND e.entity_name CONTAINS $keyword
            RETURN e
            LIMIT $limit
            """)
    List<EntityNode> searchByKeyword(
            @Param("keyword") String keyword,
            @Param("tenant_code") String tenant_code,
            @Param("limit") Integer limit);

    /**
     * 按实体类型查询实体
     *
     * @param entity_type 实体类型（supplier, product, contract等）
     * @param tenant_code 租户ID
     * @param limit 返回数量限制
     * @return 实体节点列表
     */
    @Query("""
            MATCH (e:Entity {entity_type: $entity_type, tenant_code: $tenant_code})
            RETURN e
            LIMIT $limit
            """)
    List<EntityNode> findByEntityType(
            @Param("entity_type") String entity_type,
            @Param("tenant_code") String tenant_code,
            @Param("limit") Integer limit);

    /**
     * 查询知识库的所有实体
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_code 租户ID
     * @return 实体节点列表
     */
    @Query("""
            MATCH (e:Entity {kb_uuid: $kb_uuid, tenant_code: $tenant_code})
            RETURN e
            """)
    List<EntityNode> findByKbUuidAndTenantId(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_code") String tenant_code);

    /**
     * 查询实体的N度相关实体
     *
     * <p>使用可变长度路径查询（RELATED_TO 关系）</p>
     *
     * @param entity_uuid 起始实体UUID
     * @param tenant_code 租户ID
     * @param depth 查询深度（1-3）
     * @param limit 返回数量限制
     * @return 相关实体列表（包含距离信息）
     */
    @Query("""
            MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code})
                  -[:RELATED_TO*1..$depth]->(related:Entity)
            WHERE related.tenant_code = $tenant_code
            WITH related,
                 length(shortestPath((e)-[:RELATED_TO*]-(related))) AS distance
            RETURN DISTINCT related, distance
            ORDER BY distance
            LIMIT $limit
            """)
    List<Object[]> findRelatedEntitiesByDepth(
            @Param("entity_uuid") String entity_uuid,
            @Param("tenant_code") String tenant_code,
            @Param("depth") Integer depth,
            @Param("limit") Integer limit);

    /**
     * 查询两个实体之间的最短路径
     *
     * <p>返回最短路径上的所有节点和关系</p>
     *
     * @param entity1_uuid 实体1 UUID
     * @param entity2_uuid 实体2 UUID
     * @param tenant_code 租户ID
     * @param max_depth 最大搜索深度
     * @return 路径信息（节点和关系列表）
     */
    @Query("""
            MATCH (e1:Entity {entity_uuid: $entity1_uuid, tenant_code: $tenant_code}),
                  (e2:Entity {entity_uuid: $entity2_uuid, tenant_code: $tenant_code}),
                  path = shortestPath((e1)-[:RELATED_TO*1..$max_depth]-(e2))
            RETURN path
            """)
    List<Object> findShortestPath(
            @Param("entity1_uuid") String entity1_uuid,
            @Param("entity2_uuid") String entity2_uuid,
            @Param("tenant_code") String tenant_code,
            @Param("max_depth") Integer max_depth);

    /**
     * 查询两个实体之间的所有路径
     *
     * @param entity1_uuid 实体1 UUID
     * @param entity2_uuid 实体2 UUID
     * @param tenant_code 租户ID
     * @param max_depth 最大搜索深度
     * @param limit 返回路径数量限制
     * @return 路径列表
     */
    @Query("""
            MATCH (e1:Entity {entity_uuid: $entity1_uuid, tenant_code: $tenant_code}),
                  (e2:Entity {entity_uuid: $entity2_uuid, tenant_code: $tenant_code}),
                  path = (e1)-[:RELATED_TO*1..$max_depth]-(e2)
            RETURN path
            LIMIT $limit
            """)
    List<Object> findAllPaths(
            @Param("entity1_uuid") String entity1_uuid,
            @Param("entity2_uuid") String entity2_uuid,
            @Param("tenant_code") String tenant_code,
            @Param("max_depth") Integer max_depth,
            @Param("limit") Integer limit);

    /**
     * 查询实体的直接关系
     *
     * @param entity_uuid 实体UUID
     * @param tenant_code 租户ID
     * @param relation_type 关系类型（可选）
     * @return 关系和目标实体列表（Object[]包含：[0]=target EntityNode, [1]=relationType String, [2]=strength Float）
     */
    @Query("""
            MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code})
                  -[r:RELATED_TO]->(target:Entity)
            WHERE target.tenant_code = $tenant_code
              AND ($relation_type IS NULL OR r.relation_type = $relation_type)
            RETURN target, r.relation_type, r.strength
            ORDER BY r.strength DESC
            """)
    List<Object[]> findDirectRelationships(
            @Param("entity_uuid") String entity_uuid,
            @Param("tenant_code") String tenant_code,
            @Param("relation_type") String relation_type);

    /**
     * 创建实体之间的 RELATED_TO 关系
     *
     * @param entity1_uuid 实体1 UUID
     * @param entity2_uuid 实体2 UUID
     * @param tenant_code 租户ID
     * @param relation_type 关系类型
     * @param strength 关系强度（0.0-1.0）
     * @param metadata 关系元数据（JSON字符串）
     */
    @Query("""
            MATCH (e1:Entity {entity_uuid: $entity1_uuid, tenant_code: $tenant_code}),
                  (e2:Entity {entity_uuid: $entity2_uuid, tenant_code: $tenant_code})
            MERGE (e1)-[r:RELATED_TO]->(e2)
            SET r.relation_type = $relation_type,
                r.strength = $strength,
                r.metadata = $metadata,
                r.create_time = datetime()
            """)
    void createRelatedToRelationship(
            @Param("entity1_uuid") String entity1_uuid,
            @Param("entity2_uuid") String entity2_uuid,
            @Param("tenant_code") String tenant_code,
            @Param("relation_type") String relation_type,
            @Param("strength") Float strength,
            @Param("metadata") String metadata);

    /**
     * 删除知识库的所有实体节点（级联删除关系）
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_code 租户ID
     * @return 删除的节点数量
     */
    @Query("""
            MATCH (e:Entity {kb_uuid: $kb_uuid, tenant_code: $tenant_code})
            DETACH DELETE e
            RETURN count(e)
            """)
    Integer deleteByKbUuidAndTenantId(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_code") String tenant_code);

    /**
     * 统计知识库的实体数量
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_code 租户ID
     * @return 实体数量
     */
    @Query("""
            MATCH (e:Entity {kb_uuid: $kb_uuid, tenant_code: $tenant_code})
            RETURN count(e)
            """)
    Long countByKbUuidAndTenantId(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_code") String tenant_code);

    /**
     * 统计实体类型分布
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_code 租户ID
     * @return 实体类型和数量的映射
     */
    @Query("""
            MATCH (e:Entity {kb_uuid: $kb_uuid, tenant_code: $tenant_code})
            RETURN e.entity_type AS entity_type, count(e) AS count
            ORDER BY count DESC
            """)
    List<Object[]> getEntityTypeDistribution(
            @Param("kb_uuid") String kb_uuid,
            @Param("tenant_code") String tenant_code);
}
