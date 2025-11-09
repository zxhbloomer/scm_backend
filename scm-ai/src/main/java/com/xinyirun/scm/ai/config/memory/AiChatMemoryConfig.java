package com.xinyirun.scm.ai.config.memory;

import com.xinyirun.scm.ai.config.AiModelProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * AI聊天记忆配置类
 *
 * 提供Chat和Workflow两个领域的MessageChatMemoryAdvisor Bean配置
 * 实现领域隔离和依赖解耦
 *
 * @Author: jianxing (原作者)
 * @CreateTime: 2025-05-27  18:36 (原创建时间)
 * @Migration: 2025-09-21 (迁移到scm-ai)
 * @Update: 2025-01-08 (领域拆分重构)
 */
@Configuration
public class AiChatMemoryConfig {

    /**
     * Chat领域消息聊天记忆顾问
     *
     * 用于管理Chat领域的AI对话上下文记忆，支持多轮对话的连续性
     * 查询 ai_conversation_content 表
     *
     * @param chatMessageChatMemory Chat领域聊天记忆实现
     * @return MessageChatMemoryAdvisor实例
     */
    @Bean("chatMessageChatMemoryAdvisor")
    public MessageChatMemoryAdvisor chatMessageChatMemoryAdvisor(ScmChatMessageChatMemory chatMessageChatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMessageChatMemory).build();
    }

    /**
     * Workflow领域消息聊天记忆顾问
     *
     * 用于管理Workflow领域的AI对话上下文记忆，支持多轮对话的连续性
     * 查询 ai_workflow_conversation_content 表
     *
     * @param workflowMessageChatMemory Workflow领域聊天记忆实现
     * @return MessageChatMemoryAdvisor实例
     */
    @Bean("workflowMessageChatMemoryAdvisor")
    public MessageChatMemoryAdvisor workflowMessageChatMemoryAdvisor(ScmWorkflowMessageChatMemory workflowMessageChatMemory) {
        return MessageChatMemoryAdvisor.builder(workflowMessageChatMemory).build();
    }

    /**
     * Chat领域专属ChatClient
     *
     * 为Chat领域提供独立的ChatClient实例
     * 注意：不在此处配置defaultAdvisors，而是在运行时动态添加advisor和conversationId参数
     *
     * 使用@Lazy延迟初始化，避免启动时因租户上下文未设置导致无法获取模型配置
     *
     * @param aiModelProvider AI模型提供者，用于获取ChatModel实例
     * @return 配置好的ChatClient实例
     */
    @Lazy
    @Bean("chatDomainChatClient")
    public ChatClient chatDomainChatClient(AiModelProvider aiModelProvider) {
        ChatModel chatModel = aiModelProvider.getChatModel();
        return ChatClient.builder(chatModel).build();
    }

    /**
     * Workflow领域专属ChatClient
     *
     * 为Workflow领域提供独立的ChatClient实例，配置默认Advisors：
     * - workflowMessageChatMemoryAdvisor: 读取历史对话（通过conversationId）
     * - workflowConversationAdvisor: 保存新对话（通过runtime_uuid参数）
     *
     * 运行时需要通过advisors()传递参数：
     * - ChatMemory.CONVERSATION_ID: 对话ID
     * - WorkflowConversationAdvisor.RUNTIME_UUID: 运行时UUID
     *
     * 使用@Lazy延迟初始化，避免启动时因租户上下文未设置导致无法获取模型配置
     *
     * @param aiModelProvider AI模型提供者，用于获取ChatModel实例
     * @param workflowMessageChatMemoryAdvisor Workflow对话历史读取Advisor
     * @param workflowConversationAdvisor Workflow对话保存Advisor
     * @return 配置好的ChatClient实例
     */
    @Lazy
    @Bean("workflowDomainChatClient")
    public ChatClient workflowDomainChatClient(
            AiModelProvider aiModelProvider,
            MessageChatMemoryAdvisor workflowMessageChatMemoryAdvisor,
            WorkflowConversationAdvisor workflowConversationAdvisor) {
        ChatModel chatModel = aiModelProvider.getChatModel();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                    workflowMessageChatMemoryAdvisor,  // 读取历史对话
                    workflowConversationAdvisor        // 保存新对话
                )
                .build();
    }
}