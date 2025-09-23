package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.domain.AiConversation;
import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import com.xinyirun.scm.ai.bean.domain.AiConversationContentExample;
import com.xinyirun.scm.ai.bean.domain.AiConversationExample;
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
import org.apache.commons.lang3.StringUtils;
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
public class AiConversationService {

    @Resource
    AiChatBaseService aiChatBaseService;
    @Resource
    AiConversationMapper aiConversationMapper;
    @Resource
    AiConversationContentMapper aiConversationContentMapper;

    public String chat(AIChatRequest request, String userId) {
        // 持久化原始提示词
        aiChatBaseService.saveUserConversationContent(request.getConversationId(), request.getPrompt());

        AIChatOption aiChatOption = AIChatOption.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .build();
        String assistantMessage = aiChatBaseService.chatWithMemory(aiChatOption)
                .content();

        // 持久化回答内容
        aiChatBaseService.saveAssistantConversationContent(request.getConversationId(), assistantMessage);
        return assistantMessage;
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
        checkConversationPermission(userId, aiConversation);
        aiConversationMapper.deleteByPrimaryKey(conversationId);

        AiConversationContentExample example = new AiConversationContentExample();
        example.createCriteria().andConversationIdEqualTo(conversationId);
        aiConversationContentMapper.deleteByExample(example);
    }

    private void checkConversationPermission(String userId, AiConversation aiConversation) {
        if (aiConversation == null) {
            throw new MSException(MsHttpResultCode.NOT_FOUND);
        }
        if (!StringUtils.equals(aiConversation.getCreateUser(), userId)) {
            throw new MSException(MsHttpResultCode.FORBIDDEN);
        }
    }

    public List<AiConversation> list(String userId) {
        AiConversationExample example = new AiConversationExample();
        example.createCriteria().andCreateUserEqualTo(userId);
        return aiConversationMapper.selectByExample(example).reversed();
    }

    public List<AiConversationContent> chatList(String conversationId, String userId) {
        AiConversation aiConversation = aiConversationMapper.selectByPrimaryKey(conversationId);
        checkConversationPermission(userId, aiConversation);
        AiConversationContentExample example = new AiConversationContentExample();
        example.createCriteria().andConversationIdEqualTo(conversationId);
        example.setOrderByClause("create_time");
        return aiConversationContentMapper.selectByExampleWithBLOBs(example);
    }

    public AiConversation update(AIConversationUpdateRequest request, String userId) {
        AiConversation originConversation = aiConversationMapper.selectByPrimaryKey(request.getId());
        checkConversationPermission(userId, originConversation);
        AiConversation aiConversation = BeanUtils.copyBean(new AiConversation(), request);
        aiConversationMapper.updateByPrimaryKeySelective(aiConversation);
        originConversation.setTitle(aiConversation.getTitle());
        return originConversation;
    }
}