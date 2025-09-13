/*
 * SCM AI Module - Chat Model Primary Config
 * Adapted from ByteDesk AI Module for SCM System
 */
package com.xinyirun.scm.ai.springai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.SpringAIModelProperties;
import org.springframework.ai.model.SpringAIModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.xinyirun.scm.ai.constant.LlmConsts;

import lombok.extern.slf4j.Slf4j;

/**
 * 根据spring.ai.model.chat配置动态设置Primary的ChatModel
 * 支持的值：zhipuai, ollama, dashscope, deepseek, baidu, tencent, volcengine, openai, openrouter, siliconflow, gitee, none
 */
@Slf4j
@Configuration
public class ChatModelPrimaryConfig {

    @Value("${spring.ai.model.chat:none}")
    private String chatModel;

    @Autowired(required = false)
    @Qualifier("scmZhipuaiChatModel")
    private ChatModel zhipuaiChatModel;

    @Autowired(required = false)
    @Qualifier("scmOllamaChatModel")
    private ChatModel ollamaChatModel;

    @Autowired(required = false)
    @Qualifier("scmDashscopeChatModel")
    private ChatModel dashscopeChatModel;

    @Autowired(required = false)
    @Qualifier("deepseekChatModel")
    private ChatModel deepseekChatModel;

    @Autowired(required = false)
    @Qualifier("baiduChatModel")
    private ChatModel baiduChatModel;

    @Autowired(required = false)
    @Qualifier("tencentChatModel")
    private ChatModel tencentChatModel;

    @Autowired(required = false)
    @Qualifier("volcengineChatModel")
    private ChatModel volcengineChatModel;

    @Autowired(required = false)
    @Qualifier("openaiChatModel")
    private ChatModel openaiChatModel;

    @Autowired(required = false)
    @Qualifier("openrouterChatModel")
    private ChatModel openrouterChatModel;

    @Autowired(required = false)
    @Qualifier("siliconFlowChatModel")
    private ChatModel siliconflowChatModel;

    @Autowired(required = false)
    @Qualifier("giteeChatModel")
    private ChatModel giteeChatModel;

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.ZHIPUAI)
    public ChatModel primaryZhipuaiChatModel() {
        log.info("设置智谱AI聊天模型为主要模型");
        if (zhipuaiChatModel == null) {
            throw new IllegalStateException("ZhiPuAI chat model is not available. Please check if spring.ai.zhipuai.chat.enabled=true");
        }
        return zhipuaiChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.OLLAMA)
    public ChatModel primaryOllamaChatModel() {
        log.info("设置Ollama聊天模型为主要模型");
        if (ollamaChatModel == null) {
            throw new IllegalStateException("Ollama chat model is not available. Please check if spring.ai.ollama.chat.enabled=true");
        }
        return ollamaChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.DASHSCOPE)
    public ChatModel primaryDashscopeChatModel() {
        log.info("设置通义千问聊天模型为主要模型");
        if (dashscopeChatModel == null) {
            throw new IllegalStateException("Dashscope chat model is not available. Please check if spring.ai.dashscope.chat.enabled=true");
        }
        return dashscopeChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.DEEPSEEK)
    public ChatModel primaryDeepseekChatModel() {
        log.info("设置DeepSeek聊天模型为主要模型");
        if (deepseekChatModel == null) {
            throw new IllegalStateException("Deepseek chat model is not available. Please check if spring.ai.deepseek.chat.enabled=true");
        }
        return deepseekChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.BAIDU)
    public ChatModel primaryBaiduChatModel() {
        log.info("设置百度千帆聊天模型为主要模型");
        if (baiduChatModel == null) {
            throw new IllegalStateException("Baidu chat model is not available. Please check if spring.ai.baidu.chat.enabled=true");
        }
        return baiduChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.TENCENT)
    public ChatModel primaryTencentChatModel() {
        log.info("设置腾讯混元聊天模型为主要模型");
        if (tencentChatModel == null) {
            throw new IllegalStateException("Tencent chat model is not available. Please check if spring.ai.tencent.chat.enabled=true");
        }
        return tencentChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.VOLCENGINE)
    public ChatModel primaryVolcengineChatModel() {
        log.info("设置火山引擎聊天模型为主要模型");
        if (volcengineChatModel == null) {
            throw new IllegalStateException("Volcengine chat model is not available. Please check if spring.ai.volcengine.chat.enabled=true");
        }
        return volcengineChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.OPENAI)
    public ChatModel primaryOpenaiChatModel() {
        log.info("设置OpenAI聊天模型为主要模型");
        if (openaiChatModel == null) {
            throw new IllegalStateException("OpenAI chat model is not available. Please check if spring.ai.openai.chat.enabled=true");
        }
        return openaiChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.OPENROUTER)
    public ChatModel primaryOpenrouterChatModel() {
        log.info("设置OpenRouter聊天模型为主要模型");
        if (openrouterChatModel == null) {
            throw new IllegalStateException("OpenRouter chat model is not available. Please check if spring.ai.openrouter.chat.enabled=true");
        }
        return openrouterChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.SILICONFLOW)
    public ChatModel primarySiliconflowChatModel() {
        log.info("设置硅基流动聊天模型为主要模型");
        if (siliconflowChatModel == null) {
            throw new IllegalStateException("SiliconFlow chat model is not available. Please check if spring.ai.siliconflow.chat.enabled=true");
        }
        return siliconflowChatModel;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.GITEE)
    public ChatModel primaryGiteeChatModel() {
        log.info("设置Gitee AI聊天模型为主要模型");
        if (giteeChatModel == null) {
            throw new IllegalStateException("Gitee chat model is not available. Please check if spring.ai.gitee.chat.enabled=true");
        }
        return giteeChatModel;
    }

    // 当没有配置chat model时，不创建任何Primary bean，避免冲突
    // @Bean
    // @Primary
    // @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = "none", matchIfMissing = true)
    // public ChatModel noPrimaryChatModel() {
    //     log.warn("No chat model configured as Primary. Set spring.ai.model.chat to 'zhipuai', 'ollama', 'dashscope', 'deepseek', 'baidu', 'tencent', 'volcengine', 'openai', 'openrouter', 'siliconflow', or 'gitee' to use chat features.");
    //     throw new IllegalStateException("No chat model configured. Please set spring.ai.model.chat to 'zhipuai', 'ollama', 'dashscope', 'deepseek', 'baidu', 'tencent', 'volcengine', 'openai', 'openrouter', 'siliconflow', or 'gitee'");
    // }
}