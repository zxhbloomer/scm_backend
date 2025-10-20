package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai.LogAiChatProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    MessageChatMemoryAdvisor messageChatMemoryAdvisor;
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
     * 执行AI流式聊天（带记忆模式）
     *
     * 流式聊天可以实时接收AI回复的内容片段，提供更好的用户体验
     * 同样支持记忆功能和多租户环境，租户信息已包含在conversationId中
     *
     * @param aiChatOption 聊天选项配置对象，包含对话ID、提示词、系统指令、租户ID等
     * @return ChatClient.StreamResponseSpec Spring AI的流式响应规格对象，用于接收流式数据
     */
    public ChatClient.StreamResponseSpec chatWithMemoryStream(AIChatOptionVo aiChatOption) {
        // conversationId已包含租户信息，直接使用即可
        if (StringUtils.isNotBlank(aiChatOption.getSystem())) {
            return getClient(aiChatOption.getModule())
                    .prompt()
                    .system(aiChatOption.getSystem())
                    .user(aiChatOption.getPrompt())
                    .advisors(messageChatMemoryAdvisor)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId()))
                    .stream();
        }
        return getClient(aiChatOption.getModule())
                .prompt()
                .user(aiChatOption.getPrompt())
                .advisors(messageChatMemoryAdvisor)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId()))
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