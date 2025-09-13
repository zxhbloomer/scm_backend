/*
 * SCM AI Module - Chat Client Primary Config
 * Adapted from ByteDesk AI Module for SCM System
 */
package com.xinyirun.scm.ai.springai.config;

import org.springframework.ai.chat.client.ChatClient;
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
 * 根据spring.ai.model.chat配置动态设置Primary的ChatClient
 * 支持的值：zhipuai, ollama, dashscope, deepseek, baidu, tencent, volcengine, openai, openrouter, siliconflow, gitee, none
 */
@Slf4j
@Configuration
public class ChatClientPrimaryConfig {

    @Value("${spring.ai.model.chat:none}")
    private String chatModel;

    @Autowired(required = false)
    @Qualifier("scmZhipuaiChatClient")
    private ChatClient zhipuaiChatClient;

    @Autowired(required = false)
    @Qualifier("scmOllamaChatClient")
    private ChatClient ollamaChatClient;

    @Autowired(required = false)
    @Qualifier("scmDashscopeChatClient")
    private ChatClient dashscopeChatClient;

    @Autowired(required = false)
    @Qualifier("deepseekChatClient")
    private ChatClient deepseekChatClient;

    @Autowired(required = false)
    @Qualifier("baiduChatClient")
    private ChatClient baiduChatClient;

    @Autowired(required = false)
    @Qualifier("tencentChatClient")
    private ChatClient tencentChatClient;

    @Autowired(required = false)
    @Qualifier("volcengineChatClient")
    private ChatClient volcengineChatClient;

    @Autowired(required = false)
    @Qualifier("openaiChatClient")
    private ChatClient openaiChatClient;

    @Autowired(required = false)
    @Qualifier("openrouterChatClient")
    private ChatClient openrouterChatClient;

    @Autowired(required = false)
    @Qualifier("siliconFlowChatClient")
    private ChatClient siliconflowChatClient;

    @Autowired(required = false)
    @Qualifier("giteeChatClient")
    private ChatClient giteeChatClient;

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.ZHIPUAI)
    public ChatClient primaryZhipuaiChatClient() {
        log.info("设置智谱AI聊天客户端为主要客户端");
        if (zhipuaiChatClient == null) {
            throw new IllegalStateException("ZhiPuAI chat client is not available. Please check if spring.ai.zhipuai.chat.enabled=true");
        }
        return zhipuaiChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.OLLAMA)
    public ChatClient primaryOllamaChatClient() {
        log.info("设置Ollama聊天客户端为主要客户端");
        if (ollamaChatClient == null) {
            throw new IllegalStateException("Ollama chat client is not available. Please check if spring.ai.ollama.chat.enabled=true");
        }
        return ollamaChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.DASHSCOPE)
    public ChatClient primaryDashscopeChatClient() {
        log.info("设置通义千问聊天客户端为主要客户端");
        if (dashscopeChatClient == null) {
            throw new IllegalStateException("Dashscope chat client is not available. Please check if spring.ai.dashscope.chat.enabled=true");
        }
        return dashscopeChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.DEEPSEEK)
    public ChatClient primaryDeepseekChatClient() {
        log.info("设置DeepSeek聊天客户端为主要客户端");
        if (deepseekChatClient == null) {
            throw new IllegalStateException("Deepseek chat client is not available. Please check if spring.ai.deepseek.chat.enabled=true");
        }
        return deepseekChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.BAIDU)
    public ChatClient primaryBaiduChatClient() {
        log.info("设置百度千帆聊天客户端为主要客户端");
        if (baiduChatClient == null) {
            throw new IllegalStateException("Baidu chat client is not available. Please check if spring.ai.baidu.chat.enabled=true");
        }
        return baiduChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.TENCENT)
    public ChatClient primaryTencentChatClient() {
        log.info("设置腾讯混元聊天客户端为主要客户端");
        if (tencentChatClient == null) {
            throw new IllegalStateException("Tencent chat client is not available. Please check if spring.ai.tencent.chat.enabled=true");
        }
        return tencentChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.VOLCENGINE)
    public ChatClient primaryVolcengineChatClient() {
        log.info("设置火山引擎聊天客户端为主要客户端");
        if (volcengineChatClient == null) {
            throw new IllegalStateException("Volcengine chat client is not available. Please check if spring.ai.volcengine.chat.enabled=true");
        }
        return volcengineChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = SpringAIModels.OPENAI)
    public ChatClient primaryOpenaiChatClient() {
        log.info("设置OpenAI聊天客户端为主要客户端");
        if (openaiChatClient == null) {
            throw new IllegalStateException("OpenAI chat client is not available. Please check if spring.ai.openai.chat.enabled=true");
        }
        return openaiChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.OPENROUTER)
    public ChatClient primaryOpenrouterChatClient() {
        log.info("设置OpenRouter聊天客户端为主要客户端");
        if (openrouterChatClient == null) {
            throw new IllegalStateException("OpenRouter chat client is not available. Please check if spring.ai.openrouter.chat.enabled=true");
        }
        return openrouterChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.SILICONFLOW)
    public ChatClient primarySiliconflowChatClient() {
        log.info("设置硅基流动聊天客户端为主要客户端");
        if (siliconflowChatClient == null) {
            throw new IllegalStateException("SiliconFlow chat client is not available. Please check if spring.ai.siliconflow.chat.enabled=true");
        }
        return siliconflowChatClient;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = LlmConsts.GITEE)
    public ChatClient primaryGiteeChatClient() {
        log.info("设置Gitee AI聊天客户端为主要客户端");
        if (giteeChatClient == null) {
            throw new IllegalStateException("Gitee chat client is not available. Please check if spring.ai.gitee.chat.enabled=true");
        }
        return giteeChatClient;
    }

    // 当没有配置chat model时，不创建任何Primary bean，避免冲突
    // @Bean
    // @Primary
    // @ConditionalOnProperty(name = SpringAIModelProperties.CHAT_MODEL, havingValue = "none", matchIfMissing = true)
    // public ChatClient noPrimaryChatClient() {
    //     log.warn("No chat client configured as Primary. Set spring.ai.model.chat to 'zhipuai', 'ollama', 'dashscope', 'deepseek', 'baidu', 'tencent', 'volcengine', 'openai', 'openrouter', 'siliconflow', or 'gitee' to use chat features.");
    //     throw new IllegalStateException("No chat client configured. Please set spring.ai.model.chat to 'zhipuai', 'ollama', 'dashscope', 'deepseek', 'baidu', 'tencent', 'volcengine', 'openai', 'openrouter', 'siliconflow', or 'gitee'");
    // }
}