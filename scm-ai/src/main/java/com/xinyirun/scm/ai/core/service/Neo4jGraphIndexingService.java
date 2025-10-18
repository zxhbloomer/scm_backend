package com.xinyirun.scm.ai.core.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseGraphSegmentEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.event.GraphIndexCompletedEvent;
import com.xinyirun.scm.ai.core.prompt.GraphExtractPrompt;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseGraphSegmentService;
import com.xinyirun.scm.ai.core.service.splitter.JTokkitTokenTextSplitter;
import com.xinyirun.scm.ai.common.util.AdiStringUtil;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Neo4j图谱索引服务
 *
 * <p>功能说明：</p>
 * <p>使用LLM从文档中提取实体和关系，构建知识图谱并存储到Neo4j</p>
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>文档分割 - 使用JTokkitTokenTextSplitter按Token数分割</li>
 *   <li>实体提取 - ChatModel分析每个segment，提取实体</li>
 *   <li>关系提取 - ChatModel识别实体间的关系</li>
 *   <li>存储图谱 - 使用Neo4j的MERGE语句实现增量更新</li>
 * </ol>
 *
 * <p>技术实现：</p>
 * <ul>
 *   <li>提示词：基于Microsoft GraphRAG标准提示词（GraphExtractPrompt）</li>
 *   <li>增量更新：MERGE + ON CREATE/ON MATCH实现实体和关系的增量追加</li>
 *   <li>segment关联：所有实体和关系都关联segment_ids数组，支持溯源</li>
 *   <li>多租户：tenant_code字段隔离不同租户的图谱数据</li>
 * </ul>
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
     *
     * <p>处理流程：</p>
     * <ol>
     *   <li>文档分割 - splitDocumentForGraph()，使用JTokkitTokenTextSplitter</li>
     *   <li>遍历segment - 保存segment到MySQL，调用LLM提取实体和关系</li>
     *   <li>存储实体 - storeEntityWithSegment()，使用Neo4j MERGE实现增量更新</li>
     *   <li>存储关系 - storeRelationshipWithSegment()，使用Neo4j MERGE实现增量更新</li>
     *   <li>发布事件 - GraphIndexCompletedEvent，触发统计更新</li>
     * </ol>
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @return 索引的实体和关系数量（格式：实体数,关系数）
     */
    public String ingestDocument(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item) {
        try {
            log.info("开始图谱化索引，item_uuid: {}, kb_uuid: {}", item.getItemUuid(), item.getKbUuid());

            // 1. 文档分割
            List<String> segments = splitDocumentForGraph(item.getRemark(), kb);
            log.info("文档分割完成，item_uuid: {}, 文本段数量: {}", item.getItemUuid(), segments.size());

            int totalEntities = 0;
            int totalRelations = 0;

            // 2. 处理每个segment
            for (int i = 0; i < segments.size(); i++) {
                String segmentText = segments.get(i);

                // 2.1 保存segment到数据库
                String segmentUuid = saveGraphSegment(kb, item, segmentText, i);
                log.info("Segment保存完成，item_uuid: {}, segment_uuid: {}, index: {}/{}",
                        item.getItemUuid(), segmentUuid, i + 1, segments.size());

                // 2.2 使用LLM提取实体和关系
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
     * 文档分割（图谱索引专用）
     *
     * <p>分割策略：</p>
     * <ul>
     *   <li>使用JTokkitTokenTextSplitter进行Token级精确分割</li>
     *   <li>默认chunkSize：2000 tokens（较大chunk适合图谱提取）</li>
     *   <li>overlap：从知识库配置读取（默认50 tokens）</li>
     *   <li>编码方式：CL100K_BASE（与GPT-4/GPT-3.5一致）</li>
     * </ul>
     *
     * @param content 文档内容
     * @param kb 知识库配置
     * @return 分割后的文本段列表
     */
    private List<String> splitDocumentForGraph(String content, AiKnowledgeBaseEntity kb) {
        // 获取overlap参数
        int overlap = kb.getIngestMaxOverlap() != null ? kb.getIngestMaxOverlap() : 50;

        // 使用JTokkitTokenTextSplitter
        // 使用默认chunkSize（2000 tokens）
        JTokkitTokenTextSplitter splitter = JTokkitTokenTextSplitter.builder()
                .withOverlapSize(overlap)     // maxOverlapSizeInTokens
                .build();

        // 创建Document对象
        Document document = new Document(content);

        // 执行分割并返回文本内容列表
        return splitter.apply(Collections.singletonList(document))
                .stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

    /**
     * 保存图谱segment到MySQL数据库
     *
     * <p>segment用途：</p>
     * <ul>
     *   <li>记录LLM提取的原始文本段</li>
     *   <li>与Neo4j中的实体和关系关联（通过segment_uuid）</li>
     *   <li>支持图谱数据的溯源查询</li>
     * </ul>
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @param segmentText 文本段内容
     * @param segmentIndex 文本段索引（从0开始）
     * @return segment的UUID
     */
    private String saveGraphSegment(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item,
                                     String segmentText, int segmentIndex) {
        // 生成segment UUID
        String segmentUuid = UuidUtil.createShort();

        // 构建实体
        AiKnowledgeBaseGraphSegmentEntity segmentEntity = new AiKnowledgeBaseGraphSegmentEntity();
        segmentEntity.setUuid(segmentUuid);
        segmentEntity.setKbUuid(item.getKbUuid());
        segmentEntity.setKbItemUuid(item.getItemUuid());
        segmentEntity.setRemark(segmentText);
        segmentEntity.setIsDeleted(0);

        // 保存到数据库
        // c_id, c_time, u_id, u_time, dbversion 由 MyBatis-Plus 自动填充
        graphSegmentService.save(segmentEntity);

        return segmentUuid;
    }

    /**
     * 使用LLM提取segment中的实体和关系
     *
     * <p>技术实现：</p>
     * <ul>
     *   <li>ChatModel：使用Spring AI的ChatModel接口</li>
     *   <li>Prompt：基于Microsoft GraphRAG标准提示词（GraphExtractPrompt.GRAPH_EXTRACTION_PROMPT_CN）</li>
     *   <li>解析：使用##分隔记录，使用&lt;|&gt;分隔字段</li>
     * </ul>
     *
     * @param segmentText segment文本内容
     * @return 提取结果（实体和关系）
     */
    private GraphExtractionResult extractGraphFromSegment(String segmentText) {
        // 构建提示词
        String promptText = buildGraphExtractionPrompt(segmentText);

        // 调用LLM提取
        Prompt prompt = new Prompt(promptText);
        ChatResponse response = aiModelProvider.getChatModel().call(prompt);
        String jsonResult = response.getResult().getOutput().getText();

        // 解析JSON结果
        return parseGraphExtractionResult(jsonResult);
    }

    /**
     * 构建图谱提取的Prompt
     *
     * <p>使用Microsoft GraphRAG标准中文提示词，替换{input_text}占位符</p>
     *
     * @param content 文档内容
     * @return Prompt文本
     */
    private String buildGraphExtractionPrompt(String content) {
        // 直接使用 Microsoft GraphRAG 标准提示词，替换 {input_text} 占位符
        return GraphExtractPrompt.GRAPH_EXTRACTION_PROMPT_CN.replace("{input_text}", content);
    }

    /**
     * 解析LLM返回的图谱提取结果
     *
     * <p>LLM返回格式（Microsoft GraphRAG标准）:</p>
     * <pre>
     * ("entity"<|>Ming Dynasty<|>organization<|>The ruling dynasty...)
     * ##
     * ("entity"<|>Xuande Period<|>event<|>The era in Ming Dynasty...)
     * ##
     * ("relationship"<|>Ming Dynasty<|>Xuande Period<|>The Xuande Period was...<|>8)
     * ##
     * <|COMPLETE|>
     * </pre>
     *
     * <p>解析流程：</p>
     * <ol>
     *   <li>使用##分隔每条记录</li>
     *   <li>移除首尾括号</li>
     *   <li>使用&lt;|&gt;分隔字段</li>
     *   <li>判断类型（entity/relationship）并解析字段</li>
     *   <li>使用AdiStringUtil.removeSpecialChar()清理实体名称特殊字符</li>
     * </ol>
     *
     * @param llmResponse LLM返回的字符串（使用##分隔记录,使用&lt;|&gt;分隔字段）
     * @return 解析后的实体和关系
     */
    private GraphExtractionResult parseGraphExtractionResult(String llmResponse) {
        GraphExtractionResult result = new GraphExtractionResult();

        try {
            if (StringUtils.isBlank(llmResponse)) {
                log.warn("LLM响应为空");
                return result;
            }

            // 分隔符常量
            final String RECORD_DELIMITER = "##";
            final String TUPLE_DELIMITER = "<\\|>";

            // 第1步: 用 ## 分割每条记录
            String[] rows = llmResponse.split(RECORD_DELIMITER);

            for (String row : rows) {
                String graphRow = row.trim();

                // 跳过结束标记
                if (graphRow.contains("<|COMPLETE|>") || graphRow.isEmpty()) {
                    continue;
                }

                // 第2步: 移除首尾括号
                graphRow = graphRow.replaceAll("^\\(|\\)$", "");

                // 第3步: 用 <|> 分割字段
                String[] recordAttributes = graphRow.split(TUPLE_DELIMITER);

                if (recordAttributes.length < 4) {
                    log.warn("记录字段数量不足，跳过: {}", graphRow);
                    continue;
                }

                // 第4步: 判断是实体还是关系
                String recordType = recordAttributes[0].replaceAll("\"", "").trim();

                if ("entity".equalsIgnoreCase(recordType) || "实体".equals(recordType)) {
                    // 解析实体
                    // 优化1: 使用AdiStringUtil.tail()截断名称为最大20字符
                    // 优化2: 使用AdiStringUtil.removeSpecialChar()清理特殊字符
                    String entityName = AdiStringUtil.tail(
                            AdiStringUtil.removeSpecialChar(clearStr(recordAttributes[1]).toUpperCase()),
                            20
                    );
                    String entityType = clearStr(recordAttributes[2]).toUpperCase()
                            .replaceAll("[^a-zA-Z0-9\\s\\u4E00-\\u9FA5]+", "")
                            .replace(" ", "");
                    String entityDescription = clearStr(recordAttributes[3]);

                    EntityNode entity = new EntityNode();
                    entity.setId(UuidUtil.createShort());
                    entity.setName(entityName);
                    entity.setType(entityType);
                    entity.setDescription(entityDescription);
                    result.getEntities().add(entity);

                    log.debug("解析实体: name={}, type={}, desc={}", entityName, entityType, entityDescription);

                } else if ("relationship".equalsIgnoreCase(recordType) || "关系".equals(recordType)) {
                    // 解析关系
                    // 优化1: 使用AdiStringUtil.tail()截断名称为最大20字符
                    // 优化2: 使用AdiStringUtil.removeSpecialChar()清理特殊字符
                    String sourceName = AdiStringUtil.tail(
                            AdiStringUtil.removeSpecialChar(clearStr(recordAttributes[1]).toUpperCase()),
                            20
                    );
                    String targetName = AdiStringUtil.tail(
                            AdiStringUtil.removeSpecialChar(clearStr(recordAttributes[2]).toUpperCase()),
                            20
                    );
                    String edgeDescription = clearStr(recordAttributes[3]);

                    // 第5个字段是权重(可选,默认1.0) 
                    double weight = 1.0;
                    if (recordAttributes.length > 4) {
                        String weightStr = recordAttributes[4].trim();
                        try {
                            weight = Double.parseDouble(weightStr);
                        } catch (NumberFormatException e) {
                            log.warn("权重解析失败，使用默认值1.0: {}", weightStr);
                        }
                    }

                    RelationshipEdge relation = new RelationshipEdge();
                    relation.setId(UuidUtil.createShort());
                    relation.setFromEntityName(sourceName);
                    relation.setToEntityName(targetName);
                    relation.setType("RELATES_TO"); // 默认关系类型
                    relation.setDescription(edgeDescription);
                    relation.setWeight(weight);
                    result.getRelations().add(relation);

                    log.debug("解析关系: from={}, to={}, desc={}, weight={}",
                             sourceName, targetName, edgeDescription, weight);
                }
            }

            log.info("图谱提取解析完成，实体数: {}, 关系数: {}",
                    result.getEntities().size(), result.getRelations().size());

            return result;

        } catch (Exception e) {
            log.error("解析LLM返回的图谱提取结果失败，response: {}, 错误: {}", llmResponse, e.getMessage(), e);
            // 返回空结果，不影响整体流程
            return new GraphExtractionResult();
        }
    }

    /**
     * 清理字符串（移除引号、转义符等）
     *
     * @param str 原始字符串
     * @return 清理后的字符串
     */
    private String clearStr(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        return str.replaceAll("\"", "")
                .replaceAll("'", "")
                .replaceAll("\\\\", "")
                .trim();
    }

    /**
     * 存储实体到Neo4j（关联segment_uuid）
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
     * <p>Neo4j Cypher说明：</p>
     * <ul>
     *   <li>使用MERGE + ON CREATE/ON MATCH实现增量更新</li>
     *   <li>实体唯一性：通过entity_name、kb_uuid、entity_type三元组确定</li>
     *   <li>segment_ids：数组类型，记录该实体出现在哪些segment中</li>
     *   <li>tenant_code：多租户隔离字段</li>
     * </ul>
     *
     * @param entity 实体节点
     * @param item 文档项
     * @param segmentUuid segment的UUID
     */
    private void storeEntityWithSegment(EntityNode entity, AiKnowledgeBaseItemEntity item, String segmentUuid) {
        try (Session session = neo4jDriver.session()) {
            // 使用name作为唯一标识（标准化为大写，
            String standardizedName = entity.getName().toUpperCase();

            // 从kb_uuid中提取租户编码（用于多租户隔离）
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());

            // MERGE + ON CREATE / ON MATCH 实现增量更新
            // 添加tenant_code字段用于多租户隔离
            String cypher = """
                    MERGE (e:Entity {entity_name: $name, kb_uuid: $kb_uuid, entity_type: $type})
                    ON CREATE SET
                        e.entity_uuid = $id,
                        e.kb_item_uuid = $kb_item_uuid,
                        e.entity_metadata = $description,
                        e.segment_ids = [$segmentUuid],
                        e.tenant_code = $tenant_code,
                        e.create_time = datetime()
                    ON MATCH SET
                        e.entity_metadata = CASE
                            WHEN NOT (e.entity_metadata CONTAINS $description)
                            THEN e.entity_metadata + '\\n' + $description
                            ELSE e.entity_metadata
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
            params.put("tenant_code", tenantCode);

            session.run(cypher, params);
        } catch (Exception e) {
            log.error("存储实体失败，name: {}, segment_uuid: {}, 错误: {}",
                    entity.getName(), segmentUuid, e.getMessage(), e);
        }
    }

    /**
     * 从kb_uuid中提取租户编码
     * kb_uuid格式：tenant_code::uuid
     *
     * @param kbUuid 知识库UUID
     * @return 租户编码
     */
    private String extractTenantCodeFromKbUuid(String kbUuid) {
        if (kbUuid == null || !kbUuid.contains("::")) {
            log.warn("kb_uuid格式不正确，无法提取tenant_code: {}", kbUuid);
            return "";
        }
        return kbUuid.split("::", 2)[0];
    }

    /**
     * 存储关系到Neo4j（关联segment_uuid）
     *
     * <p>增量更新逻辑：</p>
     * <ul>
     *   <li>使用MERGE创建关系，ON CREATE / ON MATCH实现segment_ids追踪</li>
     *   <li>weight权重累加：同一关系在多个segment中出现时，权重会累加</li>
     *   <li>metadata追加：不同segment的description会追加（用换行符分隔）</li>
     * </ul>
     *
     * <p>Neo4j Cypher说明：</p>
     * <ul>
     *   <li>MATCH查找源实体和目标实体</li>
     *   <li>MERGE创建或更新关系</li>
     *   <li>strength：关系强度，每次出现累加</li>
     * </ul>
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
            // weight累加逻辑
            // 从kb_uuid中提取租户编码
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());

            String cypher = """
                    MATCH (from:Entity {entity_name: $fromName, kb_uuid: $kb_uuid})
                    MATCH (to:Entity {entity_name: $toName, kb_uuid: $kb_uuid})
                    MERGE (from)-[r:RELATED_TO]->(to)
                    ON CREATE SET
                        r.relation_type = $type,
                        r.metadata = $description,
                        r.kb_item_uuid = $kb_item_uuid,
                        r.tenant_code = $tenant_code,
                        r.segment_ids = [$segmentUuid],
                        r.strength = $weight,
                        r.create_time = datetime()
                    ON MATCH SET
                        r.metadata = CASE
                            WHEN NOT (r.metadata CONTAINS $description)
                            THEN r.metadata + '\\n' + $description
                            ELSE r.metadata
                        END,
                        r.segment_ids = CASE
                            WHEN NOT ($segmentUuid IN r.segment_ids)
                            THEN r.segment_ids + $segmentUuid
                            ELSE r.segment_ids
                        END,
                        r.strength = r.strength + $weight,
                        r.update_time = datetime()
                    """;

            Map<String, Object> params = new HashMap<>();
            params.put("fromName", standardizedFromName);
            params.put("toName", standardizedToName);
            params.put("kb_uuid", item.getKbUuid());
            params.put("type", relation.getType());
            params.put("description", relation.getDescription());
            params.put("kb_item_uuid", item.getItemUuid());
            params.put("tenant_code", tenantCode);
            params.put("segmentUuid", segmentUuid);
            params.put("weight", relation.getWeight()); // 权重参数

            session.run(cypher, params);

        } catch (Exception e) {
            log.error("存储关系失败，from: {}, to: {}, segment_uuid: {}, 错误: {}",
                    relation.getFromEntityName(), relation.getToEntityName(), segmentUuid, e.getMessage(), e);
        }
    }

    /**
     * 删除知识库的所有图谱数据（批量删除）
     * <p>SCM系统统一使用物理删除</p>
     *
     * <p>删除步骤：</p>
     * <ol>
     *   <li>删除Neo4j中的所有关系（WHERE kb_uuid = ?）</li>
     *   <li>删除Neo4j中的所有实体（WHERE kb_uuid = ?）</li>
     * </ol>
     *
     * @param kbUuid 知识库UUID
     * @return 删除的实体和关系数量（格式：实体数,关系数）
     */
    public String deleteKnowledgeBaseGraph(String kbUuid) {
        try (Session session = neo4jDriver.session()) {
            log.info("开始删除知识库图谱（物理删除），kb_uuid: {}", kbUuid);

            // 1. 删除Neo4j关系（批量删除整个知识库的关系）
            String deleteRelationsCypher = """
                    MATCH ()-[r:RELATED_TO]->()
                    WHERE r.kb_uuid = $kbUuid OR
                          (startNode(r)).kb_uuid IS NOT NULL AND (startNode(r)).kb_uuid = $kbUuid
                    DELETE r
                    RETURN count(r) as deletedRelations
                    """;

            Map<String, Object> params = new HashMap<>();
            params.put("kbUuid", kbUuid);

            var relResult = session.run(deleteRelationsCypher, params);
            int deletedRelations = relResult.hasNext() ? relResult.next().get("deletedRelations").asInt() : 0;

            // 2. 删除Neo4j实体（批量删除整个知识库的实体）
            String deleteNodesCypher = """
                    MATCH (e:Entity {kb_uuid: $kbUuid})
                    DELETE e
                    RETURN count(e) as deletedNodes
                    """;

            var nodeResult = session.run(deleteNodesCypher, params);
            int deletedNodes = nodeResult.hasNext() ? nodeResult.next().get("deletedNodes").asInt() : 0;

            log.info("知识库图谱删除完成（物理删除），kb_uuid: {}, 删除实体: {}, 删除关系: {}",
                    kbUuid, deletedNodes, deletedRelations);

            return deletedNodes + "," + deletedRelations;

        } catch (Exception e) {
            log.error("知识库图谱删除失败，kb_uuid: {}, 错误: {}", kbUuid, e.getMessage(), e);
            throw new RuntimeException("知识库图谱删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文档的所有图谱数据
     * 
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
                    MATCH ()-[r:RELATED_TO {kb_item_uuid: $itemUuid}]->()
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

            // 3. 删除MySQL中的segment数据
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
        private double weight; // 关系权重（LLM提取，表示关系强度）
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
                MATCH (n:Entity)
                WHERE n.kb_uuid = $kb_uuid
                RETURN count(n) as count
                """;

            Long entityCount = session.run(entityQuery, Map.of("kb_uuid", kb_uuid))
                    .single()
                    .get("count")
                    .asLong();

            // 统计关系数量
            String relationQuery = """
                MATCH ()-[r:RELATED_TO]->()
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
}
