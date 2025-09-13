/*
 * SCM AI Module - Vector Store Config
 * Adapted from ByteDesk AI Module for SCM System
 */
package com.xinyirun.scm.ai.springai.config;

import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
// import org.springframework.ai.vectorstore.elasticsearch.MetadataField; // API兼容性问题，暂时注释
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xinyirun.scm.ai.constant.LlmConsts;
import lombok.extern.slf4j.Slf4j;

/**
 * 向量存储配置类
 * 为不同的向量存储服务提供配置
 */
@Slf4j
@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.vectorstore.elasticsearch.index-name}")
    private String elasticsearchIndexName;

    @Value("${spring.ai.vectorstore.elasticsearch.dimensions}")
    private Integer elasticsearchDimensions;

    /**
     * Elasticsearch向量存储配置
     * 只有当 embedding 模型可用且 elasticsearch 启用时才创建
     */
    @Bean("elasticsearchVectorStore")
    @ConditionalOnProperty(prefix = "spring.ai.vectorstore.elasticsearch", name = "enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnBean(EmbeddingModel.class)
    public ElasticsearchVectorStore elasticsearchVectorStore(RestClient restClient, EmbeddingModel embeddingModel) {
        
        log.info("正在配置Elasticsearch向量存储，索引名: {}，维度数: {}", 
                elasticsearchIndexName, elasticsearchDimensions);
        
        // TODO: MetadataField在Spring AI 1.0.1中API有变化，暂时简化配置
        // 待API兼容性问题解决后，重新启用metadata fields配置
        
        // 创建选项对象
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        options.setIndexName(elasticsearchIndexName);
        options.setDimensions(elasticsearchDimensions);
        // 智谱embedding-v2模型，固定维度为1024
        // ollama bgm-m3模型，固定维度为1024
        // 根据配置动态设置维度，而不是硬编码
        
        // 使用正确的builder方法调用，提供必需的RestClient和EmbeddingModel参数
        // 暂时不使用metadataFields，等API兼容性问题解决
        ElasticsearchVectorStore vectorStore = ElasticsearchVectorStore.builder(restClient, embeddingModel)
                .options(options)
                .initializeSchema(true)
                .build();
        
        return vectorStore;
    }
}