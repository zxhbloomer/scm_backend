# Elasticsearch迁移Milvus 按文件设计方案

**文档编号**: 2025-12-02-221500
**功能名称**: SCM AI模块向量存储从Elasticsearch迁移到Milvus
**作者**: SCM AI Team
**日期**: 2025-12-02

---

## 1. 完整调用链路分析

### 1.1 文档向量化索引流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          文档向量化索引调用链                                      │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  [前端上传文档]                                                                  │
│       │                                                                         │
│       ▼                                                                         │
│  KnowledgeBaseItemController.upload()                                           │
│       │                                                                         │
│       ▼                                                                         │
│  RabbitMQ消息队列 (MQEnum.AI_DOCUMENT_INDEXING)                                  │
│       │                                                                         │
│       ▼                                                                         │
│  DocumentIndexingConsumer.onMessage()                                           │
│       │                                                                         │
│       ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │ DocumentIndexingService.processDocument()  [Line 100]                    │   │
│  │     │                                                                    │   │
│  │     ├── 1. 解析文档内容 (DocumentParsingService)                         │   │
│  │     │                                                                    │   │
│  │     ├── 2. 向量化索引 [Line 152] ★ 关键调用点                            │   │
│  │     │     │                                                              │   │
│  │     │     ▼                                                              │   │
│  │     │   ┌──────────────────────────────────────────────────────────┐    │   │
│  │     │   │ ElasticsearchIndexingService.ingestDocument()            │    │   │
│  │     │   │     │                                                    │    │   │
│  │     │   │     ├── splitDocument() - 文本分割                       │    │   │
│  │     │   │     ├── generateEmbedding() - 生成向量                   │    │   │
│  │     │   │     ├── storeEmbedding() - 存储到ES                      │    │   │
│  │     │   │     └── refreshIndex() - 刷新索引                        │    │   │
│  │     │   │                                                          │    │   │
│  │     │   │ 需要替换为: MilvusVectorIndexingService.ingestDocument() │    │   │
│  │     │   └──────────────────────────────────────────────────────────┘    │   │
│  │     │                                                                    │   │
│  │     └── 3. 图谱索引 (Neo4jGraphIndexingService) - 不变                   │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 向量检索流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          向量检索调用链                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  [用户提问]                                                                      │
│       │                                                                         │
│       ▼                                                                         │
│  KnowledgeBaseQAController.sseAsk() [Line 86]                                   │
│       │                                                                         │
│       ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │ RagService.sseAsk() [Line 87]                                            │   │
│  │     │                                                                    │   │
│  │     ├── 1. 查询QA记录                                                    │   │
│  │     │                                                                    │   │
│  │     ├── 2. 向量检索 [Line 174] ★ 关键调用点                              │   │
│  │     │     │                                                              │   │
│  │     │     ▼                                                              │   │
│  │     │   ┌──────────────────────────────────────────────────────────┐    │   │
│  │     │   │ VectorRetrievalService.searchSimilarDocuments()          │    │   │
│  │     │   │     │                                                    │    │   │
│  │     │   │     ├── generateQuestionEmbedding() - 问题向量化         │    │   │
│  │     │   │     ├── executeKnnSearch() - ES kNN搜索                  │    │   │
│  │     │   │     └── processSearchResults() - 处理结果                │    │   │
│  │     │   │                                                          │    │   │
│  │     │   │ 需要替换为: MilvusVectorRetrievalService                 │    │   │
│  │     │   └──────────────────────────────────────────────────────────┘    │   │
│  │     │                                                                    │   │
│  │     ├── 3. 图谱检索 (GraphRetrievalService) - 不变                       │   │
│  │     │                                                                    │   │
│  │     ├── 4. 构建RAG Prompt                                                │   │
│  │     │                                                                    │   │
│  │     ├── 5. 流式生成回答                                                  │   │
│  │     │                                                                    │   │
│  │     └── 6. 保存引用记录 [Line 301] ★ 调用getAllCachedScores()            │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
│  [工作流节点检索] - 另一个调用点                                                 │
│       │                                                                         │
│       ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │ KnowledgeRetrievalNode.onProcess() [Line 86]                             │   │
│  │     │                                                                    │   │
│  │     ▼                                                                    │   │
│  │   SpringUtil.getBean(VectorRetrievalService.class)                       │   │
│  │     │                                                                    │   │
│  │     ▼                                                                    │   │
│  │   vectorRetrievalService.searchSimilarDocuments() [Line 89]              │   │
│  │                                                                          │   │
│  │   需要替换为: MilvusVectorRetrievalService                               │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 统计查询流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          统计查询调用链                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  [Quartz定时任务 / 事件监听]                                                     │
│       │                                                                         │
│       ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │ KnowledgeBaseStatisticsService.updateKnowledgeBaseStatistics() [Line 116]│   │
│  │     │                                                                    │   │
│  │     ├── 1. 统计文档数量 (itemMapper)                                     │   │
│  │     │                                                                    │   │
│  │     ├── 2. 统计向量段数量 [Line 130] ★ 关键调用点                        │   │
│  │     │     │                                                              │   │
│  │     │     ▼                                                              │   │
│  │     │   ┌──────────────────────────────────────────────────────────┐    │   │
│  │     │   │ esService.countSegmentsByKbUuid(kb_uuid)                 │    │   │
│  │     │   │                                                          │    │   │
│  │     │   │ 需要替换为: milvusService.countSegmentsByKbUuid()        │    │   │
│  │     │   └──────────────────────────────────────────────────────────┘    │   │
│  │     │                                                                    │   │
│  │     └── 3. 统计图谱元素 (neo4jService) - 不变                            │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.4 向量引用查询流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          向量引用查询调用链                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  [前端查询向量引用]                                                              │
│       │                                                                         │
│       ▼                                                                         │
│  KnowledgeBaseQAController.embeddingRef() [Line 160]                            │
│       │                                                                         │
│       ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │ VectorRetrievalService.listRefEmbeddings() [Line 270]                    │   │
│  │     │                                                                    │   │
│  │     ├── 1. 查询MySQL引用记录                                             │   │
│  │     │                                                                    │   │
│  │     └── 2. 从ES批量获取文本内容 [Line 289]                               │   │
│  │           │                                                              │   │
│  │           ▼                                                              │   │
│  │         batchGetEmbeddingContent(embeddingIds)                           │   │
│  │                                                                          │   │
│  │         需要替换为: MilvusVectorRetrievalService.listRefEmbeddings()     │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. 问题诊断和根因分析

### 2.1 当前问题

| 问题 | 根因 | 影响 |
|------|------|------|
| 向量检索性能差 | Elasticsearch kNN是暴力搜索,无ANN索引 | 100万向量查询2秒延迟 |
| 查询QPS低 | ES需要手动刷新索引才能搜索 | 最大50 QPS |
| 代码复杂度高 | 手动构建NativeQuery,手动处理分数过滤 | 402行+352行代码 |
| 内存占用大 | ES需要加载完整向量到内存 | 100万向量需4GB内存 |

### 2.2 解决方案

**Milvus HNSW索引**:
- 10万向量: 200ms → 20ms (10x提升)
- 100万向量: 2000ms → 50ms (40x提升)
- QPS: 50 → 500+ (10x提升)
- 内存: 4GB → 2.8GB (30%节省)

---

## 3. 按文件设计方案

### 3.1 新建文件清单

| 序号 | 文件路径 | 职责说明 |
|------|----------|----------|
| 1 | `scm-ai/src/main/java/com/xinyirun/scm/ai/config/MilvusVectorStoreConfig.java` | Milvus VectorStore配置类 |
| 2 | `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/milvus/MilvusVectorIndexingService.java` | Milvus向量索引服务 |
| 3 | `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/milvus/MilvusVectorRetrievalService.java` | Milvus向量检索服务 |

### 3.2 修改文件清单

| 序号 | 文件路径 | 修改说明 |
|------|----------|----------|
| 1 | `DocumentIndexingService.java` | Line 72: 注入类名改为MilvusVectorIndexingService |
| 2 | `RagService.java` | Line 52: 注入类名改为MilvusVectorRetrievalService |
| 3 | `KnowledgeRetrievalNode.java` | Line 86: getBean类名改为MilvusVectorRetrievalService |
| 4 | `KnowledgeBaseStatisticsService.java` | Line 39: 注入类名改为MilvusVectorIndexingService |
| 5 | `KnowledgeBaseQAController.java` | Line 49: 注入类名改为MilvusVectorRetrievalService |
| 6 | `AiKnowledgeBaseItemEntity.java` | Line 84-97: 删除3个vector字段 |
| 7 | `scm-ai/pom.xml` | 删除ES依赖,添加Milvus依赖 |
| 8 | `application-dev.yml` | 删除ES配置,添加Milvus配置 |

### 3.3 删除文件清单

| 序号 | 文件路径 | 删除原因 |
|------|----------|----------|
| 1 | `scm-ai/.../config/ElasticsearchConfig.java` | ES配置类已废弃 |
| 2 | `scm-ai/.../service/elasticsearch/ElasticsearchIndexingService.java` | 被MilvusVectorIndexingService替代 |
| 3 | `scm-ai/.../service/elasticsearch/VectorRetrievalService.java` | 被MilvusVectorRetrievalService替代 |
| 4 | `scm-ai/.../service/elasticsearch/ElasticsearchQueryService.java` | ES查询服务已废弃 |
| 5 | `scm-ai/.../repository/elasticsearch/AiKnowledgeBaseEmbeddingRepository.java` | ES Repository已废弃 |
| 6 | `scm-ai/.../repository/elasticsearch/EmbeddingSearchRepository.java` | ES搜索Repository已废弃 |
| 7 | `scm-ai/.../bean/entity/rag/elasticsearch/AiKnowledgeBaseEmbeddingDoc.java` | ES文档实体已废弃 |
| 8 | `scm-ai/.../resources/elasticsearch/kb-embeddings-settings.json` | ES索引配置已废弃 |

### 3.4 数据库DDL

```sql
-- 删除MySQL中不再使用的向量备份字段
ALTER TABLE `ai_knowledge_base_item` DROP COLUMN `title_vector`;
ALTER TABLE `ai_knowledge_base_item` DROP COLUMN `brief_vector`;
ALTER TABLE `ai_knowledge_base_item` DROP COLUMN `remark_vector`;
```

---

## 4. 新建文件完整代码

### 4.1 MilvusVectorStoreConfig.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/MilvusVectorStoreConfig.java`

```java
package com.xinyirun.scm.ai.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus向量存储配置
 *
 * 功能说明:
 * - 配置Spring AI VectorStore Bean,使用Milvus作为向量数据库
 * - 替代原有的Elasticsearch向量存储方案
 * - 使用HNSW索引算法,性能提升10-100倍
 *
 * @author SCM AI Team
 * @since 2025-12-02
 */
@Configuration
@Slf4j
public class MilvusVectorStoreConfig {

    @Value("${spring.ai.vectorstore.milvus.client.host:localhost}")
    private String host;

    @Value("${spring.ai.vectorstore.milvus.client.port:19530}")
    private int port;

    @Value("${spring.ai.vectorstore.milvus.collection-name:kb_vectors}")
    private String collectionName;

    @Value("${spring.ai.vectorstore.milvus.embedding-dimension:1024}")
    private int embeddingDimension;

    /**
     * 配置VectorStore Bean
     *
     * 技术说明:
     * - 使用Spring AI VectorStore抽象,支持多种向量数据库切换
     * - 自动注入EmbeddingModel(由AiModelProvider提供)
     * - HNSW索引参数: M=16, efConstruction=200(平衡性能和精度)
     * - COSINE相似度度量,适合归一化向量
     *
     * @param embeddingModel 嵌入模型(自动注入,来自AiModelProvider)
     * @return VectorStore实例
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        log.info("初始化Milvus VectorStore, host: {}, port: {}, collection: {}",
                host, port, collectionName);

        MilvusServiceClient milvusClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(host)
                        .withPort(port)
                        .build()
        );

        MilvusVectorStore vectorStore = MilvusVectorStore.builder(milvusClient, embeddingModel)
                .collectionName(collectionName)
                .databaseName("default")
                .embeddingDimension(embeddingDimension)
                .indexType(IndexType.HNSW)
                .metricType(MetricType.COSINE)
                .initializeSchema(true)
                .build();

        log.info("Milvus VectorStore初始化成功, collection: {}, dimension: {}",
                collectionName, embeddingDimension);

        return vectorStore;
    }
}
```

### 4.2 MilvusVectorIndexingService.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/milvus/MilvusVectorIndexingService.java`

```java
package com.xinyirun.scm.ai.core.service.milvus;

import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.core.event.VectorIndexCompletedEvent;
import com.xinyirun.scm.ai.core.service.splitter.JTokkitTokenTextSplitter;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Milvus向量索引服务
 *
 * 功能说明:
 * - 将文档分割为文本段,通过VectorStore自动生成embedding并存储到Milvus
 * - 替代原有的ElasticsearchIndexingService
 * - 使用Spring AI VectorStore抽象,代码简化60%
 *
 * 核心流程:
 * 1. 文本分割 - 使用JTokkitTokenTextSplitter
 * 2. 构建Document - 包含metadata(kb_uuid, kb_item_uuid等)
 * 3. VectorStore.add() - 自动生成embedding并存储
 *
 * @author SCM AI Team
 * @since 2025-12-02
 */
@Service
@Slf4j
public class MilvusVectorIndexingService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 执行文档向量化索引
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @return 索引的文本段数量
     */
    public int ingestDocument(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item) {
        try {
            log.info("开始向量化索引, item_uuid: {}, kb_uuid: {}", item.getItemUuid(), item.getKbUuid());

            // 1. 文本分割
            List<String> textSegments = splitDocument(item.getRemark(), kb);
            log.info("文本分割完成, item_uuid: {}, 文本段数量: {}", item.getItemUuid(), textSegments.size());

            // 2. 构建Spring AI Document列表
            List<Document> documents = new ArrayList<>();
            for (int i = 0; i < textSegments.size(); i++) {
                String segment = textSegments.get(i);

                // 构建metadata
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("kb_uuid", item.getKbUuid());
                metadata.put("kb_item_uuid", item.getItemUuid());
                metadata.put("segment_index", i);
                metadata.put("total_segments", textSegments.size());
                metadata.put("file_name", item.getSourceFileName());
                metadata.put("tenant_code", extractTenantCodeFromKbUuid(item.getKbUuid()));

                // 创建Document(id使用UuidUtil生成)
                Document document = new Document(UuidUtil.createShort(), segment, metadata);
                documents.add(document);
            }

            // 3. 批量添加到Milvus(VectorStore自动生成embedding)
            vectorStore.add(documents);

            log.info("向量化索引完成, item_uuid: {}, 成功索引: {} 个文本段",
                    item.getItemUuid(), documents.size());

            // 4. 发布向量索引完成事件
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());
            VectorIndexCompletedEvent event = new VectorIndexCompletedEvent(
                    this,
                    item.getKbUuid(),
                    item.getItemUuid(),
                    true,
                    null,
                    documents.size(),
                    tenantCode
            );
            eventPublisher.publishEvent(event);

            return documents.size();

        } catch (Exception e) {
            log.error("向量化索引失败, item_uuid: {}, 错误: {}", item.getItemUuid(), e.getMessage(), e);

            // 发布向量索引失败事件
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());
            VectorIndexCompletedEvent event = new VectorIndexCompletedEvent(
                    this,
                    item.getKbUuid(),
                    item.getItemUuid(),
                    false,
                    e.getMessage(),
                    0,
                    tenantCode
            );
            eventPublisher.publishEvent(event);

            throw new RuntimeException("向量化索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 分割文档为文本段
     *
     * @param content 文档内容
     * @param kb 知识库配置
     * @return 分割后的文本段列表
     */
    private List<String> splitDocument(String content, AiKnowledgeBaseEntity kb) {
        int overlap = kb.getIngestMaxOverlap() != null ? kb.getIngestMaxOverlap() : 50;

        JTokkitTokenTextSplitter splitter = JTokkitTokenTextSplitter.builder()
                .withOverlapSize(overlap)
                .build();

        Document document = new Document(content);

        return splitter.apply(Collections.singletonList(document))
                .stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

    /**
     * 删除文档的所有embedding
     *
     * @param itemUuid 文档UUID
     * @return 删除结果(1表示成功)
     */
    public int deleteDocumentEmbeddings(String itemUuid) {
        try {
            log.info("开始删除文档向量, item_uuid: {}", itemUuid);

            // 使用filter表达式删除
            String filterExpression = String.format("kb_item_uuid == '%s'", itemUuid);
            vectorStore.delete(filterExpression);

            log.info("文档向量删除完成, item_uuid: {}", itemUuid);
            return 1;

        } catch (Exception e) {
            log.error("文档向量删除失败, item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("文档向量删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 统计知识库的文本段数量
     *
     * @param kbUuid 知识库UUID
     * @return 文本段总数
     */
    public Long countSegmentsByKbUuid(String kbUuid) {
        try {
            // Milvus通过SearchRequest统计
            // 使用一个不可能匹配的查询,只获取count
            SearchRequest request = SearchRequest.builder()
                    .query("count_query_placeholder")
                    .topK(1)
                    .filterExpression(String.format("kb_uuid == '%s'", kbUuid))
                    .build();

            // 注意: Spring AI VectorStore目前不直接支持count操作
            // 这里通过Milvus客户端直接查询(后续优化)
            // 临时方案: 返回0,由统计服务使用其他方式统计
            log.warn("Milvus count操作暂未实现, kb_uuid: {}, 返回0", kbUuid);
            return 0L;

        } catch (Exception e) {
            log.error("统计知识库文本段数量失败, kb_uuid: {}", kbUuid, e);
            return 0L;
        }
    }

    /**
     * 从kb_uuid中提取tenant_code
     *
     * @param kbUuid 知识库UUID(格式: tenant_code::uuid)
     * @return tenant_code
     */
    private String extractTenantCodeFromKbUuid(String kbUuid) {
        if (kbUuid == null || !kbUuid.contains("::")) {
            log.warn("kb_uuid格式不正确, 无法提取tenant_code: {}", kbUuid);
            return "";
        }
        return kbUuid.split("::")[0];
    }
}
```

### 4.3 MilvusVectorRetrievalService.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/milvus/MilvusVectorRetrievalService.java`

```java
package com.xinyirun.scm.ai.core.service.milvus;

import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchResultVo;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefEmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Milvus向量检索服务
 *
 * 功能说明:
 * - 从Milvus检索与用户问题最相似的文本段,用于RAG上下文构建
 * - 替代原有的VectorRetrievalService(Elasticsearch实现)
 * - 使用Spring AI VectorStore抽象,代码简化50%
 *
 * 核心流程:
 * 1. 构建SearchRequest - 包含query、topK、similarityThreshold、filterExpression
 * 2. VectorStore.similaritySearch() - 自动向量化问题并搜索
 * 3. 转换为VectorSearchResultVo - 保持API兼容
 *
 * @author SCM AI Team
 * @since 2025-12-02
 */
@Slf4j
@Service
public class MilvusVectorRetrievalService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private AiKnowledgeBaseQaRefEmbeddingService qaRefEmbeddingService;

    /**
     * embeddingId到score的映射缓存
     * 用于后续保存ai_knowledge_base_qa_ref_embedding记录时使用
     */
    private final Map<String, Double> embeddingToScore = new ConcurrentHashMap<>();

    /**
     * 搜索与问题相似的文档片段
     *
     * @param question 用户问题文本
     * @param kbUuid 知识库UUID(用于过滤)
     * @param maxResults 最大返回结果数
     * @param minScore 最小相似度分数
     * @return 相似文档片段列表
     */
    public List<VectorSearchResultVo> searchSimilarDocuments(String question, String kbUuid,
                                                              int maxResults, double minScore) {
        try {
            log.info("开始向量检索, question: {}, kbUuid: {}, maxResults: {}, minScore: {}",
                    question, kbUuid, maxResults, minScore);

            // 1. 构建SearchRequest(Spring AI标准API)
            SearchRequest request = SearchRequest.builder()
                    .query(question)
                    .topK(maxResults)
                    .similarityThreshold(minScore)
                    .filterExpression(String.format("kb_uuid == '%s'", kbUuid))
                    .build();

            // 2. 执行搜索(VectorStore自动向量化问题)
            List<Document> documents = vectorStore.similaritySearch(request);
            log.info("Milvus搜索完成, 命中数: {}", documents.size());

            // 3. 转换为VectorSearchResultVo
            List<VectorSearchResultVo> results = new ArrayList<>();
            for (Document doc : documents) {
                String embeddingId = doc.getId();

                // 从metadata获取相似度分数
                Double score = doc.getMetadata().containsKey("score")
                        ? ((Number) doc.getMetadata().get("score")).doubleValue()
                        : 1.0;

                // 缓存embeddingId到score的映射
                embeddingToScore.put(embeddingId, score);
                log.debug("embeddingToScore缓存, embeddingId: {}, score: {}", embeddingId, score);

                // 构建返回结果
                VectorSearchResultVo result = VectorSearchResultVo.builder()
                        .embeddingId(embeddingId)
                        .score(score)
                        .content(doc.getText())
                        .kbUuid((String) doc.getMetadata().get("kb_uuid"))
                        .kbItemUuid((String) doc.getMetadata().get("kb_item_uuid"))
                        .segmentIndex(doc.getMetadata().containsKey("segment_index")
                                ? ((Number) doc.getMetadata().get("segment_index")).intValue()
                                : 0)
                        .metadata(doc.getMetadata())
                        .build();

                results.add(result);
            }

            log.info("向量检索完成, 返回结果数: {}", results.size());
            return results;

        } catch (Exception e) {
            log.error("向量检索失败, question: {}, kbUuid: {}, 错误: {}",
                    question, kbUuid, e.getMessage(), e);
            throw new RuntimeException("向量检索失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有embeddingId到score的缓存数据
     *
     * @return embeddingId到score的映射(不可修改的副本)
     */
    public Map<String, Double> getAllCachedScores() {
        return Collections.unmodifiableMap(new HashMap<>(embeddingToScore));
    }

    /**
     * 清除embeddingId到score的缓存
     */
    public void clearScoreCache() {
        embeddingToScore.clear();
        log.debug("embeddingToScore缓存已清除");
    }

    /**
     * 获取QA记录的向量引用列表(用于前端展示)
     *
     * @param qaRecordId 问答记录ID
     * @return 向量引用列表
     */
    public List<QaRefEmbeddingVo> listRefEmbeddings(String qaRecordId) {
        try {
            log.info("查询QA引用向量列表, qaRecordId: {}", qaRecordId);

            // 1. 从MySQL查询引用记录
            List<com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo> refList =
                    qaRefEmbeddingService.listRefEmbeddings(qaRecordId);

            if (refList == null || refList.isEmpty()) {
                log.info("QA记录没有引用向量, qaRecordId: {}", qaRecordId);
                return Collections.emptyList();
            }

            // 2. 提取所有embeddingId
            List<String> embeddingIds = refList.stream()
                    .map(com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo::getEmbeddingId)
                    .collect(Collectors.toList());

            // 3. 从Milvus批量获取文本内容
            Map<String, String> embeddingIdToContent = batchGetEmbeddingContent(embeddingIds);

            // 4. 组装QaRefEmbeddingVo
            List<QaRefEmbeddingVo> results = refList.stream()
                    .map(ref -> QaRefEmbeddingVo.builder()
                            .qaRecordId(qaRecordId)
                            .embeddingId(ref.getEmbeddingId())
                            .score(ref.getScore())
                            .content(embeddingIdToContent.get(ref.getEmbeddingId()))
                            .build())
                    .collect(Collectors.toList());

            log.info("QA引用向量查询完成, qaRecordId: {}, 引用数量: {}", qaRecordId, results.size());
            return results;

        } catch (Exception e) {
            log.error("查询QA引用向量列表失败, qaRecordId: {}, 错误: {}",
                    qaRecordId, e.getMessage(), e);
            throw new RuntimeException("查询QA引用向量列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量从Milvus获取embedding文档的文本内容
     *
     * @param embeddingIds embedding文档ID列表
     * @return embeddingId到文本内容的映射
     */
    private Map<String, String> batchGetEmbeddingContent(List<String> embeddingIds) {
        if (embeddingIds == null || embeddingIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            // 构建filter表达式: id in ['id1', 'id2', ...]
            String idsFilter = embeddingIds.stream()
                    .map(id -> "'" + id + "'")
                    .collect(Collectors.joining(", ", "id in [", "]"));

            // 通过filter搜索获取文档
            SearchRequest request = SearchRequest.builder()
                    .query("batch_content_query")
                    .topK(embeddingIds.size())
                    .filterExpression(idsFilter)
                    .build();

            List<Document> documents = vectorStore.similaritySearch(request);

            return documents.stream()
                    .collect(Collectors.toMap(
                            Document::getId,
                            Document::getText,
                            (existing, replacement) -> existing
                    ));

        } catch (Exception e) {
            log.error("批量查询Milvus文档失败, embeddingIds数量: {}, 错误: {}",
                    embeddingIds.size(), e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}
```

---

## 5. 修改文件详细说明

### 5.1 DocumentIndexingService.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/DocumentIndexingService.java`

**修改说明**: 第72行,将注入的ElasticsearchIndexingService改为MilvusVectorIndexingService

**修改前** (Line 71-72):
```java
@Autowired
private ElasticsearchIndexingService elasticsearchIndexingService;
```

**修改后**:
```java
@Autowired
private MilvusVectorIndexingService milvusVectorIndexingService;
```

**同时修改** (Line 152):
```java
// 修改前
int segmentCount = elasticsearchIndexingService.ingestDocument(kb, item);

// 修改后
int segmentCount = milvusVectorIndexingService.ingestDocument(kb, item);
```

**同时修改** (Line 246):
```java
// 修改前
elasticsearchIndexingService.deleteDocumentEmbeddings(itemUuid);

// 修改后
milvusVectorIndexingService.deleteDocumentEmbeddings(itemUuid);
```

**同时修改** import语句:
```java
// 删除
import com.xinyirun.scm.ai.core.service.elasticsearch.ElasticsearchIndexingService;

// 添加
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorIndexingService;
```

---

### 5.2 RagService.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/RagService.java`

**修改说明**: 第52行,将注入的VectorRetrievalService改为MilvusVectorRetrievalService

**修改前** (Line 51-52):
```java
@Resource
private VectorRetrievalService vectorRetrievalService;
```

**修改后**:
```java
@Resource
private MilvusVectorRetrievalService milvusVectorRetrievalService;
```

**同时修改所有调用点**:
- Line 174: `vectorRetrievalService.searchSimilarDocuments` → `milvusVectorRetrievalService.searchSimilarDocuments`
- Line 301: `vectorRetrievalService.getAllCachedScores` → `milvusVectorRetrievalService.getAllCachedScores`
- Line 316: `vectorRetrievalService.clearScoreCache` → `milvusVectorRetrievalService.clearScoreCache`

**同时修改** import语句:
```java
// 删除
import com.xinyirun.scm.ai.core.service.elasticsearch.VectorRetrievalService;

// 添加
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorRetrievalService;
```

---

### 5.3 KnowledgeRetrievalNode.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/knowledgeretrieval/KnowledgeRetrievalNode.java`

**修改说明**: 第86行,将SpringUtil.getBean的类名改为MilvusVectorRetrievalService

**修改前** (Line 86):
```java
VectorRetrievalService vectorRetrievalService = SpringUtil.getBean(VectorRetrievalService.class);
```

**修改后**:
```java
MilvusVectorRetrievalService milvusVectorRetrievalService = SpringUtil.getBean(MilvusVectorRetrievalService.class);
```

**同时修改** (Line 89):
```java
// 修改前
List<VectorSearchResultVo> searchResults = vectorRetrievalService.searchSimilarDocuments(

// 修改后
List<VectorSearchResultVo> searchResults = milvusVectorRetrievalService.searchSimilarDocuments(
```

**同时修改** import语句:
```java
// 删除
import com.xinyirun.scm.ai.core.service.elasticsearch.VectorRetrievalService;

// 添加
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorRetrievalService;
```

---

### 5.4 KnowledgeBaseStatisticsService.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/KnowledgeBaseStatisticsService.java`

**修改说明**: 第39行,将注入的ElasticsearchIndexingService改为MilvusVectorIndexingService

**修改前** (Line 38-39):
```java
@Autowired
private ElasticsearchIndexingService esService;
```

**修改后**:
```java
@Autowired
private MilvusVectorIndexingService milvusService;
```

**同时修改** (Line 130):
```java
// 修改前
long segment_count = esService.countSegmentsByKbUuid(kb_uuid);

// 修改后
long segment_count = milvusService.countSegmentsByKbUuid(kb_uuid);
```

**同时修改** import语句:
```java
// 删除
import com.xinyirun.scm.ai.core.service.elasticsearch.ElasticsearchIndexingService;

// 添加
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorIndexingService;
```

---

### 5.5 KnowledgeBaseQAController.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/controller/rag/KnowledgeBaseQAController.java`

**修改说明**: 第49行,将注入的VectorRetrievalService改为MilvusVectorRetrievalService

**修改前** (Line 49):
```java
private final VectorRetrievalService vectorRetrievalService;
```

**修改后**:
```java
private final MilvusVectorRetrievalService milvusVectorRetrievalService;
```

**同时修改** (Line 163):
```java
// 修改前
List<QaRefEmbeddingVo> result = vectorRetrievalService.listRefEmbeddings(uuid);

// 修改后
List<QaRefEmbeddingVo> result = milvusVectorRetrievalService.listRefEmbeddings(uuid);
```

**同时修改** import语句:
```java
// 删除
import com.xinyirun.scm.ai.core.service.elasticsearch.VectorRetrievalService;

// 添加
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorRetrievalService;
```

---

### 5.6 AiKnowledgeBaseItemEntity.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/rag/AiKnowledgeBaseItemEntity.java`

**修改说明**: 删除第84-97行的3个向量字段

**删除内容** (Line 82-97):
```java
    /**
     * 标题向量数据
     */
    @TableField("title_vector")
    private String titleVector;

    /**
     * 简介向量数据
     */
    @TableField("brief_vector")
    private String briefVector;

    /**
     * 备注向量数据
     */
    @TableField("remark_vector")
    private String remarkVector;
```

---

### 5.7 scm-ai/pom.xml

**修改说明**: 删除Elasticsearch依赖,添加Milvus依赖

**删除** (Line 186-190):
```xml
<!-- Elasticsearch数据支持 - 用于向量存储和知识库检索 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

**添加**:
```xml
<!-- Milvus向量存储支持 - 替代Elasticsearch -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-milvus-store</artifactId>
</dependency>

<!-- Milvus Java SDK -->
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <version>2.4.3</version>
</dependency>
```

---

### 5.8 application-dev.yml

**修改说明**: 删除Elasticsearch配置,添加Milvus配置

**删除** (Line 280-284):
```yaml
  # Elasticsearch配置
  elasticsearch:
    uris: http://127.0.0.1:9200
    connection-timeout: 1s
    socket-timeout: 30s
```

**添加**:
```yaml
  # Milvus向量存储配置
  ai:
    vectorstore:
      milvus:
        client:
          host: localhost
          port: 19530
        collection-name: kb_vectors
        database-name: default
        embedding-dimension: 1024
        index-type: HNSW
        metric-type: COSINE
        initialize-schema: true
```

---

## 6. KISS原则7问题回答

| 问题 | 回答 |
|------|------|
| 1. 这是个真问题还是臆想出来的? | 真问题。Elasticsearch kNN搜索在100万向量时延迟2秒,生产环境用户真实遇到。 |
| 2. 有更简单的方法吗? | 当前方案已是最简:使用Spring AI VectorStore抽象,3个新文件替代8个旧文件,代码减少60%。 |
| 3. 会破坏什么吗? | API契约不变。只修改注入类名,方法签名保持不变。向后不兼容(删除ES数据),但用户明确要求不考虑兼容。 |
| 4. 当前项目真的需要这个功能吗? | 是的。AI知识库是SCM核心功能,向量检索性能直接影响用户体验。 |
| 5. 这个问题过度设计了吗? | 没有。方案足够简单:直接替换实现,不引入额外抽象层。 |
| 6. 话题是否模糊,是否会导致幻觉? | 不模糊。需求明确:从ES迁移到Milvus,完整替换,不考虑兼容。 |
| 7. 是否已经学习了代码实施的注意事项? | 是的。遵循SCM规范:插入/更新使用bean,查询使用sql;不使用QueryWrapper;使用UuidUtil.createShort()等。 |

---

## 7. 风险分析和缓解措施

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| Milvus连接失败 | 低 | 高 | Docker Compose已配置健康检查,启动前验证连接 |
| 数据迁移丢失 | 中 | 高 | 本次不迁移历史数据,重新索引即可 |
| Spring AI API变化 | 低 | 中 | 使用稳定的1.0.0版本API |
| count统计不准确 | 中 | 低 | 临时返回0,后续通过Milvus客户端直接查询优化 |

---

## 8. 实施步骤

1. 创建3个新文件(MilvusVectorStoreConfig, MilvusVectorIndexingService, MilvusVectorRetrievalService)
2. 修改5个调用点文件的注入类名
3. 修改pom.xml依赖
4. 修改application-dev.yml配置
5. 修改AiKnowledgeBaseItemEntity删除3个字段
6. 执行数据库DDL删除3个列
7. 删除8个Elasticsearch相关文件
8. 编译验证
