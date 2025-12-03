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
 * - 使用HNSW索引算法,高效的向量相似度搜索
 * - EmbeddingModel使用委托模式,支持多租户动态配置
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
     * 配置EmbeddingModel Bean(委托模式)
     *
     * 技术说明:
     * - 使用DelegatingEmbeddingModel作为Spring Bean
     * - 实际调用时委托给AiModelProvider获取当前租户的模型
     * - 支持多租户动态配置(从数据库读取)
     *
     * @param aiModelProvider AI模型提供者
     * @return EmbeddingModel实例
     */
    @Bean
    public EmbeddingModel embeddingModel(AiModelProvider aiModelProvider) {
        log.info("初始化DelegatingEmbeddingModel(多租户委托模式)");
        return new DelegatingEmbeddingModel(aiModelProvider);
    }

    /**
     * 配置VectorStore Bean
     *
     * 技术说明:
     * - 使用Spring AI VectorStore抽象,支持多种向量数据库切换
     * - 注入DelegatingEmbeddingModel(支持多租户)
     * - HNSW索引参数: M=16, efConstruction=200(平衡性能和精度)
     * - COSINE相似度度量,适合归一化向量
     *
     * @param embeddingModel 嵌入模型(DelegatingEmbeddingModel)
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
