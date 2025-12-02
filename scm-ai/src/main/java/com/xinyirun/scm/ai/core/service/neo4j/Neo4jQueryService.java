package com.xinyirun.scm.ai.core.service.neo4j;

import com.xinyirun.scm.ai.bean.vo.rag.KbGraphVo;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Neo4j查询服务
 *
 * <p>功能说明：查询知识库文档的图谱数据</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-09
 */
@Service
@Slf4j
public class Neo4jQueryService {

    @Autowired
    private Driver neo4jDriver;

    /**
     * 查询文档的图谱数据
     *
     * @param itemUuid 知识项UUID
     * @param maxVertexId 最大顶点ID（用于分页）
     * @param maxEdgeId 最大边ID（用于分页）
     * @param limit 返回数量限制
     * @return 图谱数据VO
     */
    public KbGraphVo getGraphData(String itemUuid, Long maxVertexId, Long maxEdgeId, Integer limit) {
        try {
            log.info("查询文档图谱数据，item_uuid: {}, maxVertexId: {}, maxEdgeId: {}, limit: {}",
                    itemUuid, maxVertexId, maxEdgeId, limit);

            KbGraphVo result = new KbGraphVo();

            try (Session session = neo4jDriver.session()) {
                // 查询顶点
                List<KbGraphVo.GraphVertexVo> vertices = queryVertices(session, itemUuid, maxVertexId, limit);
                result.setVertices(vertices);

                // 查询边
                List<KbGraphVo.GraphEdgeVo> edges = queryEdges(session, itemUuid, maxEdgeId, limit);
                result.setEdges(edges);
            }

            log.info("查询文档图谱数据完成，item_uuid: {}, 顶点数: {}, 边数: {}",
                    itemUuid, result.getVertices().size(), result.getEdges().size());
            return result;

        } catch (Exception e) {
            log.error("查询文档图谱数据失败，item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("查询文档图谱数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询图谱顶点
     *
     * @param session Neo4j会话
     * @param itemUuid 知识项UUID
     * @param maxVertexId 最大顶点ID
     * @param limit 返回数量限制
     * @return 顶点列表
     */
    private List<KbGraphVo.GraphVertexVo> queryVertices(Session session, String itemUuid, Long maxVertexId, Integer limit) {
        // 处理limit=-1的情况，转换为一个合理的大值
        int actualLimit = (limit <= 0) ? 1000 : limit;

        String cypher =
            "MATCH (n:Entity {kb_item_uuid: $itemUuid}) " +
            "WHERE id(n) < $maxVertexId " +
            "RETURN id(n) as id, n.entity_name as name, n.entity_type as type, n.kb_uuid as kbUuid, n.kb_item_uuid as kbItemUuid " +
            "ORDER BY id(n) DESC " +
            "LIMIT $limit";

        Map<String, Object> params = Map.of(
                "itemUuid", itemUuid,
                "maxVertexId", maxVertexId,
                "limit", actualLimit
        );

        Result result = session.run(cypher, params);

        List<KbGraphVo.GraphVertexVo> vertices = new ArrayList<>();
        while (result.hasNext()) {
            Record record = result.next();
            KbGraphVo.GraphVertexVo vertex = new KbGraphVo.GraphVertexVo();
            vertex.setId(record.get("id").asLong());
            vertex.setName(record.get("name").asString(null));
            vertex.setType(record.get("type").asString(null));
            vertex.setKbUuid(record.get("kbUuid").asString(null));
            vertex.setKbItemUuid(record.get("kbItemUuid").asString(null));
            vertices.add(vertex);
        }

        return vertices;
    }

    /**
     * 查询图谱边
     *
     * @param session Neo4j会话
     * @param itemUuid 知识项UUID
     * @param maxEdgeId 最大边ID
     * @param limit 返回数量限制
     * @return 边列表
     */
    private List<KbGraphVo.GraphEdgeVo> queryEdges(Session session, String itemUuid, Long maxEdgeId, Integer limit) {
        // 处理limit=-1的情况，转换为一个合理的大值
        int actualLimit = (limit <= 0) ? 1000 : limit;

        // 查询RELATED_TO类型的关系，且关系的kb_item_uuid属性匹配
        String cypher =
            "MATCH (n:Entity)-[r:RELATED_TO]->(m:Entity) " +
            "WHERE r.kb_item_uuid = $itemUuid AND id(r) < $maxEdgeId " +
            "RETURN id(r) as id, id(n) as sourceId, id(m) as targetId, " +
            "r.relation_type as type, r.metadata as description " +
            "ORDER BY id(r) DESC " +
            "LIMIT $limit";

        Map<String, Object> params = Map.of(
                "itemUuid", itemUuid,
                "maxEdgeId", maxEdgeId,
                "limit", actualLimit
        );

        Result result = session.run(cypher, params);

        List<KbGraphVo.GraphEdgeVo> edges = new ArrayList<>();
        while (result.hasNext()) {
            Record record = result.next();
            KbGraphVo.GraphEdgeVo edge = new KbGraphVo.GraphEdgeVo();
            edge.setId(record.get("id").asLong());
            edge.setSourceId(record.get("sourceId").asLong());
            edge.setTargetId(record.get("targetId").asLong());
            edge.setType(record.get("type").asString(null));
            edge.setDescription(record.get("description").asString(null));
            edges.add(edge);
        }

        return edges;
    }
}
