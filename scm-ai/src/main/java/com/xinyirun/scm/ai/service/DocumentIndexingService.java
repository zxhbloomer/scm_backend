package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.service.elasticsearch.ElasticsearchIndexingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档索引主服务
 *
 * <p>功能说明：</p>
 * 严格对应aideepin的KnowledgeBaseItemService.asyncIndex()逻辑
 * 协调文档解析、向量索引、图谱索引的完整流程
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>从URL解析文档内容 - DocumentParsingService</li>
 *   <li>保存文档内容到MySQL - 更新remark字段</li>
 *   <li>向量化索引 - ElasticsearchIndexingService（embedding索引）</li>
 *   <li>图谱化索引 - Neo4jGraphIndexingService（graphical索引）</li>
 *   <li>更新索引状态 - 更新is_embedded、is_qa_parsed等字段</li>
 * </ol>
 *
 * <p>参考代码：</p>
 * aideepin: KnowledgeBaseItemService.asyncIndex()
 * 路径: D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\service\KnowledgeBaseItemService.java
 *
 * <p>aideepin核心代码：</p>
 * <pre>
 * {@literal @Async}
 * public void asyncIndex(User user, KnowledgeBase kb, KnowledgeBaseItem item, List<String> indexTypes) {
 *     if (indexTypes.contains(DOC_INDEX_TYPE_EMBEDDING)) {
 *         Metadata metadata = new Metadata();
 *         metadata.put(AdiConstant.MetadataKey.KB_UUID, item.getKbUuid());
 *         metadata.put(AdiConstant.MetadataKey.KB_ITEM_UUID, item.getUuid());
 *         Document document = new DefaultDocument(item.getRemark(), metadata);
 *         compositeRAG.getEmbeddingRAGService().ingest(document, kb.getIngestMaxOverlap(), ...);
 *     }
 *
 *     if (indexTypes.contains(DOC_INDEX_TYPE_GRAPHICAL)) {
 *         Map<String, Object> params = new HashMap<>();
 *         params.put("content", item.getRemark());
 *         params.put("kbUuid", item.getKbUuid());
 *         params.put("kbItemUuid", item.getUuid());
 *         compositeRAG.getGraphRAGService().ingest(params);
 *     }
 * }
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Service
@Slf4j
public class DocumentIndexingService {

    /**
     * 索引类型：向量索引
     * 对应aideepin的DOC_INDEX_TYPE_EMBEDDING
     */
    public static final String INDEX_TYPE_EMBEDDING = "embedding";

    /**
     * 索引类型：图谱索引
     * 对应aideepin的DOC_INDEX_TYPE_GRAPHICAL
     */
    public static final String INDEX_TYPE_GRAPHICAL = "graphical";

    @Autowired
    private AiKnowledgeBaseMapper kbMapper;

    @Autowired
    private AiKnowledgeBaseItemMapper itemMapper;

    @Autowired
    private DocumentParsingService documentParsingService;

    @Autowired
    private ElasticsearchIndexingService elasticsearchIndexingService;

    @Autowired
    private Neo4jGraphIndexingService neo4jGraphIndexingService;

    /**
     * 执行文档索引处理
     * 对应aideepin的asyncIndex()方法
     *
     * <p>调用链：</p>
     * MQ消费者 → DocumentIndexingService.processDocument → 各个索引服务
     *
     * <p>与aideepin的区别：</p>
     * - aideepin使用@Async注解实现异步
     * - scm-ai使用RabbitMQ消息队列实现异步
     *
     * @param itemUuid 文档UUID
     * @param kbUuid 知识库UUID
     * @param fileUrl 文件URL
     * @param fileName 文件名
     * @param indexTypes 索引类型列表（embedding、graphical）
     */
    public void processDocument(String itemUuid, String kbUuid, String fileUrl, String fileName, List<String> indexTypes) {
        try {
            log.info("开始处理文档索引，item_uuid: {}, kb_uuid: {}, file: {}, indexTypes: {}",
                    itemUuid, kbUuid, fileName, indexTypes);

            // 1. 查询知识库配置（对应aideepin的KnowledgeBase kb参数）
            AiKnowledgeBaseEntity kb = kbMapper.selectOne(
                    new LambdaQueryWrapper<AiKnowledgeBaseEntity>()
                            .eq(AiKnowledgeBaseEntity::getKbUuid, kbUuid)
            );
            if (kb == null) {
                throw new RuntimeException("知识库不存在: " + kbUuid);
            }

            // 2. 查询文档项（对应aideepin的KnowledgeBaseItem item参数）
            AiKnowledgeBaseItemEntity item = itemMapper.selectOne(
                    new LambdaQueryWrapper<AiKnowledgeBaseItemEntity>()
                            .eq(AiKnowledgeBaseItemEntity::getItemUuid, itemUuid)
            );
            if (item == null) {
                throw new RuntimeException("文档项不存在: " + itemUuid);
            }

            // 3. 解析文档内容（对应aideepin在uploadDoc时已完成）
            if (item.getRemark() == null || item.getRemark().isEmpty()) {
                String content = documentParsingService.parseDocumentFromUrl(fileUrl, fileName);

                // 保存文档内容到MySQL
                LambdaUpdateWrapper<AiKnowledgeBaseItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, itemUuid);
                updateWrapper.set(AiKnowledgeBaseItemEntity::getRemark, content);
                updateWrapper.set(AiKnowledgeBaseItemEntity::getBrief,
                        content.length() > 200 ? content.substring(0, 200) : content);
                itemMapper.update(null, updateWrapper);

                // 更新内存中的item对象
                item.setRemark(content);
                item.setBrief(content.length() > 200 ? content.substring(0, 200) : content);

                log.info("文档内容解析完成，item_uuid: {}, 内容长度: {}", itemUuid, content.length());
            }

            // 4. 执行向量索引
            if (indexTypes.contains(INDEX_TYPE_EMBEDDING)) {
                log.info("开始向量索引，item_uuid: {}", itemUuid);
                int segmentCount = elasticsearchIndexingService.ingestDocument(kb, item);

                // 更新向量化状态为已完成（3=已完成）
                LambdaUpdateWrapper<AiKnowledgeBaseItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, itemUuid);
                updateWrapper.set(AiKnowledgeBaseItemEntity::getEmbeddingStatus, 3);
                updateWrapper.set(AiKnowledgeBaseItemEntity::getEmbeddingStatusChangeTime, System.currentTimeMillis());
                itemMapper.update(null, updateWrapper);

                log.info("向量索引完成，item_uuid: {}, 文本段数: {}", itemUuid, segmentCount);
            }

            // 5. 执行图谱索引
            if (indexTypes.contains(INDEX_TYPE_GRAPHICAL)) {
                log.info("开始图谱索引，item_uuid: {}", itemUuid);
                String result = neo4jGraphIndexingService.ingestDocument(kb, item);
                String[] counts = result.split(",");
                int entityCount = Integer.parseInt(counts[0]);
                int relationCount = Integer.parseInt(counts[1]);

                log.info("图谱索引完成，item_uuid: {}, 实体: {}, 关系: {}", itemUuid, entityCount, relationCount);
            }

            log.info("文档索引处理完成，item_uuid: {}", itemUuid);

        } catch (Exception e) {
            log.error("文档索引处理失败，item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);

            // 更新状态为索引失败（4=失败）
            LambdaUpdateWrapper<AiKnowledgeBaseItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, itemUuid);
            updateWrapper.set(AiKnowledgeBaseItemEntity::getEmbeddingStatus, 4);
            updateWrapper.set(AiKnowledgeBaseItemEntity::getEmbeddingStatusChangeTime, System.currentTimeMillis());
            itemMapper.update(null, updateWrapper);

            throw new RuntimeException("文档索引处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文档的所有索引数据
     * 对应aideepin的删除逻辑
     *
     * @param itemUuid 文档UUID
     */
    public void deleteDocumentIndex(String itemUuid, List<String> indexTypes) {
        try {
            log.info("开始删除文档索引，item_uuid: {}, indexTypes: {}", itemUuid, indexTypes);

            // 1. 删除向量索引
            if (indexTypes.contains(INDEX_TYPE_EMBEDDING)) {
                elasticsearchIndexingService.deleteDocumentEmbeddings(itemUuid);

                // 更新向量化状态为待处理（1=待处理）
                LambdaUpdateWrapper<AiKnowledgeBaseItemEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(AiKnowledgeBaseItemEntity::getItemUuid, itemUuid);
                updateWrapper.set(AiKnowledgeBaseItemEntity::getEmbeddingStatus, 1);
                updateWrapper.set(AiKnowledgeBaseItemEntity::getEmbeddingStatusChangeTime, System.currentTimeMillis());
                itemMapper.update(null, updateWrapper);

                log.info("向量索引删除完成，item_uuid: {}", itemUuid);
            }

            // 2. 删除图谱索引
            if (indexTypes.contains(INDEX_TYPE_GRAPHICAL)) {
                neo4jGraphIndexingService.deleteDocumentGraph(itemUuid);
                log.info("图谱索引删除完成，item_uuid: {}", itemUuid);
            }

            log.info("文档索引删除完成，item_uuid: {}", itemUuid);

        } catch (Exception e) {
            log.error("文档索引删除失败，item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("文档索引删除失败: " + e.getMessage(), e);
        }
    }
}
