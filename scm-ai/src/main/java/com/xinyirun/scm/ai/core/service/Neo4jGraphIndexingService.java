package com.xinyirun.scm.ai.core.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseGraphSegmentEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.event.GraphIndexCompletedEvent;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseGraphSegmentService;
import com.xinyirun.scm.ai.core.service.splitter.OverlappingTokenTextSplitter;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Neo4j图谱索引服务
 *
 * <p>功能说明：</p>
 * 严格对应aideepin的GraphStoreIngestor.ingest()逻辑
 * 使用LLM从文档中提取实体和关系，构建知识图谱并存储到Neo4j
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>使用LLM提取实体 - ChatModel分析文档（对应aideepin的GraphStoreIngestor.extractGraph）</li>
 *   <li>使用LLM提取关系 - ChatModel识别实体间关系</li>
 *   <li>存储图谱 - 保存到Neo4j（对应aideepin保存到Apache AGE）</li>
 * </ol>
 *
 * <p>参考代码：</p>
 * aideepin: GraphStoreIngestor.ingest()
 * 路径: D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\rag\GraphStoreIngestor.java
 *
 * <p>aideepin核心代码：</p>
 * <pre>
 * public void ingest(Map<String, Object> params) {
 *     String content = (String) params.get("content");
 *     String kbUuid = (String) params.get("kbUuid");
 *     String kbItemUuid = (String) params.get("kbItemUuid");
 *
 *     // 使用LLM提取图谱
 *     Graph graph = extractGraph(content, chatModel);
 *
 *     // 存储实体和关系到Apache AGE
 *     for (Node node : graph.nodes()) {
 *         graphStore.addNode(node, kbUuid, kbItemUuid);
 *     }
 *     for (Relationship rel : graph.relationships()) {
 *         graphStore.addRelationship(rel, kbUuid, kbItemUuid);
 *     }
 * }
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Service
@Slf4j
public class Neo4jGraphIndexingService {

    @Autowired
    private AiModelProvider aiModelProvider;

    @Autowired
    private Driver neo4jDriver;

    @Autowired
    private AiKnowledgeBaseGraphSegmentService graphSegmentService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 执行文档图谱化索引
     * 对应aideepin的GraphRAG.ingest()
     *
     * <p>aideepin核心逻辑（GraphRAG.java第51-95行）：</p>
     * <pre>
     * DocumentSplitter documentSplitter = DocumentSplitters.recursive(300, overlap, tokenEstimator);
     * GraphStoreIngestor ingestor = GraphStoreIngestor.builder()
     *     .documentSplitter(documentSplitter)
     *     .segmentsFunction(segments -> {
     *         for (TextSegment segment : segments) {
     *             String segmentId = UuidUtil.createShort();
     *             // 保存segment到数据库
     *             KnowledgeBaseGraphSegment graphSegment = new KnowledgeBaseGraphSegment();
     *             graphSegment.setUuid(segmentId);
     *             graphSegment.setRemark(segment.text());
     *             getKnowledgeBaseGraphSegmentService().save(graphSegment);
     *
     *             // 调用LLM提取实体和关系
     *             String response = chatModel.chat(GraphExtractPrompt.replace("{input_text}", segment.text()));
     *             segmentIdToExtractContent.add(Triple.of(segment, segmentId, response));
     *         }
     *     })
     *     .build();
     * </pre>
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @return 索引的实体和关系数量（格式：实体数,关系数）
     */
    public String ingestDocument(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item) {
        try {
            log.info("开始图谱化索引，item_uuid: {}, kb_uuid: {}", item.getItemUuid(), item.getKbUuid());

            // 1. 文档分割（对应aideepin的DocumentSplitters.recursive）
            List<String> segments = splitDocumentForGraph(item.getRemark(), kb);
            log.info("文档分割完成，item_uuid: {}, 文本段数量: {}", item.getItemUuid(), segments.size());

            int totalEntities = 0;
            int totalRelations = 0;

            // 2. 处理每个segment（对应aideepin的segmentsFunction）
            for (int i = 0; i < segments.size(); i++) {
                String segmentText = segments.get(i);

                // 2.1 保存segment到数据库（对应aideepin第63-69行）
                String segmentUuid = saveGraphSegment(kb, item, segmentText, i);
                log.info("Segment保存完成，item_uuid: {}, segment_uuid: {}, index: {}/{}",
                        item.getItemUuid(), segmentUuid, i + 1, segments.size());

                // 2.2 使用LLM提取实体和关系（对应aideepin第81行）
                GraphExtractionResult graph = extractGraphFromSegment(segmentText);
                log.info("LLM提取完成，segment_uuid: {}, 实体数: {}, 关系数: {}",
                        segmentUuid, graph.getEntities().size(), graph.getRelations().size());

                // 2.3 存储实体到Neo4j（关联segment_uuid）
                for (EntityNode entity : graph.getEntities()) {
                    storeEntityWithSegment(entity, item, segmentUuid);
                    totalEntities++;
                }

                // 2.4 存储关系到Neo4j（关联segment_uuid）
                for (RelationshipEdge relation : graph.getRelations()) {
                    storeRelationshipWithSegment(relation, item, segmentUuid);
                    totalRelations++;
                }
            }

            log.info("图谱化索引完成，item_uuid: {}, 总计实体: {}, 总计关系: {}",
                    item.getItemUuid(), totalEntities, totalRelations);

            // 发布图谱索引完成事件
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());
            GraphIndexCompletedEvent event = new GraphIndexCompletedEvent(
                this,
                item.getKbUuid(),
                item.getItemUuid(),
                true,
                null,
                totalEntities,
                totalRelations,
                tenantCode
            );
            eventPublisher.publishEvent(event);

            return totalEntities + "," + totalRelations;

        } catch (Exception e) {
            log.error("图谱化索引失败，item_uuid: {}, 错误: {}", item.getItemUuid(), e.getMessage(), e);

            // 发布图谱索引失败事件
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());
            GraphIndexCompletedEvent event = new GraphIndexCompletedEvent(
                this,
                item.getKbUuid(),
                item.getItemUuid(),
                false,
                e.getMessage(),
                0,
                0,
                tenantCode
            );
            eventPublisher.publishEvent(event);

            throw new RuntimeException("图谱化索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 文档分割（图谱索引）
     * 对应aideepin的DocumentSplitters.recursive(300, overlap, tokenEstimator)
     *
     * <p>aideepin逻辑（GraphRAG.java第54行）：</p>
     * <pre>
     * DocumentSplitter documentSplitter = DocumentSplitters.recursive(
     *     RAG_MAX_SEGMENT_SIZE_IN_TOKENS,          // 300 tokens
     *     graphIngestParams.getOverlap(),          // 默认50 tokens
     *     TokenEstimatorFactory.create(graphIngestParams.getTokenEstimator())
     * );
     * </pre>
     *
     * @param content 文档内容
     * @param kb 知识库配置
     * @return 分割后的文本段列表
     */
    private List<String> splitDocumentForGraph(String content, AiKnowledgeBaseEntity kb) {
        // 获取overlap参数（对应aideepin的kb.getIngestMaxOverlap()）
        int overlap = kb.getIngestMaxOverlap() != null ? kb.getIngestMaxOverlap() : 50;

        // 使用OverlappingTokenTextSplitter（对应aideepin的DocumentSplitters.recursive）
        OverlappingTokenTextSplitter splitter = new OverlappingTokenTextSplitter(
                300,     // maxSegmentSizeInTokens（对应aideepin的RAG_MAX_SEGMENT_SIZE_IN_TOKENS）
                overlap  // maxOverlapSizeInTokens（对应aideepin的overlap参数）
        );

        // 创建Document对象
        Document document = new Document(content);

        // 执行分割并返回文本内容列表
        return splitter.apply(Collections.singletonList(document))
                .stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

    /**
     * 保存图谱segment到数据库
     * 对应aideepin的KnowledgeBaseGraphSegmentService.save()
     *
     * <p>aideepin逻辑（GraphRAG.java第63-69行）：</p>
     * <pre>
     * String segmentId = UuidUtil.createShort();
     * KnowledgeBaseGraphSegment graphSegment = new KnowledgeBaseGraphSegment();
     * graphSegment.setUuid(segmentId);
     * graphSegment.setRemark(segment.text());
     * graphSegment.setKbUuid(segment.metadata().getString(KB_UUID));
     * graphSegment.setKbItemUuid(segment.metadata().getString(KB_ITEM_UUID));
     * graphSegment.setUserId(user.getId());
     * getKnowledgeBaseGraphSegmentService().save(graphSegment);
     * </pre>
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @param segmentText 文本段内容
     * @param segmentIndex 文本段索引（从0开始）
     * @return segment的UUID
     */
    private String saveGraphSegment(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item,
                                     String segmentText, int segmentIndex) {
        // 生成segment UUID（对应aideepin的UuidUtil.createShort()）
        String segmentUuid = UuidUtil.createShort();

        // 构建实体（对应aideepin的KnowledgeBaseGraphSegment）
        AiKnowledgeBaseGraphSegmentEntity segmentEntity = new AiKnowledgeBaseGraphSegmentEntity();
        segmentEntity.setUuid(segmentUuid);
        segmentEntity.setKbUuid(item.getKbUuid());
        segmentEntity.setKbItemUuid(item.getItemUuid());
        segmentEntity.setRemark(segmentText);
        // 将String转换为Long（createUser字段在item中是String类型）
        segmentEntity.setUserId(item.getCreateUser() != null ? Long.parseLong(item.getCreateUser()) : null);
        segmentEntity.setCreateTime(System.currentTimeMillis());
        segmentEntity.setUpdateTime(System.currentTimeMillis());
        segmentEntity.setIsDeleted(0);

        // 保存到数据库（对应aideepin第69行）
        graphSegmentService.save(segmentEntity);

        return segmentUuid;
    }

    /**
     * 使用LLM提取segment中的实体和关系
     * 对应aideepin的GraphStoreIngestor.extractGraph()
     *
     * <p>aideepin实现：</p>
     * 使用LangChain4j的ChatModel和专门的GraphExtractor
     *
     * <p>scm-ai实现：</p>
     * 使用Spring AI的ChatModel和自定义Prompt
     *
     * @param segmentText segment文本内容
     * @return 提取结果（实体和关系）
     */
    private GraphExtractionResult extractGraphFromSegment(String segmentText) {
        // 构建提示词（对应aideepin的GraphExtractor prompt）
        String promptText = buildGraphExtractionPrompt(segmentText);

        // 调用LLM提取（对应aideepin的chatModel.chat）
        Prompt prompt = new Prompt(promptText);
        ChatResponse response = aiModelProvider.getChatModel().call(prompt);
        String jsonResult = response.getResult().getOutput().getText();

        // 解析JSON结果
        return parseGraphExtractionResult(jsonResult);
    }

    /**
     * 构建图谱提取的Prompt
     * 对应aideepin的GraphExtractor prompt模板
     *
     * <p>aideepin的prompt设计：</p>
     * 要求LLM识别文档中的实体（人物、组织、概念等）和它们之间的关系
     *
     * @param content 文档内容
     * @return Prompt文本
     */
    private String buildGraphExtractionPrompt(String content) {
        return """
                请分析以下文本，提取出其中的实体和关系，以JSON格式返回。

                要求：
                1. 实体类型包括：人物(PERSON)、组织(ORGANIZATION)、地点(LOCATION)、概念(CONCEPT)、事件(EVENT)等
                2. 关系类型包括：属于(BELONGS_TO)、位于(LOCATED_IN)、参与(PARTICIPATES_IN)、相关(RELATED_TO)等
                3. 每个实体包含：名称(name)、类型(type)、描述(description)
                4. 每个关系包含：起始实体(from)、终止实体(to)、关系类型(type)、描述(description)

                返回格式：
                {
                  "entities": [
                    {"name": "实体名称", "type": "PERSON", "description": "实体描述"}
                  ],
                  "relations": [
                    {"from": "实体1", "to": "实体2", "type": "RELATED_TO", "description": "关系描述"}
                  ]
                }

                文本内容：
                %s

                请严格按照JSON格式返回，不要包含其他内容。
                """.formatted(content.length() > 2000 ? content.substring(0, 2000) : content);
    }

    /**
     * 解析LLM返回的图谱提取结果
     * 对应aideepin的JSON解析逻辑
     *
     * @param jsonResult LLM返回的JSON字符串
     * @return 解析后的实体和关系
     */
    private GraphExtractionResult parseGraphExtractionResult(String jsonResult) {
        try {
            // 移除可能的Markdown代码块标记
            jsonResult = jsonResult.replaceAll("```json", "").replaceAll("```", "").trim();

            JSONObject json = JSON.parseObject(jsonResult);
            GraphExtractionResult result = new GraphExtractionResult();

            // 解析实体
            JSONArray entitiesArray = json.getJSONArray("entities");
            if (entitiesArray != null) {
                for (int i = 0; i < entitiesArray.size(); i++) {
                    JSONObject entityJson = entitiesArray.getJSONObject(i);
                    EntityNode entity = new EntityNode();
                    entity.setId(UuidUtil.createShort());
                    entity.setName(entityJson.getString("name"));
                    entity.setType(entityJson.getString("type"));
                    entity.setDescription(entityJson.getString("description"));
                    result.getEntities().add(entity);
                }
            }

            // 解析关系
            JSONArray relationsArray = json.getJSONArray("relations");
            if (relationsArray != null) {
                for (int i = 0; i < relationsArray.size(); i++) {
                    JSONObject relationJson = relationsArray.getJSONObject(i);
                    RelationshipEdge relation = new RelationshipEdge();
                    relation.setId(UuidUtil.createShort());
                    relation.setFromEntityName(relationJson.getString("from"));
                    relation.setToEntityName(relationJson.getString("to"));
                    relation.setType(relationJson.getString("type"));
                    relation.setDescription(relationJson.getString("description"));
                    result.getRelations().add(relation);
                }
            }

            return result;

        } catch (Exception e) {
            log.error("解析LLM返回的图谱提取结果失败，JSON: {}, 错误: {}", jsonResult, e.getMessage(), e);
            // 返回空结果，不影响整体流程
            return new GraphExtractionResult();
        }
    }

    /**
     * 存储实体到Neo4j（关联segment_uuid）
     * 对应aideepin的graphStore.addNode()，并实现增量更新逻辑
     *
     * <p>增量更新逻辑：</p>
     * <ul>
     *   <li>如果实体不存在：创建新实体，初始化segment_ids数组</li>
     *   <li>如果实体已存在：
     *     <ul>
     *       <li>description不重复则追加（用换行符分隔）</li>
     *       <li>segment_ids数组追加新的segment_uuid（去重）</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>对应aideepin的实现：</p>
     * <pre>
     * MERGE (e:Entity {name: $name, kb_uuid: $kb_uuid, type: $type})
     * ON CREATE SET e.id = $id, e.description = $description, e.segment_ids = [$segmentUuid]
     * ON MATCH SET e.description = CASE WHEN NOT (e.description CONTAINS $description)
     *                                    THEN e.description + '\n' + $description
     *                                    ELSE e.description END
     * </pre>
     *
     * @param entity 实体节点
     * @param item 文档项
     * @param segmentUuid segment的UUID
     */
    private void storeEntityWithSegment(EntityNode entity, AiKnowledgeBaseItemEntity item, String segmentUuid) {
        try (Session session = neo4jDriver.session()) {
            // 使用name作为唯一标识（标准化为大写，对应aideepin的实体名称规范）
            String standardizedName = entity.getName().toUpperCase();

            // MERGE + ON CREATE / ON MATCH 实现增量更新
            String cypher = """
                    MERGE (e:Entity {name: $name, kb_uuid: $kb_uuid, type: $type})
                    ON CREATE SET
                        e.id = $id,
                        e.kb_item_uuid = $kb_item_uuid,
                        e.tenant_id = $tenant_id,
                        e.description = $description,
                        e.segment_ids = [$segmentUuid],
                        e.create_time = datetime()
                    ON MATCH SET
                        e.description = CASE
                            WHEN NOT (e.description CONTAINS $description)
                            THEN e.description + '\\n' + $description
                            ELSE e.description
                        END,
                        e.segment_ids = CASE
                            WHEN NOT ($segmentUuid IN e.segment_ids)
                            THEN e.segment_ids + $segmentUuid
                            ELSE e.segment_ids
                        END,
                        e.update_time = datetime()
                    """;

            Map<String, Object> params = new HashMap<>();
            params.put("id", entity.getId());
            params.put("name", standardizedName);
            params.put("kb_uuid", item.getKbUuid());
            params.put("kb_item_uuid", item.getItemUuid());
            params.put("type", entity.getType());
            params.put("description", entity.getDescription());
            params.put("segmentUuid", segmentUuid);

            session.run(cypher, params);
        } catch (Exception e) {
            log.error("存储实体失败，name: {}, segment_uuid: {}, 错误: {}",
                    entity.getName(), segmentUuid, e.getMessage(), e);
        }
    }

    /**
     * 存储关系到Neo4j（关联segment_uuid）
     * 对应aideepin的graphStore.addRelationship()，并实现增量更新逻辑
     *
     * <p>增量更新逻辑：</p>
     * 使用MERGE创建关系，ON CREATE / ON MATCH实现segment_ids追踪
     *
     * @param relation 关系边
     * @param item 文档项
     * @param segmentUuid segment的UUID
     */
    private void storeRelationshipWithSegment(RelationshipEdge relation, AiKnowledgeBaseItemEntity item, String segmentUuid) {
        try (Session session = neo4jDriver.session()) {
            // 标准化实体名称（对应storeEntityWithSegment的规范）
            String standardizedFromName = relation.getFromEntityName().toUpperCase();
            String standardizedToName = relation.getToEntityName().toUpperCase();

            // 使用MERGE创建关系，ON CREATE / ON MATCH实现增量更新
            String cypher = """
                    MATCH (from:Entity {name: $fromName, kb_uuid: $kb_uuid})
                    MATCH (to:Entity {name: $toName, kb_uuid: $kb_uuid})
                    MERGE (from)-[r:RELATION {type: $type}]->(to)
                    ON CREATE SET
                        r.id = $id,
                        r.description = $description,
                        r.kb_uuid = $kb_uuid,
                        r.kb_item_uuid = $kb_item_uuid,
                        r.segment_ids = [$segmentUuid],
                        r.create_time = datetime()
                    ON MATCH SET
                        r.description = CASE
                            WHEN NOT (r.description CONTAINS $description)
                            THEN r.description + '\\n' + $description
                            ELSE r.description
                        END,
                        r.segment_ids = CASE
                            WHEN NOT ($segmentUuid IN r.segment_ids)
                            THEN r.segment_ids + $segmentUuid
                            ELSE r.segment_ids
                        END,
                        r.update_time = datetime()
                    """;

            Map<String, Object> params = new HashMap<>();
            params.put("fromName", standardizedFromName);
            params.put("toName", standardizedToName);
            params.put("kb_uuid", item.getKbUuid());
            params.put("type", relation.getType());
            params.put("id", relation.getId());
            params.put("description", relation.getDescription());
            params.put("kb_item_uuid", item.getItemUuid());
            params.put("segmentUuid", segmentUuid);

            session.run(cypher, params);

        } catch (Exception e) {
            log.error("存储关系失败，from: {}, to: {}, segment_uuid: {}, 错误: {}",
                    relation.getFromEntityName(), relation.getToEntityName(), segmentUuid, e.getMessage(), e);
        }
    }

    /**
     * 删除文档的所有图谱数据
     * 对应aideepin的删除逻辑
     *
     * <p>删除步骤：</p>
     * <ol>
     *   <li>删除Neo4j中的关系</li>
     *   <li>删除Neo4j中的实体</li>
     *   <li>删除MySQL中的segment数据</li>
     * </ol>
     *
     * @param itemUuid 文档UUID
     * @return 删除的实体和关系数量（格式：实体数,关系数）
     */
    public String deleteDocumentGraph(String itemUuid) {
        try (Session session = neo4jDriver.session()) {
            log.info("开始删除文档图谱，item_uuid: {}", itemUuid);

            // 1. 删除Neo4j关系
            String deleteRelationsCypher = """
                    MATCH ()-[r:RELATION {kb_item_uuid: $itemUuid}]->()
                    DELETE r
                    RETURN count(r) as deletedRelations
                    """;

            Map<String, Object> params = new HashMap<>();
            params.put("itemUuid", itemUuid);

            var relResult = session.run(deleteRelationsCypher, params);
            int deletedRelations = relResult.hasNext() ? relResult.next().get("deletedRelations").asInt() : 0;

            // 2. 删除Neo4j实体
            String deleteNodesCypher = """
                    MATCH (e:Entity {kb_item_uuid: $itemUuid})
                    DELETE e
                    RETURN count(e) as deletedNodes
                    """;

            var nodeResult = session.run(deleteNodesCypher, params);
            int deletedNodes = nodeResult.hasNext() ? nodeResult.next().get("deletedNodes").asInt() : 0;

            // 3. 删除MySQL中的segment数据（对应aideepin的KnowledgeBaseGraphSegmentService删除逻辑）
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiKnowledgeBaseGraphSegmentEntity> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(AiKnowledgeBaseGraphSegmentEntity::getKbItemUuid, itemUuid);
            int deletedSegments = graphSegmentService.remove(wrapper) ? 1 : 0;

            log.info("文档图谱删除完成，item_uuid: {}, 删除实体: {}, 删除关系: {}, 删除segment: {}",
                    itemUuid, deletedNodes, deletedRelations, deletedSegments);

            return deletedNodes + "," + deletedRelations;

        } catch (Exception e) {
            log.error("文档图谱删除失败，item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("文档图谱删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 图谱提取结果
     */
    @Data
    private static class GraphExtractionResult {
        private List<EntityNode> entities = new ArrayList<>();
        private List<RelationshipEdge> relations = new ArrayList<>();
    }

    /**
     * 实体节点
     */
    @Data
    private static class EntityNode {
        private String id;
        private String name;
        private String type;
        private String description;
    }

    /**
     * 关系边
     */
    @Data
    private static class RelationshipEdge {
        private String id;
        private String fromEntityName;
        private String toEntityName;
        private String type;
        private String description;
    }

    /**
     * 统计知识库的图谱元素数量
     * 用于知识库统计服务
     *
     * @param kb_uuid 知识库UUID (格式: tenant_code::uuid)
     * @return Map包含 entity_count(实体数) 和 relation_count(关系数)
     */
    public Map<String, Long> countGraphElementsByKbUuid(String kb_uuid) {
        Map<String, Long> result = new HashMap<>();
        try (Session session = neo4jDriver.session()) {
            // 统计实体数量
            String entityQuery = """
                MATCH (n:KnowledgeEntity)
                WHERE n.kb_uuid = $kb_uuid
                RETURN count(n) as count
                """;

            Long entityCount = session.run(entityQuery, Map.of("kb_uuid", kb_uuid))
                    .single()
                    .get("count")
                    .asLong();

            // 统计关系数量
            String relationQuery = """
                MATCH ()-[r:KNOWLEDGE_RELATION]->()
                WHERE r.kb_uuid = $kb_uuid
                RETURN count(r) as count
                """;

            Long relationCount = session.run(relationQuery, Map.of("kb_uuid", kb_uuid))
                    .single()
                    .get("count")
                    .asLong();

            result.put("entity_count", entityCount);
            result.put("relation_count", relationCount);

            log.debug("统计知识库图谱元素数量，kb_uuid: {}, entity_count: {}, relation_count: {}",
                     kb_uuid, entityCount, relationCount);

        } catch (Exception e) {
            log.error("统计知识库图谱元素数量失败，kb_uuid: {}", kb_uuid, e);
            result.put("entity_count", 0L);
            result.put("relation_count", 0L);
        }
        return result;
    }

    /**
     * 从kb_uuid中提取tenant_code
     * kb_uuid格式: tenant_code::uuid
     *
     * @param kb_uuid 知识库UUID
     * @return tenant_code
     */
    private String extractTenantCodeFromKbUuid(String kb_uuid) {
        if (kb_uuid == null || !kb_uuid.contains("::")) {
            log.warn("kb_uuid格式不正确，无法提取tenant_code: {}", kb_uuid);
            return "";
        }
        return kb_uuid.split("::")[0];
    }
}
