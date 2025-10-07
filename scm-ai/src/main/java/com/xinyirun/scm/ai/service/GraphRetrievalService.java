package com.xinyirun.scm.ai.service;

import com.xinyirun.scm.ai.bean.entity.rag.neo4j.EntityNode;
import com.xinyirun.scm.ai.bean.vo.rag.GraphRelationVo;
import com.xinyirun.scm.ai.bean.vo.rag.GraphSearchResultVo;
import com.xinyirun.scm.ai.repository.neo4j.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 图谱检索服务
 *
 * <p>对应 aideepin 服务：GraphStoreContentRetriever</p>
 * <p>核心功能：从Neo4j检索与问题相关的实体和关系</p>
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GraphRetrievalService {

    private final EntityRepository entityRepository;
    private final ChatModel chatModel;

    /**
     * 实体分数缓存（用于RAG评分）
     * <p>对应aideepin的entityToScore缓存</p>
     */
    private final Map<String, Double> entityToScore = new ConcurrentHashMap<>();

    /**
     * 图谱实体提取提示词模板
     * <p>对应aideepin的GraphExtractPrompt.GRAPH_EXTRACTION_PROMPT_CN</p>
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
     * <p>对应 aideepin 方法：GraphStoreContentRetriever.retrieve()</p>
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
     * @param tenantId 租户ID
     * @param maxResults 最大返回结果数
     * @return 图谱检索结果列表
     */
    public List<GraphSearchResultVo> searchRelatedEntities(String question, String kbUuid, String tenantId, Integer maxResults) {
        log.info("图谱检索开始，question: {}, kbUuid: {}, maxResults: {}", question, kbUuid, maxResults);

        if (maxResults == null || maxResults <= 0) {
            maxResults = 3;
        }

        // 1. 使用ChatModel提取实体（对应aideepin第72行）
        Set<String> extractedEntities = extractEntitiesFromQuestion(question);
        if (extractedEntities.isEmpty()) {
            log.info("从用户问题中未提取到实体");
            return Collections.emptyList();
        }

        log.info("提取到的实体：{}", extractedEntities);

        // 2. 在Neo4j中搜索实体节点（对应aideepin第104-110行）
        List<EntityNode> matchedEntities = new ArrayList<>();
        for (String entityName : extractedEntities) {
            List<EntityNode> entities = entityRepository.searchByKeyword(entityName, tenantId, maxResults);
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

        // 3. 查询每个实体的直接关系（对应aideepin第111-116行）
        List<GraphSearchResultVo> results = new ArrayList<>();
        for (EntityNode entity : uniqueEntities.values()) {
            // 查询实体的直接关系
            List<Object[]> relationships = entityRepository.findDirectRelationships(
                    entity.getEntityUuid(),
                    tenantId,
                    null
            );

            // 构建GraphRelationVo列表
            List<GraphRelationVo> relations = relationships.stream()
                    .map(row -> {
                        EntityNode targetEntity = (EntityNode) row[0];
                        String relationType = (String) row[1];
                        Float strength = (Float) row[2];

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

            // 缓存分数（对应aideepin的entityToScore）
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
     * <p>对应 aideepin 方法：GraphStoreContentRetriever.retrieve() 第72-101行</p>
     *
     * @param question 用户问题
     * @return 提取的实体名称集合（已转大写并去除特殊字符）
     */
    private Set<String> extractEntitiesFromQuestion(String question) {
        String prompt = GRAPH_EXTRACTION_PROMPT_CN.replace("{input_text}", question);

        String response = "";
        try {
            response = chatModel.call(new Prompt(prompt)).getResult().getOutput().getText();
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
                    String entityName = clearStr(recordAttributes[1].toUpperCase());
                    entities.add(entityName);
                } else if (recordAttributes[0].contains("\"relationship\"") || recordAttributes[0].contains("\"关系\"")) {
                    String sourceName = clearStr(recordAttributes[1].toUpperCase());
                    String targetName = clearStr(recordAttributes[2].toUpperCase());
                    entities.add(sourceName);
                    entities.add(targetName);
                }
            }
        }

        return entities.stream()
                .map(this::removeSpecialChar)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    /**
     * 清理字符串（去除引号和空格）
     *
     * <p>对应 aideepin 工具方法：AdiStringUtil.clearStr()</p>
     */
    private String clearStr(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\"", "").replace("'", "").trim();
    }

    /**
     * 去除特殊字符（保留中文、字母、数字）
     *
     * <p>对应 aideepin 工具方法：AdiStringUtil.removeSpecialChar()</p>
     */
    private String removeSpecialChar(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", "");
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
        String normalizedName = removeSpecialChar(entityName.toUpperCase());

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
     * 获取所有缓存的实体分数
     *
     * <p>对应 aideepin 方法：getAllCachedScores()</p>
     * <p>用于RagService将图谱检索分数与向量检索分数合并</p>
     *
     * @return 实体UUID到分数的映射
     */
    public Map<String, Double> getAllCachedScores() {
        return new HashMap<>(entityToScore);
    }

    /**
     * 清除分数缓存
     *
     * <p>对应 aideepin 方法：clearScoreCache()</p>
     * <p>每次问答结束后调用，避免缓存污染下一次查询</p>
     */
    public void clearScoreCache() {
        entityToScore.clear();
        log.debug("清除图谱检索分数缓存");
    }

    /**
     * 根据知识库条目UUID获取图谱数据
     *
     * <p>原有方法保留，用于其他功能</p>
     */
    public Map<String, Object> getGraphByKbItem(String kbItemUuid, Long maxVertexId, Long maxEdgeId, int limit) {
        // TODO: 实现Neo4j图谱查询逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("vertices", new Object[0]);
        result.put("edges", new Object[0]);
        return result;
    }

    /**
     * 根据问答UUID查询关联图谱
     *
     * <p>原有方法保留，用于其他功能</p>
     */
    public Map<String, Object> getByQaUuid(String qaUuid) {
        // TODO: 实现根据问答UUID查询关联图谱
        Map<String, Object> result = new HashMap<>();
        result.put("graph_data", new Object[0]);
        return result;
    }
}
