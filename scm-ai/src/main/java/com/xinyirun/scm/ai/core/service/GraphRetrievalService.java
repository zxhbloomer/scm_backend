package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.bean.entity.rag.neo4j.EntityNode;
import com.xinyirun.scm.ai.bean.vo.rag.DirectRelationshipVo;
import com.xinyirun.scm.ai.bean.vo.rag.GraphRelationVo;
import com.xinyirun.scm.ai.bean.vo.rag.GraphSearchResultVo;
import com.xinyirun.scm.ai.bean.vo.rag.KbGraphVo;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.repository.neo4j.EntityRepository;
import com.xinyirun.scm.ai.core.service.neo4j.Neo4jQueryService;
import com.xinyirun.scm.ai.common.util.AdiStringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 图谱检索服务
 *
 * <p>核心功能：从Neo4j检索与问题相关的实体和关系</p>
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Service
public class GraphRetrievalService {

    @Resource
    private EntityRepository entityRepository;

    @Autowired
    private AiModelProvider aiModelProvider;

    @Autowired
    private Neo4jQueryService neo4jQueryService;

    @Autowired
    private Neo4jClient neo4jClient;

    /**
     * 实体分数缓存（用于RAG评分）
     */
    private final Map<String, Double> entityToScore = new ConcurrentHashMap<>();

    /**
     * 图谱实体提取提示词模板
     */
    private static final String GRAPH_EXTRACTION_PROMPT_CN = """
            -Goal-
            根据用户输入的文本，识别文本中的所有实体和关系。

            -Steps-
            1. 识别所有实体（人物、组织、地点、产品、概念等）
            2. 识别实体之间的关系
            3. 将实体名称转换为大写

            -输出格式-
            对于每个实体，输出：("entity"<|>实体名称<|>实体类型<|>实体描述)
            对于每个关系，输出：("relationship"<|>源实体名称<|>目标实体名称<|>关系类型<|>关系描述)
            每行一个记录，使用<|>作为分隔符

            -示例-
            输入：张三在ABC公司工作，负责采购钢材产品。
            输出：
            ("entity"<|>张三<|>人物<|>员工)
            ("entity"<|>ABC公司<|>组织<|>公司)
            ("entity"<|>钢材<|>产品<|>采购物资)
            ("relationship"<|>张三<|>ABC公司<|>工作于<|>雇佣关系)
            ("relationship"<|>张三<|>钢材<|>负责采购<|>职责关系)

            -Real Data-
            输入文本：{input_text}
            输出：
            """;

    /**
     * 搜索与问题相关的实体和关系
     *
     * <p>核心流程：</p>
     * <ol>
     *   <li>使用ChatModel从问题中提取实体名称</li>
     *   <li>在Neo4j中搜索匹配的实体节点</li>
     *   <li>查询实体的直接关系</li>
     *   <li>计算实体相关性分数并缓存</li>
     *   <li>返回GraphSearchResultVo列表</li>
     * </ol>
     *
     * @param question 用户问题
     * @param kbUuid 知识库UUID
     * @param tenantCode 租户编码
     * @param maxResults 最大返回结果数
     * @return 图谱检索结果列表
     */
    public List<GraphSearchResultVo> searchRelatedEntities(String question, String kbUuid, String tenantCode, Integer maxResults) {
        log.info("图谱检索开始，question: {}, kbUuid: {}, maxResults: {}", question, kbUuid, maxResults);

        if (maxResults == null || maxResults <= 0) {
            maxResults = 3;
        }

        // 1. 使用ChatModel提取实体
        Set<String> extractedEntities = extractEntitiesFromQuestion(question);
        if (extractedEntities.isEmpty()) {
            log.info("从用户问题中未提取到实体");
            return Collections.emptyList();
        }

        log.info("提取到的实体：{}", extractedEntities);

        // 2. 在Neo4j中搜索实体节点
        List<EntityNode> matchedEntities = new ArrayList<>();
        for (String entityName : extractedEntities) {
            List<EntityNode> entities = entityRepository.searchByKeyword(entityName, tenantCode, maxResults);
            matchedEntities.addAll(entities);
        }

        if (matchedEntities.isEmpty()) {
            log.info("Neo4j中未找到匹配的实体");
            return Collections.emptyList();
        }

        // 去重
        Map<String, EntityNode> uniqueEntities = matchedEntities.stream()
                .collect(Collectors.toMap(
                        EntityNode::getEntityUuid,
                        e -> e,
                        (e1, e2) -> e1
                ));

        log.info("Neo4j匹配到 {} 个实体", uniqueEntities.size());

        // 3. 查询每个实体的直接关系
        List<GraphSearchResultVo> results = new ArrayList<>();
        for (EntityNode entity : uniqueEntities.values()) {
            // 使用Neo4jClient查询实体的直接关系
            Collection<DirectRelationshipVo> relationshipVos = neo4jClient
                    .query("MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code}) " +
                           "-[r:RELATED_TO]->(target:Entity) " +
                           "WHERE target.tenant_code = $tenant_code " +
                           "RETURN target, r.relation_type AS relationType, r.strength AS strength " +
                           "ORDER BY r.strength DESC")
                    .bind(entity.getEntityUuid()).to("entity_uuid")
                    .bind(tenantCode).to("tenant_code")
                    .fetchAs(DirectRelationshipVo.class).mappedBy((typeSystem, record) -> {
                        // 自定义映射逻辑：将Neo4j Record转换为DirectRelationshipVo
                        Node targetNode = record.get("target").asNode();
                        String relationType = record.get("relationType").asString();
                        Float strength = record.get("strength").asFloat();

                        // 将Neo4j Node转换为EntityNode
                        EntityNode target = mapNodeToEntity(targetNode);

                        return DirectRelationshipVo.builder()
                                .target(target)
                                .relationType(relationType)
                                .strength(strength)
                                .build();
                    })
                    .all();

            // 构建GraphRelationVo列表
            List<GraphRelationVo> relations = relationshipVos.stream()
                    .map(vo -> {
                        EntityNode targetEntity = vo.getTarget();
                        String relationType = vo.getRelationType();
                        Float strength = vo.getStrength();

                        return GraphRelationVo.builder()
                                .relationId(entity.getEntityUuid() + "-" + targetEntity.getEntityUuid())
                                .relationType(relationType)
                                .sourceEntityId(entity.getEntityUuid())
                                .sourceEntityName(entity.getEntityName())
                                .targetEntityId(targetEntity.getEntityUuid())
                                .targetEntityName(targetEntity.getEntityName())
                                .weight(strength != null ? strength.doubleValue() : 0.5)
                                .build();
                    })
                    .collect(Collectors.toList());

            // 4. 计算实体相关性分数
            double score = calculateEntityScore(entity.getEntityName(), extractedEntities);

            // 缓存分数
            entityToScore.put(entity.getEntityUuid(), score);

            // 5. 构建GraphSearchResultVo
            GraphSearchResultVo result = GraphSearchResultVo.builder()
                    .entityId(entity.getEntityUuid())
                    .entityName(entity.getEntityName())
                    .entityType(entity.getEntityType())
                    .description(entity.getEntityMetadata())
                    .kbUuid(entity.getKbUuid())
                    .kbItemUuid("")
                    .relations(relations)
                    .score(score)
                    .build();

            results.add(result);
        }

        // 6. 按分数降序排序并限制数量
        results.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        if (results.size() > maxResults) {
            results = results.subList(0, maxResults);
        }

        log.info("图谱检索完成，返回 {} 个实体", results.size());
        return results;
    }

    /**
     * 从问题中提取实体名称
     *
     * @param question 用户问题
     * @return 提取的实体名称集合（已转大写并去除特殊字符）
     */
    private Set<String> extractEntitiesFromQuestion(String question) {
        String prompt = GRAPH_EXTRACTION_PROMPT_CN.replace("{input_text}", question);

        String response = "";
        try {
            response = aiModelProvider.getChatModel().call(new Prompt(prompt)).getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("图谱实体提取失败", e);
            return Collections.emptySet();
        }

        if (StringUtils.isBlank(response)) {
            return Collections.emptySet();
        }

        Set<String> entities = new HashSet<>();
        String[] records = response.split("\n");
        for (String record : records) {
            String newRecord = record.replaceAll("^\\(|\\)$", "").trim();
            String[] recordAttributes = newRecord.split("<\\|>");

            if (recordAttributes.length >= 4) {
                if (recordAttributes[0].contains("\"entity\"") || recordAttributes[0].contains("\"实体\"")) {
                    // 优化: 使用AdiStringUtil.tail()和removeSpecialChar()
                    String entityName = AdiStringUtil.tail(
                            AdiStringUtil.removeSpecialChar(clearStr(recordAttributes[1].toUpperCase())),
                            20
                    );
                    entities.add(entityName);
                } else if (recordAttributes[0].contains("\"relationship\"") || recordAttributes[0].contains("\"关系\"")) {
                    // 优化: 使用AdiStringUtil.tail()和removeSpecialChar()
                    String sourceName = AdiStringUtil.tail(
                            AdiStringUtil.removeSpecialChar(clearStr(recordAttributes[1].toUpperCase())),
                            20
                    );
                    String targetName = AdiStringUtil.tail(
                            AdiStringUtil.removeSpecialChar(clearStr(recordAttributes[2].toUpperCase())),
                            20
                    );
                    entities.add(sourceName);
                    entities.add(targetName);
                }
            }
        }

        // 已在上面应用AdiStringUtil.removeSpecialChar(),这里只需过滤空字符串
        return entities.stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    /**
     * 清理字符串（去除引号和空格）
     */
    private String clearStr(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\"", "").replace("'", "").trim();
    }


    /**
     * 计算实体相关性分数
     *
     * <p>基于实体名称与问题中提取的实体的匹配度</p>
     *
     * @param entityName 实体名称
     * @param extractedEntities 从问题中提取的实体集合
     * @return 相关性分数（0.0-1.0）
     */
    private double calculateEntityScore(String entityName, Set<String> extractedEntities) {
        // 优化: 使用AdiStringUtil.removeSpecialChar()标准化名称
        String normalizedName = AdiStringUtil.removeSpecialChar(entityName.toUpperCase());

        for (String extracted : extractedEntities) {
            if (normalizedName.equals(extracted)) {
                return 1.0;
            }
            if (normalizedName.contains(extracted) || extracted.contains(normalizedName)) {
                return 0.8;
            }
        }

        return 0.5;
    }


    /**
     * 将Neo4j Node转换为EntityNode
     *
     * @param node Neo4j节点
     * @return EntityNode实体对象
     */
    private EntityNode mapNodeToEntity(Node node) {
        EntityNode entity = new EntityNode();
        entity.setId(node.id());
        entity.setEntityUuid(node.get("entity_uuid").asString());
        entity.setEntityName(node.get("entity_name").asString());
        entity.setEntityType(node.get("entity_type").asString());
        entity.setEntityMetadata(node.get("entity_metadata").asString(""));
        entity.setKbUuid(node.get("kb_uuid").asString());
        entity.setKbItemUuid(node.get("kb_item_uuid").asString(""));
        entity.setTenantCode(node.get("tenant_code").asString());

        // 处理create_time字段（ZonedDateTime转Instant）
        if (!node.get("create_time").isNull()) {
            entity.setCreateTime(node.get("create_time").asZonedDateTime().toInstant());
        }

        return entity;
    }

    /**
     * 清除分数缓存
     *
     * <p>每次问答结束后调用，避免缓存污染下一次查询</p>
     */
    public void clearScoreCache() {
        entityToScore.clear();
        log.debug("清除图谱检索分数缓存");
    }

    /**
     * 根据知识库条目UUID获取图谱数据
     *
     * <p>查询Neo4j中存储的图谱实体和关系数据</p>
     *
     * @param kbItemUuid 知识项UUID
     * @param maxVertexId 最大顶点ID（用于分页）
     * @param maxEdgeId 最大边ID（用于分页）
     * @param limit 返回数量限制
     * @return 图谱数据（包含顶点和边）
     */
    public Map<String, Object> getGraphByKbItem(String kbItemUuid, Long maxVertexId, Long maxEdgeId, int limit) {
        log.debug("查询知识项图谱: kbItemUuid={}", kbItemUuid);

        KbGraphVo graphData = neo4jQueryService.getGraphData(kbItemUuid, maxVertexId, maxEdgeId, limit);

        Map<String, Object> result = new HashMap<>();
        result.put("vertices", graphData.getVertices());
        result.put("edges", graphData.getEdges());

        return result;
    }
}
