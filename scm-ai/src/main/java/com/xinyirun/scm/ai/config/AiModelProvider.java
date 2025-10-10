package com.xinyirun.scm.ai.config;

import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.core.service.config.AiConfigService;
import com.xinyirun.scm.ai.engine.common.AIChatClient;
import com.xinyirun.scm.ai.engine.common.AIChatOptions;
import com.xinyirun.scm.ai.engine.holder.ChatClientHolder;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
    private AiConfigService aiConfigService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 租户级ChatModel缓存
     * key: 数据源名称（租户标识）
     * value: ChatModel实例
     */
    private final Map<String, ChatModel> chatModelCache = new ConcurrentHashMap<>();

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
        log.info("清除租户 {} 的AI模型缓存", tenantId);
    }

    /**
     * 清除所有租户的AI模型缓存
     */
    public void clearAllCache() {
        chatModelCache.clear();
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

        // 从当前租户库读取配置
        String provider = aiConfigService.getConfigValue("RAG_PROVIDER", "DeepSeek");
        String configKeyPrefix = "RAG_" + provider.toUpperCase().replace(" ", "");

        String apiKey = aiConfigService.getConfigValue(configKeyPrefix + "_API_KEY");
        String baseUrl = aiConfigService.getConfigValue(configKeyPrefix + "_API_BASE");
        String model = aiConfigService.getConfigValue(configKeyPrefix + "_MODEL");
        String temperature = aiConfigService.getConfigValue(configKeyPrefix + "_TEMPERATURE", "0.7");
        String maxTokens = aiConfigService.getConfigValue(configKeyPrefix + "_MAX_TOKENS", "2048");

        // 验证必要配置
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException(String.format(
                "租户未配置 %s_API_KEY，请在 ai_config 表中添加配置", configKeyPrefix));
        }

        log.info("RAG ChatModel配置: provider={}, model={}, baseUrl={}", provider, model, baseUrl);

        // 构建AIChatOptions
        AIChatOptions options = new AIChatOptions();
        options.setApiKey(apiKey);
        options.setBaseUrl(baseUrl);
        options.setModelType(model);
        options.setTemperature(Double.parseDouble(temperature));
        options.setMaxTokens(Integer.parseInt(maxTokens));

        // 通过ChatClientHolder动态创建ChatModel
        AIChatClient chatClient = ChatClientHolder.getChatClientByType(provider);
        if (chatClient == null) {
            throw new IllegalStateException("不支持的RAG provider: " + provider +
                "，支持的类型：DeepSeek, Open AI, ZhiPu AI");
        }

        return chatClient.chatModel(options);
    }

    /**
     * 创建EmbeddingModel（从当前租户库读取配置）
     *
     * @param cacheKey 缓存键（租户标识）
     * @return EmbeddingModel实例
     */
    private EmbeddingModel createEmbeddingModel(String cacheKey) {
        log.info("为租户 {} 创建EmbeddingModel", cacheKey);

        // 从当前租户库读取配置
        String provider = aiConfigService.getConfigValue("EMBEDDING_PROVIDER", "siliconflow");
        log.info("读取EMBEDDING_PROVIDER: {}", provider);

        if ("siliconflow".equalsIgnoreCase(provider)) {
            log.info("开始读取 EMBEDDING_SILICONFLOW_API_KEY，常量值: {}", AiConstant.EMBEDDING_SILICONFLOW_API_KEY);
            String apiKey = aiConfigService.getConfigValue(AiConstant.EMBEDDING_SILICONFLOW_API_KEY);
            log.info("读取EMBEDDING_SILICONFLOW_API_KEY结果: {}", apiKey != null ? ("已配置，长度=" + apiKey.length()) : "未配置(null)");

            String baseUrl = aiConfigService.getConfigValue(
                AiConstant.EMBEDDING_SILICONFLOW_API_BASE,
                "https://api.siliconflow.cn/v1"
            );
            log.info("读取EMBEDDING_SILICONFLOW_API_BASE: {}", baseUrl);

            String embeddingModel = aiConfigService.getConfigValue(
                AiConstant.EMBEDDING_SILICONFLOW_MODEL,
                "BAAI/bge-m3"
            );
            log.info("读取EMBEDDING_SILICONFLOW_MODEL: {}", embeddingModel);

            // 验证API Key是否配置
            if (!StringUtils.hasText(apiKey)) {
                log.error("租户 {} 未配置 EMBEDDING_SILICONFLOW_API_KEY", cacheKey);
                throw new IllegalStateException("租户未配置 Embedding API Key");
            }

            log.info("Embedding配置: provider={}, model={}, baseUrl={}", provider, embeddingModel, baseUrl);

            return new SiliconFlowEmbeddingModel(
                baseUrl,
                apiKey,
                embeddingModel,
                restTemplate
            );
        }

        throw new IllegalStateException("不支持的Embedding provider: " + provider);
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
