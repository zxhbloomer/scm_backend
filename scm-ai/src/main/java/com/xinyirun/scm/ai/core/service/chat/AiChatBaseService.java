package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.config.memory.WorkflowConversationAdvisor;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai.LogAiChatProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI聊天基础服务类
 *
 * 提供AI聊天的核心功能，包括：
 * 1. 普通聊天（无记忆）
 * 2. 带记忆的聊天
 * 3. 流式聊天
 * 4. 对话内容持久化
 * 5. AI模型配置管理
 *
 * @author jianxing
 * @author SCM-AI重构团队
 * @since 2025-05-28
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AiChatBaseService {

    private static final String MCP_TOOL_SYSTEM_PROMPT = """
            在使用MCP工具时，请严格遵守以下要求：
            1. 不可以臆想、推测数据，必须基于工具返回的实际结果
            2. 不可以过度回复、不可以过度推测
            3. 如果你对任何方面不确定，或者无法取得必要信息，请说"我没有足够的信息来自信地评估这一点"
            4. 如果找不到相关引用，请说明"未找到相关引用"
            5. 找不到相关回答，请说明"未找到相关回答"
            6. 在回答找不到的情况时，不要过多拓展回复和过度回复
            """;

    @Resource
    MessageChatMemoryAdvisor chatMessageChatMemoryAdvisor;
    @Resource
    MessageChatMemoryAdvisor workflowMessageChatMemoryAdvisor;

    @Lazy
    @Resource
    @Qualifier("chatDomainChatClient")
    private ChatClient chatDomainChatClient;

    @Lazy
    @Resource
    @Qualifier("workflowDomainChatClient")
    private ChatClient workflowDomainChatClient;

    @Lazy
    @Resource
    @Qualifier("workflowDomainChatClientNoMcp")
    private ChatClient workflowDomainChatClientNoMcp;

    @Lazy
    @Resource
    @Qualifier("mcpToolOnlyChatClient")
    private ChatClient mcpToolOnlyChatClient;

    @Lazy
    @Resource
    @Qualifier("mcpToolCallbackMap")
    private Map<String, ToolCallback> mcpToolCallbackMap;

    @Resource
    private AiConversationContentMapper aiConversationContentMapper;
    @Resource
    private AiModelConfigService aiModelConfigService;
    @Autowired
    private LogAiChatProducer logAiChatProducer;
    @Autowired
    private AiModelProvider aiModelProvider;

    /**
     * 根据聊天请求获取AI模型配置
     *
     * @param request 聊天请求对象，包含AI类型等信息
     * @param userId 用户ID（当前未使用，保留接口兼容性）
     * @return AiModelConfigVo AI模型配置对象
     * @throws RuntimeException 当模型配置不存在或未启用时抛出异常
     */
    public AiModelConfigVo getModule(AIChatRequestVo request, String userId) {
        // 将aiType映射为modelType
        String modelType = mapAiTypeToModelType(request.getAiType());

        // 获取默认模型配置（包含完整API Key）
        return aiModelConfigService.getDefaultModelConfigWithKey(modelType);
    }

    /**
     * 将aiType映射为modelType
     *
     * @param aiType AI类型（前端传入）
     * @return modelType 模型类型（LLM/VISION/EMBEDDING）
     */
    private String mapAiTypeToModelType(String aiType) {
        if (StringUtils.isBlank(aiType)) {
            return "LLM";
        }

        switch (aiType.toUpperCase()) {
            case "VISION":
            case "IMAGE":
                return "VISION";
            case "EMBEDDING":
            case "EMB":
                return "EMBEDDING";
            case "LLM":
            case "TEXT":
            case "CHAT":
            default:
                return "LLM";
        }
    }

    /**
     * 执行AI聊天（无记忆模式）
     *
     * 这种模式下，AI不会记忆之前的对话内容，每次都是独立的对话
     * 适用于单次问答或不需要上下文关联的场景
     *
     * @param aiChatOption 聊天选项配置对象，包含提示词、模型配置等
     * @return ChatClient.CallResponseSpec Spring AI的响应规格对象，可用于获取AI回复
     */
    public ChatClient.CallResponseSpec chat(AIChatOptionVo aiChatOption) {
        return getClient(aiChatOption.getModule())
                .prompt()
                .user(aiChatOption.getPrompt())
                .call();
    }

    /**
     * 执行AI流式聊天（无记忆模式）
     *
     * 流式聊天可以实时接收AI回复的内容片段，提供更好的用户体验
     * 这种模式下，AI不会记忆之前的对话内容，每次都是独立的对话
     * 适用于工作流节点等无需上下文关联的场景
     *
     * @param aiChatOption 聊天选项配置对象，包含提示词、模型配置等
     * @return ChatClient.StreamResponseSpec Spring AI的流式响应规格对象，用于接收流式数据
     */
    public ChatClient.StreamResponseSpec chatStream(AIChatOptionVo aiChatOption) {
        return getClient(aiChatOption.getModule())
                .prompt()
                .user(aiChatOption.getPrompt())
                .stream();
    }

    /**
     * MCP工具节点专用：带MCP工具但不加载对话历史
     *
     * MCP工具调用是确定性操作，不需要对话上下文。
     * 如果加载了知识库检索等节点产生的对话历史，会干扰LLM的工具调用决策。
     *
     * @param aiChatOption 聊天选项配置对象，包含提示词和toolContext
     * @return ChatClient.StreamResponseSpec 流式响应
     */
    public ChatClient.StreamResponseSpec chatStreamWithMcpTools(AIChatOptionVo aiChatOption) {
        List<String> toolNames = aiChatOption.getToolNames();

        // 指定了工具名称 → 过滤加载
        if (toolNames != null && !toolNames.isEmpty()) {
            ToolCallback[] filteredCallbacks = toolNames.stream()
                    .map(name -> name.replace(".", "_"))
                    .filter(mcpToolCallbackMap::containsKey)
                    .map(mcpToolCallbackMap::get)
                    .toArray(ToolCallback[]::new);

            if (filteredCallbacks.length > 0) {
                log.info("MCP工具节点使用过滤模式, 加载工具: {}/{}", filteredCallbacks.length, toolNames.size());
                ChatClient filteredClient = ChatClient.builder(aiModelProvider.getChatModel())
                        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
                        .defaultToolCallbacks(filteredCallbacks)
                        .build();

                ChatClient.ChatClientRequestSpec requestSpec = filteredClient
                        .prompt()
                        .user(aiChatOption.getPrompt());

                if (aiChatOption.getToolContext() != null && !aiChatOption.getToolContext().isEmpty()) {
                    requestSpec.toolContext(aiChatOption.getToolContext());
                }
                return requestSpec.stream();
            }
            log.warn("指定的工具名称均未找到: {}, 降级为全部工具", toolNames);
        }

        // 未指定或过滤后为空 → 使用全部工具（向后兼容），动态获取ChatModel避免缓存旧模型
        log.info("MCP工具节点使用无记忆模式, 避免对话历史干扰工具调用");
        ChatClient dynamicMcpClient = ChatClient.builder(aiModelProvider.getChatModel())
                .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
                .defaultToolCallbacks(mcpToolCallbackMap.values().toArray(new ToolCallback[0]))
                .build();
        ChatClient.ChatClientRequestSpec requestSpec = dynamicMcpClient
                .prompt()
                .user(aiChatOption.getPrompt());

        if (aiChatOption.getToolContext() != null && !aiChatOption.getToolContext().isEmpty()) {
            requestSpec.toolContext(aiChatOption.getToolContext());
        }

        return requestSpec.stream();
    }

    /**
     * 执行AI流式聊天（带记忆模式）
     *
     * 流式聊天可以实时接收AI回复的内容片段，提供更好的用户体验
     * 同样支持记忆功能和多租户环境，租户信息已包含在conversationId中
     *
     * 使用预配置chatMessageChatMemoryAdvisor的chatDomainChatClient，
     * 运行时只需设置conversationId参数
     *
     * @param aiChatOption 聊天选项配置对象，包含对话ID、提示词、系统指令、租户ID等
     * @return ChatClient.StreamResponseSpec Spring AI的流式响应规格对象，用于接收流式数据
     */
    public ChatClient.StreamResponseSpec chatWithMemoryStream(AIChatOptionVo aiChatOption) {
        // Chat领域专用，conversationId已包含租户信息，直接使用即可
        // 运行时同时传递advisor实例和conversationId参数
        if (StringUtils.isNotBlank(aiChatOption.getSystem())) {
            return chatDomainChatClient
                    .prompt()
                    .system(aiChatOption.getSystem())
                    .user(aiChatOption.getPrompt())
                    .advisors(a -> {
                        a.advisors(chatMessageChatMemoryAdvisor);
                        a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId());
                    })
                    .stream();
        }
        return chatDomainChatClient
                .prompt()
                .user(aiChatOption.getPrompt())
                .advisors(a -> {
                    a.advisors(chatMessageChatMemoryAdvisor);
                    a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId());
                })
                .stream();
    }

    /**
     * Workflow领域专用：使用记忆功能的流式聊天方法
     *
     * 从ai_workflow_conversation_content表获取历史对话上下文，实现多轮对话记忆
     *
     * 使用预配置defaultAdvisors的workflowDomainChatClient：
     * - workflowMessageChatMemoryAdvisor: 读取历史对话
     * - workflowConversationAdvisor: 保存新对话（需要runtime_uuid和originalUserInput参数）
     *
     * @param aiChatOption 聊天选项对象，包含模型配置、提示词、conversationId等
     * @param runtimeUuid 运行时UUID，用于隔离不同运行实例的对话记录
     * @param originalUserInput 原始用户输入（用于对话记录，而不是渲染后的prompt）
     * @return ChatClient.StreamResponseSpec Spring AI的流式响应规格对象，用于接收流式数据
     */
    public ChatClient.StreamResponseSpec chatWithWorkflowMemoryStream(
            AIChatOptionVo aiChatOption,
            String runtimeUuid,
            String originalUserInput,
            com.xinyirun.scm.ai.common.constant.WorkflowCallSource callSource) {

        log.info("🚀 [Workflow Memory] 调用chatWithWorkflowMemoryStream - conversationId: {}, runtimeUuid: {}, originalUserInput长度: {}, prompt长度: {}, 是否有system: {}, callSource: {}",
                aiChatOption.getConversationId(),
                runtimeUuid,
                originalUserInput != null ? originalUserInput.length() : 0,
                aiChatOption.getPrompt() != null ? aiChatOption.getPrompt().length() : 0,
                StringUtils.isNotBlank(aiChatOption.getSystem()),
                callSource);

        // 根据callSource动态选择Memory Advisor
        MessageChatMemoryAdvisor selectedMemoryAdvisor;
        if (com.xinyirun.scm.ai.common.constant.WorkflowCallSource.AI_CHAT.equals(callSource)) {
            selectedMemoryAdvisor = chatMessageChatMemoryAdvisor;  // AI Chat → 读取 ai_conversation_content
            log.info("✅ [Dynamic Memory] AI_CHAT调用,使用chatMessageChatMemoryAdvisor读取ai_conversation_content");
        } else {
            selectedMemoryAdvisor = workflowMessageChatMemoryAdvisor;  // Workflow独立执行 → 读取 ai_workflow_conversation_content
            log.info("✅ [Dynamic Memory] WORKFLOW_TEST调用,使用workflowMessageChatMemoryAdvisor读取ai_workflow_conversation_content");
        }

        // 根据enableMcpTools标志动态选择ChatClient
        ChatClient selectedClient;
        if (Boolean.TRUE.equals(aiChatOption.getEnableMcpTools())) {
            selectedClient = workflowDomainChatClient;
            log.info("✅ [MCP Control] 启用MCP工具,使用workflowDomainChatClient");
        } else {
            selectedClient = workflowDomainChatClientNoMcp;
            log.info("✅ [MCP Control] 禁用MCP工具,使用workflowDomainChatClientNoMcp");
        }

        if (StringUtils.isNotBlank(aiChatOption.getSystem())) {
            ChatClient.ChatClientRequestSpec requestSpec = selectedClient
                    .prompt()
                    .system(aiChatOption.getSystem())
                    .user(aiChatOption.getPrompt());

            // 如果有toolContext，传递给ChatClient
            if (aiChatOption.getToolContext() != null && !aiChatOption.getToolContext().isEmpty()) {
                requestSpec.toolContext(aiChatOption.getToolContext());
            }

            return requestSpec.advisors(a -> {
                        // 使用动态选择的Memory Advisor
                        a.advisors(selectedMemoryAdvisor);
                        a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId());
                        a.param(WorkflowConversationAdvisor.RUNTIME_UUID, runtimeUuid);
                        // 传递原始用户输入（用于对话记录，而不是渲染后的prompt）
                        if (StringUtils.isNotBlank(originalUserInput)) {
                            a.param(WorkflowConversationAdvisor.ORIGINAL_USER_INPUT, originalUserInput);
                        }
                        // 传递调用来源
                        if (callSource != null) {
                            a.param(WorkflowConversationAdvisor.CALL_SOURCE, callSource.name());
                        }
                    })
                    .stream();
        }

        ChatClient.ChatClientRequestSpec requestSpec = selectedClient
                .prompt()
                .user(aiChatOption.getPrompt());

        // 如果有toolContext，传递给ChatClient
        if (aiChatOption.getToolContext() != null && !aiChatOption.getToolContext().isEmpty()) {
            requestSpec.toolContext(aiChatOption.getToolContext());
        }

        return requestSpec.advisors(a -> {
                    // 使用动态选择的Memory Advisor
                    a.advisors(selectedMemoryAdvisor);
                    a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId());
                    a.param(WorkflowConversationAdvisor.RUNTIME_UUID, runtimeUuid);
                    // 传递原始用户输入（用于对话记录，而不是渲染后的prompt）
                    if (StringUtils.isNotBlank(originalUserInput)) {
                        a.param(WorkflowConversationAdvisor.ORIGINAL_USER_INPUT, originalUserInput);
                    }
                    // 传递调用来源
                    if (callSource != null) {
                        a.param(WorkflowConversationAdvisor.CALL_SOURCE, callSource.name());
                    }
                })
                .stream();
    }

    /**
     * 根据模型配置创建ChatClient实例
     *
     * 使用AiModelProvider获取ChatModel（包含租户级缓存和配置管理）
     * 基于ChatModel创建ChatClient实例
     *
     * @param model AI模型配置对象（用于日志记录，实际模型通过AiModelProvider获取）
     * @return ChatClient 配置好的Spring AI ChatClient实例
     */
    private ChatClient getClient(AiModelConfigVo model) {
        // 使用 AiModelProvider 获取 ChatModel（已包含租户级缓存和配置）
        ChatModel chatModel = aiModelProvider.getChatModel();

        // 基于 ChatModel 创建 ChatClient
        return ChatClient.builder(chatModel).build();
    }
}