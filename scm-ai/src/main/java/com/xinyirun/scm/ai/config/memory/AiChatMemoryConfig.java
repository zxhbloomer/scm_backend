package com.xinyirun.scm.ai.config.memory;

import com.xinyirun.scm.ai.config.AiModelProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;

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
 * @Update: 2025-01-17 (集成 MCP Client 工具回调)
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
     * @param toolCallbackProvider MCP Client工具回调提供者（可选，仅在MCP Client启用时注入）
     * @return 配置好的ChatClient实例
     */
    @Lazy
    @Bean("chatDomainChatClient")
    public ChatClient chatDomainChatClient(
            AiModelProvider aiModelProvider,
            @Autowired(required = false) ToolCallbackProvider toolCallbackProvider) {
        ChatModel chatModel = aiModelProvider.getChatModel();
        ChatClient.Builder builder = ChatClient.builder(chatModel);

        // 如果 MCP Client 已启用，注入工具回调
        if (toolCallbackProvider != null) {
            builder.defaultToolCallbacks(toolCallbackProvider);
        }

        return builder.build();
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
     * @param toolCallbackProvider MCP Client工具回调提供者（可选，仅在MCP Client启用时注入）
     * @return 配置好的ChatClient实例
     */
    @Lazy
    @Bean("workflowDomainChatClient")
    public ChatClient workflowDomainChatClient(
            AiModelProvider aiModelProvider,
            MessageChatMemoryAdvisor workflowMessageChatMemoryAdvisor,
            WorkflowConversationAdvisor workflowConversationAdvisor,
            @Autowired(required = false) ToolCallbackProvider toolCallbackProvider) {
        ChatModel chatModel = aiModelProvider.getChatModel();

        // MCP工具使用的全局约束提示词
        String mcpToolSystemPrompt = """
            在使用MCP工具时，请严格遵守以下要求：
            1. 不可以臆想、推测数据，必须基于工具返回的实际结果
            2. 不可以过度回复、不可以过度推测
            3. 如果你对任何方面不确定，或者无法取得必要信息，请说"我没有足够的信息来自信地评估这一点"
            4. 如果找不到相关引用，请说明"未找到相关引用"
            5. 找不到相关回答，请说明"未找到相关回答"
            6. 在回答找不到的情况时，不要过多拓展回复和过度回复
            """;

        ChatClient.Builder builder = ChatClient.builder(chatModel)
                .defaultSystem(mcpToolSystemPrompt)  // MCP工具全局约束
                .defaultAdvisors(
                    workflowMessageChatMemoryAdvisor,  // 读取历史对话
                    workflowConversationAdvisor        // 保存新对话
                );

        // 如果 MCP Client 已启用，注入工具回调
        if (toolCallbackProvider != null) {
            builder.defaultToolCallbacks(toolCallbackProvider);
        }

        return builder.build();
    }

    /**
     * 工作流路由专用ChatClient
     *
     * <p>用于LLM智能路由的专用ChatClient，配置为：</p>
     * <ul>
     *   <li>无历史记忆: 路由决策基于当前输入，不需要上下文</li>
     *   <li>无特殊参数: 使用默认模型配置</li>
     * </ul>
     *
     * 使用@Lazy延迟初始化，避免启动时因租户上下文未设置导致无法获取模型配置
     *
     * @param aiModelProvider AI模型提供者，用于获取ChatModel实例
     * @param toolCallbackProvider MCP Client工具回调提供者（可选，仅在MCP Client启用时注入）
     * @return 配置好的ChatClient实例
     */
    @Lazy
    @Bean("workflowRoutingChatClient")
    public ChatClient workflowRoutingChatClient(
            AiModelProvider aiModelProvider,
            @Autowired(required = false) ToolCallbackProvider toolCallbackProvider) {
        ChatModel chatModel = aiModelProvider.getChatModel();
        ChatClient.Builder builder = ChatClient.builder(chatModel);

        // 如果 MCP Client 已启用，注入工具回调
        if (toolCallbackProvider != null) {
            builder.defaultToolCallbacks(toolCallbackProvider);
        }

        return builder.build();
    }

    /**
     * 创建工作流领域的ChatClient Bean(无MCP工具版本)
     * 用于不需要MCP工具调用的场景,如生成回答节点等
     *
     * @param aiModelProvider AI模型提供者
     * @param workflowMessageChatMemoryAdvisor 工作流消息记忆顾问
     * @param workflowConversationAdvisor 工作流对话顾问
     * @return ChatClient实例(无MCP工具支持)
     */
    @Lazy
    @Bean("workflowDomainChatClientNoMcp")
    public ChatClient workflowDomainChatClientNoMcp(
            AiModelProvider aiModelProvider,
            MessageChatMemoryAdvisor workflowMessageChatMemoryAdvisor,
            WorkflowConversationAdvisor workflowConversationAdvisor) {
        ChatModel chatModel = aiModelProvider.getChatModel();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(
                        workflowMessageChatMemoryAdvisor,
                        workflowConversationAdvisor
                )
                .build(); // 不注入toolCallbackProvider,禁用MCP工具自动调用
    }

    /**
     * Orchestrator专用ChatClient
     *
     * <p>用于Orchestrator-Workers模式的任务分解LLM调用,配置为:</p>
     * <ul>
     *   <li>支持多轮对话记忆: 通过chatMessageChatMemoryAdvisor读取历史对话上下文</li>
     *   <li>无MCP工具: Orchestrator只负责任务分解,不直接调用工具</li>
     *   <li>结构化输出: 返回OrchestratorResponse格式(analysis + tasks列表)</li>
     * </ul>
     *
     * <p>运行时需要通过advisors()传递conversationId参数:</p>
     * <pre>
     * orchestratorChatClient.prompt()
     *     .user(prompt)
     *     .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
     *     .call()
     *     .entity(OrchestratorResponse.class);
     * </pre>
     *
     * 使用@Lazy延迟初始化,避免启动时因租户上下文未设置导致无法获取模型配置
     *
     * @param aiModelProvider AI模型提供者,用于获取ChatModel实例
     * @param chatMessageChatMemoryAdvisor Chat领域对话历史读取Advisor(复用现有memory)
     * @return 配置好的ChatClient实例
     */
    @Lazy
    @Bean("orchestratorChatClient")
    public ChatClient orchestratorChatClient(
            AiModelProvider aiModelProvider,
            MessageChatMemoryAdvisor chatMessageChatMemoryAdvisor) {
        ChatModel chatModel = aiModelProvider.getChatModel();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(chatMessageChatMemoryAdvisor)  // 支持多轮对话记忆
                .build();
    }

    /**
     * MCP工具回调Map
     *
     * <p>将所有MCP工具包装为Map,供Orchestrator-Workers模式的Worker执行时使用</p>
     * <ul>
     *   <li>Key: 工具名称(ToolDefinition.name())</li>
     *   <li>Value: ToolCallback实例</li>
     * </ul>
     *
     * 注意: 此Map仅包含MCP工具,不包含Workflow
     * Workflow通过WorkflowToolCallbackService动态查询,不在此Map中
     *
     * @param toolCallbackProvider MCP Client工具回调提供者(可选,仅在MCP Client启用时注入)
     * @return MCP工具的Map,如果MCP未启用则返回空Map
     */
    @Bean("mcpToolCallbackMap")
    public Map<String, ToolCallback> mcpToolCallbackMap(
            @Autowired(required = false) ToolCallbackProvider toolCallbackProvider) {
        if (toolCallbackProvider == null) {
            return new HashMap<>();
        }

        // 将ToolCallbackProvider的所有工具转换为Map
        Map<String, ToolCallback> map = new HashMap<>();
        for (ToolCallback callback : toolCallbackProvider.getToolCallbacks()) {
            String toolName = callback.getToolDefinition().name();
            map.put(toolName, callback);
        }
        return map;
    }
}