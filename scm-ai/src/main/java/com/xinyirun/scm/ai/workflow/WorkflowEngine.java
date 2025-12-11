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

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private WorkflowStreamHandler streamHandler;
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
     * 获取租户编码（用于数据源切换）
     * @return 租户编码
     */
    public String getTenantCode() {
        return this.tenantCode;
    }

    /**
     * 设置流式处理器（用于工作流恢复时替换StreamHandler）
     * @param handler 新的流式处理器
     */
    public void setStreamHandler(WorkflowStreamHandler handler) {
        this.streamHandler = handler;
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
     * 使用 WorkflowStreamHandler 实现事件驱动的流式输出
     */
    public WorkflowEngine(
            AiWorkflowEntity workflow,
            WorkflowStreamHandler streamHandler,
            List<AiWorkflowComponentEntity> components,
            List<AiWorkflowNodeVo> nodes,
            List<AiWorkflowEdgeEntity> wfEdges,
            WorkflowCallSource callSource,
            AiWorkflowRuntimeService workflowRuntimeService,
            AiWorkflowRuntimeNodeService workflowRuntimeNodeService,
            AiConversationRuntimeService conversationRuntimeService,
            AiConversationRuntimeNodeService conversationRuntimeNodeService) {
        this(workflow, streamHandler, components, nodes, wfEdges,
            callSource, workflowRuntimeService, workflowRuntimeNodeService,
            conversationRuntimeService, conversationRuntimeNodeService, null);
    }

    /**
     * 构造函数（支持子工作流）
     * 使用 WorkflowStreamHandler 实现事件驱动的流式输出
     *
     * @param parentRuntimeUuid 父工作流的runtime_uuid，null表示顶层工作流
     */
    public WorkflowEngine(
            AiWorkflowEntity workflow,
            WorkflowStreamHandler streamHandler,
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
        this.streamHandler = streamHandler;
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

    public void run(Long userId, List<JSONObject> userInputs, String tenantCode, String parentConversationId) {
        this.userId = userId;
        this.tenantCode = tenantCode;
        DataSourceHelper.use(this.tenantCode);
        log.info("WorkflowEngine run,userId:{},workflowUuid:{},tenantCode:{},parentConversationId:{},userInputs:{}",
                 userId, workflow.getWorkflowUuid(), tenantCode, parentConversationId, userInputs);

        if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
            streamHandler.sendError(new RuntimeException("工作流已禁用"));
            throw new RuntimeException("工作流已禁用");
        }

        Long workflowId = this.workflow.getId();

        // 声明变量
        String runtimeUuid;
        String conversationId;

        // 关键修改：只有顶层工作流创建runtime记录
        if (parentRuntimeUuid == null) {
            // 顶层工作流：创建新的runtime记录
            // 根据callSource选择服务
            if (callSource == WorkflowCallSource.AI_CHAT) {
                // AI Chat调用：使用conversationRuntimeService
                if (parentConversationId != null && !parentConversationId.isEmpty()) {
                    this.conversationRuntimeResp = conversationRuntimeService.createWithConversationId(
                        userId, workflowId, parentConversationId);
                } else {
                    this.conversationRuntimeResp = conversationRuntimeService.create(userId, workflowId);
                }
                runtimeUuid = this.conversationRuntimeResp.getRuntime_uuid();
                conversationId = this.conversationRuntimeResp.getConversation_id();
                // 发送工作流开始事件
                streamHandler.sendStart(JSONObject.toJSONString(conversationRuntimeResp));
            } else {
                // Workflow独立测试：使用workflowRuntimeService
                if (parentConversationId != null && !parentConversationId.isEmpty()) {
                    this.wfRuntimeResp = workflowRuntimeService.createWithConversationId(
                        userId, workflowId, parentConversationId);
                } else {
                    this.wfRuntimeResp = workflowRuntimeService.create(userId, workflowId);
                }
                runtimeUuid = this.wfRuntimeResp.getRuntimeUuid();
                conversationId = this.wfRuntimeResp.getConversationId();
                // 发送工作流开始事件
                streamHandler.sendStart(JSONObject.toJSONString(wfRuntimeResp));
            }

            log.info("顶层工作流创建runtime记录 - runtime_uuid: {}", runtimeUuid);
        } else {
            // 子工作流：复用父runtime_uuid，不创建新记录
            log.info("子工作流复用父runtime_uuid: {}", parentRuntimeUuid);

            runtimeUuid = parentRuntimeUuid;
            conversationId = parentConversationId != null ? parentConversationId
                : (tenantCode + "::" + workflow.getWorkflowUuid() + "::" + userId);

            // 子工作流不发送start事件（父工作流已经发送过了）
            // 不创建 wfRuntimeResp（因为不创建runtime记录）
        }

        log.debug("WorkflowEngine开始执行 - runtime_uuid: {}", runtimeUuid);

        try {
            Pair<AiWorkflowNodeVo, Set<AiWorkflowNodeVo>> startAndEnds = findStartAndEndNode();
            AiWorkflowNodeVo startNode = startAndEnds.getLeft();
            List<NodeIOData> wfInputs = getAndCheckUserInput(userInputs, startNode);

            // 工作流运行实例状态
            this.wfState = new WfState(userId, wfInputs, runtimeUuid, tenantCode, conversationId);
            // 设置工作流信息(用于Start节点记录workflow选择)
            this.wfState.setWorkflowUuid(workflow.getWorkflowUuid());
            this.wfState.setWorkflowTitle(workflow.getTitle());
            // 设置流式处理器，供节点使用（如 LLM 流式响应）
            this.wfState.setStreamHandler(streamHandler);
            // 设置调用来源标识，供子工作流节点使用
            this.wfState.setCallSource(this.callSource);
            // 设置页面上下文，供MCP工具使用
            if (this.pageContext != null) {
                this.wfState.setPageContext(this.pageContext);
            }

            // 添加需要中断的节点（HumanFeedbackNode）
            for (String humanFeedbackNodeUuid : humanFeedbackNodeUuids) {
                this.wfState.addInterruptNode(humanFeedbackNodeUuid);
            }

            // 只有顶层工作流才更新runtime记录
            if (this.wfRuntimeResp != null) {
                workflowRuntimeService.updateInput(this.wfRuntimeResp.getId(), wfState);
            } else if (this.conversationRuntimeResp != null) {
                conversationRuntimeService.updateInput(this.conversationRuntimeResp.getId(), wfState);
            }

            // 主状态图
            StateGraph mainStateGraph = new StateGraph();

            // 简化版：直接构建StateGraph，让Spring AI Alibaba框架自动处理并行分叉和汇聚
            // 不再使用CompileNode中间层，直接遍历边数据构建图
            buildStateGraph(mainStateGraph, startNode);

            // 编译状态图 (移除MemorySaver和interruptBefore配置)
            // interruptBefore功能通过wfState.getInterruptNodes()在runNode中手动处理
            app = mainStateGraph.compile();
            RunnableConfig invokeConfig = RunnableConfig.builder().build();
            exe(invokeConfig, false);
        } catch (Exception e) {
            errorWhenExe(e);
        }
        log.debug("WorkflowEngine执行完成 - runtime_uuid: {}", runtimeUuid);
    }

    private void exe(RunnableConfig invokeConfig, boolean resume) {
        // 在执行前显式设置租户上下文，防止异步执行中上下文丢失
        DataSourceHelper.use(this.tenantCode);

        // 使用Spring AI Alibaba Graph的流式API
        AsyncGenerator<NodeOutput> outputs;
        try {
            outputs = app.stream(resume ? null : Map.of(), invokeConfig);
        } catch (Exception e) {
            throw new RuntimeException("工作流执行失败", e);
        }
        streamingResult(wfState, outputs);

        // 检查是否需要中断 (移除StateSnapshot,改为检查interruptNodes集合)
        // 找出还未执行的中断节点(HumanFeedbackNode)
        String nextInterruptNode = wfState.getInterruptNodes().stream()
                .filter(nodeUuid -> wfState.getCompletedNodes().stream()
                        .noneMatch(completedNode -> completedNode.getNode().getUuid().equals(nodeUuid)))
                .findFirst()
                .orElse(null);

        if (nextInterruptNode != null) {
            // 还有未执行的中断节点，进入等待用户输入状态
            String intTip = getHumanFeedbackTip(nextInterruptNode);
            // 将等待输入信息发送到客户端（通过NODE_WAIT_FEEDBACK_BY事件）
            streamHandler.sendNodeWaitFeedback(nextInterruptNode, intTip);
            InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);

            // 更新状态（只有顶层工作流才更新runtime记录）
            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_WAITING_INPUT);
            if (this.wfRuntimeResp != null) {
                workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
            } else if (this.conversationRuntimeResp != null) {
                conversationRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
            }
        } else {
            // 工作流执行完成
            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_SUCCESS);

            // 只有顶层工作流才更新runtime记录
            String outputStr;
            if (this.wfRuntimeResp != null) {
                AiWorkflowRuntimeEntity updatedRuntime = workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
                // Entity的getOutputData()返回String类型
                outputStr = updatedRuntime.getOutputData();
            } else if (this.conversationRuntimeResp != null) {
                AiConversationRuntimeEntity updatedRuntime = conversationRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
                // Entity的getOutputData()返回String类型
                outputStr = updatedRuntime.getOutputData();
            } else {
                // 子工作流：从wfState中获取输出
                outputStr = wfState.getOutputAsJsonString();
            }

            if (StringUtils.isBlank(outputStr)) {
                outputStr = "{}";
            }

            // 构建包含runtime元数据的完成事件数据
            JSONObject completeData = new JSONObject();
            completeData.put("content", outputStr);

            // 添加runtime信息（用于前端查询执行详情）
            if (this.wfRuntimeResp != null) {
                // Workflow独立测试场景
                completeData.put("runtime_id", this.wfRuntimeResp.getId());
                completeData.put("runtime_uuid", this.wfRuntimeResp.getRuntimeUuid());
                completeData.put("workflow_uuid", this.workflow.getWorkflowUuid());
            } else if (this.conversationRuntimeResp != null) {
                // AI Chat场景
                completeData.put("runtime_id", this.conversationRuntimeResp.getId());
                completeData.put("runtime_uuid", this.conversationRuntimeResp.getRuntime_uuid());
                completeData.put("workflow_uuid", this.workflow.getWorkflowUuid());
            }
            // 子工作流不需要添加runtime信息（复用父runtime，但前端不需要显示子工作流的执行详情按钮）

            // 发送完成事件
            streamHandler.sendComplete(completeData.toJSONString());
            InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
        }
    }

    /**
     * 中断流程等待用户输入时，会进行暂停状态，用户输入后调用本方法执行流程剩余部分
     *
     * @param userInput 用户输入
     */
    public void resume(String userInput) {
        RunnableConfig invokeConfig = RunnableConfig.builder().build();
        try {
            app.updateState(invokeConfig, Map.of(HUMAN_FEEDBACK_KEY, userInput), null);
            exe(invokeConfig, true);
        } catch (Exception e) {
            errorWhenExe(e);
        } finally {
            // 有可能多次接收人机交互，待整个流程完全执行后才能删除
            if (wfState.getProcessStatus() != WORKFLOW_PROCESS_STATUS_WAITING_INPUT) {
                InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
            }
        }
    }

    private void errorWhenExe(Exception e) {
        log.error("Workflow execution error", e);
        String errorMsg = e.getMessage();
        if (errorMsg != null && errorMsg.contains("parallel node doesn't support conditional branch")) {
            errorMsg = "并行节点中不能包含条件分支";
        }
        // 发送错误事件
        streamHandler.sendError(e);

        // 只有顶层工作流才更新runtime状态（子工作流没有自己的runtime记录）
        if (this.wfRuntimeResp != null) {
            // 在更新状态前切换到正确的租户数据源
            // 因为updateStatus需要查询和更新ai_workflow_runtime表，必须使用租户数据源
            DataSourceHelper.use(this.tenantCode);
            workflowRuntimeService.updateStatus(wfRuntimeResp.getId(), WORKFLOW_PROCESS_STATUS_FAIL, errorMsg);
        } else if (this.conversationRuntimeResp != null) {
            // 在更新状态前切换到正确的租户数据源
            // 因为updateStatus需要查询和更新ai_conversation_runtime表，必须使用租户数据源
            DataSourceHelper.use(this.tenantCode);
            conversationRuntimeService.updateStatus(conversationRuntimeResp.getId(), WORKFLOW_PROCESS_STATUS_FAIL, errorMsg);
        }
    }

    /**
     * 执行单个节点
     *
     * @param wfNode 节点定义
     * @param nodeState 节点状态
     * @return 执行结果Map
     */
    private Map<String, Object> runNode(AiWorkflowNodeVo wfNode, WfNodeState nodeState) {
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
                // 发送节点运行开始消息
                streamHandler.sendNodeRun(wfNode.getUuid(), JSONObject.toJSONString(nodeVo));
            } else {
                AiWorkflowRuntimeNodeVo nodeVo = workflowRuntimeNodeService.createByState(
                        userId, wfNode.getId(), runtimeId, nodeState);
                wfState.getRuntimeNodes().add(nodeVo);
                runtimeNodeId = nodeVo.getId();
                // 发送节点运行开始消息
                streamHandler.sendNodeRun(wfNode.getUuid(), JSONObject.toJSONString(nodeVo));
            }

            // 5. 执行节点，带输入输出回调
            NodeProcessResult processResult = abstractWfNode.process(
                    // 输入回调
                    (is) -> {
                        // 在回调中设置租户上下文，防止异步执行时上下文丢失
                        DataSourceHelper.use(this.tenantCode);
                        // 根据callSource调用对应的service
                        if (callSource == WorkflowCallSource.AI_CHAT) {
                            conversationRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
                        } else {
                            workflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
                        }

                        // 跳过以下节点的INPUT事件：
                        // - Start节点：用户输入不需要回显
                        // - Answer节点：用户输入通过CHUNK事件逐字输出，不需要单独发送JSON格式
                        // - SubWorkflow节点：子工作流内部会处理输入
                        // - End节点：结束节点不需要发送INPUT事件
                        String componentName = wfComponent.getName();
                        if (!"Start".equals(componentName)
                            && !"Answer".equals(componentName)
                            && !"SubWorkflow".equals(componentName)
                            && !"End".equals(componentName)) {
                            for (NodeIOData input : nodeState.getInputs()) {
                                streamHandler.sendNodeInput(wfNode.getUuid(), JSONObject.toJSONString(input));
                            }
                        } else {
                            log.info("跳过NODE_INPUT事件 - componentName: {}（用户输入回显/流式节点/结束节点）", componentName);
                        }
                    },
                    // 输出回调
                    (is) -> {
                        // 在回调中设置租户上下文，防止异步执行时上下文丢失
                        DataSourceHelper.use(this.tenantCode);
                        // 根据callSource调用对应的service
                        if (callSource == WorkflowCallSource.AI_CHAT) {
                            conversationRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
                        } else {
                            workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
                        }

                        // 并行节点内部的节点执行结束后，需要主动向客户端发送输出结果
                        // 但是对于流式输出节点（Answer组件）和子工作流节点（SubWorkflow组件），不需要发送NODE_OUTPUT：
                        // - Answer组件：已经通过NODE_CHUNK事件发送了流式内容
                        // - SubWorkflow组件：子工作流内部的Answer节点已经通过CHUNK事件发送了流式内容
                        String nodeUuid = wfNode.getUuid();
                        String componentName = wfComponent.getName();

                        log.info("WorkflowEngine输出回调 - nodeUuid: {}, componentName: {}, wfComponent.getId: {}",
                                nodeUuid, componentName, wfComponent.getId());

                        // 跳过以下节点的OUTPUT事件：
                        // - Start节点：用户输入不需要回显给前端（前端已经显示了用户输入）
                        // - Answer节点：已经通过NODE_CHUNK事件发送了流式内容
                        // - SubWorkflow节点：子工作流内部的Answer节点已经通过CHUNK事件发送了流式内容
                        // - End节点：结束节点的输出是最终结果，不需要单独发送OUTPUT事件
                        // - 配置了show_process_output=false的节点：用户不希望在聊天界面显示执行过程

                        // 检查节点配置中的show_process_output开关
                        boolean showProcessOutput = true;  // 默认显示
                        JSONObject nodeConfig = wfNode.getNodeConfig();
                        if (nodeConfig != null && nodeConfig.containsKey("show_process_output")) {
                            showProcessOutput = nodeConfig.getBooleanValue("show_process_output");
                        }

                        if (!"Start".equals(componentName)
                            && !"Answer".equals(componentName)
                            && !"SubWorkflow".equals(componentName)
                            && !"End".equals(componentName)
                            && showProcessOutput) {
                            log.info("发送NODE_OUTPUT事件 - nodeUuid: {}, componentName: {}", nodeUuid, componentName);
                            List<NodeIOData> nodeOutputs = nodeState.getOutputs();
                            for (NodeIOData output : nodeOutputs) {
                                streamHandler.sendNodeOutput(nodeUuid, JSONObject.toJSONString(output));
                            }
                        } else {
                            log.info("跳过NODE_OUTPUT事件 - componentName: {}, showProcessOutput: {}（用户输入回显/流式输出/结束节点/静默模式）",
                                    componentName, showProcessOutput);
                        }
                    }
            );

            // 6. 设置下一个节点(如果有)
            if (StringUtils.isNotBlank(processResult.getNextNodeUuid())) {
                resultMap.put("next", processResult.getNextNodeUuid());
            }

        } catch (Exception e) {
            log.error("Node run error: {} ({})", wfNode.getTitle(), wfNode.getUuid(), e);
            throw new RuntimeException(e);
        }

        // 7. 设置节点名称
        resultMap.put("name", wfNode.getTitle());

        return resultMap;
    }

    /**
     * 流式输出结果
     *
     * @param wfState 工作流状态
     * @param outputs 输出AsyncGenerator流
     */
    private void streamingResult(WfState wfState, AsyncGenerator<NodeOutput> outputs) {
        // 在流式处理中显式设置租户上下文，防止异步迭代器中上下文丢失
        DataSourceHelper.use(this.tenantCode);

        // 使用for循环迭代AsyncGenerator
        for (NodeOutput out : outputs) {
            // 在异步迭代过程中再次设置租户上下文，防止线程切换导致上下文丢失
            DataSourceHelper.use(this.tenantCode);

            // 找到对应的 abstractWfNode
            AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
                    .filter(item -> item.getNode().getUuid().endsWith(out.node()))
                    .findFirst()
                    .orElse(null);

            if (null != abstractWfNode) {
                // 找到对应的运行时节点
                // 注意: wfState.getRuntimeNodes()只适用于Workflow独立测试场景
                // AI Chat场景下,需要直接根据nodeUuid查找node ID
                Long runtimeNodeId = null;

                if (callSource == WorkflowCallSource.AI_CHAT) {
                    // AI Chat场景: 由于我们没有存储conversation node到wfState中,
                    // 这里暂时跳过更新(或者可以通过数据库查询获取node ID)
                    log.debug("AI Chat场景下streamingResult暂时跳过node更新");
                } else {
                    // Workflow独立测试场景: 使用wfState中的runtime node
                    AiWorkflowRuntimeNodeVo runtimeNodeVo = wfState.getRuntimeNodeByNodeUuid(out.node());
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
                    log.warn("Can not find runtime node, node uuid:{}", out.node());
                }

                // 设置工作流最终输出（适用于所有场景）
                wfState.setOutput(abstractWfNode.getState().getOutputs());
            } else {
                log.warn("Can not find node state,node uuid:{}", out.node());
            }
        }
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

        // 4. 遍历所有边，添加到StateGraph
        // Spring AI Alibaba会自动处理：
        // - 同源多目标 → 并行分叉（自动创建ParallelNode）
        // - 多源同目标 → 汇聚（自动等待所有上游完成）
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

        // 6. 找到所有结束节点，添加到END的边
        for (AiWorkflowNodeVo node : wfNodes) {
            if (isEndNode(node)) {
                addEdgeToStateGraph(stateGraph, node.getUuid(), END);
            }
        }
    }

    /**
     * 处理条件分支边
     *
     * <p>只处理Switcher和Classifier组件的边作为条件边，普通节点的边不应被处理为条件边</p>
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

        // 只收集条件分支节点的出边
        Map<String, List<AiWorkflowEdgeEntity>> conditionalEdgesMap = new HashMap<>();
        for (AiWorkflowEdgeEntity edge : wfEdges) {
            // 只处理条件分支节点的边
            if (conditionalNodeUuids.contains(edge.getSourceNodeUuid())) {
                conditionalEdgesMap
                    .computeIfAbsent(edge.getSourceNodeUuid(), k -> new ArrayList<>())
                    .add(edge);
            }
        }

        // 为每个条件分支节点添加ConditionalEdge
        for (Map.Entry<String, List<AiWorkflowEdgeEntity>> entry : conditionalEdgesMap.entrySet()) {
            String sourceUuid = entry.getKey();
            List<AiWorkflowEdgeEntity> conditionalEdges = entry.getValue();

            // 构建条件映射
            Map<String, String> mappings = new HashMap<>();
            for (AiWorkflowEdgeEntity edge : conditionalEdges) {
                mappings.put(edge.getTargetNodeUuid(), edge.getTargetNodeUuid());
            }

            // 添加条件边（增加null安全检查）
            stateGraph.addConditionalEdges(
                sourceUuid,
                edge_async(state -> {
                    Object next = state.data().get("next");
                    if (next == null) {
                        log.warn("条件分支节点[{}]未设置next字段，使用默认路由", sourceUuid);
                        // 返回第一个目标节点作为默认路由
                        return conditionalEdges.isEmpty() ? null : conditionalEdges.get(0).getTargetNodeUuid();
                    }
                    return next.toString();
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
                .orElseThrow();
        if ("human_feedback".equals(wfComponent.getName())) {
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

    public CompiledGraph getApp() {
        return app;
    }
}
