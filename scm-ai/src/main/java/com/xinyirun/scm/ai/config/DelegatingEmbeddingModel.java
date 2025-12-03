package com.xinyirun.scm.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;

/**
 * 委托模式EmbeddingModel
 *
 * 功能说明:
 * - 作为Spring Bean注入到VectorStore
 * - 实际调用时委托给AiModelProvider获取当前租户的EmbeddingModel
 * - 支持多租户动态配置
 *
 * 设计原因:
 * - Spring AI VectorStore需要在初始化时注入EmbeddingModel
 * - 但本系统的EmbeddingModel配置存储在数据库，按租户隔离
 * - 通过委托模式解决这个矛盾
 *
 * @author SCM AI Team
 * @since 2025-12-02
 */
@Slf4j
public class DelegatingEmbeddingModel implements EmbeddingModel {

    private final AiModelProvider aiModelProvider;

    public DelegatingEmbeddingModel(AiModelProvider aiModelProvider) {
        this.aiModelProvider = aiModelProvider;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        EmbeddingModel delegate = aiModelProvider.getEmbeddingModel();
        return delegate.call(request);
    }

    @Override
    public float[] embed(String text) {
        EmbeddingModel delegate = aiModelProvider.getEmbeddingModel();
        return delegate.embed(text);
    }

    @Override
    public float[] embed(Document document) {
        EmbeddingModel delegate = aiModelProvider.getEmbeddingModel();
        return delegate.embed(document);
    }

    @Override
    public List<float[]> embed(List<String> texts) {
        EmbeddingModel delegate = aiModelProvider.getEmbeddingModel();
        return delegate.embed(texts);
    }

    @Override
    public int dimensions() {
        // BAAI/bge-m3 固定为 1024 维
        return 1024;
    }
}
