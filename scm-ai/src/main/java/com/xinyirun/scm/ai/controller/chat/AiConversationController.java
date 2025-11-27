package com.xinyirun.scm.ai.controller.chat;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeNodeVo;
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
import com.xinyirun.scm.ai.core.service.workflow.AiConversationRuntimeService;
import com.xinyirun.scm.ai.core.service.workflow.AiConversationRuntimeNodeService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowNodeService;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import lombok.Data;
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
import jakarta.annotation.PostConstruct;


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

    @PostConstruct
    public void init() {
        log.info("【AiConversationController】初始化完成, enableWorkflowRouting={}", enableWorkflowRouting);
    }

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
    private AiConversationRuntimeNodeService conversationRuntimeNodeService;

    @Resource
    private AiConversationRuntimeService conversationRuntimeService;

    @Resource
    private AiWorkflowService aiWorkflowService;

    @Resource
    private AiWorkflowNodeService workflowNodeService;

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
     *
     * 注意：此API会删除:
     * 1. ai_conversation_runtime 表中的运行记录
     * 2. ai_conversation_runtime_node 表中的节点记录
     * 3. ai_conversation_content 表中的消息记录
     * 但保留 ai_conversation 表中的对话主记录
     */
    @PostMapping(value = "/clear/{conversationId}")
    @Operation(summary = "清空对话内容")
    @SysLogAnnotion("清空对话内容")
    public ResponseEntity<Void> clearConversationContent(@PathVariable String conversationId) {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : "system";

        log.info("🧹【API-清空对话】收到清空对话请求 - conversationId: {}, userId: {}", conversationId, userId);
        aiConversationService.clearConversationContent(conversationId, userId);
        log.info("🧹【API-清空对话】清空对话完成 - conversationId: {}", conversationId);
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
    public ResponseEntity<AiConversationRuntimeVo> getRuntimeDetail(@PathVariable String runtimeUuid) {
        try {
            log.info("【AI-Chat-Runtime详情】查询runtime详情, runtimeUuid: {}", runtimeUuid);
            AiConversationRuntimeVo runtime = conversationRuntimeService.getDetailByUuid(runtimeUuid);
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
    public ResponseEntity<List<AiConversationRuntimeNodeVo>> listRuntimeNodes(@PathVariable String runtimeUuid) {
        try {
            log.info("【AI-Chat-Runtime节点】查询runtime节点列表, runtimeUuid: {}", runtimeUuid);

            // 先根据UUID查询runtime,获取ID
            AiConversationRuntimeVo runtime = conversationRuntimeService.getDetailByUuid(runtimeUuid);
            if (runtime == null) {
                throw new AiBusinessException("工作流运行时实例不存在: " + runtimeUuid);
            }

            List<AiConversationRuntimeNodeVo> nodes =
                conversationRuntimeNodeService.listByWfRuntimeId(runtime.getId());
            return ResponseEntity.ok(nodes);
        } catch (Exception e) {
            log.error("获取AI Chat工作流执行详情失败, runtimeUuid: {}", runtimeUuid, e);
            throw new AiBusinessException("获取工作流执行详情失败: " + e.getMessage());
        }
    }

    /**
     * 删除AI Chat聊天消息记录
     *
     * 注意：此API只删除ai_conversation_content表中的单条消息记录
     * 不会删除ai_conversation_runtime和ai_conversation_runtime_node表的数据
     * 如需清空整个对话，请使用 POST /clear/{conversationId}
     *
     * @param messageId 消息ID
     * @return 删除结果
     */
    @DeleteMapping(value = "/message/{messageId}")
    @Operation(summary = "删除聊天消息", description = "物理删除AI Chat聊天消息记录(只删除单条消息)")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId) {
        try {
            log.info("🗑️【API-删除单条消息】收到删除请求 - messageId: {} (注意: 此操作只删除ai_conversation_content中的单条记录)", messageId);

            boolean success = aiConversationContentService.deleteByMessageId(messageId);

            if (success) {
                log.info("🗑️【API-删除单条消息】删除成功 - messageId: {}", messageId);
                return ResponseEntity.ok("删除成功");
            } else {
                log.warn("🗑️【API-删除单条消息】删除失败 - messageId: {}", messageId);
                return ResponseEntity.status(500).body("删除失败");
            }
        } catch (Exception e) {
            log.error("🗑️【API-删除单条消息】删除异常 - messageId: {}", messageId, e);
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
        log.info("【chatStream】入口, enableWorkflowRouting={}, prompt={}",
                 enableWorkflowRouting, request.getPrompt());

        // Feature Toggle: 工作流路由功能开关
        if (!enableWorkflowRouting) {
            log.info("【chatStream】走原有逻辑(工作流未启用)");
            // 工作流功能未启用，走原有逻辑
            return chatStreamWithoutWorkflow(request);
        }

        log.info("【chatStream】进入工作流路由模式");

        // 获取用户上下文（外层变量，供闭包捕获）
        String conversationId = request.getConversationId();
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId.toString();
        String tenantId = conversationId.split("::", 2)[0];
        request.setTenantId(tenantId);

        // 获取页面上下文(用于MCP工具)
        Map<String, Object> pageContext = request.getPageContext();

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
                            ).map(event -> convertWorkflowEventToResponse(event));
                        } else {
                            // 场景2B: 新意图,清理旧状态,使用Orchestrator-Workers模式路由新工作流
                            aiConversationService.updateWorkflowState(
                                conversationId,
                                WorkflowStateConstant.STATE_IDLE,
                                null,
                                null
                            );

                            // 使用新的routeAndExecute方法(Orchestrator-Workers模式)
                            return workflowRoutingService.routeAndExecute(
                                request.getPrompt(),
                                operatorId,
                                tenantId,
                                conversationId,
                                pageContext,
                                null  // specifiedWorkflowUuid为null,走Orchestrator-Workers模式
                            );
                        }

                    } else {
                        // 场景1：新请求 - 使用Orchestrator-Workers模式路由并执行
                        return workflowRoutingService.routeAndExecute(
                            request.getPrompt(),
                            operatorId,
                            tenantId,
                            conversationId,
                            pageContext,
                            null  // specifiedWorkflowUuid为null,走Orchestrator-Workers模式
                        );
                    }
                })
                // Step 3: KISS优化2 - 只在workflow事件响应时更新状态(2次)
                // 注意：使用map而非doOnNext，确保response修改在发送前完成
                .map(response -> {
                    // 累积AI回复内容（用于保存到数据库）
                    // 【修复】只累积中间chunk的内容，isComplete=true时响应已包含完整内容
                    if (!Boolean.TRUE.equals(response.getIsComplete())) {
                        if (response.getResults() != null && !response.getResults().isEmpty()) {
                            ChatResponseVo.Generation generation = response.getResults().get(0);
                            if (generation.getOutput() != null && generation.getOutput().getContent() != null) {
                                aiResponseBuilder.append(generation.getOutput().getContent());
                            }
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
                        // 【修复】isComplete响应中已包含完整内容，直接使用而不是累积的内容
                        String fullContent = "";
                        if (response.getResults() != null && !response.getResults().isEmpty()) {
                            ChatResponseVo.Generation generation = response.getResults().get(0);
                            if (generation.getOutput() != null && generation.getOutput().getContent() != null) {
                                fullContent = generation.getOutput().getContent();
                            }
                        }
                        // 如果isComplete响应没有内容，才使用累积的内容（兼容旧逻辑）
                        if (fullContent.isEmpty()) {
                            fullContent = aiResponseBuilder.toString();
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
                            String finalAiResponse = fullContent;
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

        // done事件的content字段包含工作流输出JSON结构，不应作为文本内容累积
        // 真正的文本内容已通过NODE_CHUNK事件累积到aiResponseBuilder
        boolean isDoneEvent = "done".equals(event.getEvent());

        // 设置基础字段（排除NODE_INPUT/OUTPUT和done事件）
        if (event.getData() != null && !isNodeInputOutput && !isDoneEvent) {
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
        if (isDoneEvent) {
            response.setIsComplete(true);
            // 尝试从event.data中提取runtime信息
            if (event.getData() != null) {
                try {
                    JSONObject dataJson = JSONObject.parseObject(event.getData());

                    // done事件的content字段包含工作流输出JSON（如{"output":{"type":1,"value":"..."}}）
                    // 不应作为文本内容累积，真正的文本内容通过NODE_CHUNK事件累积
                    // 已在第607行的条件判断中排除done事件的content提取

                    // 只提取runtime信息
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
                                                        "ai_conversation_runtime_node",    // serial_type: AI Chat场景
                                                        null,                              // serial_id: 无Workflow runtime时为null
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

    // ==================== Workflow Slash Command 接口 (2025-11-24) ====================

    /**
     * 获取可用的workflow列表（用于斜杠命令下拉选择）
     *
     * @return 可用workflow列表
     */
    @GetMapping(value = "/workflow/available")
    @Operation(summary = "获取可用workflow列表")
    @SysLogAnnotion("获取可用workflow列表")
    public ResponseEntity<List<Map<String, Object>>> getAvailableWorkflows() {
        try {
            Long userId = SecurityUtil.getStaff_id();

            // 查询可用的workflow列表
            List<AiWorkflowVo> workflows = aiWorkflowService.getAvailableWorkflowsForRouting(
                DataSourceHelper.getCurrentDataSourceName(),
                userId
            );

            // 转换为简化的VO结构（前端只需要 workflowUuid, title, desc）
            List<Map<String, Object>> result = new ArrayList<>();
            for (AiWorkflowVo wf : workflows) {
                Map<String, Object> item = new HashMap<>();
                item.put("workflowUuid", wf.getWorkflowUuid());
                item.put("title", wf.getTitle());
                item.put("desc", wf.getDesc());
                result.add(item);
            }

            log.info("【Workflow Slash Command】获取可用workflow列表成功, userId: {}, 数量: {}", userId, result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("【Workflow Slash Command】获取可用workflow列表失败", e);
            throw new AiBusinessException("获取可用workflow列表失败: " + e.getMessage());
        }
    }

    /**
     * 执行workflow命令（斜杠命令触发）
     *
     * @param request 包含 conversationId, workflowUuid, userInput, fileUrls
     * @return SSE流式响应
     */
    @PostMapping(value = "/workflow/execute", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "执行workflow命令")
    @SysLogAnnotion("执行workflow命令")
    public Flux<ChatResponseVo> executeWorkflowCommand(@Validated @RequestBody WorkflowCommandRequest request) {
        try {
            Long userId = SecurityUtil.getStaff_id();
            String conversationId = request.getConversationId();
            String workflowUuid = request.getWorkflowUuid();
            String userInput = request.getUserInput();
            List<String> fileUrls = request.getFileUrls();
            Map<String, String> pageContext = request.getPageContext();

            log.info("【Workflow Slash Command】执行workflow命令, userId: {}, conversationId: {}, workflowUuid: {}, userInput: {}, fileUrls: {}, pageContext: {}",
                userId, conversationId, workflowUuid, userInput, fileUrls, pageContext);

            // 查询workflow和开始节点配置
            AiWorkflowVo workflow = aiWorkflowService.getDtoByUuid(workflowUuid);
            if (workflow == null) {
                throw new RuntimeException("Workflow not found: " + workflowUuid);
            }

            AiWorkflowNodeVo startNode = workflowNodeService.getStartNode(workflow.getId());
            if (startNode == null || startNode.getInputConfig() == null) {
                throw new RuntimeException("开始节点配置不存在");
            }

            // 动态构建workflow输入参数（根据开始节点配置）
            List<JSONObject> workflowInputs = buildWorkflowInputsFromConfig(
                startNode.getInputConfig(),
                userInput,
                fileUrls
            );

            // 转换pageContext为Map<String, Object>类型
            Map<String, Object> pageContextMap = null;
            if (pageContext != null) {
                pageContextMap = new HashMap<>(pageContext);
            }

            // 调用workflow执行引擎（带pageContext参数）
            Flux<WorkflowEventVo> workflowEvents = workflowStarter.streaming(
                workflowUuid,
                workflowInputs,
                DataSourceHelper.getCurrentDataSourceName(),
                WorkflowCallSource.AI_CHAT,
                conversationId,
                pageContextMap
            );

            // 累积AI回复内容（用于注入到done事件）
            StringBuilder aiResponseBuilder = new StringBuilder();
            log.info("【Workflow Slash Command-调试】创建StringBuilder用于累积内容");

            // 转换为ChatResponseVo流,并累积内容注入done事件
            return workflowEvents
                .map(event -> {
                    log.info("【Workflow Slash Command-调试】收到WorkflowEventVo, event={}, data长度={}",
                        event.getEvent(), event.getData() != null ? event.getData().length() : 0);
                    return convertWorkflowEventToResponse(event);
                })
                .map(response -> {
                    log.info("【Workflow Slash Command-调试】进入map处理response, isComplete={}, results={}",
                        response.getIsComplete(),
                        response.getResults() != null ? response.getResults().size() : 0);

                    // 累积LLM输出内容
                    if (response.getResults() != null && !response.getResults().isEmpty()) {
                        ChatResponseVo.Generation generation = response.getResults().get(0);
                        if (generation.getOutput() != null && generation.getOutput().getContent() != null) {
                            String content = generation.getOutput().getContent();
                            aiResponseBuilder.append(content);
                            log.info("【Workflow Slash Command-调试】累积内容chunk, length={}, 当前总长度={}",
                                content.length(), aiResponseBuilder.length());
                        }
                    }

                    // 在done事件中注入累积的完整内容（与/chat/stream保持一致）
                    if (Boolean.TRUE.equals(response.getIsComplete())) {
                        log.info("【Workflow Slash Command-调试】检测到done事件, 准备注入累积内容");
                        String fullContent = aiResponseBuilder.toString();
                        log.info("【Workflow Slash Command-调试】累积内容总长度={}, 内容预览={}",
                            fullContent.length(),
                            fullContent.length() > 50 ? fullContent.substring(0, 50) + "..." : fullContent);

                        if (!fullContent.isEmpty()) {
                            response.setResults(List.of(
                                ChatResponseVo.Generation.builder()
                                    .output(ChatResponseVo.AssistantMessage.builder()
                                        .content(fullContent)
                                        .build())
                                    .build()
                            ));
                            log.info("【Workflow Slash Command】done事件已注入累积内容, length={}", fullContent.length());
                        } else {
                            log.warn("【Workflow Slash Command-调试】累积内容为空,未注入");
                        }
                    }

                    return response;
                })
                .doOnComplete(() -> {
                    log.info("【Workflow Slash Command】workflow执行完成, workflowUuid: {}, conversationId: {}, 最终累积内容长度={}",
                        workflowUuid, conversationId, aiResponseBuilder.length());
                })
                .doOnError(error -> {
                    log.error("【Workflow Slash Command】workflow执行失败, workflowUuid: {}, conversationId: {}",
                        workflowUuid, conversationId, error);
                });

        } catch (Exception e) {
            log.error("【Workflow Slash Command】执行workflow命令失败", e);
            // 使用ChatResponseVo的嵌套结构构建错误响应
            ChatResponseVo errorResponse = ChatResponseVo.builder()
                .results(List.of(
                    ChatResponseVo.Generation.builder()
                        .output(ChatResponseVo.AssistantMessage.builder()
                            .content("执行workflow失败: " + e.getMessage())
                            .messageType(AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT)
                            .build())
                        .build()
                ))
                .isComplete(true)
                .build();
            return Flux.just(errorResponse);
        }
    }

    /**
     * 根据开始节点配置动态构建workflow输入参数
     *
     * @param inputConfig 开始节点的输入配置
     * @param userInput 用户文本输入
     * @param fileUrls 用户上传的文件URL列表
     * @return workflow输入参数列表
     */
    private List<JSONObject> buildWorkflowInputsFromConfig(
        AiWfNodeInputConfigVo inputConfig,
        String userInput,
        List<String> fileUrls
    ) {
        List<JSONObject> workflowInputs = new ArrayList<>();

        if (inputConfig.getUserInputs() == null || inputConfig.getUserInputs().isEmpty()) {
            log.warn("开始节点没有配置userInputs");
            return workflowInputs;
        }

        for (AiWfNodeIOVo userInputDef : inputConfig.getUserInputs()) {
            Integer type = userInputDef.getType();
            String name = userInputDef.getName();
            String title = userInputDef.getTitle();
            Boolean required = userInputDef.getRequired();

            // 类型1: TEXT文本输入
            if (type != null && type == 1 && StringUtils.isNotBlank(userInput)) {
                JSONObject input = new JSONObject();
                input.put("name", name);  // 动态使用配置的参数名

                JSONObject content = new JSONObject();
                content.put("type", 1);
                content.put("value", userInput);
                content.put("title", title != null ? title : "用户输入");
                input.put("content", content);
                input.put("required", required != null ? required : false);

                workflowInputs.add(input);
                log.info("【动态参数】添加TEXT输入: name={}, value={}", name, userInput);
            }

            // 类型4: FILES文件输入
            if (type != null && type == 4 && fileUrls != null && !fileUrls.isEmpty()) {
                JSONObject input = new JSONObject();
                input.put("name", name);  // 动态使用配置的参数名

                JSONObject content = new JSONObject();
                content.put("type", 4);
                content.put("value", fileUrls);
                content.put("title", title != null ? title : "用户上传文件");
                input.put("content", content);
                input.put("required", required != null ? required : false);

                workflowInputs.add(input);
                log.info("【动态参数】添加FILES输入: name={}, fileCount={}", name, fileUrls.size());
            }
        }

        return workflowInputs;
    }

    /**
     * Workflow命令请求参数
     */
    @Data
    public static class WorkflowCommandRequest {
        private String conversationId;
        private String workflowUuid;
        private String userInput;
        private List<String> fileUrls;
        /**
         * 当前页面上下文（用于MCP工具回答"我在哪个页面"等问题）
         */
        private Map<String, String> pageContext;
    }

}