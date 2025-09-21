package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.engine.ChatToolEngine;
import com.xinyirun.scm.ai.engine.common.AIChatOptions;
import com.xinyirun.scm.ai.bean.dto.request.AiChatRequest;
import com.xinyirun.scm.ai.bean.entity.AiConversationContent;
// import com.xinyirun.scm.ai.core.mapper.AiConversationContentMapper; // TODO: 待创建
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AI聊天基础服务类
 *
 * 提供AI聊天的核心功能，包括：
 * 1. 普通聊天功能（不保存上下文）
 * 2. 记忆聊天功能（保存对话上下文）
 * 3. 对话内容持久化管理
 * 4. AI模型配置管理
 *
 * 从MeterSphere迁移而来，适配scm-ai模块的架构
 *
 * @Author: jianxing (原作者)
 * @CreateTime: 2025-05-28  13:44 (原创建时间)
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AiChatBaseService {

    @Resource
    MessageChatMemoryAdvisor messageChatMemoryAdvisor;

    // TODO: 待创建AiConversationContentMapper
    // @Resource
    // private AiConversationContentMapper aiConversationContentMapper;

    /**
     * AI聊天选项配置内部类
     * 用于封装聊天请求的配置参数
     */
    public static class AIChatOption {
        private String prompt;
        private String system;
        private String conversationId;
        private String modelProvider;
        private String modelName;
        private Double temperature;
        private Integer maxTokens;

        // Getters and Setters
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }

        public String getSystem() { return system; }
        public void setSystem(String system) { this.system = system; }

        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }

        public String getModelProvider() { return modelProvider; }
        public void setModelProvider(String modelProvider) { this.modelProvider = modelProvider; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }

        public Integer getMaxTokens() { return maxTokens; }
        public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    }

    /**
     * 将AiChatRequest转换为AIChatOption
     *
     * @param request 聊天请求
     * @param conversationId 对话ID
     * @return AI聊天选项配置
     */
    public AIChatOption buildAIChatOption(AiChatRequest request, String conversationId) {
        AIChatOption option = new AIChatOption();
        option.setPrompt(request.getMessage());
        option.setSystem(request.getSystem_prompt());
        option.setConversationId(conversationId);
        option.setModelProvider(request.getModel_provider());
        option.setModelName(request.getModel_name());
        option.setTemperature(request.getTemperature());
        option.setMaxTokens(request.getMax_tokens());
        return option;
    }

    /**
     * 聊天不记忆对话
     *
     * 执行单次聊天，不保存对话上下文，适用于独立的问答场景
     *
     * @param aiChatOption 聊天选项配置
     * @return ChatClient响应规范
     */
    public ChatClient.CallResponseSpec chat(AIChatOption aiChatOption) {
        try {
            ChatToolEngine.Builder builder = getBuilderFromOption(aiChatOption);
            return builder.getChatClient()
                    .prompt()
                    .user(aiChatOption.getPrompt())
                    .call();
        } catch (Exception e) {
            log.error("AI聊天失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI聊天失败", e);
        }
    }

    /**
     * 聊天并记忆
     *
     * 执行聊天并保存对话上下文，支持连续对话和上下文理解
     * 使用MessageChatMemoryAdvisor来管理对话记忆
     *
     * @param aiChatOption 聊天选项配置，必须包含conversationId
     * @return ChatClient响应规范
     */
    public ChatClient.CallResponseSpec chatWithMemory(AIChatOption aiChatOption) {
        try {
            ChatToolEngine.Builder builder = getBuilderFromOption(aiChatOption);
            ChatClient chatClient = builder.getChatClient();

            if (StringUtils.isNotBlank(aiChatOption.getSystem())) {
                return chatClient
                        .prompt()
                        .system(aiChatOption.getSystem())
                        .user(aiChatOption.getPrompt())
                        .advisors(messageChatMemoryAdvisor)
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId()))
                        .call();
            }
            return chatClient
                    .prompt()
                    .user(aiChatOption.getPrompt())
                    .advisors(messageChatMemoryAdvisor)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId()))
                    .call();
        } catch (Exception e) {
            log.error("AI记忆聊天失败: {}", e.getMessage(), e);
            throw new RuntimeException("AI记忆聊天失败", e);
        }
    }

    /**
     * 根据选项创建ChatToolEngine Builder
     *
     * @param option 聊天选项配置
     * @return ChatToolEngine Builder
     */
    private ChatToolEngine.Builder getBuilderFromOption(AIChatOption option) {
        // 构建AI聊天选项
        AIChatOptions aiChatOptions = new AIChatOptions();
        aiChatOptions.setModelType(option.getModelName());

        // 根据现有配置设置其他参数
        if (option.getTemperature() != null) {
            aiChatOptions.setTemperature(option.getTemperature());
        }
        if (option.getMaxTokens() != null) {
            aiChatOptions.setMaxTokens(option.getMaxTokens());
        }

        // 使用默认的模型提供商，如果没有指定
        String modelProvider = StringUtils.isNotBlank(option.getModelProvider())
                ? option.getModelProvider()
                : "openai"; // 默认使用OpenAI

        return ChatToolEngine.builder(modelProvider, aiChatOptions);
    }

    /**
     * 保存用户对话内容
     *
     * 使用独立事务保存用户消息，避免主事务回滚影响
     *
     * @param conversationId 对话ID
     * @param content 消息内容
     * @return 保存的对话内容实体
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public AiConversationContent saveUserConversationContent(String conversationId, String content) {
        return saveConversationContent(conversationId, content, AiConversationContent.TYPE_USER);
    }

    /**
     * 保存助手对话内容
     *
     * 使用独立事务保存AI回复消息，避免主事务回滚影响
     *
     * @param conversationId 对话ID
     * @param content 消息内容
     * @return 保存的对话内容实体
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public AiConversationContent saveAssistantConversationContent(String conversationId, String content) {
        return saveConversationContent(conversationId, content, AiConversationContent.TYPE_AI);
    }

    /**
     * 保存对话内容的通用方法
     *
     * @param conversationId 对话ID
     * @param content 消息内容
     * @param type 消息类型（USER/AI）
     * @return 保存的对话内容实体
     */
    private AiConversationContent saveConversationContent(String conversationId, String content, String type) {
        AiConversationContent aiConversationContent = new AiConversationContent();
        aiConversationContent.setId(generateId());
        aiConversationContent.setConversation_id(conversationId);
        aiConversationContent.setContent(content);
        aiConversationContent.setType(type);
        aiConversationContent.setCreate_time(System.currentTimeMillis());
        aiConversationContent.setDefaults(); // 设置默认值

        // TODO: 待创建Mapper后启用
        // aiConversationContentMapper.insert(aiConversationContent);
        log.info("保存对话内容: conversationId={}, type={}, contentLength={}",
                conversationId, type, content.length());

        return aiConversationContent;
    }

    /**
     * 生成唯一ID
     * TODO: 后续可以替换为统一的ID生成器
     *
     * @return 唯一ID
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 执行简单聊天（基于现有AiChatRequest结构）
     *
     * @param request 聊天请求
     * @return 聊天回复内容
     */
    public String executeSimpleChat(AiChatRequest request) {
        String conversationId = request.getConversation_id() != null
                ? request.getConversation_id().toString()
                : generateId();

        AIChatOption option = buildAIChatOption(request, conversationId);

        try {
            // 保存用户消息
            saveUserConversationContent(conversationId, request.getMessage());

            // 执行聊天
            String response;
            if (conversationId != null && !conversationId.isEmpty()) {
                response = chatWithMemory(option).content();
            } else {
                response = chat(option).content();
            }

            // 保存AI回复
            saveAssistantConversationContent(conversationId, response);

            return response;
        } catch (Exception e) {
            log.error("执行聊天失败: {}", e.getMessage(), e);
            throw new RuntimeException("聊天执行失败: " + e.getMessage(), e);
        }
    }
}