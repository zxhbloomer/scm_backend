package com.xinyirun.scm.ai.workflow;

import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.*;
import com.xinyirun.scm.ai.bean.vo.workflow.*;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.core.service.workflow.AiConversationRuntimeNodeService;
import com.xinyirun.scm.ai.core.service.workflow.AiConversationRuntimeService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowRuntimeNodeService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowRuntimeService;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import com.alibaba.cloud.ai.graph.*;
import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.*;

/**
 * 工作流执行引擎
 *
 * <p>基于Spring AI Alibaba Graph实现DAG工作流执行</p>
 *
 * @author zxh
 * @since 2025-10-21
 */
@Slf4j
public class WorkflowEngine {
    private CompiledGraph app;
    private final AiWorkflowEntity workflow;
    private final List<AiWorkflowComponentEntity> components;
    private final List<AiWorkflowNodeVo> wfNodes;
    private final List<AiWorkflowEdgeEntity> wfEdges;
    private final WorkflowCallSource callSource;
    private final AiWorkflowRuntimeService workflowRuntimeService;
    private final AiWorkflowRuntimeNodeService workflowRuntimeNodeService;
    private final AiConversationRuntimeService conversationRuntimeService;
    private final AiConversationRuntimeNodeService conversationRuntimeNodeService;

    private final Map<String, List<StateGraph>> stateGraphNodes = new HashMap<>();
    private final Map<String, List<StateGraph>> stateGraphEdges = new HashMap<>();

    private Long userId;
    private String tenantCode;
    private WfState wfState;
    private AiWorkflowRuntimeVo wfRuntimeResp;
    private AiConversationRuntimeVo conversationRuntimeResp;

    /**
     * 页面上下文 (前端传递的当前页面信息，用于MCP工具)
     */
    private Map<String, Object> pageContext;

    /**
     * 父工作流的runtime_uuid（用于子工作流复用）
     * null表示顶层工作流，非null表示子工作流
     */
    private final String parentRuntimeUuid;

    /**
     * 需要在执行前中断的节点UUID集合（HumanFeedbackNode）
     */
    private final Set<String> humanFeedbackNodeUuids = new HashSet<>();

    /**
     * 节点事件Sink引用，用于在GraphLifecycleListener回调中发送节点事件
     * 使用AtomicReference确保resume()调用时创建新Sink不发生订阅冲突
     */
    private final AtomicReference<Sinks.Many<WorkflowEventVo>> sinkRef = new AtomicReference<>();

    /**
     * 节点开始时间记录，用于计算节点执行耗时
     * 格式：nodeUuid → 开始时间戳（毫秒）
     */
    private final ConcurrentHashMap<String, Long> nodeStartTimes = new ConcurrentHashMap<>();

    /**
     * 获取租户编码（用于数据源切换）
     * @return 租户编码
     */
    public String getTenantCode() {
        return this.tenantCode;
    }

    /**
     * 设置页面上下文（用于MCP工具获取当前页面信息）
     * @param pageContext 页面上下文
     */
    public void setPageContext(Map<String, Object> pageContext) {
        this.pageContext = pageContext;
    }

    /**
     * 构造函数（顶层工作流）
     * 对齐Spring AI Alibaba - run()方法直接返回Flux，无需回调
     */
    public WorkflowEngine(
            AiWorkflowEntity workflow,
            List<AiWorkflowComponentEntity> components,
            List<AiWorkflowNodeVo> nodes,
            List<AiWorkflowEdgeEntity> wfEdges,
            WorkflowCallSource callSource,
            AiWorkflowRuntimeService workflowRuntimeService,
            AiWorkflowRuntimeNodeService workflowRuntimeNodeService,
            AiConversationRuntimeService conversationRuntimeService,
            AiConversationRuntimeNodeService conversationRuntimeNodeService) {
        this(workflow, components, nodes, wfEdges,
            callSource, workflowRuntimeService, workflowRuntimeNodeService,
            conversationRuntimeService, conversationRuntimeNodeService, null);
    }

    /**
     * 构造函数（支持子工作流）
     * 对齐Spring AI Alibaba - run()方法直接返回Flux，无需回调
     *
     * @param parentRuntimeUuid 父工作流的runtime_uuid，null表示顶层工作流
     */
    public WorkflowEngine(
            AiWorkflowEntity workflow,
            List<AiWorkflowComponentEntity> components,
            List<AiWorkflowNodeVo> nodes,
            List<AiWorkflowEdgeEntity> wfEdges,
            WorkflowCallSource callSource,
            AiWorkflowRuntimeService workflowRuntimeService,
            AiWorkflowRuntimeNodeService workflowRuntimeNodeService,
            AiConversationRuntimeService conversationRuntimeService,
            AiConversationRuntimeNodeService conversationRuntimeNodeService,
            String parentRuntimeUuid) {
        this.workflow = workflow;
        this.components = components;
        this.wfNodes = nodes;
        this.wfEdges = wfEdges;
        this.callSource = callSource;
        this.workflowRuntimeService = workflowRuntimeService;
        this.workflowRuntimeNodeService = workflowRuntimeNodeService;
        this.conversationRuntimeService = conversationRuntimeService;
        this.conversationRuntimeNodeService = conversationRuntimeNodeService;
        this.parentRuntimeUuid = parentRuntimeUuid;

        // 识别所有 HumanFeedbackNode
        for (AiWorkflowNodeVo node : nodes) {
            AiWorkflowComponentEntity component = components.stream()
                .filter(c -> c.getId().equals(node.getWorkflowComponentId()))
                .findFirst()
                .orElse(null);

            if (component != null && COMPONENT_UUID_HUMAN_FEEDBACK.equals(component.getComponentUuid())) {
                humanFeedbackNodeUuids.add(node.getUuid());
            }
        }
    }

    /**
     * 流式执行工作流 - 对齐Spring AI Alibaba GraphRunner模式
     *
     * <p>使用 Flux.defer() + Flux.just() + concatWith() 递归模式：
     * 事件是Flux管道的直接产物，不依赖外部回调</p>
     *
     * @param userId 用户ID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param parentConversationId 父对话ID
     * @return Flux事件流
     */
    public Flux<WorkflowEventVo> run(Long userId, List<JSONObject> userInputs, String tenantCode, String parentConversationId) {
        log.info("[WorkflowEngine] run()方法被调用, userId={}, workflowUuid={}", userId, workflow.getWorkflowUuid());
        return Flux.defer(() -> {
            log.info("[WorkflowEngine] Flux.defer()开始执行");
            try {
                // 1. 初始化（同步执行）
                this.userId = userId;
                this.tenantCode = tenantCode;
                DataSourceHelper.use(this.tenantCode);
                log.info("WorkflowEngine run,userId:{},workflowUuid:{},tenantCode:{},parentConversationId:{},userInputs:{}",
                        userId, workflow.getWorkflowUuid(), tenantCode, parentConversationId, userInputs);

                if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
                    return Flux.error(new RuntimeException("工作流已禁用"));
                }

                Long workflowId = this.workflow.getId();

                // 2. 创建 runtime 记录
                String runtimeUuid;
                String conversationId;

                if (parentRuntimeUuid == null) {
                    // 顶层工作流：创建新的runtime记录
                    if (callSource == WorkflowCallSource.AI_CHAT) {
                        if (parentConversationId != null && !parentConversationId.isEmpty()) {
                            this.conversationRuntimeResp = conversationRuntimeService.createWithConversationId(
                                    userId, workflowId, parentConversationId);
                        } else {
                            this.conversationRuntimeResp = conversationRuntimeService.create(userId, workflowId);
                        }
                        runtimeUuid = this.conversationRuntimeResp.getRuntime_uuid();
                        conversationId = this.conversationRuntimeResp.getConversation_id();
                    } else {
                        if (parentConversationId != null && !parentConversationId.isEmpty()) {
                            this.wfRuntimeResp = workflowRuntimeService.createWithConversationId(
                                    userId, workflowId, parentConversationId);
                        } else {
                            this.wfRuntimeResp = workflowRuntimeService.create(userId, workflowId);
                        }
                        runtimeUuid = this.wfRuntimeResp.getRuntimeUuid();
                        conversationId = this.wfRuntimeResp.getConversationId();
                    }
                    log.info("顶层工作流创建runtime记录 - runtime_uuid: {}", runtimeUuid);
                } else {
                    // 子工作流：复用父runtime_uuid，不创建新记录
                    log.info("子工作流复用父runtime_uuid: {}", parentRuntimeUuid);
                    runtimeUuid = parentRuntimeUuid;
                    conversationId = parentConversationId != null ? parentConversationId
                            : (tenantCode + "::" + workflow.getWorkflowUuid() + "::" + userId);
                }

                log.debug("WorkflowEngine开始执行 - runtime_uuid: {}", runtimeUuid);

                // 3. 初始化工作流状态
                Pair<AiWorkflowNodeVo, Set<AiWorkflowNodeVo>> startAndEnds = findStartAndEndNode();
                AiWorkflowNodeVo startNode = startAndEnds.getLeft();
                List<NodeIOData> wfInputs = getAndCheckUserInput(userInputs, startNode);

                this.wfState = new WfState(userId, wfInputs, runtimeUuid, tenantCode, conversationId);
                this.wfState.setWorkflowUuid(workflow.getWorkflowUuid());
                this.wfState.setWorkflowTitle(workflow.getTitle());
                this.wfState.setCallSource(this.callSource);
                if (this.pageContext != null) {
                    this.wfState.setPageContext(this.pageContext);
                }

                // 添加需要中断的节点（HumanFeedbackNode）
                for (String humanFeedbackNodeUuid : humanFeedbackNodeUuids) {
                    this.wfState.addInterruptNode(humanFeedbackNodeUuid);
                }

                // 更新runtime记录的input
                if (this.wfRuntimeResp != null) {
                    workflowRuntimeService.updateInput(this.wfRuntimeResp.getId(), wfState);
                } else if (this.conversationRuntimeResp != null) {
                    conversationRuntimeService.updateInput(this.conversationRuntimeResp.getId(), wfState);
                }

                // 4. 构建 StateGraph 并编译
                StateGraph mainStateGraph = new StateGraph();
                buildStateGraph(mainStateGraph, startNode);
                Sinks.Many<WorkflowEventVo> newSink = Sinks.many().unicast().onBackpressureBuffer();
                sinkRef.set(newSink);
                wfState.setEventSink(newSink);
                CompileConfig compileConfig = CompileConfig.builder()
                    .withLifecycleListener(new NodeEventListener())
                    .build();
                app = mainStateGraph.compile(compileConfig);

                // 5. 发送runtime数据（如果是顶层工作流），然后执行工作流
                if (parentRuntimeUuid == null) {
                    // 顶层工作流：发送runtime数据，然后执行
                    Long runtimeId = callSource == WorkflowCallSource.AI_CHAT
                            ? this.conversationRuntimeResp.getId()
                            : this.wfRuntimeResp.getId();
                    log.info("[WorkflowEngine] 准备发送runtime数据并执行工作流, runtime_uuid: {}", runtimeUuid);
                    return Flux.just(WorkflowEventVo.createRuntimeData(runtimeUuid, runtimeId,
                                    workflow.getWorkflowUuid(), conversationId))
                            .doOnNext(event -> log.info("[WorkflowEngine] 发送runtime数据: data={}", event.getData()))
                            .concatWith(executeWorkflow(false)
                                    .doOnSubscribe(sub -> log.info("[WorkflowEngine] executeWorkflow() Flux已订阅"))
                                    .doOnNext(evt -> log.info("[WorkflowEngine] executeWorkflow()发出事件: data={}", evt.getData()))
                                    .doOnComplete(() -> log.info("[WorkflowEngine] executeWorkflow() Flux完成"))
                                    .doOnError(e -> log.error("[WorkflowEngine] executeWorkflow() Flux错误", e)));
                } else {
                    // 子工作流：直接执行，不发送start事件
                    log.info("[WorkflowEngine] 子工作流直接执行");
                    return executeWorkflow(false);
                }
            } catch (Exception e) {
                log.error("工作流初始化失败", e);
                return Flux.error(new RuntimeException("工作流执行失败: " + e.getMessage()));
            }
        });
    }

    /**
     * 执行工作流 - 使用Spring AI Alibaba的graphResponseStream模式
     *
     * <p>使用graphResponseStream替代已废弃的AsyncGenerator，
     * 通过Flux的flatMap响应式处理GraphResponse事件</p>
     *
     * @param resume 是否是恢复执行
     * @return Flux事件流
     */
    private Flux<WorkflowEventVo> executeWorkflow(boolean resume) {
        log.info("[WorkflowEngine] ===== executeWorkflow()开始 ===== resume={}", resume);
        return Flux.defer(() -> {
            log.info("[WorkflowEngine] executeWorkflow() Flux.defer()内部开始执行");
            try {
                DataSourceHelper.use(this.tenantCode);

                // 创建OverAllState
                OverAllState initialState = createOverAllState(resume);
                log.info("[WorkflowEngine] OverAllState创建完成, data={}", initialState.data());
                RunnableConfig invokeConfig = RunnableConfig.builder().build();

                // 使用Spring AI Alibaba的graphResponseStream API
                log.info("[WorkflowEngine] 准备调用app.graphResponseStream()...");
                Flux<GraphResponse<NodeOutput>> graphStream = app.graphResponseStream(initialState, invokeConfig);
                log.info("[WorkflowEngine] graphResponseStream创建完成(Flux对象已创建，但尚未订阅)");

                // 获取当前Sink，用于在graphStream完成时关闭Sink
                Sinks.Many<WorkflowEventVo> localSink = sinkRef.get();

                // 合并图执行流和节点事件流：节点事件在图执行过程中通过Sink侧道发送
                Flux<WorkflowEventVo> graphEventFlux = graphStream
                    .doOnSubscribe(sub -> log.info("[graphStream] 已被订阅"))
                    .doFirst(() -> log.info("[graphStream] 开始执行"))
                    .doOnNext(resp -> log.info("[graphStream] 收到元素: isDone={}, isError={}, hasOutput={}",
                            resp.isDone(), resp.isError(), resp.getOutput() != null))
                    .doOnComplete(() -> {
                        log.info("[graphStream] 完成");
                        // 工作流完成时更新状态（对齐Spring AI Alibaba Flux.complete()信号）
                        updateWorkflowComplete();
                        // OpenPage节点：将JSON数据通过事件流传递给前端
                        if (wfState.getAi_open_dialog_para() != null && localSink != null) {
                            localSink.tryEmitNext(
                                WorkflowEventVo.createAiOpenDialogParaEvent(wfState.getAi_open_dialog_para())
                            );
                        }
                        // 图执行完成后关闭Sink，触发节点事件流完成
                        if (localSink != null) {
                            localSink.tryEmitComplete();
                        }
                    })
                    .doOnError(e -> {
                        log.error("[graphStream] 错误", e);
                        if (localSink != null) {
                            localSink.tryEmitError(e);
                        }
                    })
                    .doOnCancel(() -> log.warn("[graphStream] 被取消"))
                    .flatMap(graphResponse -> {
                        log.info("[flatMap] 处理graphResponse, isDone={}, isError={}",
                                graphResponse.isDone(), graphResponse.isError());
                        return handleGraphResponse(graphResponse)
                                .doOnNext(evt -> log.info("[flatMap] handleGraphResponse返回事件"))
                                .doOnComplete(() -> log.debug("[flatMap] handleGraphResponse Flux完成"));
                    })
                    .doOnNext(evt -> log.info("[executeWorkflow] flatMap后收到事件"))
                    .doOnSubscribe(sub -> log.info("[executeWorkflow] 最终Flux已被订阅"))
                    .doOnComplete(() -> log.info("[executeWorkflow] 最终Flux完成"));

                Flux<WorkflowEventVo> nodeEventFlux = (localSink != null) ? localSink.asFlux() : Flux.empty();

                return Flux.merge(graphEventFlux, nodeEventFlux);

            } catch (Exception e) {
                log.error("工作流执行失败", e);
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("parallel node doesn't support conditional branch")) {
                    errorMsg = "并行节点中不能包含条件分支";
                }

                // 更新runtime状态
                DataSourceHelper.use(this.tenantCode);
                if (this.wfRuntimeResp != null) {
                    workflowRuntimeService.updateStatus(wfRuntimeResp.getId(), WORKFLOW_PROCESS_STATUS_FAIL, errorMsg);
                } else if (this.conversationRuntimeResp != null) {
                    conversationRuntimeService.updateStatus(conversationRuntimeResp.getId(), WORKFLOW_PROCESS_STATUS_FAIL, errorMsg);
                }

                return Flux.error(new RuntimeException("工作流执行失败: " + errorMsg));
            }
        });
    }

    /**
     * 创建OverAllState
     */
    private OverAllState createOverAllState(boolean resume) {
        if (resume) {
            // 恢复模式：状态已在updateState中更新
            return OverAllStateBuilder.builder()
                .withKeyStrategies(app.getKeyStrategyMap())
                .withData(new HashMap<>())
                .build();
        } else {
            // 初始执行：从wfState构建初始状态
            Map<String, Object> stateData = new HashMap<>();
            for (NodeIOData input : wfState.getInput()) {
                stateData.put(input.getName(), input.getContent().getValue());
            }

            return OverAllStateBuilder.builder()
                .withKeyStrategies(app.getKeyStrategyMap())
                .withData(stateData)
                .build();
        }
    }

    /**
     * 处理GraphResponse事件 - 核心适配逻辑
     *
     * 重要说明：Spring AI Alibaba 在多种情况下会发送 isDone()=true：
     * 1. 节点LLM流式输出聚合完成（NodeExecutor.transformFluxToGraphResponse）
     * 2. 人机交互中断（MainGraphExecutor多处）
     * 3. 工作流真正完成（handleCompletion）
     *
     * 正确的处理方式：不在这里处理isDone()，让Spring AI Alibaba自动递归执行
     * 工作流完成在executeWorkflow中通过concatWith处理
     */
    private Flux<WorkflowEventVo> handleGraphResponse(GraphResponse<NodeOutput> graphResponse) {
        // 1. 检查是否是完成信号（isDone）
        // Spring AI Alibaba会在节点完成、中断、工作流完成时发送isDone()=true
        // 但只有工作流完成时Flux才会结束，所以这里不处理isDone()
        // 工作流完成的done事件在executeWorkflow的concatWith中处理
        if (graphResponse.isDone()) {
            // 检查resultValue，如果是InterruptionMetadata则处理中断
            if (graphResponse.resultValue().isPresent()) {
                Object result = graphResponse.resultValue().get();
                if (result instanceof com.alibaba.cloud.ai.graph.action.InterruptionMetadata) {
                    // 人机交互中断，需要发送等待事件
                    log.debug("检测到人机交互中断信号");
                    return handleInterruption(graphResponse);
                }
            }
            // 其他isDone情况（节点完成、工作流完成）：返回空，让Spring AI Alibaba继续执行
            // 真正的工作流完成会在Flux结束时通过concatWith处理
            return Flux.<WorkflowEventVo>empty();
        }

        // 2. 检查是否错误（对齐Spring AI Alibaba：使用Flux.error()传递错误）
        if (graphResponse.isError()) {
            return Mono.fromFuture(graphResponse.getOutput())
                .flatMapMany(output -> Flux.<WorkflowEventVo>error(new RuntimeException("节点执行失败")))
                .onErrorResume(e -> Flux.error(new RuntimeException("节点执行失败: " + e.getMessage())));
        }

        // 3. 正常节点输出（对齐Spring AI Alibaba：发送output数据）
        return Mono.fromFuture(graphResponse.getOutput())
            .flatMapMany(nodeOutput -> {
                // 处理节点输出（更新数据库、记录状态等）
                processNodeOutput(nodeOutput);

                // 获取节点输出数据并发送（对齐Spring AI Alibaba）
                String nodeId = nodeOutput.node();
                AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
                    .filter(item -> item.getNode().getUuid().equals(nodeId))
                    .findFirst()
                    .orElse(null);

                if (abstractWfNode != null) {
                    // 已通过chunk事件流式输出的节点，跳过output事件避免内容重复
                    if (wfState.hasNodeStreamed(nodeId)) {
                        log.debug("节点{}已流式输出，跳过output事件", nodeId);
                        return Flux.<WorkflowEventVo>empty();
                    }
                    List<NodeIOData> outputList = abstractWfNode.getState().getOutputs();
                    // 保留完整NodeIOData结构，前端需要name和content.value字段
                    Map<String, Object> outputs = outputList.stream()
                        .collect(Collectors.toMap(NodeIOData::getName, nodeIOData -> nodeIOData, (v1, v2) -> v2));
                    String componentName = abstractWfNode.getWfComponent() != null ? abstractWfNode.getWfComponent().getName() : "";
                    log.debug("发送output数据: nodeId={}, componentName={}, outputs数量={}", nodeId, componentName, outputs.size());
                    return Flux.just(WorkflowEventVo.createNodeOutputData(nodeId, componentName, outputs));
                }

                return Flux.<WorkflowEventVo>empty();
            })
            .onErrorResume(e -> {
                log.error("处理节点输出失败", e);
                return Flux.error(new RuntimeException("处理节点输出失败: " + e.getMessage()));
            });
    }

    /**
     * 处理人机交互中断
     * 对齐Spring AI Alibaba：发送interrupt数据（type=interrupt）
     */
    private Flux<WorkflowEventVo> handleInterruption(GraphResponse<NodeOutput> graphResponse) {
        DataSourceHelper.use(this.tenantCode);

        // 获取中断节点信息
        String nextInterruptNode = wfState.getInterruptNodes().stream()
            .filter(nodeUuid -> wfState.getCompletedNodes().stream()
                .noneMatch(completedNode -> completedNode.getNode().getUuid().equals(nodeUuid)))
            .findFirst()
            .orElse(null);

        if (nextInterruptNode != null) {
            String intTip = getHumanFeedbackTip(nextInterruptNode);
            InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);

            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_READY);
            if (this.wfRuntimeResp != null) {
                workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
            } else if (this.conversationRuntimeResp != null) {
                conversationRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
            }

            // 对齐Spring AI Alibaba：发送interrupt数据
            return Flux.just(WorkflowEventVo.createInterruptData(nextInterruptNode, intTip));
        }

        return Flux.<WorkflowEventVo>empty();
    }

    /**
     * 更新工作流完成状态
     * 对齐Spring AI Alibaba：Flux.complete()信号时更新数据库状态
     * 不再发送done事件，前端通过onComplete回调感知完成
     */
    private void updateWorkflowComplete() {
        DataSourceHelper.use(this.tenantCode);

        // 工作流正常完成
        wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_SUCCESS);

        if (this.wfRuntimeResp != null) {
            workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
        } else if (this.conversationRuntimeResp != null) {
            conversationRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
        }

        InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
        log.info("WorkflowEngine执行完成 - runtime_uuid: {}", wfState.getUuid());
    }

    /**
     * 处理节点输出（更新数据库、记录状态等）
     * 对应原streamingResult()的逻辑
     */
    private void processNodeOutput(NodeOutput nodeOutput) {
        DataSourceHelper.use(this.tenantCode);

        // 提取节点信息
        String nodeId = nodeOutput.node();

        // 找到对应的 abstractWfNode
        AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
            .filter(item -> item.getNode().getUuid().equals(nodeId))
            .findFirst()
            .orElse(null);

        if (null != abstractWfNode) {
            // 找到对应的运行时节点
            Long runtimeNodeId = null;

            if (callSource == WorkflowCallSource.AI_CHAT) {
                // AI Chat场景: 由于我们没有存储conversation node到wfState中，
                // 这里暂时跳过更新(或者可以通过数据库查询获取node ID)
                log.debug("AI Chat场景下processNodeOutput暂时跳过node更新");
            } else {
                // Workflow独立测试场景: 使用wfState中的runtime node
                AiWorkflowRuntimeNodeVo runtimeNodeVo = wfState.getRuntimeNodeByNodeUuid(nodeId);
                if (null != runtimeNodeVo) {
                    runtimeNodeId = runtimeNodeVo.getId();
                }
            }

            // 更新运行时节点的输出
            if (runtimeNodeId != null) {
                if (callSource == WorkflowCallSource.AI_CHAT) {
                    conversationRuntimeNodeService.updateOutput(runtimeNodeId, abstractWfNode.getState());
                } else {
                    workflowRuntimeNodeService.updateOutput(runtimeNodeId, abstractWfNode.getState());
                }
            } else {
                log.warn("Can not find runtime node, node uuid:{}", nodeId);
            }

            // 设置工作流最终输出（适用于所有场景）
            wfState.setOutput(abstractWfNode.getState().getOutputs());
        } else {
            log.warn("Can not find node state,node uuid:{}", nodeId);
        }
    }

    /**
     * 恢复中断的工作流 - 返回Flux事件流
     *
     * <p>中断流程等待用户输入时，会进行暂停状态，用户输入后调用本方法执行流程剩余部分</p>
     * <p>对齐Spring AI Alibaba：使用Flux.error()传递错误</p>
     *
     * @param userInput 用户输入
     * @return Flux事件流
     */
    public Flux<WorkflowEventVo> resume(String userInput) {
        return Flux.defer(() -> {
            try {
                RunnableConfig invokeConfig = RunnableConfig.builder().build();
                app.updateState(invokeConfig, Map.of(HUMAN_FEEDBACK_KEY, userInput), null);
                return executeWorkflow(true);
            } catch (Exception e) {
                log.error("工作流恢复执行失败", e);
                // 有可能多次接收人机交互，待整个流程完全执行后才能删除
                // 使用WORKFLOW_PROCESS_STATUS_READY（WAITING_INPUT已deprecated）
                if (wfState.getProcessStatus() != WORKFLOW_PROCESS_STATUS_READY) {
                    InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
                }
                // 对齐Spring AI Alibaba：使用Flux.error()传递错误
                return Flux.error(new RuntimeException("工作流恢复执行失败: " + e.getMessage()));
            }
        });
    }

    /**
     * 执行单个节点
     *
     * @param wfNode 节点定义
     * @param nodeState 节点状态
     * @return 执行结果Map
     */
    private Map<String, Object> runNode(AiWorkflowNodeVo wfNode, WfNodeState nodeState) {
        log.debug("runNode开始执行: nodeUuid={}, nodeTitle={}", wfNode.getUuid(), wfNode.getTitle());
        long startTime = System.currentTimeMillis();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 在异步线程中重新设置数据源上下文
            // StateGraph异步执行时创建的新线程无法继承ThreadLocal的数据源上下文
            DataSourceHelper.use(this.tenantCode);

            // 检查是否是中断节点且未resume (替代interruptBefore机制)
            // 如果是首次遇到HumanFeedbackNode,直接返回空结果,不执行节点
            if (wfState.getInterruptNodes().contains(wfNode.getUuid())
                    && !nodeState.data().containsKey(HUMAN_FEEDBACK_KEY)) {
                log.info("检测到中断节点(HumanFeedbackNode),跳过执行: {}", wfNode.getUuid());
                // 返回空结果,让workflow暂停在此节点之前
                resultMap.put("name", wfNode.getTitle());
                return resultMap;
            }

            // 1. 找到对应的组件
            AiWorkflowComponentEntity wfComponent = components.stream()
                    .filter(item -> item.getId().equals(wfNode.getWorkflowComponentId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("组件不存在"));

            // 2. 通过工厂创建节点实例
            AbstractWfNode abstractWfNode = WfNodeFactory.create(wfComponent, wfNode, wfState, nodeState);

            // 3. 创建运行时节点记录
            // 获取runtime_id：顶层工作流使用wfRuntimeResp.getId()，子工作流需要查询获取
            Long runtimeId;
            if (this.wfRuntimeResp != null) {
                // 顶层工作流（Workflow独立测试）：直接使用wfRuntimeResp
                runtimeId = this.wfRuntimeResp.getId();
            } else if (this.conversationRuntimeResp != null) {
                // 顶层工作流（AI Chat调用）：直接使用conversationRuntimeResp
                runtimeId = this.conversationRuntimeResp.getId();
            } else {
                // 子工作流:通过wfState.getUuid()查询获取runtime_id(复用父工作流的runtime记录)
                if (callSource == WorkflowCallSource.AI_CHAT) {
                    AiConversationRuntimeEntity runtime = conversationRuntimeService.getByUuid(wfState.getUuid());
                    runtimeId = runtime.getId();
                } else {
                    AiWorkflowRuntimeEntity runtime = workflowRuntimeService.getByUuid(wfState.getUuid());
                    runtimeId = runtime.getId();
                }
            }

            // 根据callSource创建运行时节点记录
            Long runtimeNodeId; // 存储节点ID用于后续更新操作
            if (callSource == WorkflowCallSource.AI_CHAT) {
                AiConversationRuntimeNodeVo nodeVo = conversationRuntimeNodeService.createByState(
                        runtimeId, nodeState, wfNode.getId(), this.userId);
                runtimeNodeId = nodeVo.getId();
            } else {
                AiWorkflowRuntimeNodeVo nodeVo = workflowRuntimeNodeService.createByState(
                        userId, wfNode.getId(), runtimeId, nodeState);
                wfState.getRuntimeNodes().add(nodeVo);
                runtimeNodeId = nodeVo.getId();
            }

            // 5. 执行节点（简化版 - 对齐Spring AI Alibaba设计）
            NodeProcessResult processResult = abstractWfNode.process();

            // 6. 更新节点输入到数据库
            DataSourceHelper.use(this.tenantCode);
            if (callSource == WorkflowCallSource.AI_CHAT) {
                conversationRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
            } else {
                workflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
            }

            // 7. 更新节点输出到数据库
            if (callSource == WorkflowCallSource.AI_CHAT) {
                conversationRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
            } else {
                workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
            }

            // 8. 设置下一个节点(如果有)
            if (StringUtils.isNotBlank(processResult.getNextNodeUuid())) {
                resultMap.put("next", processResult.getNextNodeUuid());
            }

            // 6.1 设置条件分支匹配的sourceHandle(如果有)
            // 用于支持同一条件分支触发多个下游节点并行执行
            if (StringUtils.isNotBlank(processResult.getNextSourceHandle())) {
                resultMap.put("next_source_handle", processResult.getNextSourceHandle());
                log.info("[runNode] 条件分支返回sourceHandle: {}", processResult.getNextSourceHandle());
            }

        } catch (Exception e) {
            log.error("Node run error: {} ({})", wfNode.getTitle(), wfNode.getUuid(), e);
            throw new RuntimeException(e);
        }

        // 7. 设置节点名称
        resultMap.put("name", wfNode.getTitle());

        // 8. 将节点输出放入返回Map，让框架自动合并到OverAllState
        // 使用nodeUuid作为key确保并行节点输出不会互相覆盖
        String outputKey = NODE_OUTPUT_KEY_PREFIX + wfNode.getUuid();
        resultMap.put(outputKey, nodeState.getOutputs());

        log.debug("runNode执行完成: nodeUuid={}, 耗时={}ms, outputs数量={}",
                wfNode.getUuid(), System.currentTimeMillis() - startTime, nodeState.getOutputs().size());

        return resultMap;
    }

    /**
     * 校验用户输入并组装成工作流的输入
     *
     * @param userInputs 用户输入
     * @param startNode  开始节点定义
     * @return 正确的用户输入列表
     */
    private List<NodeIOData> getAndCheckUserInput(List<JSONObject> userInputs, AiWorkflowNodeVo startNode) {
        // 获取 Start 节点的输入定义列表
        List<AiWfNodeIOVo> defList = startNode.getInputConfig().getUserInputs();
        List<NodeIOData> wfInputs = new ArrayList<>();

        // 如果StartNode没有配置输入定义，直接使用传入的userInputs（兼容简单工作流）
        if (defList == null || defList.isEmpty()) {
            log.info("StartNode未配置输入定义，直接使用传入的userInputs");
            for (JSONObject userInput : userInputs) {
                NodeIOData nodeIOData = WfNodeIODataUtil.createNodeIOData(userInput);
                wfInputs.add(nodeIOData);
            }
            return wfInputs;
        }

        // 遍历每个输入定义,验证用户输入
        for (AiWfNodeIOVo paramDefinition : defList) {
            String paramNameFromDef = paramDefinition.getName();
            boolean requiredParamMissing = paramDefinition.getRequired();

            for (JSONObject userInput : userInputs) {
                // 转换用户输入为 NodeIOData
                NodeIOData nodeIOData = WfNodeIODataUtil.createNodeIOData(userInput);
                if (!paramNameFromDef.equalsIgnoreCase(nodeIOData.getName())) {
                    continue;
                }

                // 检查数据类型
                Integer dataType = nodeIOData.getContent().getType();
                if (null == dataType) {
                    throw new RuntimeException("用户输入数据类型无效");
                }

                requiredParamMissing = false;

                // 调用 checkValue 验证
                boolean valid = paramDefinition.checkValue(nodeIOData);
                if (!valid) {
                    log.error("用户输入无效,workflowId:{}", startNode.getWorkflowId());
                    throw new RuntimeException("用户输入无效");
                }

                wfInputs.add(nodeIOData);
            }

            // 检查必填参数是否缺失
            if (requiredParamMissing) {
                log.error("在流程定义中必填的参数没有传进来,name:{}", paramNameFromDef);
                throw new RuntimeException("必填参数缺失: " + paramNameFromDef);
            }
        }

        return wfInputs;
    }

    /**
     * 查找开始及结束节点
     * 开始节点只能有一个，结束节点可能多个
     *
     * @return 开始节点及结束节点列表
     */
    public Pair<AiWorkflowNodeVo, Set<AiWorkflowNodeVo>> findStartAndEndNode() {
        AiWorkflowNodeVo startNode = null;
        Set<AiWorkflowNodeVo> endNodes = new HashSet<>();

        for (AiWorkflowNodeVo node : wfNodes) {
            Optional<AiWorkflowComponentEntity> wfComponent = components.stream()
                    .filter(item -> item.getId().equals(node.getWorkflowComponentId()))
                    .findFirst();

            if (wfComponent.isPresent() && "Start".equals(wfComponent.get().getName())) {
                if (null != startNode) {
                    throw new RuntimeException("工作流中存在多个开始节点");
                }
                startNode = node;
            } else if (wfComponent.isPresent() && "End".equals(wfComponent.get().getName())) {
                endNodes.add(node);
            }
        }

        if (null == startNode) {
            log.error("没有开始节点,workflowId:{}", wfNodes.get(0).getWorkflowId());
            throw new RuntimeException("未找到开始节点");
        }

        // Find all end nodes (没有出边的节点也是结束节点)
        wfNodes.forEach(item -> {
            String nodeUuid = item.getUuid();
            boolean source = false;
            boolean target = false;
            for (AiWorkflowEdgeEntity edgeDef : wfEdges) {
                if (edgeDef.getSourceNodeUuid().equals(nodeUuid)) {
                    source = true;
                } else if (edgeDef.getTargetNodeUuid().equals(nodeUuid)) {
                    target = true;
                }
            }
            if (!source && target) {
                endNodes.add(item);
            }
        });

        log.info("start node:{}", startNode);
        log.info("end nodes:{}", endNodes);

        if (endNodes.isEmpty()) {
            log.error("没有结束节点,workflowId:{}", startNode.getWorkflowId());
            throw new RuntimeException("未找到结束节点");
        }
        return Pair.of(startNode, endNodes);
    }

    /**
     * 简化版buildStateGraph - 直接遍历边数据构建StateGraph
     * 让Spring AI Alibaba框架自动处理并行分叉和汇聚
     *
     * @param stateGraph 状态图
     * @param startNode  开始节点
     * @throws GraphStateException 状态图异常
     */
    private void buildStateGraph(StateGraph stateGraph, AiWorkflowNodeVo startNode) throws GraphStateException {
        // 1. 添加所有节点到StateGraph
        for (AiWorkflowNodeVo node : wfNodes) {
            addNodeToStateGraph(stateGraph, node.getUuid());
        }

        // 2. 添加START到开始节点的边
        stateGraph.addEdge(START, startNode.getUuid());
        wfState.addEdge(START, startNode.getUuid());

        // 3. 找出所有条件分支节点的UUID（Switcher和Classifier组件）
        // 注意：sourceHandle="right"只是X6图的默认连接点，不代表条件分支
        Set<String> conditionalNodeUuids = new HashSet<>();
        for (AiWorkflowNodeVo node : wfNodes) {
            AiWorkflowComponentEntity component = components.stream()
                .filter(c -> c.getId().equals(node.getWorkflowComponentId()))
                .findFirst()
                .orElse(null);
            if (component != null && ("Switcher".equals(component.getName()) || "Classifier".equals(component.getName()))) {
                conditionalNodeUuids.add(node.getUuid());
            }
        }

        // 3.5 线性化发散并行路径，绕过Alibaba框架的汇聚验证
        // 将 fork→A→End1, fork→B→End2 转为 fork→A→End1→B→End2 顺序链
        Set<String> intermediateEndNodes = linearizeDivergentPaths(conditionalNodeUuids);

        // 4. 遍历所有边，添加到StateGraph（使用线性化后的wfEdges）
        for (AiWorkflowEdgeEntity edge : wfEdges) {
            String source = edge.getSourceNodeUuid();
            String target = edge.getTargetNodeUuid();

            // 跳过条件分支节点的边（由processConditionalEdges单独处理）
            if (conditionalNodeUuids.contains(source)) {
                continue;
            }

            // 普通边：直接添加
            addEdgeToStateGraph(stateGraph, source, target);
        }

        // 5. 处理条件分支节点
        processConditionalEdges(stateGraph);

        // 6. 找到所有结束节点，添加到END的边（跳过线性化链中的中间End节点）
        for (AiWorkflowNodeVo node : wfNodes) {
            if (isEndNode(node) && !intermediateEndNodes.contains(node.getUuid())) {
                addEdgeToStateGraph(stateGraph, node.getUuid(), END);
            }
        }
    }

    /**
     * 处理条件分支边
     *
     * <p>只处理Switcher和Classifier组件的边作为条件边，普通节点的边不应被处理为条件边</p>
     * <p>支持同一sourceHandle对应多个目标节点的并行执行：通过创建虚拟并行分发节点来桥接</p>
     *
     * @param stateGraph 状态图
     * @throws GraphStateException 状态图异常
     */
    private void processConditionalEdges(StateGraph stateGraph) throws GraphStateException {
        // 找出所有条件分支节点的UUID（Switcher和Classifier组件）
        Set<String> conditionalNodeUuids = new HashSet<>();
        for (AiWorkflowNodeVo node : wfNodes) {
            AiWorkflowComponentEntity component = components.stream()
                .filter(c -> c.getId().equals(node.getWorkflowComponentId()))
                .findFirst()
                .orElse(null);
            // 只有Switcher和Classifier组件才是条件分支节点
            if (component != null && ("Switcher".equals(component.getName()) || "Classifier".equals(component.getName()))) {
                conditionalNodeUuids.add(node.getUuid());
            }
        }

        // 为每个条件分支节点处理其出边
        for (String switcherUuid : conditionalNodeUuids) {
            // 收集该Switcher的所有出边，按sourceHandle分组
            Map<String, List<AiWorkflowEdgeEntity>> edgesBySourceHandle = new HashMap<>();
            for (AiWorkflowEdgeEntity edge : wfEdges) {
                if (switcherUuid.equals(edge.getSourceNodeUuid())) {
                    String sourceHandle = edge.getSourceHandle();
                    if (StringUtils.isBlank(sourceHandle)) {
                        sourceHandle = "default_handle";
                    }
                    edgesBySourceHandle
                        .computeIfAbsent(sourceHandle, k -> new ArrayList<>())
                        .add(edge);
                }
            }

            log.info("[processConditionalEdges] Switcher节点 {} 的边按sourceHandle分组: {}",
                    switcherUuid, edgesBySourceHandle.keySet());

            // 构建条件路由映射: sourceHandle -> 目标节点UUID
            Map<String, String> mappings = new HashMap<>();

            for (Map.Entry<String, List<AiWorkflowEdgeEntity>> entry : edgesBySourceHandle.entrySet()) {
                String sourceHandle = entry.getKey();
                List<AiWorkflowEdgeEntity> edges = entry.getValue();

                // 去重：同一sourceHandle可能有重复的边记录
                Set<String> uniqueTargets = edges.stream()
                    .map(AiWorkflowEdgeEntity::getTargetNodeUuid)
                    .collect(Collectors.toSet());

                if (uniqueTargets.size() == 1) {
                    // 单目标：直接映射到目标节点
                    String targetUuid = uniqueTargets.iterator().next();
                    mappings.put(sourceHandle, targetUuid);
                    log.info("[processConditionalEdges] sourceHandle {} -> 单目标 {}", sourceHandle, targetUuid);
                } else {
                    // 多目标：创建虚拟并行节点，并行执行所有分支链后汇聚
                    String convergenceNodeId = findConvergenceNodeUuid(uniqueTargets);
                    if (convergenceNodeId == null) {
                        // 找不到汇聚点，退化为取第一个，保证工作流不中断
                        String targetUuid = new ArrayList<>(uniqueTargets).get(0);
                        mappings.put(sourceHandle, targetUuid);
                        log.warn("[processConditionalEdges] sourceHandle {} 的多目标分支找不到汇聚点，退化为单目标: {}",
                                sourceHandle, targetUuid);
                        continue;
                    }

                    // 追踪每条分支链（从各目标节点到汇聚点，不含汇聚点自身）
                    List<List<String>> chains = new ArrayList<>();
                    for (String targetId : uniqueTargets) {
                        chains.add(traceChainNodeUuids(targetId, convergenceNodeId));
                    }

                    // 创建虚拟并行节点，负责并行执行所有分支链
                    String virtualNodeId = VIRTUAL_PARALLEL_PREFIX + switcherUuid + "_" + sourceHandle;
                    final List<List<String>> finalChains = chains;
                    stateGraph.addNode(virtualNodeId, state ->
                            CompletableFuture.supplyAsync(() -> executeParallelChains(finalChains, state)));

                    mappings.put(sourceHandle, virtualNodeId);
                    addEdgeToStateGraph(stateGraph, virtualNodeId, convergenceNodeId);
                    log.info("[processConditionalEdges] sourceHandle {} -> 并行节点 {}, 汇聚点 {}, 分支数 {}",
                            sourceHandle, virtualNodeId, convergenceNodeId, chains.size());
                }
            }

            // 添加条件边：使用next_source_handle进行路由
            stateGraph.addConditionalEdges(
                switcherUuid,
                edge_async(state -> {
                    Object nextSourceHandle = state.data().get("next_source_handle");
                    if (nextSourceHandle == null) {
                        // 兼容旧版本：如果没有next_source_handle，尝试使用next
                        Object next = state.data().get("next");
                        if (next != null) {
                            log.info("[条件路由] 使用旧版next字段: {}", next);
                            return next.toString();
                        }
                        log.warn("[条件路由] Switcher[{}]未设置next_source_handle，使用default_handle", switcherUuid);
                        return "default_handle";
                    }
                    log.info("[条件路由] Switcher[{}] -> sourceHandle: {}", switcherUuid, nextSourceHandle);
                    return nextSourceHandle.toString();
                }),
                mappings
            );
        }
    }

    /**
     * 判断是否是结束节点（End组件或没有出边的节点）
     *
     * @param node 节点
     * @return true表示是结束节点
     */
    private boolean isEndNode(AiWorkflowNodeVo node) {
        // 检查是否是End组件
        AiWorkflowComponentEntity component = components.stream()
            .filter(c -> c.getId().equals(node.getWorkflowComponentId()))
            .findFirst()
            .orElse(null);

        if (component != null && "End".equals(component.getName())) {
            return true;
        }

        // 检查是否没有出边
        boolean hasOutEdge = wfEdges.stream()
            .anyMatch(edge -> edge.getSourceNodeUuid().equals(node.getUuid()));

        return !hasOutEdge;
    }

    /**
     * 添加节点到状态图
     *
     * @param stateGraph         状态图
     * @param stateGraphNodeUuid 节点UUID
     * @throws GraphStateException 状态图异常
     */
    private void addNodeToStateGraph(StateGraph stateGraph, String stateGraphNodeUuid) throws GraphStateException {
        List<StateGraph> stateGraphList = stateGraphNodes.computeIfAbsent(stateGraphNodeUuid, k -> new ArrayList<>());
        boolean exist = stateGraphList.stream().anyMatch(item -> item == stateGraph);
        if (exist) {
            log.info("state graph node exist,stateGraphNodeUuid:{}", stateGraphNodeUuid);
            return;
        }

        log.info("addNodeToStateGraph,node uuid:{}", stateGraphNodeUuid);
        AiWorkflowNodeVo wfNode = getNodeByUuid(stateGraphNodeUuid);
        // 使用Spring AI Alibaba的AsyncNodeAction方式添加节点
        // 注意：OverAllState是final类，WfNodeState使用组合模式包装
        stateGraph.addNode(stateGraphNodeUuid, state -> CompletableFuture.supplyAsync(() -> {
            // 从OverAllState创建WfNodeState（复制data数据）
            WfNodeState nodeState = new WfNodeState(state.data());
            return runNode(wfNode, nodeState);
        }));
        stateGraphList.add(stateGraph);

        // 记录人机交互节点
        AiWorkflowComponentEntity wfComponent = components.stream()
                .filter(item -> item.getId().equals(wfNode.getWorkflowComponentId()))
                .findFirst()
                .orElse(null);
        if (wfComponent != null && "HumanFeedback".equals(wfComponent.getName())) {
            this.wfState.addInterruptNode(stateGraphNodeUuid);
        }
    }

    private void addEdgeToStateGraph(StateGraph stateGraph, String source, String target) throws GraphStateException {
        String key = source + "_" + target;
        List<StateGraph> stateGraphList = stateGraphEdges.computeIfAbsent(key, k -> new ArrayList<>());
        boolean exist = stateGraphList.stream().anyMatch(item -> item == stateGraph);
        if (exist) {
            log.info("state graph edge exist,source:{},target:{}", source, target);
            return;
        }

        log.info("addEdgeToStateGraph,source:{},target:{}", source, target);
        stateGraph.addEdge(source, target);
        stateGraphList.add(stateGraph);
    }

    private AiWorkflowNodeVo getNodeByUuid(String nodeUuid) {
        return wfNodes.stream()
                .filter(item -> item.getUuid().equals(nodeUuid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到节点: " + nodeUuid));
    }

    private String getHumanFeedbackTip(String nextNode) {
        return wfNodes.stream()
                .filter(node -> node.getUuid().equals(nextNode))
                .findFirst()
                .map(node -> "等待用户输入: " + node.getTitle())
                .orElse("等待用户输入");
    }

    /**
     * 获取节点的直接后继节点UUID（单出边场景兼容方法）。
     * 仅适用于确定只有单出边的普通节点。
     */
    private String getDirectNextNodeUuid(String nodeUuid) {
        return wfEdges.stream()
                .filter(e -> nodeUuid.equals(e.getSourceNodeUuid()))
                .map(AiWorkflowEdgeEntity::getTargetNodeUuid)
                .findFirst()
                .orElse(null);
    }

    /**
     * 线性化发散并行路径。
     *
     * <p>当非条件节点有2+个出边且各路径终止于不同End节点时，
     * 将发散并行改为顺序链：fork→A→End1→B→End2。
     * 绕过Alibaba框架CompiledGraph.compile()的并行汇聚验证限制。</p>
     *
     * <p>副作用：修改内存中的wfEdges列表（不影响数据库记录）。</p>
     *
     * @param conditionalNodeUuids 条件分支节点UUID集合（Switcher/Classifier）
     * @return 中间End节点UUID集合（不应连接__END__的End节点）
     */
    private Set<String> linearizeDivergentPaths(Set<String> conditionalNodeUuids) {
        Set<String> intermediateEndNodes = new HashSet<>();

        // 按source分组所有非条件边
        Map<String, List<AiWorkflowEdgeEntity>> edgesBySource = new LinkedHashMap<>();
        for (AiWorkflowEdgeEntity edge : wfEdges) {
            if (!conditionalNodeUuids.contains(edge.getSourceNodeUuid())) {
                edgesBySource.computeIfAbsent(edge.getSourceNodeUuid(), k -> new ArrayList<>()).add(edge);
            }
        }

        for (Map.Entry<String, List<AiWorkflowEdgeEntity>> entry : edgesBySource.entrySet()) {
            List<AiWorkflowEdgeEntity> edges = entry.getValue();
            if (edges.size() < 2) continue;

            String forkUuid = entry.getKey();

            // 追踪每条路径到End节点
            List<List<String>> paths = new ArrayList<>();
            for (AiWorkflowEdgeEntity edge : edges) {
                paths.add(tracePathToEnd(edge.getTargetNodeUuid()));
            }

            // 检查是否发散（各路径终止于不同End节点）
            Set<String> endNodeUuids = paths.stream()
                .map(path -> path.get(path.size() - 1))
                .collect(Collectors.toSet());

            if (endNodeUuids.size() <= 1) continue; // 汇聚并行，框架原生处理

            log.info("[linearize] 检测到发散并行：fork={}, 路径数={}", forkUuid, paths.size());

            // 线性化：保留第一条边，后续路径链到前一路径的End节点
            for (int i = 1; i < paths.size(); i++) {
                List<String> prevPath = paths.get(i - 1);
                String prevEnd = prevPath.get(prevPath.size() - 1);
                String nextStart = paths.get(i).get(0);

                // 移除 fork→nextStart 的直接边
                final String finalNextStart = nextStart;
                wfEdges.removeIf(e ->
                    forkUuid.equals(e.getSourceNodeUuid()) &&
                    finalNextStart.equals(e.getTargetNodeUuid()));

                // 添加 prevEnd→nextStart 的链式边
                AiWorkflowEdgeEntity chainEdge = new AiWorkflowEdgeEntity();
                chainEdge.setSourceNodeUuid(prevEnd);
                chainEdge.setTargetNodeUuid(nextStart);
                wfEdges.add(chainEdge);

                log.info("[linearize] 链式连接：{} → {}", prevEnd, nextStart);

                // 标记中间End节点（不连接__END__）
                if (isEndNode(getNodeByUuid(prevEnd))) {
                    intermediateEndNodes.add(prevEnd);
                    log.info("[linearize] 中间End节点（不连接__END__）：{}", prevEnd);
                }
            }
        }

        return intermediateEndNodes;
    }

    /**
     * 从指定节点开始，追踪路径直到End节点或无出边。
     *
     * @param startNodeUuid 起始节点UUID
     * @return 路径上的节点UUID列表（含起点和End节点）
     */
    private List<String> tracePathToEnd(String startNodeUuid) {
        List<String> path = new ArrayList<>();
        String current = startNodeUuid;
        Set<String> visited = new HashSet<>();

        while (current != null && !visited.contains(current)) {
            visited.add(current);
            path.add(current);

            if (isEndNode(getNodeByUuid(current))) break;

            // 跟踪下一个节点（取第一条出边）
            String next = null;
            for (AiWorkflowEdgeEntity edge : wfEdges) {
                if (current.equals(edge.getSourceNodeUuid())) {
                    next = edge.getTargetNodeUuid();
                    break;
                }
            }
            current = next;
        }

        return path;
    }

    /**
     * 获取节点的所有直接后继节点UUID。
     * 支持多出边场景（如一个节点同时连接美化返回和打开前端页面）。
     */
    private List<String> getAllNextNodeUuids(String nodeUuid) {
        return wfEdges.stream()
                .filter(e -> nodeUuid.equals(e.getSourceNodeUuid()))
                .map(AiWorkflowEdgeEntity::getTargetNodeUuid)
                .collect(Collectors.toList());
    }

    /**
     * 找到多条分支链的汇聚点（后支配节点）。
     * 使用BFS遍历所有出边，找到所有分支都必须经过的第一个公共节点。
     */
    private String findConvergenceNodeUuid(Set<String> startNodeIds) {
        // 对每个起始节点，BFS收集所有可达节点（按BFS序）
        List<List<String>> allReachable = new ArrayList<>();
        for (String startId : startNodeIds) {
            List<String> reachable = new ArrayList<>();
            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(startId);
            visited.add(startId);
            while (!queue.isEmpty()) {
                String current = queue.poll();
                reachable.add(current);
                for (String next : getAllNextNodeUuids(current)) {
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
            allReachable.add(reachable);
        }

        if (allReachable.isEmpty() || allReachable.get(0).isEmpty()) {
            return null;
        }

        // 取所有分支共同可达的节点（保持第一条链的BFS序）
        Set<String> commonNodes = new LinkedHashSet<>(allReachable.get(0));
        for (int i = 1; i < allReachable.size(); i++) {
            commonNodes.retainAll(new HashSet<>(allReachable.get(i)));
        }
        // 移除起始节点自身
        commonNodes.removeAll(startNodeIds);

        if (commonNodes.isEmpty()) {
            return null;
        }

        // 按BFS序检查：第一个"后支配节点"即为汇聚点
        // 后支配节点 = 从任何起始节点出发，都无法绕过该节点到达终点
        for (String candidate : commonNodes) {
            boolean isDominator = true;
            for (String startId : startNodeIds) {
                if (hasPathBypassingNode(startId, candidate)) {
                    isDominator = false;
                    break;
                }
            }
            if (isDominator) {
                return candidate;
            }
        }

        // 退化：没有严格后支配节点，返回BFS序最早的公共节点
        return commonNodes.iterator().next();
    }

    /**
     * 检查从startId出发是否存在一条绕过bypassNode的路径到达任何终点。
     * 终点定义：出度为0的节点，或名为END的节点。
     */
    private boolean hasPathBypassingNode(String startId, String bypassNode) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(startId);
        visited.add(startId);
        visited.add(bypassNode); // 禁止经过此节点
        while (!queue.isEmpty()) {
            String current = queue.poll();
            List<String> nexts = getAllNextNodeUuids(current);
            if (nexts.isEmpty() && !current.equals(startId)) {
                // 到达终点且绕过了bypassNode
                return true;
            }
            for (String next : nexts) {
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }

    /**
     * 追踪从 startNodeId 到 convergenceNodeId 之间的所有节点（BFS，不含汇聚点自身）。
     * 支持多出边场景：一个节点可能有多个后继。
     */
    private List<String> traceChainNodeUuids(String startNodeId, String convergenceNodeId) {
        List<String> chain = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(startNodeId);
        visited.add(startNodeId);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(convergenceNodeId)) {
                continue; // 不含汇聚点
            }
            chain.add(current);
            for (String next : getAllNextNodeUuids(current)) {
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return chain;
    }

    /**
     * 并行执行多条节点链，合并所有链的输出结果。
     * 每条链内部顺序执行，多条链之间并行执行。
     * 支持共享节点：出现在多条链中的节点只执行一次（在合并阶段顺序执行）。
     */
    private Map<String, Object> executeParallelChains(List<List<String>> chains, OverAllState parentState) {
        // 统计每个节点出现在几条链中
        Map<String, Integer> nodeAppearCount = new HashMap<>();
        for (List<String> chain : chains) {
            for (String nodeUuid : chain) {
                nodeAppearCount.merge(nodeUuid, 1, Integer::sum);
            }
        }

        // 分离：共享节点（出现在多条链） vs 独占节点
        Set<String> sharedNodes = new HashSet<>();
        for (Map.Entry<String, Integer> entry : nodeAppearCount.entrySet()) {
            if (entry.getValue() > 1) {
                sharedNodes.add(entry.getKey());
            }
        }

        // 构建去重后的独占链
        List<List<String>> uniqueChains = new ArrayList<>();
        for (List<String> chain : chains) {
            List<String> uniqueChain = chain.stream()
                    .filter(n -> !sharedNodes.contains(n))
                    .collect(Collectors.toList());
            if (!uniqueChain.isEmpty()) {
                uniqueChains.add(uniqueChain);
            }
        }

        Map<String, Object> snapshotData = new HashMap<>(parentState.data());
        Map<String, Object> merged = new HashMap<>();

        // 阶段1：并行执行独占链
        if (!uniqueChains.isEmpty()) {
            merged.putAll(runChainsInParallel(uniqueChains, snapshotData));
        }

        // 阶段2：顺序执行共享节点（使用合并后的状态）
        if (!sharedNodes.isEmpty()) {
            Map<String, Object> mergedState = new HashMap<>(snapshotData);
            mergedState.putAll(merged);
            for (String nodeUuid : sharedNodes) {
                AiWorkflowNodeVo node = getNodeByUuid(nodeUuid);
                WfNodeState nodeState = new WfNodeState(mergedState);
                Map<String, Object> nodeResult = runNode(node, nodeState);
                mergedState.putAll(nodeResult);
                merged.putAll(nodeResult);
            }
        }

        return merged;
    }

    /**
     * 并行执行多条独占链，返回合并结果。
     */
    private Map<String, Object> runChainsInParallel(List<List<String>> chains, Map<String, Object> snapshotData) {
        List<CompletableFuture<Map<String, Object>>> futures = chains.stream()
                .map(chain -> CompletableFuture.supplyAsync(() -> {
                    DataSourceHelper.use(this.tenantCode);
                    Map<String, Object> chainState = new HashMap<>(snapshotData);
                    Map<String, Object> chainResult = new HashMap<>();
                    for (String nodeUuid : chain) {
                        AiWorkflowNodeVo node = getNodeByUuid(nodeUuid);
                        WfNodeState nodeState = new WfNodeState(chainState);
                        Map<String, Object> nodeResult = runNode(node, nodeState);
                        chainState.putAll(nodeResult);
                        chainResult.putAll(nodeResult);
                    }
                    return chainResult;
                }))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        Map<String, Object> merged = new HashMap<>();
        for (CompletableFuture<Map<String, Object>> future : futures) {
            try {
                merged.putAll(future.get());
            } catch (Exception e) {
                throw new RuntimeException("并行分支执行失败", e);
            }
        }
        return merged;
    }

    public CompiledGraph getApp() {
        return app;
    }

    /**
     * 节点生命周期监听器
     * 在每个节点执行前后发送node_start和node_complete事件
     * 通过sinkRef发送到Flux管道，前端实时展示执行步骤
     */
    private class NodeEventListener implements GraphLifecycleListener {

        // 展示的6种节点类型（设计文档3.2节定义）
        // 不展示：Start、End、Template、Switcher、SubWorkflow、HttpRequest、MailSend、KeywordExtractor、FaqExtractor
        private static final Set<String> VISIBLE_NODES = Set.of(
            "Classifier", "KnowledgeRetrieval", "TempKnowledgeBase",
            "Answer", "McpTool", "DocumentExtractor", "LLM"
        );

        @Override
        public void before(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
            String componentName = findComponentName(nodeId);
            if (!VISIBLE_NODES.contains(componentName)) {
                return;
            }
            nodeStartTimes.put(nodeId, curTime);
            String nodeTitle = findNodeTitle(nodeId);
            Sinks.Many<WorkflowEventVo> sink = sinkRef.get();
            if (sink != null) {
                sink.tryEmitNext(WorkflowEventVo.createNodeStartData(nodeId, componentName, nodeTitle, curTime));
            }
        }

        @Override
        public void after(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
            String componentName = findComponentName(nodeId);
            if (!VISIBLE_NODES.contains(componentName)) {
                return;
            }
            Long startTime = nodeStartTimes.remove(nodeId);
            long duration = (startTime != null) ? (curTime - startTime) : 0L;
            String nodeTitle = findNodeTitle(nodeId);
            Map<String, Object> summary = buildSummary(componentName, nodeId, state);
            Sinks.Many<WorkflowEventVo> sink = sinkRef.get();
            if (sink != null) {
                sink.tryEmitNext(WorkflowEventVo.createNodeCompleteData(nodeId, componentName, nodeTitle, duration, summary));
            }
        }

        private String findComponentName(String nodeId) {
            return wfNodes.stream()
                .filter(n -> nodeId.equals(n.getUuid()))
                .findFirst()
                .map(n -> components.stream()
                    .filter(c -> c.getId().equals(n.getWorkflowComponentId()))
                    .findFirst()
                    .map(AiWorkflowComponentEntity::getName)
                    .orElse(""))
                .orElse("");
        }

        private String findNodeTitle(String nodeId) {
            return wfNodes.stream()
                .filter(n -> nodeId.equals(n.getUuid()))
                .findFirst()
                .map(AiWorkflowNodeVo::getTitle)
                .orElse("");
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> buildSummary(String componentName, String nodeId, Map<String, Object> state) {
            Map<String, Object> summary = null;
            try {
                String outputKey = NODE_OUTPUT_KEY_PREFIX + nodeId;
                Object outputObj = state.get(outputKey);
                Map<String, Object> outputMap = (outputObj instanceof Map) ? (Map<String, Object>) outputObj : null;

                switch (componentName) {
                    case "KnowledgeRetrieval": {
                        if (outputMap != null) {
                            Object matchCount = outputMap.get("matchCount");
                            if (matchCount != null) {
                                summary = new HashMap<>();
                                summary.put("matchCount", matchCount);
                            }
                        }
                        break;
                    }
                    case "Classifier": {
                        if (outputMap != null) {
                            Object result = outputMap.get("result");
                            if (result == null) result = outputMap.get("output");
                            if (result != null) {
                                summary = new HashMap<>();
                                summary.put("result", String.valueOf(result));
                            }
                        }
                        break;
                    }
                    case "McpTool": {
                        if (outputMap != null) {
                            Object toolName = outputMap.get("toolName");
                            if (toolName != null) {
                                summary = new HashMap<>();
                                summary.put("toolName", String.valueOf(toolName));
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                log.debug("获取{}节点摘要失败: {}", componentName, e.getMessage());
            }
            // 追加Token消耗（Classifier/Answer/LLM等调用LLM的节点）
            long[] tokens = wfState.getNodeTokens(nodeId);
            if (tokens != null) {
                if (summary == null) summary = new HashMap<>();
                summary.put("totalTokens", tokens[0] + tokens[1]);
            }
            return summary;
        }
    }
}
