package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.config.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.config.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationEntity;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIConversationUpdateRequestVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiPromptVo;
import com.xinyirun.scm.ai.common.exception.MSException;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationMapper;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.core.mapper.chat.ExtAiConversationContentMapper;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private ExtAiConversationContentMapper extAiConversationContentMapper;

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
        AiConversationVo vo = new AiConversationVo();
        BeanUtils.copyProperties(aiConversation, vo);
        return vo;
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
        wrapper.eq("c_id", Long.valueOf(userId)); // 使用c_id字段查询创建人
        wrapper.orderByDesc("c_time");
        List<AiConversationEntity> entities = aiConversationMapper.selectList(wrapper);
        return entities.stream().map(entity -> {
            AiConversationVo vo = new AiConversationVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据conversationId获取对话信息
     * @param conversationId 对话ID
     * @return 对话信息
     */
    public AiConversationVo getConversation(String conversationId) {
        AiConversationEntity entity = aiConversationMapper.selectById(conversationId);
        if (entity != null) {
            AiConversationVo vo = new AiConversationVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }


    /**
     * 从Spring AI Usage接口记录Token使用情况（流式聊天回调）
     * 该方法与Spring AI框架集成，用于记录实际的Token消耗
     *
     * @param conversationId 对话ID
     * @param userId 用户ID
     * @param aiProvider AI提供商
     * @param modelSourceId 模型源ID
     * @param modelType 模型类型（base_name）
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     */
    public void recordTokenUsageFromSpringAI(String conversationId, String userId,
                                            String aiProvider, String modelSourceId, String modelType,
                                            Long promptTokens, Long completionTokens) {
        try {
            // 直接使用传入的modelSourceId，无需查找
            aiTokenUsageService.recordTokenUsageAsync(
                    conversationId,
                    modelSourceId, // 直接使用传入的model_source_id
                    userId,
                    aiProvider,
                    modelType,     // 使用真正的模型类型
                    promptTokens,
                    completionTokens,
                    true, // success
                    0L    // responseTime
            );

            log.info("流式聊天回调Token使用记录 - conversationId: {}, userId: {}, modelSourceId: {}, modelType: {}, tokens: {}",
                    conversationId, userId, modelSourceId, modelType, (promptTokens + completionTokens));

        } catch (Exception e) {
            log.error("流式聊天回调Token使用记录失败", e);
            // 记录失败不抛出异常，避免影响主业务流程
        }
    }

    public List<AiConversationContentVo> chatList(String conversationId, String userId) {
        QueryWrapper<AiConversationContentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId);
        wrapper.orderByAsc("c_time");
        List<AiConversationContentEntity> entities = aiConversationContentMapper.selectList(wrapper);
        return entities.stream().map(entity -> {
            AiConversationContentVo vo = new AiConversationContentVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
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
     * 获取对话历史记录（用于ChatMemory）
     * @param conversationId 对话ID
     * @param limit 限制条数
     * @return 对话内容列表
     */
    public List<AiConversationContentVo> getConversationHistory(String conversationId, int limit) {
        return extAiConversationContentMapper.selectLastByConversationIdByLimit(conversationId, limit);
    }

}