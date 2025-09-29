package com.xinyirun.scm.ai.controller.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.domain.AiConversation;
import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import com.xinyirun.scm.ai.bean.dto.request.AIChatRequest;
import com.xinyirun.scm.ai.bean.dto.request.AiConversationUpdateRequest;
import com.xinyirun.scm.ai.bean.dto.response.ChatResponse;
import com.xinyirun.scm.ai.common.util.SessionUtils;
import com.xinyirun.scm.ai.config.ScmMessageChatMemory;
import com.xinyirun.scm.ai.core.service.chat.AiConversationService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * @Author: jianxing
 * @CreateTime: 2025-05-28  13:44
 */
@Tag(name = "AI对话")
@RestController
@RequestMapping(value = "/api/v1/ai/conversation")
public class AiConversationController {

    @Resource
    private AiConversationService aiConversationService;

    @GetMapping(value = "/list")
    @Operation(summary = "对话列表")
    public List<AiConversation> list() {
        return aiConversationService.list(SessionUtils.getUserId());
    }

    @GetMapping(value = "/chat/list/{conversationId}")
    @Operation(summary = "对话内容列表")
    public List<AiConversationContent> chatList(@PathVariable String conversationId) {
        return aiConversationService.chatList(conversationId, SessionUtils.getUserId());
    }

    @PostMapping(value = "/add")
    @Operation(summary = "添加对话")
    public AiConversation add(@Validated @RequestBody AIChatRequest request) {
        return aiConversationService.add(request, SessionUtils.getUserId());
    }

    @PostMapping(value = "/update")
    @Operation(summary = "修改对话标题")
    public AiConversation add(@Validated @RequestBody AiConversationUpdateRequest request) {
        return aiConversationService.update(request, SessionUtils.getUserId());
    }

//    @PostMapping(value = "/chat")
//    @Operation(summary = "聊天")
//    public String chat(@Validated @RequestBody AIChatRequest request) {
//        return aiConversationService.chat(request, SessionUtils.getUserId());
//    }

    @DeleteMapping(value = "/delete/{conversationId}")
    @Operation(summary = "删除对话")
    public void delete(@PathVariable String conversationId) {
        aiConversationService.delete(conversationId, SessionUtils.getUserId());
    }

    @PostMapping(value = "/clear/{conversationId}")
    @Operation(summary = "清空对话内容")
    public void clearConversationContent(@PathVariable String conversationId) {
        aiConversationService.clearConversationContent(conversationId, SessionUtils.getUserId());
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式聊天 (Spring AI标准)")
    public Flux<ChatResponse> chatStream(@Validated @RequestBody AIChatRequest request) {
        // 创建Reactor Sink用于发送数据
        Sinks.Many<ChatResponse> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 获取用户ID
        String userId = SessionUtils.getUserId();

        // 在后台线程异步处理
        Flux<ChatResponse> responseFlux = Flux.<ChatResponse>create(fluxSink -> {
            try {
                // 设置多租户数据源和ThreadLocal
                if (request.getTenantId() != null && !request.getTenantId().isEmpty()) {
                    DataSourceHelper.use(request.getTenantId());
                    ScmMessageChatMemory.setCurrentTenant(request.getTenantId());
                }
                // 持久化原始提示词（需要获取模型ID）
                // 注意：这里使用chatModelId作为modelSourceId，与业务逻辑保持一致
                aiConversationService.saveUserConversationContent(request.getConversationId(), request.getPrompt(), request.getChatModelId());

                // 创建回调流式处理器
                AiStreamHandler.CallbackStreamHandler streamHandler =
                        new AiStreamHandler.CallbackStreamHandler(
                                new AiStreamHandler.CallbackStreamHandler.StreamCallback() {
                                    @Override
                                    public void onStreamStart() {
                                        // 发送开始响应 - 空内容块
                                        ChatResponse startResponse = ChatResponse.createContentChunk("");
                                        fluxSink.next(startResponse);
                                    }

                                    @Override
                                    public void onStreamContent(String content) {
                                        // 发送内容块
                                        ChatResponse contentResponse = ChatResponse.createContentChunk(content);
                                        fluxSink.next(contentResponse);
                                    }

                                    @Override
                                    public void onStreamComplete(AiEngineAdapter.AiResponse response) {
                                        try {
                                            // 保存完整回复内容
                                            aiConversationService.saveAssistantConversationContent(
                                                    request.getConversationId(), response.getContent(), request.getChatModelId());

                                            // 记录Token使用情况
                                            if (response.getUsage() != null) {
                                                // 通过conversationId获取conversation对象以获取tenant
                                                AiConversation conversation = aiConversationService.getConversation(request.getConversationId());
                                                String tenant = conversation != null ? conversation.getTenant() : null;

                                                aiConversationService.recordTokenUsageFromSpringAI(
                                                        request.getConversationId(),
                                                        userId,
                                                        tenant,  // 从ai_conversation表中获取的tenant
                                                        "OpenAI",  // 根据实际AI提供商
                                                        request.getChatModelId(),
                                                        response.getUsage().getPromptTokens() != null ? response.getUsage().getPromptTokens().longValue() : 0L,
                                                        response.getUsage().getCompletionTokens() != null ? response.getUsage().getCompletionTokens().longValue() : 0L
                                                );
                                            }

                                            // 发送完成响应
                                            ChatResponse completeResponse = ChatResponse.createCompleteResponse(
                                                    response.getContent(), request.getChatModelId());
                                            fluxSink.next(completeResponse);
                                            fluxSink.complete();
                                        } catch (Exception e) {
                                            fluxSink.error(e);
                                        }
                                    }

                                    @Override
                                    public void onStreamError(Throwable error) {
                                        fluxSink.error(error);
                                    }
                                });

                // 调用流式聊天服务
                aiConversationService.chatStreamWithCallback(request, userId, streamHandler);

            } catch (Exception e) {
                fluxSink.error(e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic()) // 在弹性线程池中执行
        .doFinally(signalType -> {
            // 清理数据源连接和ThreadLocal
            DataSourceHelper.close();
            ScmMessageChatMemory.clearCurrentTenant();
        });

        return responseFlux;
    }

}