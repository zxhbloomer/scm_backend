package com.xinyirun.scm.ai.core.service;

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
import com.xinyirun.scm.ai.common.exception.MsHttpResultCode;
import com.xinyirun.scm.ai.common.util.BeanUtils;
import com.xinyirun.scm.ai.common.util.LogUtils;
import com.xinyirun.scm.ai.core.mapper.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.AiConversationMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: jianxing
 * @CreateTime: 2025-05-28  13:44
 */
@DS("master")
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

    public String chat(AIChatRequest request, String userId) {
        // 持久化原始提示词
        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt());

        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .build();

        try {
            String assistantMessage = aiChatBaseService.chatWithMemory(aiChatOption)
                    .content();
            // 持久化回答内容
            aiChatBaseService.saveAssistantConversationContent(request.getConversationId(), assistantMessage);
            return assistantMessage;
        } catch (Exception e) {
            LogUtils.error("AI聊天失败", e);
            throw e;
        }
    }

    /**
     * 流式聊天
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param sessionId WebSocket会话ID
     */
    public void chatStream(AIChatRequest request, String userId, String sessionId) {
        // 持久化原始提示词
        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt());

        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
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
                    aiChatBaseService.saveAssistantConversationContent(request.getConversationId(), fullContent);

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
        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .build();

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
     * 保存用户消息内容 (从控制器调用)
     */
    public void saveUserConversationContent(String conversationId, String content) {
        aiChatBaseService.saveUserConversationContent(conversationId, content);
    }

    /**
     * 保存助手消息内容 (从控制器调用)
     */
    public void saveAssistantConversationContent(String conversationId, String content) {
        aiChatBaseService.saveAssistantConversationContent(conversationId, content);
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
    public void createConversationForUser(String convUuid, Long userId, String userName) {
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

            aiConversationMapper.insert(aiConversation);

            LogUtils.info("在chat-ai数据库中创建AI会话记录成功 - convUuid={}, userId={}, userName={}",
                    convUuid, userId, userName);
        } catch (Exception e) {
            log.error("创建AI会话记录失败：userId={}, convUuid={}", userId, convUuid, e);
            throw e; // 重新抛出异常，让调用方处理
        }
    }
}