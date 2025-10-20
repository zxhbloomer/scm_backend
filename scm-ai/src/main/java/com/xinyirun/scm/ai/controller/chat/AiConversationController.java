package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.ai.config.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.config.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AIConversationUpdateRequestVo;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.core.service.chat.AiConversationContentService;
import com.xinyirun.scm.ai.core.service.chat.AiConversationService;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.ai.core.service.chat.AiTokenUsageService;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.ai.common.constant.AICommonConstants;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.mapper.client.user.MUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private AiModelConfigService aiModelConfigService;

    @Resource
    private MUserMapper mUserMapper;

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
        String userId =  operatorId.toString() ;
        String tenant_id = request.getConversationId().split("::", 2)[0];;
        request.setTenantId(tenant_id);
        // 在后台线程异步处理
        Flux<ChatResponseVo> responseFlux = Flux.<ChatResponseVo>create(fluxSink -> {
            try {
                // 设置多租户数据源
                DataSourceHelper.use(tenant_id);

                // 将aiType映射为modelType并获取模型配置
                String modelType = mapAiTypeToModelType(request.getAiType());
                AiModelConfigVo selectedModel = aiModelConfigService.getDefaultModelConfigWithKey(modelType);
                log.info("已选择AI模型: [提供商: {}, 模型: {}, ID: {}]",
                        selectedModel.getProvider(), selectedModel.getModelName(), selectedModel.getId());

                // 持久化原始提示词（使用选中的模型信息）
                aiConversationContentService.saveConversationContent(
                        request.getConversationId(),
                        AICommonConstants.MESSAGE_TYPE_USER,
                        request.getPrompt(),
                        selectedModel.getId().toString(),
                        selectedModel.getProvider(),
                        selectedModel.getModelName(),
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
                                                    selectedModel.getId().toString(),
                                                    selectedModel.getProvider(),
                                                    selectedModel.getModelName(),
                                                    operatorId
                                            );

                                            // 记录Token使用情况
                                            if (response.getUsage() != null) {
                                                // 通过conversationId获取conversation对象以获取tenant
                                                AiConversationVo conversation = aiConversationService.getConversation(request.getConversationId());

                                                aiConversationService.recordTokenUsageFromSpringAI(
                                                        request.getConversationId(),
                                                        null,                              // conversationContentId (ASSISTANT消息ID，在此处为null)
                                                        String.valueOf(userId),            // 将userId转换为String
                                                        selectedModel.getProvider(),       // AI提供商
                                                        selectedModel.getId().toString(),  // 模型源ID
                                                        selectedModel.getModelName(),      // 模型类型（model_name）
                                                        response.getUsage().getPromptTokens() != null ? response.getUsage().getPromptTokens().longValue() : 0L,
                                                        response.getUsage().getCompletionTokens() != null ? response.getUsage().getCompletionTokens().longValue() : 0L
                                                );
                                            }

                                            // 发送完成响应
                                            ChatResponseVo completeResponse = ChatResponseVo.createCompleteResponse(
                                                    response.getContent(), selectedModel.getId().toString());
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
            // 清理数据源连接
            DataSourceHelper.close();
        });

        return responseFlux;
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

}