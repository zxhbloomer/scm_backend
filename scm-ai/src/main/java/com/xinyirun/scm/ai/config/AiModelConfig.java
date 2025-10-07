package com.xinyirun.scm.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * AI 模型配置类
 * 配置 Ollama 本地模型用于知识库 RAG
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Configuration
public class AiModelConfig {

    @Value("${spring.ai.ollama.base-url:http://127.0.0.1:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model:qwen2.5:7b}")
    private String chatModel;

    @Value("${spring.ai.ollama.embedding.options.model:all-minilm:l6-v2}")
    private String embeddingModel;

    /**
     * Ollama API Bean
     * 使用 Builder 模式创建 OllamaApi（Spring AI 推荐方式）
     */
    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    /**
     * Ollama 聊天模型 Bean
     * 用于 RAG 问答生成
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "spring.ai.ollama.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
    public ChatModel ollamaChatModel(OllamaApi ollamaApi) {
        OllamaOptions options = OllamaOptions.builder()
                .model(chatModel)
                .temperature(0.7)
                .build();

        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(options)
                .build();
    }

    /**
     * Ollama 嵌入模型 Bean
     * 用于文本向量化（384维）
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "spring.ai.ollama.embedding", name = "enabled", havingValue = "true", matchIfMissing = false)
    public EmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi) {
        OllamaOptions options = OllamaOptions.builder()
                .model(embeddingModel)
                .build();

        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(options)
                .build();
    }
}
