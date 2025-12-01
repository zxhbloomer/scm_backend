package com.xinyirun.scm.ai.controller.chat;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationRuntimeNodeVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.common.constant.WorkflowStateConstant;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AIConversationUpdateRequestVo;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.core.adapter.WorkflowEventAdapter;
import com.xinyirun.scm.ai.core.service.chat.AiConversationContentService;
import com.xinyirun.scm.ai.core.service.chat.AiConversationService;
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
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


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

    @Resource
    private WorkflowEventAdapter workflowEventAdapter;

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
        log.info("【chatStream】入口, prompt={}", request.getPrompt());

        // 获取用户上下文
        String conversationId = request.getConversationId();
        Long operatorId = SecurityUtil.getStaff_id();
        String tenantId = conversationId.split("::", 2)[0];
        request.setTenantId(tenantId);
        Map<String, Object> pageContext = request.getPageContext();
        String userPrompt = request.getPrompt();

        // 使用AtomicReference解决Reactor流中的线程安全问题
        // StringBuilder不是线程安全的,在Flux的map操作中可能被多个线程访问
        AtomicReference<String> aiResponseAccumulator = new AtomicReference<>("");

        // Spring AI模式：Mono → Flux 链式调用
        return Mono.fromCallable(() -> {
                    DataSourceHelper.use(tenantId);
                    AiConversationVo conversation = aiConversationService.getConversation(conversationId);
                    if (conversation == null) {
                        throw new AiBusinessException("对话不存在: " + conversationId);
                    }
                    return conversation;
                })
                .flatMapMany(conversation -> {
                    // 在flatMapMany内部重新设置数据源,防止线程切换导致ThreadLocal丢失
                    DataSourceHelper.use(tenantId);

                    String workflowState = conversation.getWorkflowState();
                    if (workflowState == null) {
                        workflowState = WorkflowStateConstant.STATE_IDLE;
                    }

                    if (WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT.equals(workflowState)) {
                        boolean isContinuation = workflowRoutingService.isInputContinuation(
                            request.getPrompt(),
                            conversation.getCurrentWorkflowUuid(),
                            operatorId
                        );

                        if (isContinuation) {
                            // 继续当前工作流
                            log.info("【chatStream】继续工作流, conversationId={}, runtimeUuid={}, workflowUuid={}",
                                conversationId, conversation.getCurrentRuntimeUuid(), conversation.getCurrentWorkflowUuid());
                            return workflowRoutingService.resumeWorkflow(
                                conversation.getCurrentRuntimeUuid(),
                                conversation.getCurrentWorkflowUuid(),
                                request.getPrompt(),
                                tenantId,
                                conversationId
                            );
                        } else {
                            // 新意图,清理旧状态
                            log.info("【chatStream】检测到新意图, 清理旧工作流状态, conversationId={}, 旧workflowUuid={}",
                                conversationId, conversation.getCurrentWorkflowUuid());
                            aiConversationService.updateWorkflowState(
                                conversationId,
                                WorkflowStateConstant.STATE_IDLE,
                                null,
                                null
                            );
                        }
                    }

                    // 新请求或新意图 - 路由并执行
                    log.info("【chatStream】新请求路由, conversationId={}, userPrompt={}", conversationId, userPrompt);
                    return workflowRoutingService.routeAndExecute(
                        request.getPrompt(),
                        operatorId,
                        tenantId,
                        conversationId,
                        pageContext,
                        null
                    );
                })
                .map(response -> {
                    // 在每次map操作前确保数据源上下文正确
                    DataSourceHelper.use(tenantId);
                    return handleResponse(response, conversationId, userPrompt, operatorId, aiResponseAccumulator);
                })
                .timeout(Duration.ofMinutes(30))
                .onErrorResume(e -> handleError(e, conversationId, tenantId))
                .doOnCancel(() -> {
                    log.info("【chatStream】用户取消工作流执行, conversationId={}", conversationId);
                    // 取消时重置状态,防止状态泄漏
                    try {
                        DataSourceHelper.use(tenantId);
                        aiConversationService.updateWorkflowState(
                            conversationId,
                            WorkflowStateConstant.STATE_IDLE,
                            null,
                            null
                        );
                        log.info("【chatStream】取消时已重置对话状态为IDLE, conversationId={}", conversationId);
                    } catch (Exception ex) {
                        log.error("【chatStream】取消时重置状态失败, conversationId={}", conversationId, ex);
                    }
                })
                .doFinally(signalType -> DataSourceHelper.close());
    }

    /**
     * 处理单个响应，更新状态和保存对话
     *
     * @param response 响应对象
     * @param conversationId 对话ID
     * @param userPrompt 用户输入
     * @param operatorId 操作人ID
     * @param aiResponseAccumulator 线程安全的AI响应累积器
     * @return 处理后的响应
     */
    private ChatResponseVo handleResponse(ChatResponseVo response, String conversationId,
            String userPrompt, Long operatorId, AtomicReference<String> aiResponseAccumulator) {
        // 累积AI回复内容 (使用AtomicReference保证线程安全)
        if (!Boolean.TRUE.equals(response.getIsComplete())) {
            if (response.getResults() != null && !response.getResults().isEmpty()) {
                ChatResponseVo.Generation generation = response.getResults().get(0);
                if (generation.getOutput() != null && generation.getOutput().getContent() != null) {
                    String content = generation.getOutput().getContent();
                    // 原子操作累积内容
                    aiResponseAccumulator.updateAndGet(current -> current + content);
                }
            }
        }

        if (Boolean.TRUE.equals(response.getIsWaitingInput())) {
            aiConversationService.updateWorkflowState(
                conversationId,
                WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT,
                response.getWorkflowUuid(),
                response.getRuntimeUuid()
            );
        }

        if (Boolean.TRUE.equals(response.getIsComplete())) {
            String fullContent = "";
            if (response.getResults() != null && !response.getResults().isEmpty()) {
                ChatResponseVo.Generation generation = response.getResults().get(0);
                if (generation.getOutput() != null && generation.getOutput().getContent() != null) {
                    fullContent = generation.getOutput().getContent();
                }
            }
            if (fullContent.isEmpty()) {
                fullContent = aiResponseAccumulator.get();
            }

            aiConversationService.updateWorkflowState(
                conversationId,
                WorkflowStateConstant.STATE_IDLE,
                null,
                null
            );

            // 保存对话内容
            try {
                String finalAiResponse = fullContent;
                String runtimeUuid = response.getRuntimeUuid();

                log.info("【AI-Chat-保存】准备保存对话内容: conversationId={}, runtimeUuid={}",
                    conversationId, runtimeUuid);

                if (!userPrompt.isEmpty() || !finalAiResponse.isEmpty()) {
                    aiConversationContentService.saveContent(
                        conversationId, 1, userPrompt, operatorId, null
                    );
                    var aiContentVo = aiConversationContentService.saveContent(
                        conversationId, 2, finalAiResponse, operatorId, runtimeUuid
                    );
                    if (aiContentVo != null && aiContentVo.getMessage_id() != null) {
                        response.setMessageId(aiContentVo.getMessage_id());
                    }
                }
            } catch (Exception e) {
                log.error("保存对话内容失败: conversationId={}", conversationId, e);
            }
        }
        return response;
    }

    /**
     * 错误处理
     *
     * @param e 异常
     * @param conversationId 对话ID
     * @param tenantId 租户ID
     * @return 错误响应流
     */
    private Flux<ChatResponseVo> handleError(Throwable e, String conversationId, String tenantId) {
        log.error("【chatStream】工作流执行异常, conversationId={}", conversationId, e);

        // 重置对话状态,防止状态泄漏导致用户再次发消息时卡死
        try {
            DataSourceHelper.use(tenantId);
            aiConversationService.updateWorkflowState(
                conversationId,
                WorkflowStateConstant.STATE_IDLE,
                null,
                null
            );
            log.info("【chatStream】错误处理时已重置对话状态为IDLE, conversationId={}", conversationId);
        } catch (Exception resetError) {
            log.error("【chatStream】错误处理时重置状态失败, conversationId={}", conversationId, resetError);
        }

        String errorMsg = e instanceof TimeoutException ?
            "工作流执行超时，已自动取消" : "工作流执行失败: " + e.getMessage();
        return Flux.just(ChatResponseVo.createErrorResponse(errorMsg));
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
                    return workflowEventAdapter.convert(event);
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