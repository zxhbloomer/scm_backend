package com.xinyirun.scm.ai.config;

import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI模型提供者（租户级工厂）
 *
 * <p>设计目标：</p>
 * <ul>
 *   <li>租户级配置隔离：每个租户使用自己的API Key和模型配置</li>
 *   <li>运行时动态获取：根据当前租户上下文从数据库读取配置</li>
 *   <li>支持多种LLM：DeepSeek、OpenAI、智谱AI等</li>
 *   <li>租户级缓存：避免重复创建，提升性能</li>
 * </ul>
 *
 * <p>租户库配置示例（每个租户库的ai_config表）：</p>
 * <pre>
 * -- RAG ChatModel配置
 * RAG_PROVIDER            | DeepSeek
 * RAG_DEEPSEEK_API_KEY    | sk-tenant-specific-key
 * RAG_DEEPSEEK_API_BASE   | https://api.deepseek.com
 * RAG_DEEPSEEK_MODEL      | deepseek-chat
 *
 * -- Embedding配置
 * EMBEDDING_PROVIDER              | siliconflow
 * EMBEDDING_SILICONFLOW_API_KEY   | sk-tenant-specific-key
 * EMBEDDING_SILICONFLOW_API_BASE  | https://api.siliconflow.cn/v1
 * EMBEDDING_SILICONFLOW_MODEL     | BAAI/bge-m3
 * </pre>
 *
 * <p>使用方式：</p>
 * <pre>
 * // 在Service中注入
 * {@code @Autowired}
 * private AiModelProvider aiModelProvider;
 *
 * // 使用时动态获取（自动读取当前租户配置）
 * ChatModel chatModel = aiModelProvider.getChatModel();
 * EmbeddingModel embeddingModel = aiModelProvider.getEmbeddingModel();
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-10
 */
@Slf4j
@Component
public class AiModelProvider {

    @Autowired
    private AiModelConfigService aiModelConfigService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 租户级ChatModel缓存（默认模型）
     * key: 数据源名称（租户标识）
     * value: ChatModel实例
     */
    private final Map<String, ChatModel> chatModelCache = new ConcurrentHashMap<>();

    /**
     * 租户级ChatModel缓存（按模型名称）
     * key: 数据源名称::模型名称（例如：scm_tenant_20250519_001::gpt-3.5-turbo）
     * value: ChatModel实例
     */
    private final Map<String, ChatModel> namedChatModelCache = new ConcurrentHashMap<>();

    /**
     * 租户级EmbeddingModel缓存
     * key: 数据源名称（租户标识）
     * value: EmbeddingModel实例
     */
    private final Map<String, EmbeddingModel> embeddingModelCache = new ConcurrentHashMap<>();

    /**
     * 获取当前租户的ChatModel（用于RAG对话生成）
     *
     * <p>租户上下文由系统拦截器自动设置，无需手动处理</p>
     *
     * @return ChatModel实例
     */
    public ChatModel getChatModel() {
        String cacheKey = DataSourceHelper.getCurrentDataSourceName();
        return chatModelCache.computeIfAbsent(cacheKey, this::createChatModel);
    }

    /**
     * 获取当前租户的指定模型ChatModel（用于工作流节点指定模型场景）
     *
     * <p>租户上下文由系统拦截器自动设置，无需手动处理</p>
     *
     * @param modelName 模型名称（例如：gpt-3.5-turbo、deepseek-chat）
     * @return ChatModel实例
     */
    public ChatModel getChatModelByName(String modelName) {
        log.info("===== AiModelProvider.getChatModelByName 被调用 =====");
        log.info("接收到的 modelName 参数: [{}]", modelName);

        String tenantId = DataSourceHelper.getCurrentDataSourceName();
        log.info("当前租户ID: [{}]", tenantId);

        String cacheKey = tenantId + "::" + modelName;
        log.info("缓存Key: [{}]", cacheKey);

        return namedChatModelCache.computeIfAbsent(cacheKey, key -> {
            log.info("缓存中未找到，开始创建新的ChatModel实例");
            log.info("为租户 [{}] 创建指定模型 [{}] 的ChatModel", tenantId, modelName);

            AiModelConfigVo config = aiModelConfigService.getModelConfigByName(modelName);
            log.info("从数据库查询到的模型配置: id={}, name={}, modelName={}, provider={}, baseUrl={}",
                config.getId(), config.getName(), config.getModelName(), config.getProvider(), config.getBaseUrl());

            ChatModel chatModel = buildChatModel(config);
            log.info("ChatModel实例创建成功");

            return chatModel;
        });
    }

    /**
     * 获取当前租户的EmbeddingModel（用于文本向量化）
     *
     * <p>租户上下文由系统拦截器自动设置，无需手动处理</p>
     *
     * @return EmbeddingModel实例
     */
    public EmbeddingModel getEmbeddingModel() {
        String cacheKey = DataSourceHelper.getCurrentDataSourceName();
        return embeddingModelCache.computeIfAbsent(cacheKey, this::createEmbeddingModel);
    }

    /**
     * 清除指定租户的AI模型缓存
     *
     * <p>使用场景：租户修改AI配置后调用，清除缓存使新配置生效</p>
     *
     * @param tenantId 租户标识（数据源名称）
     */
    public void clearCache(String tenantId) {
        chatModelCache.remove(tenantId);
        embeddingModelCache.remove(tenantId);

        // 清除指定租户的所有命名模型缓存
        namedChatModelCache.keySet().removeIf(key -> key.startsWith(tenantId + "::"));

        log.info("清除租户 {} 的AI模型缓存", tenantId);
    }

    /**
     * 清除所有租户的AI模型缓存
     */
    public void clearAllCache() {
        chatModelCache.clear();
        namedChatModelCache.clear();
        embeddingModelCache.clear();
        log.info("清除所有租户的AI模型缓存");
    }

    /**
     * 创建ChatModel（从当前租户库读取配置）
     *
     * @param cacheKey 缓存键（租户标识）
     * @return ChatModel实例
     */
    private ChatModel createChatModel(String cacheKey) {
        log.info("为租户 {} 创建ChatModel", cacheKey);

        // 获取默认LLM模型配置
        AiModelConfigVo config = aiModelConfigService.getDefaultModelConfigWithKey("LLM");

        log.info("ChatModel配置: provider={}, model={}, baseUrl={}",
            config.getProvider(), config.getModelName(), config.getBaseUrl());

        return buildChatModel(config);
    }

    /**
     * 根据模型配置构建ChatModel实例
     *
     * @param config 模型配置
     * @return ChatModel实例
     */
    private ChatModel buildChatModel(AiModelConfigVo config) {
        // 使用 OpenAI 兼容 API 创建 ChatModel
        // 大部分 AI provider（SiliconFlow、DeepSeek、Moonshot、智谱AI等）都兼容 OpenAI API
        OpenAiApi openAiApi = OpenAiApi.builder()
            .apiKey(config.getApiKey())
            .baseUrl(config.getBaseUrl())
            .build();

        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .defaultOptions(OpenAiChatOptions.builder()
                .model(config.getModelName())
                .temperature(config.getTemperature().doubleValue())
                .maxTokens(config.getMaxTokens())
                .topP(config.getTopP().doubleValue())
                .build())
            .build();
    }

    /**
     * 创建EmbeddingModel（从当前租户库读取配置）
     *
     * @param cacheKey 缓存键（租户标识）
     * @return EmbeddingModel实例
     */
    private EmbeddingModel createEmbeddingModel(String cacheKey) {
        log.info("为租户 {} 创建EmbeddingModel", cacheKey);

        // 获取默认EMBEDDING模型配置
        AiModelConfigVo config = aiModelConfigService.getDefaultModelConfigWithKey("EMBEDDING");

        log.info("EmbeddingModel配置: provider={}, model={}, baseUrl={}",
            config.getProvider(), config.getModelName(), config.getBaseUrl());

        if ("siliconflow".equalsIgnoreCase(config.getProvider())) {
            return new SiliconFlowEmbeddingModel(
                config.getBaseUrl(),
                config.getApiKey(),
                config.getModelName(),
                restTemplate
            );
        }

        throw new IllegalStateException("不支持的Embedding provider: " + config.getProvider());
    }

    /**
     * RestTemplate Bean（用于HTTP调用）
     */
    @Autowired(required = false)
    public void setRestTemplate(RestTemplate restTemplate) {
        if (restTemplate == null) {
            this.restTemplate = new RestTemplate();
        }
    }
}
