package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import com.xinyirun.scm.ai.engine.ChatToolEngine;
import com.xinyirun.scm.ai.engine.common.AIChatOptions;
import com.xinyirun.scm.ai.engine.utils.JSON;
import com.xinyirun.scm.ai.common.util.CommonBeanFactory;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai.LogAiChatProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private AiModelSelectionService aiModelSelectionService;
    @Resource
    private AiModelSourceMapper aiModelSourceMapper;
    @Autowired
    private LogAiChatProducer logAiChatProducer;

    /**
     * 根据聊天请求获取AI模型配置
     *
     * @param request 聊天请求对象，包含AI类型等信息
     * @param userId 用户ID，用于权限验证和配置获取
     * @return AiModelSourceVo AI模型源配置对象
     * @throws RuntimeException 当模型配置不存在或无权限访问时抛出异常
     */
    public AiModelSourceVo getModule(AIChatRequestVo request, String userId) {
        // 使用动态模型选择服务根据AI类型选择合适的模型
        AiModelSourceEntity selectedModel = aiModelSelectionService.selectAvailableModel(request.getAiType());

        // 通过模型ID获取完整的模型配置VO对象
        return Objects.requireNonNull(CommonBeanFactory.getBean(SystemAIConfigService.class))
                .getModelSourceVoWithKey(selectedModel.getId(), userId);
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
     * 执行AI聊天（带记忆模式）
     *
     * 这种模式下，AI会记忆当前对话的历史内容，能够进行连续对话
     * 使用MessageChatMemoryAdvisor来维护对话上下文
     * 支持多租户环境，租户信息已包含在conversationId中，无需ThreadLocal管理
     *
     * @param aiChatOption 聊天选项配置对象，包含对话ID、提示词、系统指令、租户ID等
     * @return ChatClient.CallResponseSpec Spring AI的响应规格对象，可用于获取AI回复
     */
    public ChatClient.CallResponseSpec chatWithMemory(AIChatOptionVo aiChatOption) {
        // conversationId已包含租户信息，直接使用即可
        if (StringUtils.isNotBlank(aiChatOption.getSystem())) {
            return getClient(aiChatOption.getModule())
                    .prompt()
                    .system(aiChatOption.getSystem())
                    .user(aiChatOption.getPrompt())
                    .advisors(messageChatMemoryAdvisor)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId()))
                    .call();
        }
        return getClient(aiChatOption.getModule())
                .prompt()
                .user(aiChatOption.getPrompt())
                .advisors(messageChatMemoryAdvisor)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, aiChatOption.getConversationId()))
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
     * 通过ChatToolEngine构建器创建配置好的ChatClient
     * 包含模型提供商信息和聊天选项配置
     *
     * @param model AI模型源配置对象，包含提供商、API密钥等信息
     * @return ChatClient 配置好的Spring AI ChatClient实例
     */
    private ChatClient getClient(AiModelSourceVo model) {
        return ChatToolEngine.builder(model.getProviderName(), getAiChatOptions(model))
                .getChatClient();
    }

    /**
     * 根据模型配置构建AI聊天选项
     *
     * 从模型的高级设置参数列表中提取有效的配置项，
     * 转换为AIChatOptions对象，并设置基本的模型信息
     *
     * @param model AI模型源配置对象，包含高级设置参数列表
     * @return AIChatOptions 包含完整配置的AI聊天选项对象
     */
    private AIChatOptions getAiChatOptions(AiModelSourceVo model) {
        // 获取模块的高级设置参数
        Map<String, Object> paramMap = Optional.ofNullable(model.getAdvSettingVoList())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(item -> StringUtils.isNotBlank(item.getName()) && BooleanUtils.isTrue(item.getEnable()))
                .collect(Collectors.toMap(
                        AdvSettingVo::getName,
                        AdvSettingVo::getValue,
                        (v1, v2) -> v2
                ));

        AIChatOptions aiChatOptions = JSON.parseObject(JSON.toJSONString(paramMap), AIChatOptions.class);

        // 设置模型信息
        aiChatOptions.setModelType(model.getBaseName());
        aiChatOptions.setApiKey(model.getAppKey());
        aiChatOptions.setBaseUrl(model.getApiUrl());
        return aiChatOptions;
    }

    /**
     * 保存用户发送的对话内容
     *
     * 将用户的输入消息持久化到数据库中，消息类型为USER
     * 使用NOT_SUPPORTED事务传播级别，避免与主业务事务冲突
     *
     * @param conversationId 对话ID，用于关联对话会话
     * @param content 用户输入的消息内容
     * @param modelSourceId AI模型源ID，标识使用的AI模型
     * @return AiConversationContentEntity 保存后的对话内容实体对象
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public AiConversationContentEntity saveUserConversationContent(String conversationId, String content, String modelSourceId) {
        return saveConversationContent(conversationId, content, MessageType.USER.getValue(), modelSourceId);
    }

    /**
     * 保存AI助手回复的对话内容
     *
     * 将AI的回复消息持久化到数据库中，消息类型为ASSISTANT
     * 使用NOT_SUPPORTED事务传播级别，避免与主业务事务冲突
     *
     * @param conversationId 对话ID，用于关联对话会话
     * @param content AI助手回复的消息内容
     * @param modelSourceId AI模型源ID，标识使用的AI模型
     * @return AiConversationContentEntity 保存后的对话内容实体对象
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public AiConversationContentEntity saveAssistantConversationContent(String conversationId, String content, String modelSourceId) {
        return saveConversationContent(conversationId, content, MessageType.ASSISTANT.getValue(), modelSourceId);
    }

    /**
     * 保存对话内容的通用方法
     *
     * 创建对话内容实体并持久化到数据库
     * 自动生成唯一ID和创建时间
     *
     * @param conversationId 对话ID，用于关联对话会话
     * @param content 消息内容
     * @param type 消息类型（USER/ASSISTANT/SYSTEM等）
     * @param modelSourceId AI模型源ID，标识使用的AI模型
     * @return AiConversationContentEntity 保存后的对话内容实体对象
     */
    private AiConversationContentEntity saveConversationContent(String conversationId, String content, String type, String modelSourceId) {
        AiConversationContentEntity aiConversationContent = new AiConversationContentEntity();
        aiConversationContent.setId(generateId());
        aiConversationContent.setConversationId(conversationId);
        aiConversationContent.setContent(content);
        aiConversationContent.setType(type);
        aiConversationContent.setModelSourceId(modelSourceId);

        // 1. 保存到MySQL
        aiConversationContentMapper.insert(aiConversationContent);

        // 2. 异步发送MQ消息到ClickHouse日志系统
        try {
            SLogAiChatVo logVo = buildLogVo(aiConversationContent);
            logAiChatProducer.mqSendMq(logVo);
            log.debug("发送AI聊天日志MQ消息成功，conversation_id: {}, type: {}",
                    conversationId, type);
        } catch (Exception e) {
            // 日志发送失败不影响主业务，仅记录错误
            log.error("发送AI聊天日志MQ消息失败，conversation_id: {}, type: {}",
                    conversationId, type, e);
        }

        return aiConversationContent;
    }

    /**
     * 构建AI聊天日志VO对象
     *
     * <p>从MySQL实体对象转换为MQ消息VO对象
     * <p>补充应用层字段：tenant_code、c_name、request_id
     * <p>补充模型信息：provider_name、base_name
     *
     * @param entity MySQL实体对象
     * @return SLogAiChatVo MQ消息VO对象
     */
    private SLogAiChatVo buildLogVo(AiConversationContentEntity entity) {
        SLogAiChatVo vo = new SLogAiChatVo();

        // 从entity拷贝基础字段
        vo.setConversation_id(entity.getConversationId());
        vo.setType(entity.getType());
        vo.setContent(entity.getContent());
        vo.setModel_source_id(entity.getModelSourceId());
        vo.setC_id(entity.getCId());
        // MyBatis Plus自动填充只在数据库层面，不会回填到entity对象，所以直接使用当前时间
        vo.setC_time(java.time.LocalDateTime.now());

        // 设置租户编码（从当前数据源上下文获取）
        vo.setTenant_code(DataSourceHelper.getCurrentDataSourceName());

        // 设置创建人名称（从entity获取，如果为null则留空）
        vo.setC_name(entity.getCId() != null ? String.valueOf(entity.getCId()) : null);

        // 设置请求标识（使用conversation_id作为请求标识）
        vo.setRequest_id(entity.getConversationId());

        // 获取模型信息（provider_name和base_name）
        if (StringUtils.isNotBlank(entity.getModelSourceId())) {
            try {
                AiModelSourceEntity modelSource = aiModelSourceMapper.selectById(entity.getModelSourceId());
                if (modelSource != null) {
                    vo.setProvider_name(modelSource.getProviderName());
                    vo.setBase_name(modelSource.getBaseName());
                }
            } catch (Exception e) {
                log.warn("获取AI模型信息失败，model_source_id: {}", entity.getModelSourceId(), e);
                // 失败时provider_name和base_name保持null（ClickHouse表允许null）
            }
        }

        return vo;
    }

    /**
     * 生成唯一ID
     *
     * 使用UUID生成32位无连字符的唯一标识符
     * 用于对话内容记录的主键ID
     *
     * @return String 32位无连字符的唯一ID
     */
    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}