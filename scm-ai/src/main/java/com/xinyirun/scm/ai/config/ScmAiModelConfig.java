package com.xinyirun.scm.ai.config;

import com.xinyirun.scm.ai.config.properties.ScmAiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@EnableConfigurationProperties(ScmAiProperties.class)
public class ScmAiModelConfig {

    @Autowired
    private ScmAiProperties properties;

    /**
     * 主要聊天客户端（根据配置动态选择厂商）
     */
    @Bean
    @Primary
    public ChatClient primaryChatClient(
            @Autowired(required = false) OpenAiChatModel openAiChatModel,
            @Autowired(required = false) AnthropicChatModel anthropicChatModel,
            @Autowired(required = false) ZhiPuAiChatModel zhipuChatModel) {

        String provider = properties.getPrimaryProvider();
        log.info("配置主要AI厂商: {}", provider);

        ChatModel chatModel = selectChatModel(provider, openAiChatModel, anthropicChatModel, zhipuChatModel);

        if (chatModel == null) {
            log.warn("主要厂商 {} 不可用，使用默认厂商", provider);
            chatModel = getFirstAvailableModel(openAiChatModel, anthropicChatModel, zhipuChatModel);
        }

        return ChatClient.builder(chatModel)
                .defaultSystem("你是SCM进销存系统的AI助手，请用中文回答用户问题。" +
                        "回答要准确、简洁、专业。对于业务操作，请确认相关参数的准确性。")
                .build();
    }

    /**
     * 备用聊天客户端
     */
    @Bean("fallbackChatClient")
    public ChatClient fallbackChatClient(
            @Autowired(required = false) ZhiPuAiChatModel zhipuChatModel,
            @Autowired(required = false) OpenAiChatModel openAiChatModel,
            @Autowired(required = false) AnthropicChatModel anthropicChatModel) {

        String fallbackProvider = properties.getFallbackProvider();
        log.info("配置备用AI厂商: {}", fallbackProvider);

        ChatModel chatModel = selectChatModel(fallbackProvider, openAiChatModel, anthropicChatModel, zhipuChatModel);

        if (chatModel == null) {
            log.warn("备用厂商 {} 不可用，使用任意可用厂商", fallbackProvider);
            chatModel = getFirstAvailableModel(zhipuChatModel, openAiChatModel, anthropicChatModel);
        }

        return ChatClient.builder(chatModel)
                .defaultSystem("你是SCM进销存系统的备用AI助手，请用中文回答用户问题。")
                .build();
    }

    /**
     * 根据厂商名称选择对应的ChatModel
     */
    private ChatModel selectChatModel(String provider, ChatModel openAi, ChatModel anthropic, ChatModel zhipu) {
        if (provider == null) {
            return null;
        }

        return switch (provider.toLowerCase()) {
            case "openai" -> openAi;
            case "anthropic" -> anthropic;
            case "zhipuai", "zhipu" -> zhipu;
            default -> {
                log.warn("未知的AI厂商: {}", provider);
                yield null;
            }
        };
    }

    /**
     * 获取第一个可用的模型
     */
    private ChatModel getFirstAvailableModel(ChatModel... models) {
        for (ChatModel model : models) {
            if (model != null) {
                return model;
            }
        }
        throw new IllegalStateException("没有可用的AI厂商配置");
    }
}