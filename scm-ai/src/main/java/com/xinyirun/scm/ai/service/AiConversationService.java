package com.xinyirun.scm.ai.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationEntity;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIConversationUpdateRequestVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiPromptVo;
import com.xinyirun.scm.ai.core.service.chat.AiChatBaseService;
import com.xinyirun.scm.ai.common.exception.MSException;
import com.xinyirun.scm.ai.common.util.BeanUtils;
import com.xinyirun.scm.ai.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.mapper.chat.AiConversationMapper;
import com.xinyirun.scm.ai.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.mapper.chat.ExtAiConversationContentMapper;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: jianxing
 * @CreateTime: 2025-05-28  13:44
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
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
    private ExtAiConversationContentMapper extAiConversationContentMapper;
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
            AiPromptVo defaultPrompt = aiPromptService.getByNickname("CS_DEFAULT");
            return defaultPrompt != null ? defaultPrompt.getPrompt() : null;
        } catch (Exception e) {
            log.error("获取默认系统提示词失败", e);
            return null;
        }
    }

    /**
     * 流式聊天
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param sessionId WebSocket会话ID
     */
    public void chatStream(AIChatRequestVo request, String userId, String sessionId) {
        // 获取模型ID
        String modelId = aiChatBaseService.getModule(request, userId).getId();

        // 持久化原始提示词
        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt(), modelId);

        AIChatOptionVo aiChatOption = AIChatOptionVo.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .tenantId(request.getTenantId())
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
                    log.error(error.getMessage());
                    streamHandler.onError(error);
                })
                .subscribe();

            // 发送开始事件
            streamHandler.onStart();

        } catch (Exception e) {
            log.error(e.getMessage());
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
    public void chatStreamWithCallback(AIChatRequestVo request, String userId, AiStreamHandler.CallbackStreamHandler streamHandler) {
        // 获取模型ID
        final String modelId = aiChatBaseService.getModule(request, userId).getId();

        AIChatOptionVo aiChatOption = AIChatOptionVo.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .tenantId(request.getTenantId())
                .build();
        StringBuilder completeContent = new StringBuilder();
        final Usage[] finalUsage = new Usage[1];

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
                    // 异步 手动指定租户数据库
                    DataSourceHelper.use(request.getTenantId());
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
                    log.error(error.getMessage());
                    streamHandler.onError(error);
                })
                .subscribe();

            // 发送开始事件
            streamHandler.onStart();

        } catch (Exception e) {
            log.error(e.getMessage());
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

    public AiConversationVo add(AIChatRequestVo request, String userId) {
        String prompt = """
                概况用户输入的主旨生成本轮对话的标题，只返回标题，不带标点符号，最好50字以内，不超过255。
                用户输入:
                """ + request.getPrompt();
        AIChatOptionVo aiChatOption = AIChatOptionVo.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(prompt)
                .tenantId(request.getTenantId())
                .build();

        String conversationTitle = request.getPrompt();
        try {
            conversationTitle = aiChatBaseService.chat(aiChatOption)
                    .content();
            conversationTitle = conversationTitle.replace("\"", "");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (conversationTitle.length() > 255) {
            conversationTitle = conversationTitle.substring(0, 255);
        }
        AiConversationEntity aiConversation = new AiConversationEntity();
        aiConversation.setId(request.getConversationId());
        aiConversation.setTitle(conversationTitle);
        // 注意：c_time 和 c_id 字段由MyBatis Plus自动填充，不需要手动设置
        // @TableField(fill = FieldFill.INSERT) 会自动处理创建时间和创建人
        aiConversationMapper.insert(aiConversation);
        return convertToVo(aiConversation);
    }

    public void delete(String conversationId, String userId) {
        AiConversationEntity aiConversation = aiConversationMapper.selectById(conversationId);
        aiConversationMapper.deleteById(conversationId);

        QueryWrapper<AiConversationContentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId);
        aiConversationContentMapper.delete(wrapper);
    }

    /**
     * 清空对话内容（保留对话记录，只删除消息内容）
     * @param conversationId 对话ID
     * @param userId 用户ID
     */
    public void clearConversationContent(String conversationId, String userId) {
        try {
            // 删除所有对话内容
            QueryWrapper<AiConversationContentEntity> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("conversation_id", conversationId);
            int deletedCount = aiConversationContentMapper.delete(deleteWrapper);

            // 记录操作日志
            log.info("对话内容已清空 - conversationId: {}, deletedCount: {}", conversationId, deletedCount);

        } catch (Exception e) {
            log.error("清空对话内容失败", e);
            throw new MSException("清空对话内容失败：" + e.getMessage());
        }
    }

    public List<AiConversationVo> list(String userId) {
        QueryWrapper<AiConversationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user", userId);
        wrapper.orderByDesc("create_time");
        List<AiConversationEntity> entities = aiConversationMapper.selectList(wrapper);
        return entities.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    /**
     * 根据conversationId获取对话信息
     * @param conversationId 对话ID
     * @return 对话信息
     */
    public AiConversationVo getConversation(String conversationId) {
        AiConversationEntity entity = aiConversationMapper.selectById(conversationId);
        return entity != null ? convertToVo(entity) : null;
    }

    /**
     * 根据模型base_name查找对应的model_source_id
     * @param baseName 模型基础名称（如：deepseek-chat, gpt-4等）
     * @return model_source_id，如果未找到返回null
     */
    private String findModelSourceIdByBaseName(String baseName) {
        try {
            log.debug("查找模型源ID - baseName: {}", baseName);
            return null;
        } catch (Exception e) {
            log.error("查找模型源ID失败 - baseName: {}", baseName, e);
            return null;
        }
    }

    /**
     * 从Spring AI Usage接口记录Token使用情况（流式聊天回调）
     * 该方法与Spring AI框架集成，用于记录实际的Token消耗
     *
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     */
    public void recordTokenUsageFromSpringAI(String conversationId, String userId,
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
                    aiProvider,
                    aiModelType,
                    promptTokens,
                    completionTokens,
                    true, // success
                    0L    // responseTime
            );

            log.info("流式聊天回调Token使用记录 - conversationId: {}, userId: {}, tokens: {}",
                    new Object[]{conversationId, userId, (promptTokens + completionTokens)});

        } catch (Exception e) {
            log.error("流式聊天回调Token使用记录失败", e);
            // 记录失败不抛出异常，避免影响主业务流程
        }
    }

    public List<AiConversationContentVo> chatList(String conversationId, String userId) {
        QueryWrapper<AiConversationContentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId);
        wrapper.orderByAsc("create_time");
        List<AiConversationContentEntity> entities = aiConversationContentMapper.selectList(wrapper);
        return entities.stream().map(this::convertContentToVo).collect(Collectors.toList());
    }

    public AiConversationVo update(AIConversationUpdateRequestVo request, String userId) {
        AiConversationEntity aiConversation = new AiConversationEntity();
        aiConversation.setId(request.getId());
        aiConversation.setTitle(request.getTitle());
        aiConversationMapper.updateById(aiConversation);
        return getConversation(request.getId());
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
            AiConversationEntity existingConversation = aiConversationMapper.selectById(convUuid);
            if (existingConversation != null) {
                log.info("AI会话记录已存在，跳过创建：convUuid={}", convUuid);
                return;
            }

            // 创建AI会话记录
            AiConversationEntity aiConversation = new AiConversationEntity();
            aiConversation.setId(convUuid);
            aiConversation.setTitle("新对话"); // 设置默认标题
            // 注意：c_time 和 c_id 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT) 会自动处理创建时间和创建人

            aiConversationMapper.insert(aiConversation);

            log.info("在chat-ai数据库中创建AI会话记录成功 - convUuid={}, userId={}, userName={}",
                    convUuid, userId, userName);
        } catch (Exception e) {
            log.error("创建AI会话记录失败：userId={}, convUuid={}", userId, convUuid, e);
            throw e; // 重新抛出异常，让调用方处理
        }
    }

    /**
     * 结束对话
     */
    public void endConversation(String conversationId, String userId) {
        log.info("对话已结束 - conversationId: {}, userId: {}", conversationId, userId);
    }

    /**
     * 从Spring AI Usage中记录Token使用情况
     */
    private void recordTokenUsageFromSpringAI(AIChatRequestVo request, String userId,
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
                    aiProvider,
                    aiModelType,
                    promptTokens,
                    completionTokens,
                    success,
                    responseTime
            );

            log.debug("Token使用记录已提交 - conversationId: {}, promptTokens: {}, completionTokens: {}",
                    new Object[]{request.getConversationId(), promptTokens, completionTokens});

        } catch (Exception e) {
            log.error("记录Token使用失败", e);
            // Token记录失败不影响主业务流程
        }
    }

    /**
     * 记录失败情况下的Token使用（通常没有Token信息）
     */
    private void recordTokenUsageOnFailure(AIChatRequestVo request, String userId,  long responseTime) {
        try {
            // 失败时通常没有Token消耗，记录0
            aiTokenUsageService.recordTokenUsageAsync(
                    request.getConversationId(),
                    request.getChatModelId(),
                    userId,
                    "unknown",
                    "unknown",
                    0L,
                    0L,
                    false,
                    responseTime
            );

            log.debug("失败请求Token记录已提交 - conversationId: {}", request.getConversationId());

        } catch (Exception e) {
            log.error("记录失败Token使用失败", e);
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
    public List<AiConversationContentVo> getConversationHistory(String conversationId, int limit) {
        return extAiConversationContentMapper.selectLastByConversationIdByLimit(conversationId, limit);
    }

    // 转换方法
    private AiConversationVo convertToVo(AiConversationEntity entity) {
        AiConversationVo vo = new AiConversationVo();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());

        // 字段映射：Entity使用标准审计字段，VO使用业务字段
        if (entity.getC_id() != null) {
            vo.setCreate_user(String.valueOf(entity.getC_id())); // Long -> String转换
        }
        if (entity.getC_time() != null) {
            // LocalDateTime -> Long时间戳转换
            vo.setCreate_time(entity.getC_time().atZone(java.time.ZoneId.of("Asia/Shanghai")).toEpochSecond() * 1000);
        }

        return vo;
    }

    private AiConversationContentVo convertContentToVo(AiConversationContentEntity entity) {
        AiConversationContentVo vo = new AiConversationContentVo();
        vo.setId(entity.getId());
        vo.setConversation_id(entity.getConversation_id());
        vo.setContent(entity.getContent());
        return vo;
    }
}