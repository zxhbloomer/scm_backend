package com.xinyirun.scm.ai.controller.chat;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.common.constant.WorkflowStateConstant;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
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
import com.xinyirun.scm.ai.core.service.workflow.WorkflowRoutingService;
import com.xinyirun.scm.ai.core.service.workflow.AiConversationWorkflowRuntimeService;
import com.xinyirun.scm.ai.core.service.workflow.AiConversationWorkflowRuntimeNodeService;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.mapper.client.user.MUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;


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

    @Resource
    private WorkflowStarter workflowStarter;

    @Resource
    private WorkflowRoutingService workflowRoutingService;

    @Resource
    private AiConversationWorkflowRuntimeNodeService conversationWorkflowRuntimeNodeService;

    @Resource
    private AiConversationWorkflowRuntimeService conversationWorkflowRuntimeService;

    /**
     * Feature Toggle: 工作流路由功能开关
     * 默认false,生产环境通过配置文件启用
     */
    @Value("${scm.ai.workflow.enabled:false}")
    private boolean enableWorkflowRouting;

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
     * 获取AI Chat工作流运行时详情
     *
     * @param runtimeUuid AI Chat工作流运行时UUID
     * @return 运行时详情
     */
    @GetMapping(value = "/workflow/runtime/{runtimeUuid}")
    @Operation(summary = "获取AI Chat工作流运行时详情")
    @SysLogAnnotion("获取AI Chat工作流运行时详情")
    public ResponseEntity<AiConversationWorkflowRuntimeVo> getRuntimeDetail(@PathVariable String runtimeUuid) {
        try {
            log.info("【AI-Chat-Runtime详情】查询runtime详情, runtimeUuid: {}", runtimeUuid);
            AiConversationWorkflowRuntimeVo runtime = conversationWorkflowRuntimeService.getDetailByUuid(runtimeUuid);
            if (runtime == null) {
                throw new AiBusinessException("工作流运行时实例不存在: " + runtimeUuid);
            }
            return ResponseEntity.ok(runtime);
        } catch (Exception e) {
            log.error("获取AI Chat工作流运行时详情失败, runtimeUuid: {}", runtimeUuid, e);
            throw new AiBusinessException("获取工作流运行时详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取AI Chat工作流运行时节点详情列表
     *
     * @param runtimeUuid AI Chat工作流运行时UUID
     * @return 节点详情列表
     */
    @GetMapping(value = "/workflow/runtime/nodes/{runtimeUuid}")
    @Operation(summary = "获取AI Chat工作流执行详情")
    @SysLogAnnotion("获取AI Chat工作流执行详情")
    public ResponseEntity<List<AiConversationWorkflowRuntimeNodeVo>> listRuntimeNodes(@PathVariable String runtimeUuid) {
        try {
            log.info("【AI-Chat-Runtime节点】查询runtime节点列表, runtimeUuid: {}", runtimeUuid);

            // 先根据UUID查询runtime,获取ID
            AiConversationWorkflowRuntimeVo runtime = conversationWorkflowRuntimeService.getDetailByUuid(runtimeUuid);
            if (runtime == null) {
                throw new AiBusinessException("工作流运行时实例不存在: " + runtimeUuid);
            }

            List<AiConversationWorkflowRuntimeNodeVo> nodes =
                conversationWorkflowRuntimeNodeService.listByWfRuntimeId(runtime.getId());
            return ResponseEntity.ok(nodes);
        } catch (Exception e) {
            log.error("获取AI Chat工作流执行详情失败, runtimeUuid: {}", runtimeUuid, e);
            throw new AiBusinessException("获取工作流执行详情失败: " + e.getMessage());
        }
    }

    /**
     * 删除AI Chat聊天消息记录
     *
     * @param messageId 消息ID
     * @return 删除结果
     */
    @DeleteMapping(value = "/message/{messageId}")
    @Operation(summary = "删除聊天消息", description = "物理删除AI Chat聊天消息记录")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId) {
        try {
            log.info("删除AI Chat聊天消息: messageId={}", messageId);

            boolean success = aiConversationContentService.deleteByMessageId(messageId);

            if (success) {
                return ResponseEntity.ok("删除成功");
            } else {
                return ResponseEntity.status(500).body("删除失败");
            }
        } catch (Exception e) {
            log.error("删除AI Chat聊天消息失败: messageId={}", messageId, e);
            return ResponseEntity.status(500).body("删除失败: " + e.getMessage());
        }
    }

    /**
     * AI流式聊天
     * ai chat 入口 (支持工作流路由+智能意图判断)
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式聊天 (支持工作流路由+智能意图判断)")
    @SysLogAnnotion("AI流式聊天")
    public Flux<ChatResponseVo> chatStream(@Validated @RequestBody AIChatRequestVo request) {
        // Feature Toggle: 工作流路由功能开关
        if (!enableWorkflowRouting) {
            // 工作流功能未启用，走原有逻辑
            return chatStreamWithoutWorkflow(request);
        }

        // 获取用户上下文（外层变量，供闭包捕获）
        String conversationId = request.getConversationId();
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId.toString();
        String tenantId = conversationId.split("::", 2)[0];
        request.setTenantId(tenantId);

        // 捕获用户问题和AI回复内容（用于最终保存到数据库）
        String userPrompt = request.getPrompt();
        StringBuilder aiResponseBuilder = new StringBuilder();

        // Spring AI模式：Mono → Flux 链式调用
        return Mono.fromCallable(() -> {
                    // Step 1: 异步查询对话状态
                    DataSourceHelper.use(tenantId);
                    AiConversationVo conversation = aiConversationService.getConversation(conversationId);
                    if (conversation == null) {
                        throw new AiBusinessException("对话不存在: " + conversationId);
                    }
                    return conversation;
                })
                .flatMapMany(conversation -> {
                    // Step 2: 根据状态决定执行路径（Mono → Flux转换）
                    String workflowState = conversation.getWorkflowState();
                    if (workflowState == null) {
                        workflowState = WorkflowStateConstant.STATE_IDLE;
                    }

                    if (WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT.equals(workflowState)) {
                        // KISS优化5: 智能路由判断
                        boolean isContinuation = isInputContinuation(
                            request.getPrompt(),
                            conversation.getCurrentWorkflowUuid(),
                            operatorId
                        );

                        if (isContinuation) {
                            // 场景2A: 继续当前工作流(如"ORD-001"、"继续")
                            String runtimeUuid = conversation.getCurrentRuntimeUuid();

                            // KISS优化2: 删除预更新状态,只在事件响应时更新
                            return workflowStarter.resumeFlowAsFlux(
                                runtimeUuid,
                                conversation.getCurrentWorkflowUuid(),
                                request.getPrompt(),
                                tenantId,
                                WorkflowCallSource.AI_CHAT,
                                conversationId
                            );
                        } else {
                            // 场景2B: 新意图,清理旧状态,路由新工作流
                            aiConversationService.updateWorkflowState(
                                conversationId,
                                WorkflowStateConstant.STATE_IDLE,
                                null,
                                null
                            );

                            String newWorkflowUuid = workflowRoutingService.route(
                                request.getPrompt(),
                                operatorId,
                                null
                            );

                            if (newWorkflowUuid == null) {
                                newWorkflowUuid = getFallbackWorkflowUuid(operatorId);
                                if (newWorkflowUuid == null) {
                                    return Flux.error(new AiBusinessException("没有可用的工作流"));
                                }
                            }

                            List<JSONObject> userInputs = List.of(
                                new JSONObject()
                                    .fluentPut("name", "var_user_input")
                                    .fluentPut("content", new JSONObject()
                                        .fluentPut("type", 1)
                                        .fluentPut("title", "用户输入")
                                        .fluentPut("value", request.getPrompt())
                                    )
                            );
                            return workflowStarter.streaming(newWorkflowUuid, userInputs, tenantId, WorkflowCallSource.AI_CHAT, conversationId);
                        }

                    } else {
                        // 场景1：新请求 - 路由到工作流
                        String workflowUuid = workflowRoutingService.route(
                            request.getPrompt(),
                            operatorId,
                            null
                        );

                        if (workflowUuid == null) {
                            // 获取兜底工作流
                            workflowUuid = getFallbackWorkflowUuid(operatorId);
                            if (workflowUuid == null) {
                                return Flux.error(new AiBusinessException("没有可用的工作流"));
                            }
                        }

                        // KISS优化2: 不预更新状态,直接执行工作流
                        List<JSONObject> userInputs = List.of(
                            new JSONObject()
                                .fluentPut("name", "var_user_input")
                                .fluentPut("content", new JSONObject()
                                    .fluentPut("type", 1)
                                    .fluentPut("title", "用户输入")
                                    .fluentPut("value", request.getPrompt())
                                )
                        );
                        return workflowStarter.streaming(workflowUuid, userInputs, tenantId, WorkflowCallSource.AI_CHAT, conversationId);
                    }
                })
                // Step 3: 转换工作流事件为响应格式
                .map(event -> convertWorkflowEventToResponse(event))

                // Step 4: KISS优化2 - 只在workflow事件响应时更新状态(2次)
                // 注意：使用map而非doOnNext，确保response修改在发送前完成
                .map(response -> {
                    // 累积AI回复内容（用于保存到数据库）
                    if (response.getResults() != null && !response.getResults().isEmpty()) {
                        ChatResponseVo.Generation generation = response.getResults().get(0);
                        if (generation.getOutput() != null && generation.getOutput().getContent() != null) {
                            aiResponseBuilder.append(generation.getOutput().getContent());
                        }
                    }

                    if (Boolean.TRUE.equals(response.getIsWaitingInput())) {
                        // 更新1: 工作流等待用户输入
                        aiConversationService.updateWorkflowState(
                            conversationId,
                            WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT,
                            response.getWorkflowUuid(),
                            response.getRuntimeUuid()
                        );
                    }
                    if (Boolean.TRUE.equals(response.getIsComplete())) {
                        // 注入累积的LLM输出到done事件响应
                        String fullContent = aiResponseBuilder.toString();
                        if (!fullContent.isEmpty()) {
                            response.setResults(List.of(
                                ChatResponseVo.Generation.builder()
                                    .output(ChatResponseVo.AssistantMessage.builder()
                                        .content(fullContent)
                                        .build())
                                    .build()
                            ));
                        }

                        // 更新2: 工作流完成
                        aiConversationService.updateWorkflowState(
                            conversationId,
                            WorkflowStateConstant.STATE_IDLE,
                            null,
                            null
                        );

                        // 保存对话内容到数据库
                        try {
                            String finalAiResponse = aiResponseBuilder.toString();
                            String runtimeUuid = response.getRuntimeUuid(); // 获取运行时UUID

                            log.info("【AI-Chat-保存】准备保存对话内容: conversationId={}, runtimeUuid={}, userPrompt长度={}, aiResponse长度={}",
                                conversationId, runtimeUuid, userPrompt.length(), finalAiResponse.length());

                            if (!userPrompt.isEmpty() || !finalAiResponse.isEmpty()) {
                                // 保存用户问题（ROLE=1，无需runtime_uuid）
                                aiConversationContentService.saveContent(
                                    conversationId,
                                    1, // ROLE=1表示用户
                                    userPrompt,
                                    operatorId,
                                    null // 用户消息不关联workflow runtime
                                );
                                log.info("【AI-Chat-保存】用户消息已保存");

                                // 保存AI回复（ROLE=2，关联runtime_uuid）
                                // aiResponseBuilder累积的内容已经是纯文本（done事件中已提取）
                                var aiContentVo = aiConversationContentService.saveContent(
                                    conversationId,
                                    2, // ROLE=2表示AI
                                    finalAiResponse,
                                    operatorId,
                                    runtimeUuid // 传递运行时UUID
                                );
                                // 将AI消息ID设置到响应中，供前端更新本地消息ID
                                if (aiContentVo != null && aiContentVo.getMessage_id() != null) {
                                    response.setMessageId(aiContentVo.getMessage_id());
                                    log.info("【AI-Chat-保存】AI消息已保存, messageId={}, runtimeUuid={}",
                                        aiContentVo.getMessage_id(), runtimeUuid);
                                } else {
                                    log.info("【AI-Chat-保存】AI消息已保存, runtimeUuid={}", runtimeUuid);
                                }

                                log.info("对话内容已保存: conversationId={}, userPrompt={}, aiResponse={}",
                                    conversationId,
                                    userPrompt.length() > 50 ? userPrompt.substring(0, 50) + "..." : userPrompt,
                                    finalAiResponse.length() > 50 ? finalAiResponse.substring(0, 50) + "..." : finalAiResponse
                                );
                            }
                        } catch (Exception e) {
                            log.error("保存对话内容失败: conversationId={}", conversationId, e);
                        }
                    }
                    return response; // 返回修改后的response
                })

                // Step 5: 错误和资源管理(统一处理)
                .timeout(Duration.ofMinutes(30)) // KISS优化3: 延长到30分钟
                .onErrorResume(e -> {
                    // 统一错误处理(包含timeout和普通异常)
                    log.error("工作流执行异常: conversationId={}", conversationId, e);
                    aiConversationService.updateWorkflowState(
                        conversationId,
                        WorkflowStateConstant.STATE_IDLE,
                        null,
                        null
                    );
                    String errorMsg = e instanceof TimeoutException ?
                        "工作流执行超时，已自动取消" : "工作流执行失败: " + e.getMessage();
                    return Flux.just(ChatResponseVo.createErrorResponse(errorMsg));
                })
                .doOnCancel(() -> {
                    // 用户取消: 保留状态(支持误点停止/临时中断)
                    log.info("用户取消工作流执行: conversationId={}", conversationId);
                })
                .doFinally(signalType -> {
                    DataSourceHelper.close();
                });
    }

    /**
     * KISS优化5: 智能路由判断 - 判断用户输入是继续当前工作流还是新意图
     *
     * @param userInput 用户输入
     * @param currentWorkflowUuid 当前工作流UUID
     * @param userId 用户ID
     * @return true-继续当前工作流, false-新意图需要路由
     */
    private boolean isInputContinuation(String userInput, String currentWorkflowUuid, Long userId) {
        // 策略1: 明确的继续关键词
        if (userInput.matches("(?i)继续|continue|是|好|确认|ok")) {
            return true;
        }

        // 策略2: 短输入(<20字符),可能是具体值(订单号/数量等)
        if (userInput.length() <= 20) {
            return true;
        }

        // 策略3: 路由判断 - 是否匹配到新工作流
        String newWorkflowUuid = workflowRoutingService.route(userInput, userId, null);

        // 没有匹配新工作流,或匹配的还是当前工作流 → 继续
        return newWorkflowUuid == null || newWorkflowUuid.equals(currentWorkflowUuid);
    }

    /**
     * 转换WorkflowEventVo为ChatResponseVo
     */
    private ChatResponseVo convertWorkflowEventToResponse(WorkflowEventVo event) {
        ChatResponseVo.ChatResponseVoBuilder builder = ChatResponseVo.builder();

        // NODE_OUTPUT事件特殊处理：提取MCP工具返回值
        String eventName = event.getEvent();
        boolean isNodeOutput = eventName != null && eventName.startsWith("[NODE_OUTPUT_");
        boolean isNodeInput = eventName != null && eventName.startsWith("[NODE_INPUT_");

        if (isNodeOutput && event.getData() != null) {
            // 解析NODE_OUTPUT事件,查找MCP工具调用结果
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                String name = dataJson.getString("name");

                // 检查是否是MCP工具调用结果 (name格式: mcp_tool_call_xxx)
                if (name != null && name.startsWith("mcp_tool_call_")) {
                    JSONObject content = dataJson.getJSONObject("content");
                    if (content != null && content.getInteger("type") == 3) {
                        // type=3表示MCP工具调用
                        JSONObject value = content.getJSONObject("value");
                        String toolName = value.getString("toolName");  // ← 修正:从value中获取toolName

                        log.info("【MCP工具结果】检测到MCP工具调用: toolName={}, value={}", toolName, value);

                        // 将MCP工具结果添加到response中
                        List<Map<String, Object>> mcpResults = new ArrayList<>();
                        Map<String, Object> toolResult = new HashMap<>();
                        toolResult.put("toolName", toolName);
                        toolResult.put("result", value);
                        mcpResults.add(toolResult);
                        builder.mcpToolResults(mcpResults);

                        // NODE_OUTPUT事件不生成文本内容
                        return builder.build();
                    }
                }
            } catch (Exception e) {
                log.warn("解析NODE_OUTPUT事件中的MCP工具结果失败", e);
            }
        }

        // 跳过 NODE_INPUT 和 NODE_OUTPUT 事件的内容提取
        // 这些事件用于前端实时显示工作流状态，不应累积到最终对话内容中
        boolean isNodeInputOutput = isNodeInput || isNodeOutput;

        // 设置基础字段
        if (event.getData() != null && !isNodeInputOutput) {
            // 解析event.data JSON获取内容
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                String content = dataJson.getString("content");
                if (content != null) {
                    builder.results(List.of(
                        ChatResponseVo.Generation.builder()
                            .output(ChatResponseVo.AssistantMessage.builder()
                                .content(content)
                                .build())
                            .build()
                    ));
                }
            } catch (Exception e) {
                // 如果data不是JSON,直接作为content
                builder.results(List.of(
                    ChatResponseVo.Generation.builder()
                        .output(ChatResponseVo.AssistantMessage.builder()
                            .content(event.getData())
                            .build())
                        .build()
                ));
            }
        }

        ChatResponseVo response = builder.build();

        // 标记特殊事件并提取runtime信息
        if ("done".equals(event.getEvent())) {
            response.setIsComplete(true);
            // 尝试从event.data中提取runtime信息和content
            if (event.getData() != null) {
                try {
                    JSONObject dataJson = JSONObject.parseObject(event.getData());

                    // 提取content字段（新格式：done事件的data是JSON对象，包含content和runtime信息）
                    String content = dataJson.getString("content");

                    if (content != null) {
                        // 从工作流JSON输出中提取纯文本内容
                        // JSON格式: {"output":{"type":1,"value":"实际文本"}}
                        try {
                            JSONObject contentJson = JSONObject.parseObject(content);
                            if (contentJson.containsKey("output")) {
                                JSONObject output = contentJson.getJSONObject("output");
                                if (output != null && output.containsKey("value")) {
                                    content = output.getString("value");
                                    log.debug("从done事件的工作流JSON输出中提取文本: {}", content);
                                }
                            }
                        } catch (Exception e) {
                            // 如果content不是JSON格式(普通Chat),直接使用原始内容
                            log.debug("done事件content不是JSON格式,使用原始内容");
                        }

                        // 更新response中的content（已提取纯文本）
                        response.getResults().get(0).getOutput().setContent(content);
                    }

                    // 提取runtime信息
                    String runtimeUuid = dataJson.getString("runtime_uuid");
                    Long runtimeId = dataJson.getLong("runtime_id");
                    String workflowUuid = dataJson.getString("workflow_uuid");

                    log.info("【AI-Chat-Done事件】提取runtime信息: runtimeUuid={}, runtimeId={}, workflowUuid={}",
                        runtimeUuid, runtimeId, workflowUuid);

                    if (runtimeUuid != null) {
                        response.setRuntimeUuid(runtimeUuid);
                        log.info("【AI-Chat-Done事件】已设置response.runtimeUuid={}", runtimeUuid);
                    }
                    if (runtimeId != null) {
                        response.setRuntimeId(runtimeId);
                    }
                    if (workflowUuid != null) {
                        response.setWorkflowUuid(workflowUuid);
                    }
                } catch (Exception e) {
                    // 如果解析失败,忽略runtime信息提取
                    log.debug("Failed to extract runtime info from done event", e);
                }
            }
        }
        if (event.getEvent() != null && event.getEvent().startsWith("[NODE_WAIT_FEEDBACK_BY_")) {
            response.setIsWaitingInput(true);
            response.setRuntimeUuid(extractRuntimeUuidFromEvent(event));
            response.setWorkflowUuid(extractWorkflowUuidFromEvent(event));
        }

        return response;
    }

    /**
     * 从事件中提取runtimeUuid
     */
    private String extractRuntimeUuidFromEvent(WorkflowEventVo event) {
        // 从event.data JSON中提取runtime_uuid
        if (event.getData() != null) {
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                return dataJson.getString("runtime_uuid");
            } catch (Exception e) {
                log.warn("Failed to extract runtimeUuid from event", e);
            }
        }
        return null;
    }

    /**
     * 从事件中提取workflowUuid
     */
    private String extractWorkflowUuidFromEvent(WorkflowEventVo event) {
        // 从event.data JSON中提取workflow_uuid
        if (event.getData() != null) {
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                return dataJson.getString("workflow_uuid");
            } catch (Exception e) {
                log.warn("Failed to extract workflowUuid from event", e);
            }
        }
        return null;
    }

    /**
     * 获取用户的兜底工作流UUID
     */
    private String getFallbackWorkflowUuid(Long userId) {
        // TODO: 查询用户配置的默认工作流
        // 暂时返回null,让调用方返回error
        return null;
    }

    /**
     * 原有逻辑（不启用工作流路由时使用）
     */
    private Flux<ChatResponseVo> chatStreamWithoutWorkflow(AIChatRequestVo request) {
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
                        AiMessageTypeConstant.MESSAGE_TYPE_USER,
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
                                                    AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT,
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