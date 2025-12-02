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
import org.springframework.ai.chat.model.ChatModel;
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
     * 搜索与问题相关的实体和关系（使用指定模型）
     *
     * <p>核心流程：</p>
     * <ol>
     *   <li>使用指定的ChatModel从问题中提取实体名称</li>
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
     * @param modelName 模型名称（例如：gpt-3.5-turbo、deepseek-chat）
     * @return 图谱检索结果列表
     */
    public List<GraphSearchResultVo> searchRelatedEntities(String question, String kbUuid, String tenantCode, Integer maxResults, String modelName) {
        log.info("==================== 图谱检索服务开始（使用指定模型）====================");
        log.info("问题: [{}]", question);
        log.info("知识库UUID: [{}]", kbUuid);
        log.info("租户编码: [{}]", tenantCode);
        log.info("最大结果数: [{}]", maxResults);
        log.info("指定模型: [{}]", modelName);

        if (maxResults == null || maxResults <= 0) {
            maxResults = 3;
            log.debug("使用默认最大结果数: 3");
        }

        // 1. 使用指定模型的ChatModel提取实体
        log.info("步骤1: 开始从问题中提取实体（使用指定LLM: {}）", modelName);
        Set<String> extractedEntities = extractEntitiesFromQuestion(question, modelName);
        if (extractedEntities.isEmpty()) {
            log.warn("⚠️ 步骤1失败: 从用户问题中未提取到任何实体");
            log.info("==================== 图谱检索服务结束（未提取到实体）====================");
            return Collections.emptyList();
        }

        log.info("✅ 步骤1完成: 提取到 {} 个实体", extractedEntities.size());
        log.info("提取的实体列表: {}", extractedEntities);

        // 2. 在Neo4j中搜索实体节点
        log.info("步骤2: 开始在Neo4j中搜索匹配的实体节点");
        List<EntityNode> matchedEntities = new ArrayList<>();
        for (String entityName : extractedEntities) {
            log.debug("  搜索实体: [{}]", entityName);
            List<EntityNode> entities = entityRepository.searchByKeyword(entityName, tenantCode, maxResults);
            log.debug("    找到 {} 个匹配节点", entities.size());
            matchedEntities.addAll(entities);
        }

        if (matchedEntities.isEmpty()) {
            log.warn("⚠️ 步骤2失败: Neo4j中未找到任何匹配的实体");
            log.info("==================== 图谱检索服务结束（Neo4j未匹配）====================");
            return Collections.emptyList();
        }

        // 去重
        Map<String, EntityNode> uniqueEntities = matchedEntities.stream()
                .collect(Collectors.toMap(
                        EntityNode::getEntityUuid,
                        e -> e,
                        (e1, e2) -> e1
                ));

        log.info("✅ 步骤2完成: Neo4j匹配到 {} 个唯一实体", uniqueEntities.size());
        uniqueEntities.values().forEach(entity -> {
            log.debug("  - {}: {} (类型: {})", entity.getEntityUuid(), entity.getEntityName(), entity.getEntityType());
        });

        // 3. 查询每个实体的直接关系
        log.info("步骤3: 开始查询每个实体的直接关系");
        List<GraphSearchResultVo> results = new ArrayList<>();
        int entityIndex = 0;
        for (EntityNode entity : uniqueEntities.values()) {
            entityIndex++;
            log.debug("  处理实体 {}/{}: {} (UUID: {})",
                    entityIndex, uniqueEntities.size(), entity.getEntityName(), entity.getEntityUuid());

            // 使用Neo4jClient查询实体的直接关系（双向：包含入度和出度）
            log.debug("    执行Neo4j Cypher查询: MATCH (e:Entity)-[r:RELATED_TO]-(target:Entity)");
            Collection<DirectRelationshipVo> relationshipVos = neo4jClient
                    .query("MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code}) " +
                           "-[r:RELATED_TO]-(target:Entity) " +
                           "WHERE target.tenant_code = $tenant_code " +
                           "RETURN target, r.relation_type AS relationType, r.strength AS strength " +
                           "ORDER BY r.strength DESC")
                    .bind(entity.getEntityUuid()).to("entity_uuid")
                    .bind(tenantCode).to("tenant_code")
                    .fetchAs(DirectRelationshipVo.class).mappedBy((typeSystem, record) -> {
                        Node targetNode = record.get("target").asNode();
                        String relationType = record.get("relationType").asString();
                        Float strength = record.get("strength").asFloat();

                        EntityNode target = mapNodeToEntity(targetNode);

                        return DirectRelationshipVo.builder()
                                .target(target)
                                .relationType(relationType)
                                .strength(strength)
                                .build();
                    })
                    .all();

            log.debug("    查询到 {} 条关系", relationshipVos.size());

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
            log.debug("    计算相关性分数: {}", score);

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

        log.info("✅ 步骤3完成: 构建了 {} 个图谱检索结果", results.size());

        // 6. 按分数降序排序并限制数量
        log.info("步骤4: 按分数排序并限制数量");
        results.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        if (results.size() > maxResults) {
            log.debug("  结果数 {} 超过限制 {}，进行截断", results.size(), maxResults);
            results = results.subList(0, maxResults);
        }

        log.info("✅ 步骤4完成: 最终返回 {} 个实体", results.size());
        log.info("==================== 图谱检索服务结束（成功）====================");
        log.info("最终图谱检索结果:");
        results.forEach(result -> {
            log.info("  实体: {} | 类型: {} | 关系数: {} | 分数: {}",
                    result.getEntityName(),
                    result.getEntityType(),
                    result.getRelations() != null ? result.getRelations().size() : 0,
                    result.getScore());
        });

        return results;
    }

    /**
     * 搜索与问题相关的实体和关系（使用默认模型）
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
        log.info("==================== 图谱检索服务开始 ====================");
        log.info("问题: [{}]", question);
        log.info("知识库UUID: [{}]", kbUuid);
        log.info("租户编码: [{}]", tenantCode);
        log.info("最大结果数: [{}]", maxResults);

        if (maxResults == null || maxResults <= 0) {
            maxResults = 3;
            log.debug("使用默认最大结果数: 3");
        }

        // 1. 使用ChatModel提取实体
        log.info("步骤1: 开始从问题中提取实体（使用LLM）");
        Set<String> extractedEntities = extractEntitiesFromQuestion(question);
        if (extractedEntities.isEmpty()) {
            log.warn("⚠️ 步骤1失败: 从用户问题中未提取到任何实体");
            log.info("==================== 图谱检索服务结束（未提取到实体）====================");
            return Collections.emptyList();
        }

        log.info("✅ 步骤1完成: 提取到 {} 个实体", extractedEntities.size());
        log.info("提取的实体列表: {}", extractedEntities);

        // 2. 在Neo4j中搜索实体节点
        log.info("步骤2: 开始在Neo4j中搜索匹配的实体节点");
        List<EntityNode> matchedEntities = new ArrayList<>();
        for (String entityName : extractedEntities) {
            log.debug("  搜索实体: [{}]", entityName);
            List<EntityNode> entities = entityRepository.searchByKeyword(entityName, tenantCode, maxResults);
            log.debug("    找到 {} 个匹配节点", entities.size());
            matchedEntities.addAll(entities);
        }

        if (matchedEntities.isEmpty()) {
            log.warn("⚠️ 步骤2失败: Neo4j中未找到任何匹配的实体");
            log.info("==================== 图谱检索服务结束（Neo4j未匹配）====================");
            return Collections.emptyList();
        }

        // 去重
        Map<String, EntityNode> uniqueEntities = matchedEntities.stream()
                .collect(Collectors.toMap(
                        EntityNode::getEntityUuid,
                        e -> e,
                        (e1, e2) -> e1
                ));

        log.info("✅ 步骤2完成: Neo4j匹配到 {} 个唯一实体", uniqueEntities.size());
        uniqueEntities.values().forEach(entity -> {
            log.debug("  - {}: {} (类型: {})", entity.getEntityUuid(), entity.getEntityName(), entity.getEntityType());
        });

        // 3. 查询每个实体的直接关系
        log.info("步骤3: 开始查询每个实体的直接关系");
        List<GraphSearchResultVo> results = new ArrayList<>();
        int entityIndex = 0;
        for (EntityNode entity : uniqueEntities.values()) {
            entityIndex++;
            log.debug("  处理实体 {}/{}: {} (UUID: {})",
                    entityIndex, uniqueEntities.size(), entity.getEntityName(), entity.getEntityUuid());

            // 使用Neo4jClient查询实体的直接关系（双向：包含入度和出度）
            log.debug("    执行Neo4j Cypher查询: MATCH (e:Entity)-[r:RELATED_TO]-(target:Entity)");
            Collection<DirectRelationshipVo> relationshipVos = neo4jClient
                    .query("MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code}) " +
                           "-[r:RELATED_TO]-(target:Entity) " +
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

            log.debug("    查询到 {} 条关系", relationshipVos.size());

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
            log.debug("    计算相关性分数: {}", score);

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

        log.info("✅ 步骤3完成: 构建了 {} 个图谱检索结果", results.size());

        // 6. 按分数降序排序并限制数量
        log.info("步骤4: 按分数排序并限制数量");
        results.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        if (results.size() > maxResults) {
            log.debug("  结果数 {} 超过限制 {}，进行截断", results.size(), maxResults);
            results = results.subList(0, maxResults);
        }

        log.info("✅ 步骤4完成: 最终返回 {} 个实体", results.size());
        log.info("==================== 图谱检索服务结束（成功）====================");
        log.info("最终图谱检索结果:");
        results.forEach(result -> {
            log.info("  实体: {} | 类型: {} | 关系数: {} | 分数: {}",
                    result.getEntityName(),
                    result.getEntityType(),
                    result.getRelations() != null ? result.getRelations().size() : 0,
                    result.getScore());
        });

        return results;
    }

    /**
     * 从问题中提取实体名称（使用指定模型）
     *
     * @param question 用户问题
     * @param modelName 模型名称
     * @return 提取的实体名称集合（已转大写并去除特殊字符）
     */
    private Set<String> extractEntitiesFromQuestion(String question, String modelName) {
        log.info("===== GraphRetrievalService.extractEntitiesFromQuestion(指定模型) 被调用 =====");
        log.info("接收到的 modelName 参数: [{}]", modelName);
        log.info("接收到的 question 参数: [{}]", question);

        String prompt = GRAPH_EXTRACTION_PROMPT_CN.replace("{input_text}", question);

        String response = "";
        try {
            log.info("准备调用 aiModelProvider.getChatModelByName([{}])", modelName);
            ChatModel chatModel = aiModelProvider.getChatModelByName(modelName);
            log.info("成功获取到 ChatModel 实例，准备发送请求");

            response = chatModel.call(new Prompt(prompt))
                    .getResult()
                    .getOutput()
                    .getText();

            log.info("LLM调用成功，响应长度: {} 字符", response.length());
            log.debug("LLM响应内容: [{}]", response);
        } catch (Exception e) {
            log.error("❌ 图谱实体提取失败（模型: {}）: {}", modelName, e.getMessage(), e);
            return Collections.emptySet();
        }

        if (StringUtils.isBlank(response)) {
            log.warn("⚠️ LLM返回空响应");
            return Collections.emptySet();
        }

        Set<String> entities = parseEntitiesFromResponse(response);
        log.info("从LLM响应中解析出 {} 个实体", entities.size());

        return entities;
    }

    /**
     * 从问题中提取实体名称（使用默认模型）
     *
     * @param question 用户问题
     * @return 提取的实体名称集合（已转大写并去除特殊字符）
     */
    private Set<String> extractEntitiesFromQuestion(String question) {
        log.debug("调用默认LLM提取实体，问题: [{}]", question);
        String prompt = GRAPH_EXTRACTION_PROMPT_CN.replace("{input_text}", question);

        String response = "";
        try {
            log.debug("发送LLM请求（使用默认模型）...");
            response = aiModelProvider.getChatModel()
                    .call(new Prompt(prompt))
                    .getResult()
                    .getOutput()
                    .getText();
            log.debug("LLM响应: [{}]", response);
        } catch (Exception e) {
            log.error("❌ 图谱实体提取失败: {}", e.getMessage(), e);
            return Collections.emptySet();
        }

        if (StringUtils.isBlank(response)) {
            log.warn("⚠️ LLM返回空响应");
            return Collections.emptySet();
        }

        return parseEntitiesFromResponse(response);
    }

    /**
     * 从LLM响应中解析实体名称
     *
     * @param response LLM响应文本
     * @return 提取的实体名称集合
     */
    private Set<String> parseEntitiesFromResponse(String response) {
        Set<String> entities = new HashSet<>();
        String[] records = response.split("\n");
        for (String record : records) {
            String newRecord = record.replaceAll("^\\(|\\)$", "").trim();
            String[] recordAttributes = newRecord.split("<\\|>");

            if (recordAttributes.length >= 4) {
                if (recordAttributes[0].contains("\"entity\"") || recordAttributes[0].contains("\"实体\"")) {
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
