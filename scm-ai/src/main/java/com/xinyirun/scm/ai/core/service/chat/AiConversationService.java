package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.domain.AiConversation;
import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import com.xinyirun.scm.ai.bean.domain.AiConversationContentExample;
import com.xinyirun.scm.ai.bean.domain.AiConversationExample;
import com.xinyirun.scm.ai.bean.domain.AiPrompt;
import com.xinyirun.scm.ai.bean.dto.request.AIChatRequest;
import com.xinyirun.scm.ai.bean.dto.request.AIChatOption;
import com.xinyirun.scm.ai.bean.dto.request.AIConversationUpdateRequest;
import com.xinyirun.scm.ai.common.exception.MSException;
import com.xinyirun.scm.ai.common.util.BeanUtils;
import com.xinyirun.scm.ai.common.util.LogUtils;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationMapper;
import com.xinyirun.scm.ai.core.mapper.chat.AiModelSourceMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: jianxing
 * @CreateTime: 2025-05-28  13:44
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiConversationService {

    @Resource
    AiChatBaseService aiChatBaseService;
    @Resource
    AiConversationMapper aiConversationMapper;
    @Resource
    AiConversationContentMapper aiConversationContentMapper;
    @Resource
    AiPromptService aiPromptService;
    @Resource
    AiTokenUsageService aiTokenUsageService;
    @Resource
    AiUserQuotaService aiUserQuotaService;
    @Resource
    private com.xinyirun.scm.ai.core.mapper.chat.ExtAiConversationContentMapper extAiConversationContentMapper;
    @Resource
    AiConfigService aiConfigService;
    @Resource
    AiModelSourceMapper aiModelSourceMapper;

    /**
     * 获取默认系统提示词
     * @return 默认系统提示词，如果不存在返回null
     */
    private String getDefaultSystemPrompt() {
        try {
            AiPrompt defaultPrompt = aiPromptService.getPromptByCode("CS_DEFAULT");
            return defaultPrompt != null ? defaultPrompt.getPrompt() : null;
        } catch (Exception e) {
            LogUtils.error("获取默认系统提示词失败", e);
            return null;
        }
    }

//    public String chat(AIChatRequest request, String userId) {
//        // 1. 检查用户配额（预估Token使用量）
//        String tenant = getCurrentTenant(); // TODO: 实现获取当前租户方法
//        Long estimatedTokens = estimateTokenUsage(request.getPrompt());
//
//        if (!aiUserQuotaService.checkUserQuota(userId, tenant, estimatedTokens)) {
//            throw new MSException("Token配额不足，请稍后再试或联系管理员");
//        }
//
//        // 持久化原始提示词
//        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt());
//
//        AIChatOption aiChatOption = AIChatOption.builder()
//                .conversationId(request.getConversationId())
//                .module(aiChatBaseService.getModule(request, userId))
//                .prompt(request.getPrompt())
//                .system(getDefaultSystemPrompt())
//                .build();
//
//        long startTime = System.currentTimeMillis();
//        try {
//            // 2. 执行AI聊天
//            var callResponse = aiChatBaseService.chatWithMemory(aiChatOption);
//            var chatResponse = callResponse.chatResponse();
//            String assistantMessage = chatResponse.getResult().getOutput().getText();
//
//            // 持久化回答内容
//            aiChatBaseService.saveAssistantConversationContent(request.getConversationId(), assistantMessage);
//
//            // 3. 记录Token使用情况（从Spring AI Usage中提取）
//            var usage = chatResponse.getMetadata().getUsage();
//            if (usage != null) {
//                recordTokenUsageFromSpringAI(request, userId, tenant, usage, startTime, true);
//            }
//
//            return assistantMessage;
//        } catch (Exception e) {
//            LogUtils.error("AI聊天失败", e);
//
//            // 记录失败的Token使用（如果有的话）
//            long responseTime = System.currentTimeMillis() - startTime;
//            recordTokenUsageOnFailure(request, userId, tenant, responseTime);
//
//            throw e;
//        }
//    }

    /**
     * 流式聊天
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param sessionId WebSocket会话ID
     */
    public void chatStream(AIChatRequest request, String userId, String sessionId) {
        // 获取模型ID
        String modelId = aiChatBaseService.getModule(request, userId).getId();

        // 持久化原始提示词
        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt(), modelId);

        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .tenant(request.getTenantId())
                .build();

        // 创建WebSocket流式处理器
        AiStreamHandler.WebSocketStreamHandler streamHandler =
                new AiStreamHandler.WebSocketStreamHandler(sessionId);

        StringBuilder completeContent = new StringBuilder();

        try {
            // 使用流式聊天
            aiChatBaseService.chatWithMemoryStream(aiChatOption)
                .content()
                .doOnNext(content -> {
                    // 发送内容片段
                    streamHandler.onContent(content);
                    completeContent.append(content);
                })
                .doOnComplete(() -> {
                    // 完成时保存完整内容
                    String fullContent = completeContent.toString();
                    aiChatBaseService.saveAssistantConversationContent(request.getConversationId(), fullContent, modelId);

                    // 发送完成事件
                    AiEngineAdapter.AiResponse finalResponse = new AiEngineAdapter.AiResponse();
                    finalResponse.setContent(fullContent);
                    finalResponse.setSuccess(true);
                    streamHandler.onComplete(finalResponse);
                })
                .doOnError(error -> {
                    LogUtils.error(error);
                    streamHandler.onError(error);
                })
                .subscribe();

            // 发送开始事件
            streamHandler.onStart();

        } catch (Exception e) {
            LogUtils.error(e);
            streamHandler.onError(e);
        }
    }

    /**
     * 使用回调方式的流式聊天
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param streamHandler 流式处理器
     */
    public void chatStreamWithCallback(AIChatRequest request, String userId, AiStreamHandler.CallbackStreamHandler streamHandler) {
        // 获取模型ID
        final String modelId = aiChatBaseService.getModule(request, userId).getId();

        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .tenant(request.getTenantId())
                .build();
        StringBuilder completeContent = new StringBuilder();
        final org.springframework.ai.chat.metadata.Usage[] finalUsage = new org.springframework.ai.chat.metadata.Usage[1];

        // 持久化原始提示词
        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt(), modelId);

        try {
            // 使用流式聊天
            aiChatBaseService.chatWithMemoryStream(aiChatOption)
                .chatResponse()
                .doOnNext(chatResponse -> {
                    // 获取内容片段
                    String content = chatResponse.getResult().getOutput().getText();
                    // 发送内容片段
                    streamHandler.onContent(content);
                    completeContent.append(content);

                    // 保存最后一个响应的Usage信息（通常在最后一个响应中）
                    if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
                        finalUsage[0] = chatResponse.getMetadata().getUsage();
                    }
                })
                .doOnComplete(() -> {
                    // 完成时保存完整内容
                    String fullContent = completeContent.toString();
                    aiChatBaseService.saveAssistantConversationContent(request.getConversationId(), fullContent, modelId);

                    // 发送完成事件
                    AiEngineAdapter.AiResponse finalResponse = new AiEngineAdapter.AiResponse();
                    finalResponse.setContent(fullContent);
                    finalResponse.setSuccess(true);

                    // 设置Usage信息
                    if (finalUsage[0] != null) {
                        finalResponse.setUsageFromSpringAi(finalUsage[0]);
                    }

                    streamHandler.onComplete(finalResponse);
                })
                .doOnError(error -> {
                    LogUtils.error(error);
                    streamHandler.onError(error);
                })
                .subscribe();

            // 发送开始事件
            streamHandler.onStart();

        } catch (Exception e) {
            LogUtils.error(e);
            streamHandler.onError(e);
        }
    }

    /**
     * 保存用户消息内容 (从控制器调用)
     */
    public void saveUserConversationContent(String conversationId, String content, String modelSourceId) {
        aiChatBaseService.saveUserConversationContent(conversationId, content, modelSourceId);
    }

    /**
     * 保存助手消息内容 (从控制器调用)
     */
    public void saveAssistantConversationContent(String conversationId, String content, String modelSourceId) {
        aiChatBaseService.saveAssistantConversationContent(conversationId, content, modelSourceId);
    }

    public AiConversation add(AIChatRequest request, String userId) {
        String prompt = """
                概况用户输入的主旨生成本轮对话的标题，只返回标题，不带标点符号，最好50字以内，不超过255。
                用户输入:
                """ + request.getPrompt();
        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(prompt)
                .tenant(request.getTenantId())
                .build();

        String conversationTitle = request.getPrompt();
        try {
            conversationTitle = aiChatBaseService.chat(aiChatOption)
                    .content();
            conversationTitle = conversationTitle.replace("\"", "");
        } catch (Exception e) {
            LogUtils.error(e);
        }

        if (conversationTitle.length() > 255) {
            conversationTitle = conversationTitle.substring(0, 255);
        }
        AiConversation aiConversation = new AiConversation();
        aiConversation.setId(request.getConversationId());
        aiConversation.setTitle(conversationTitle);
        aiConversation.setCreateUser(userId);
        aiConversation.setCreateTime(System.currentTimeMillis());
        aiConversationMapper.insert(aiConversation);
        return aiConversation;
    }

    public void delete(String conversationId, String userId) {
        AiConversation aiConversation = aiConversationMapper.selectByPrimaryKey(conversationId);
        aiConversationMapper.deleteByPrimaryKey(conversationId);

        AiConversationContentExample example = new AiConversationContentExample();
        example.createCriteria().andConversationIdEqualTo(conversationId);
        aiConversationContentMapper.deleteByExample(example);
    }

    /**
     * 清空对话内容（保留对话记录，只删除消息内容）
     * @param conversationId 对话ID
     * @param userId 用户ID
     */
    public void clearConversationContent(String conversationId, String userId) {
        try {
            // 删除所有对话内容
            AiConversationContentExample deleteExample = new AiConversationContentExample();
            deleteExample.createCriteria().andConversationIdEqualTo(conversationId);
            int deletedCount = aiConversationContentMapper.deleteByExample(deleteExample);

            // 记录操作日志
            LogUtils.info("对话内容已清空 - conversationId: {}, deletedCount: {}", conversationId, deletedCount);

        } catch (Exception e) {
            LogUtils.error("清空对话内容失败", e);
            throw new MSException("清空对话内容失败：" + e.getMessage());
        }
    }


    // 权限检查逻辑已删除 - 不再需要用户权限验证

    public List<AiConversation> list(String userId) {
        AiConversationExample example = new AiConversationExample();
        example.createCriteria().andCreateUserEqualTo(userId);
        return aiConversationMapper.selectByExample(example).reversed();
    }

    /**
     * 根据conversationId获取对话信息
     * @param conversationId 对话ID
     * @return 对话信息
     */
    public AiConversation getConversation(String conversationId) {
        return aiConversationMapper.selectByPrimaryKey(conversationId);
    }

    /**
     * 根据模型base_name查找对应的model_source_id
     * @param baseName 模型基础名称（如：deepseek-chat, gpt-4等）
     * @return model_source_id，如果未找到返回null
     */
    private String findModelSourceIdByBaseName(String baseName) {
        try {
            // 使用AiModelSourceService查找对应的模型源
            // 这里假设有AiModelSourceMapper或相应的查询方法
            // 暂时返回null，避免外键约束错误
            LogUtils.debug("查找模型源ID - baseName: {}", baseName);

            // TODO: 实现根据base_name查询ai_model_source表的逻辑
            // 示例SQL: SELECT id FROM ai_model_source WHERE base_name = ?

            return null; // 暂时返回null，允许model_source_id为空
        } catch (Exception e) {
            LogUtils.error("查找模型源ID失败 - baseName: {}", baseName, e);
            return null;
        }
    }

    /**
     * 从Spring AI Usage接口记录Token使用情况（流式聊天回调）
     * 该方法与Spring AI框架集成，用于记录实际的Token消耗
     *
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     */
    public void recordTokenUsageFromSpringAI(String conversationId, String userId, String tenant,
                                            String aiProvider, String aiModelType,
                                            Long promptTokens, Long completionTokens) {
        try {
            // 根据模型类型查找对应的model_source_id
            String modelSourceId = findModelSourceIdByBaseName(aiModelType);

            // 异步记录Token使用情况
            aiTokenUsageService.recordTokenUsageAsync(
                    conversationId,
                    modelSourceId, // 根据模型类型查找到的model_source_id
                    userId,
                    tenant,
                    aiProvider,
                    aiModelType,
                    promptTokens,
                    completionTokens,
                    true, // success
                    0L    // responseTime
            );

            LogUtils.info("流式聊天回调Token使用记录 - conversationId: {}, userId: {}, tokens: {}",
                    new Object[]{conversationId, userId, (promptTokens + completionTokens)});

        } catch (Exception e) {
            LogUtils.error("流式聊天回调Token使用记录失败", e);
            // 记录失败不抛出异常，避免影响主业务流程
        }
    }

    public List<AiConversationContent> chatList(String conversationId, String userId) {

        AiConversationContentExample example = new AiConversationContentExample();
        example.createCriteria().andConversationIdEqualTo(conversationId);
        example.setOrderByClause("create_time");
        return aiConversationContentMapper.selectByExampleWithBLOBs(example);
    }

    public AiConversation update(AIConversationUpdateRequest request, String userId) {
        AiConversation aiConversation = BeanUtils.copyBean(new AiConversation(), request);
        aiConversationMapper.updateByPrimaryKeySelective(aiConversation);
        return aiConversationMapper.selectByPrimaryKey(request.getId());
    }

    /**
     * 为用户创建AI会话记录（由事件触发）
     *
     * @param convUuid 会话UUID
     * @param userId 用户ID
     * @param userName 用户名
     */
    public void createConversationForUser(String convUuid, Long userId, String userName, String tenant) {
        try {
            // 检查会话是否已存在
            AiConversation existingConversation = aiConversationMapper.selectByPrimaryKey(convUuid);
            if (existingConversation != null) {
                LogUtils.info("AI会话记录已存在，跳过创建：convUuid={}", convUuid);
                return;
            }

            // 创建AI会话记录
            AiConversation aiConversation = new AiConversation();
            aiConversation.setId(convUuid);
            aiConversation.setTitle("新对话"); // 设置默认标题
            aiConversation.setCreateUser(String.valueOf(userId));
            aiConversation.setCreateTime(System.currentTimeMillis());
            aiConversation.setTenant(tenant);

            aiConversationMapper.insert(aiConversation);

            LogUtils.info("在chat-ai数据库中创建AI会话记录成功 - convUuid={}, userId={}, userName={}",
                    convUuid, userId, userName);
        } catch (Exception e) {
            log.error("创建AI会话记录失败：userId={}, convUuid={}", userId, convUuid, e);
            throw e; // 重新抛出异常，让调用方处理
        }
    }

    /**
     * 从Spring AI Usage中记录Token使用情况
     */
    private void recordTokenUsageFromSpringAI(AIChatRequest request, String userId, String tenant,
                                             Usage usage, long startTime, boolean success) {
        try {
            long responseTime = System.currentTimeMillis() - startTime;

            // 从Spring AI Usage中获取Token信息
            Long promptTokens = usage.getPromptTokens() != null ? usage.getPromptTokens().longValue() : 0L;
            Long completionTokens = usage.getCompletionTokens() != null ? usage.getCompletionTokens().longValue() : 0L;

            // 获取AI提供商和模型信息（从请求配置中获取）
            String aiProvider = "deepseek"; // 默认提供商，实际应该从配置中获取
            String aiModelType = "deepseek-chat"; // 默认模型，实际应该从配置中获取

            // 异步记录Token使用情况
            aiTokenUsageService.recordTokenUsageAsync(
                    request.getConversationId(),
                    request.getChatModelId(), // 模型源ID
                    userId,
                    tenant,
                    aiProvider,
                    aiModelType,
                    promptTokens,
                    completionTokens,
                    success,
                    responseTime
            );

            LogUtils.debug("Token使用记录已提交 - conversationId: {}, promptTokens: {}, completionTokens: {}",
                    new Object[]{request.getConversationId(), promptTokens, completionTokens});

        } catch (Exception e) {
            LogUtils.error("记录Token使用失败", e);
            // Token记录失败不影响主业务流程
        }
    }

    /**
     * 记录失败情况下的Token使用（通常没有Token信息）
     */
    private void recordTokenUsageOnFailure(AIChatRequest request, String userId, String tenant, long responseTime) {
        try {
            // 失败时通常没有Token消耗，记录0
            aiTokenUsageService.recordTokenUsageAsync(
                    request.getConversationId(),
                    request.getChatModelId(),
                    userId,
                    tenant,
                    "unknown",
                    "unknown",
                    0L,
                    0L,
                    false,
                    responseTime
            );

            LogUtils.debug("失败请求Token记录已提交 - conversationId: {}", request.getConversationId());

        } catch (Exception e) {
            LogUtils.error("记录失败Token使用失败", e);
        }
    }

    /**
     * 估算Token使用量（简单实现，可以根据实际情况优化）
     */
    private Long estimateTokenUsage(String prompt) {
        if (StringUtils.isBlank(prompt)) {
            return 0L;
        }

        // 简单估算：按照字符数的1/4估算Token数
        // 实际应用中可以使用更精确的Token计算库
        int charCount = prompt.length();
        long estimatedTokens = (long) Math.ceil(charCount / 4.0);

        // 预估输出Token（通常是输入的1-2倍）
        estimatedTokens = estimatedTokens + (estimatedTokens / 2);

        return Math.max(estimatedTokens, 100L); // 最少估算100个Token
    }

    /**
     * 获取当前租户ID
     * TODO: 实现获取当前租户的逻辑
     */
    private String getCurrentTenant() {
        // 这里需要根据实际的租户获取逻辑来实现
        // 可能从ThreadLocal、Session、请求头等获取
        return null;
    }

    /**
     * 从响应中提取输入Token数
     */
    private Long extractPromptTokens(AiEngineAdapter.AiResponse response) {
        if (response != null) {
            return response.getPromptTokens();
        }
        return 0L;
    }

    /**
     * 从响应中提取输出Token数
     */
    private Long extractCompletionTokens(AiEngineAdapter.AiResponse response) {
        if (response != null) {
            return response.getCompletionTokens();
        }
        return 0L;
    }

    /**
     * 从响应中提取AI提供商信息
     */
    private String extractAiProvider(AiEngineAdapter.AiResponse response) {
        if (response != null && response.getModelProvider() != null) {
            return response.getModelProvider();
        }
        // 从配置获取默认值
        return "OpenAI"; // 默认AI提供商
    }

    /**
     * 从响应中提取AI模型类型
     */
    private String extractAiModelType(AiEngineAdapter.AiResponse response) {
        if (response != null && response.getModelName() != null) {
            return response.getModelName();
        }
        // 从配置获取默认值
        return "gpt-4o"; // 默认AI模型
    }

    /**
     * 获取对话历史记录（用于ChatMemory）
     * @param conversationId 对话ID
     * @param limit 限制条数
     * @return 对话内容列表
     */
    public List<AiConversationContent> getConversationHistory(String conversationId, int limit) {
        return extAiConversationContentMapper.selectLastByConversationIdByLimit(conversationId, limit);
    }

}