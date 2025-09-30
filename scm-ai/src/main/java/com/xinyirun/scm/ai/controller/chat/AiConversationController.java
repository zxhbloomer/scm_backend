package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.ai.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AIConversationUpdateRequestVo;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.config.ScmMessageChatMemory;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.service.AiConversationContentService;
import com.xinyirun.scm.ai.service.AiConversationService;
import com.xinyirun.scm.ai.service.AiModelSelectionService;
import com.xinyirun.scm.ai.service.AiTokenUsageService;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.ai.constants.AICommonConstants;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * AI对话控制器
 *
 * 提供AI对话管理功能的REST API接口，包括对话的创建、查询、更新等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Slf4j
@Tag(name = "AI对话")
@RestController
@RequestMapping(value = "/api/v1/ai/conversation")
public class AiConversationController {

    @Resource
    private AiConversationService aiConversationService;

    @Resource
    private AiConversationContentService aiConversationContentService;

    @Resource
    private AiTokenUsageService aiTokenUsageService;

    @Resource
    private AiModelSelectionService aiModelSelectionService;

    /**
     * 获取用户对话列表
     */
    @GetMapping(value = "/list")
    @Operation(summary = "对话列表")
    @SysLogAnnotion("获取对话列表")
    public ResponseEntity<List<AiConversationVo>> list() {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        List<AiConversationVo> result = aiConversationService.list(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取对话内容列表
     */
    @GetMapping(value = "/chat/list/{conversationId}")
    @Operation(summary = "对话内容列表")
    @SysLogAnnotion("获取对话内容")
    public ResponseEntity<List<AiConversationContentVo>> chatList(@PathVariable String conversationId) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        List<AiConversationContentVo> result = aiConversationService.chatList(conversationId, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新对话
     */
    @PostMapping(value = "/add")
    @Operation(summary = "添加对话")
    @SysLogAnnotion("创建新对话")
    public ResponseEntity<AiConversationVo> add(@Validated @RequestBody AIChatRequestVo request) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        AiConversationVo result = aiConversationService.add(request, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新对话信息
     */
    @PostMapping(value = "/update")
    @Operation(summary = "修改对话标题")
    @SysLogAnnotion("修改对话标题")
    public ResponseEntity<AiConversationVo> update(@Validated @RequestBody AIConversationUpdateRequestVo request) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        AiConversationVo result = aiConversationService.update(request, userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除对话
     */
    @DeleteMapping(value = "/delete/{conversationId}")
    @Operation(summary = "删除对话")
    @SysLogAnnotion("删除对话")
    public ResponseEntity<Void> delete(@PathVariable String conversationId) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        aiConversationService.delete(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 清空对话内容
     */
    @PostMapping(value = "/clear/{conversationId}")
    @Operation(summary = "清空对话内容")
    @SysLogAnnotion("清空对话内容")
    public ResponseEntity<Void> clearConversationContent(@PathVariable String conversationId) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        aiConversationService.clearConversationContent(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 结束对话
     */
    @PostMapping(value = "/end/{conversationId}")
    @Operation(summary = "结束对话")
    @SysLogAnnotion("结束对话")
    public ResponseEntity<Void> endConversation(@PathVariable String conversationId) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        aiConversationService.endConversation(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * AI流式聊天
     * ai chat 入口
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式聊天 (Spring AI标准)")
    @SysLogAnnotion("AI流式聊天")
    public Flux<ChatResponseVo> chatStream(@Validated @RequestBody AIChatRequestVo request) {
        // 获取用户ID
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        // 在后台线程异步处理
        Flux<ChatResponseVo> responseFlux = Flux.<ChatResponseVo>create(fluxSink -> {
            try {
                // 设置多租户数据源和ThreadLocal
                if (request.getTenantId() != null && !request.getTenantId().isEmpty()) {
                    DataSourceHelper.use(request.getTenantId());
                    ScmMessageChatMemory.setCurrentTenant(request.getTenantId());
                }
                log.debug("租户数据库：{}", request.getTenantId());

                // 动态选择AI模型
                AiModelSourceEntity selectedModel = aiModelSelectionService.selectAvailableModel(request.getAiType());
                log.info("已选择AI模型: [提供商: {}, 模型: {}, ID: {}]",
                        selectedModel.getProvider_name(), selectedModel.getBase_name(), selectedModel.getId());

                // 持久化原始提示词（使用选中的模型信息）
                aiConversationContentService.saveConversationContent(
                        request.getConversationId(),
                        AICommonConstants.MESSAGE_TYPE_USER,
                        request.getPrompt(),
                        selectedModel.getId(),
                        selectedModel.getProvider_name(),
                        selectedModel.getBase_name(),
                        operatorId
                );

                // 创建回调流式处理器
                AiStreamHandler.CallbackStreamHandler streamHandler =
                        new AiStreamHandler.CallbackStreamHandler(
                                new AiStreamHandler.CallbackStreamHandler.StreamCallback() {
                                    @Override
                                    public void onStreamStart() {
                                        // 发送开始响应 - 空内容块
                                        ChatResponseVo startResponse = ChatResponseVo.createContentChunk("");
                                        fluxSink.next(startResponse);
                                    }

                                    @Override
                                    public void onStreamContent(String content) {
                                        // 发送内容块
                                        ChatResponseVo contentResponse = ChatResponseVo.createContentChunk(content);
                                        fluxSink.next(contentResponse);
                                    }

                                    @Override
                                    public void onStreamComplete(AiEngineAdapter.AiResponse response) {
                                        try {
                                            // 保存完整回复内容（使用选中的模型信息）
                                            aiConversationContentService.saveConversationContent(
                                                    request.getConversationId(),
                                                    AICommonConstants.MESSAGE_TYPE_ASSISTANT,
                                                    response.getContent(),
                                                    selectedModel.getId(),
                                                    selectedModel.getProvider_name(),
                                                    selectedModel.getBase_name(),
                                                    operatorId
                                            );

                                            // 记录Token使用情况
                                            if (response.getUsage() != null) {
                                                // 通过conversationId获取conversation对象以获取tenant
                                                AiConversationVo conversation = aiConversationService.getConversation(request.getConversationId());

                                                aiConversationService.recordTokenUsageFromSpringAI(
                                                        request.getConversationId(),
                                                        userId,
                                                        selectedModel.getProvider_name(),  // 使用动态选择的提供商
                                                        selectedModel.getId(),  // 使用模型源ID
                                                        response.getUsage().getPromptTokens() != null ? response.getUsage().getPromptTokens().longValue() : 0L,
                                                        response.getUsage().getCompletionTokens() != null ? response.getUsage().getCompletionTokens().longValue() : 0L
                                                );
                                            }

                                            // 发送完成响应
                                            ChatResponseVo completeResponse = ChatResponseVo.createCompleteResponse(
                                                    response.getContent(), selectedModel.getId());
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