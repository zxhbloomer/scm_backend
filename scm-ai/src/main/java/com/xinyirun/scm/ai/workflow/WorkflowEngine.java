package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.*;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiConversationWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.bsc.langgraph4j.langchain4j.generators.StreamingChatGenerator;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.StateSnapshot;
import org.bsc.langgraph4j.streaming.StreamingOutput;

import java.util.*;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.*;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 工作流执行引擎
 *
 * <p>基于LangGraph4j实现DAG工作流执行</p>
 *
 * @author zxh
 * @since 2025-10-21
 */
@Slf4j
public class WorkflowEngine {
    private CompiledGraph<WfNodeState> app;
    private final AiWorkflowEntity workflow;
    private WorkflowStreamHandler streamHandler;
    private final List<AiWorkflowComponentEntity> components;
    private final List<AiWorkflowNodeVo> wfNodes;
    private final List<AiWorkflowEdgeEntity> wfEdges;
    private final WorkflowCallSource callSource;
    private final AiWorkflowRuntimeService workflowRuntimeService;
    private final AiWorkflowRuntimeNodeService workflowRuntimeNodeService;
    private final AiConversationWorkflowRuntimeService conversationWorkflowRuntimeService;
    private final AiConversationWorkflowRuntimeNodeService conversationWorkflowRuntimeNodeService;

    private final ObjectStreamStateSerializer<WfNodeState> stateSerializer = new ObjectStreamStateSerializer<>(WfNodeState::new);
    private final Map<String, List<StateGraph<WfNodeState>>> stateGraphNodes = new HashMap<>();
    private final Map<String, List<StateGraph<WfNodeState>>> stateGraphEdges = new HashMap<>();
    private final Map<String, String> rootToSubGraph = new HashMap<>();
    private final Map<String, GraphCompileNode> nodeToParallelBranch = new HashMap<>();

    private Long userId;
    private String tenantCode;
    private WfState wfState;
    private AiWorkflowRuntimeVo wfRuntimeResp;
    private AiConversationWorkflowRuntimeVo conversationRuntimeResp;

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
            AiConversationWorkflowRuntimeService conversationWorkflowRuntimeService,
            AiConversationWorkflowRuntimeNodeService conversationWorkflowRuntimeNodeService) {
        this(workflow, streamHandler, components, nodes, wfEdges,
            callSource, workflowRuntimeService, workflowRuntimeNodeService,
            conversationWorkflowRuntimeService, conversationWorkflowRuntimeNodeService, null);
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
            AiConversationWorkflowRuntimeService conversationWorkflowRuntimeService,
            AiConversationWorkflowRuntimeNodeService conversationWorkflowRuntimeNodeService,
            String parentRuntimeUuid) {
        this.workflow = workflow;
        this.streamHandler = streamHandler;
        this.components = components;
        this.wfNodes = nodes;
        this.wfEdges = wfEdges;
        this.callSource = callSource;
        this.workflowRuntimeService = workflowRuntimeService;
        this.workflowRuntimeNodeService = workflowRuntimeNodeService;
        this.conversationWorkflowRuntimeService = conversationWorkflowRuntimeService;
        this.conversationWorkflowRuntimeNodeService = conversationWorkflowRuntimeNodeService;
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
                // AI Chat调用：使用conversationWorkflowRuntimeService
                if (parentConversationId != null && !parentConversationId.isEmpty()) {
                    this.conversationRuntimeResp = conversationWorkflowRuntimeService.createWithConversationId(
                        userId, workflowId, parentConversationId);
                } else {
                    this.conversationRuntimeResp = conversationWorkflowRuntimeService.create(userId, workflowId);
                }
                runtimeUuid = this.conversationRuntimeResp.getRuntimeUuid();
                conversationId = this.conversationRuntimeResp.getConversationId();
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
                conversationWorkflowRuntimeService.updateInput(this.conversationRuntimeResp.getId(), wfState);
            }

            CompileNode rootCompileNode = new CompileNode();
            rootCompileNode.setId(startNode.getUuid());

            // 构建整棵树
            buildCompileNode(rootCompileNode, startNode);

            // 主状态图
            StateGraph<WfNodeState> mainStateGraph = new StateGraph<>(stateSerializer);
            this.wfState.addEdge(START, startNode.getUuid());

            // 构建包括所有节点的状态图
            buildStateGraph(null, mainStateGraph, rootCompileNode);

            MemorySaver saver = new MemorySaver();
            CompileConfig compileConfig = CompileConfig.builder()
                    .checkpointSaver(saver)
                    .interruptBefore(wfState.getInterruptNodes().toArray(String[]::new))
                    .build();
            app = mainStateGraph.compile(compileConfig);
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

        // 不使用langgraph4j state的update相关方法，无需传入input
        AsyncGenerator<NodeOutput<WfNodeState>> outputs = app.stream(resume ? null : Map.of(), invokeConfig);
        streamingResult(wfState, outputs);

        StateSnapshot<WfNodeState> stateSnapshot = app.getState(invokeConfig);
        String nextNode = stateSnapshot.config().nextNode().orElse("");

        // 还有下个节点，表示进入中断状态，等待用户输入后继续执行
        if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
            String intTip = getHumanFeedbackTip(nextNode);
            // 将等待输入信息发送到客户端（通过NODE_WAIT_FEEDBACK_BY事件）
            streamHandler.sendNodeWaitFeedback(nextNode, intTip);
            InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);

            // 更新状态（只有顶层工作流才更新runtime记录）
            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_WAITING_INPUT);
            if (this.wfRuntimeResp != null) {
                workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
            } else if (this.conversationRuntimeResp != null) {
                conversationWorkflowRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
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
                AiConversationWorkflowRuntimeEntity updatedRuntime = conversationWorkflowRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
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
                completeData.put("runtime_uuid", this.conversationRuntimeResp.getRuntimeUuid());
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
            // 因为updateStatus需要查询和更新ai_conversation_workflow_runtime表，必须使用租户数据源
            DataSourceHelper.use(this.tenantCode);
            conversationWorkflowRuntimeService.updateStatus(conversationRuntimeResp.getId(), WORKFLOW_PROCESS_STATUS_FAIL, errorMsg);
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
            // LangGraph4j创建的新线程无法继承ThreadLocal的数据源上下文
            DataSourceHelper.use(this.tenantCode);

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
                // 子工作流：通过wfState.getUuid()查询获取runtime_id（复用父工作流的runtime记录）
                if (callSource == WorkflowCallSource.AI_CHAT) {
                    AiConversationWorkflowRuntimeEntity runtime = conversationWorkflowRuntimeService.getByUuid(wfState.getUuid());
                    runtimeId = runtime.getId();
                } else {
                    AiWorkflowRuntimeEntity runtime = workflowRuntimeService.getByUuid(wfState.getUuid());
                    runtimeId = runtime.getId();
                }
            }

            // 根据callSource创建运行时节点记录
            Long runtimeNodeId; // 存储节点ID用于后续更新操作
            if (callSource == WorkflowCallSource.AI_CHAT) {
                AiConversationWorkflowRuntimeNodeVo nodeVo = conversationWorkflowRuntimeNodeService.createByState(
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
                            conversationWorkflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
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
                            conversationWorkflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
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
                        if (!"Start".equals(componentName)
                            && !"Answer".equals(componentName)
                            && !"SubWorkflow".equals(componentName)
                            && !"End".equals(componentName)) {
                            log.info("发送NODE_OUTPUT事件 - nodeUuid: {}, componentName: {}", nodeUuid, componentName);
                            List<NodeIOData> nodeOutputs = nodeState.getOutputs();
                            for (NodeIOData output : nodeOutputs) {
                                streamHandler.sendNodeOutput(nodeUuid, JSONObject.toJSONString(output));
                            }
                        } else {
                            log.info("跳过NODE_OUTPUT事件 - componentName: {}（用户输入回显/流式输出/结束节点）", componentName);
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
     * @param wfState    工作流状态
     * @param outputs    输出
     */
    private void streamingResult(WfState wfState, AsyncGenerator<NodeOutput<WfNodeState>> outputs) {
        // 在流式处理中显式设置租户上下文，防止异步迭代器中上下文丢失
        DataSourceHelper.use(this.tenantCode);

        for (NodeOutput<WfNodeState> out : outputs) {
            if (out instanceof StreamingOutput<WfNodeState> streamingOutput) {
                String node = streamingOutput.node();
                String chunk = streamingOutput.chunk();
                log.info("node:{},chunk:{}", node, chunk);
                sendNodeChunk(node, chunk);
            } else {
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
                        // TODO: 如果需要在streamingResult中更新AI Chat的节点,需要实现查询逻辑
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
                            conversationWorkflowRuntimeNodeService.updateOutput(runtimeNodeId, abstractWfNode.getState());
                        } else {
                            workflowRuntimeNodeService.updateOutput(runtimeNodeId, abstractWfNode.getState());
                        }
                    } else {
                        log.warn("Can not find runtime node, node uuid:{}", out.node());
                    }

                    // 设置工作流最终输出（适用于所有场景）
                    // 修复: 将此行移到if块外，确保AI_CHAT场景也能正确设置输出
                    wfState.setOutput(abstractWfNode.getState().getOutputs());
                } else {
                    log.warn("Can not find node state,node uuid:{}", out.node());
                }
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

    private void buildCompileNode(CompileNode parentNode, AiWorkflowNodeVo node) {
        log.info("buildByNode, parentNode:{}, node:{},name:{}", parentNode.getId(), node.getUuid(), node.getTitle());
        CompileNode newNode;
        List<String> upstreamNodeUuids = getUpstreamNodeUuids(node.getUuid());

        if (upstreamNodeUuids.isEmpty()) {
            log.error("节点{}没有上游节点", node.getUuid());
            newNode = parentNode;
        } else if (upstreamNodeUuids.size() == 1) {
            String upstreamUuid = upstreamNodeUuids.get(0);
            boolean pointToParallel = pointToParallelBranch(upstreamUuid);

            if (pointToParallel) {
                String rootId = node.getUuid();
                GraphCompileNode graphCompileNode = getOrCreateGraphCompileNode(rootId);
                appendToNextNodes(parentNode, graphCompileNode);
                newNode = graphCompileNode;
            } else if (parentNode instanceof GraphCompileNode graphCompileNode) {
                newNode = CompileNode.builder().id(node.getUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
                graphCompileNode.appendToLeaf(newNode);
            } else {
                newNode = CompileNode.builder().id(node.getUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
                appendToNextNodes(parentNode, newNode);
            }
        } else {
            newNode = CompileNode.builder().id(node.getUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
            GraphCompileNode parallelBranch = nodeToParallelBranch.get(parentNode.getId());
            appendToNextNodes(Objects.requireNonNullElse(parallelBranch, parentNode), newNode);
        }

        if (null == newNode) {
            log.error("节点{}不存在", node.getUuid());
            return;
        }

        List<String> downstreamUuids = getDownstreamNodeUuids(node.getUuid());
        for (String downstream : downstreamUuids) {
            Optional<AiWorkflowNodeVo> n = wfNodes.stream()
                    .filter(item -> item.getUuid().equals(downstream))
                    .findFirst();
            n.ifPresent(workflowNode -> buildCompileNode(newNode, workflowNode));
        }
    }

    /**
     * 构建完整的stategraph
     *
     * @param upstreamCompileNode 上游节点
     * @param stateGraph          当前状态图
     * @param compileNode         当前节点
     * @throws GraphStateException 状态图异常
     */
    private void buildStateGraph(CompileNode upstreamCompileNode, StateGraph<WfNodeState> stateGraph, CompileNode compileNode) throws GraphStateException {
        log.info("buildStateGraph,upstreamCompileNode:{},node:{}", upstreamCompileNode, compileNode.getId());
        String stateGraphNodeUuid = compileNode.getId();

        if (null == upstreamCompileNode) {
            addNodeToStateGraph(stateGraph, stateGraphNodeUuid);
            addEdgeToStateGraph(stateGraph, START, compileNode.getId());
        } else {
            if (compileNode instanceof GraphCompileNode graphCompileNode) {
                String stateGraphId = graphCompileNode.getId();
                CompileNode root = graphCompileNode.getRoot();
                String rootId = root.getId();
                String existSubGraphId = rootToSubGraph.get(rootId);

                if (StringUtils.isBlank(existSubGraphId)) {
                    StateGraph<WfNodeState> subgraph = new StateGraph<>(stateSerializer);
                    addNodeToStateGraph(subgraph, rootId);
                    addEdgeToStateGraph(subgraph, START, rootId);

                    for (CompileNode child : root.getNextNodes()) {
                        buildStateGraph(root, subgraph, child);
                    }

                    addEdgeToStateGraph(subgraph, graphCompileNode.getTail().getId(), END);
                    stateGraph.addNode(stateGraphId, subgraph.compile());
                    rootToSubGraph.put(rootId, stateGraphId);
                    stateGraphNodeUuid = stateGraphId;
                } else {
                    stateGraphNodeUuid = existSubGraphId;
                }
            } else {
                addNodeToStateGraph(stateGraph, stateGraphNodeUuid);
            }

            // ConditionalEdge 的创建另外处理
            if (Boolean.FALSE.equals(upstreamCompileNode.getConditional())) {
                addEdgeToStateGraph(stateGraph, upstreamCompileNode.getId(), stateGraphNodeUuid);
            }
        }

        List<CompileNode> nextNodes = compileNode.getNextNodes();
        if (nextNodes.size() > 1) {
            boolean conditional = nextNodes.stream().noneMatch(item -> item instanceof GraphCompileNode);
            compileNode.setConditional(conditional);

            for (CompileNode nextNode : nextNodes) {
                buildStateGraph(compileNode, stateGraph, nextNode);
            }

            // 节点是"条件分支"或"分类"的情况下不支持并行执行，所以直接使用条件ConditionalEdge
            if (conditional) {
                List<String> targets = nextNodes.stream().map(CompileNode::getId).toList();
                Map<String, String> mappings = new HashMap<>();
                for (String target : targets) {
                    mappings.put(target, target);
                }
                stateGraph.addConditionalEdges(
                        stateGraphNodeUuid,
                        edge_async(state -> state.data().get("next").toString()),
                        mappings
                );
            }
        } else if (nextNodes.size() == 1) {
            for (CompileNode nextNode : nextNodes) {
                buildStateGraph(compileNode, stateGraph, nextNode);
            }
        } else {
            addEdgeToStateGraph(stateGraph, stateGraphNodeUuid, END);
        }
    }

    private GraphCompileNode getOrCreateGraphCompileNode(String rootId) {
        GraphCompileNode exist = nodeToParallelBranch.get(rootId);
        if (null == exist) {
            GraphCompileNode graphCompileNode = new GraphCompileNode();
            graphCompileNode.setId("parallel_" + rootId);
            graphCompileNode.setRoot(CompileNode.builder().id(rootId).conditional(false).nextNodes(new ArrayList<>()).build());
            nodeToParallelBranch.put(rootId, graphCompileNode);
            exist = graphCompileNode;
        }
        return exist;
    }

    private List<String> getUpstreamNodeUuids(String nodeUuid) {
        return this.wfEdges.stream()
                .filter(edge -> edge.getTargetNodeUuid().equals(nodeUuid))
                .map(AiWorkflowEdgeEntity::getSourceNodeUuid)
                .toList();
    }

    private List<String> getDownstreamNodeUuids(String nodeUuid) {
        return this.wfEdges.stream()
                .filter(edge -> edge.getSourceNodeUuid().equals(nodeUuid))
                .map(AiWorkflowEdgeEntity::getTargetNodeUuid)
                .toList();
    }

    // 判断节点是否属于子图
    private boolean pointToParallelBranch(String nodeUuid) {
        int edgeCount = 0;
        for (AiWorkflowEdgeEntity edge : this.wfEdges) {
            if (edge.getSourceNodeUuid().equals(nodeUuid) && StringUtils.isBlank(edge.getSourceHandle())) {
                edgeCount = edgeCount + 1;
            }
        }
        return edgeCount > 1;
    }

    /**
     * 添加节点到状态图
     *
     * @param stateGraph         状态图
     * @param stateGraphNodeUuid 节点UUID
     * @throws GraphStateException 状态图异常
     */
    private void addNodeToStateGraph(StateGraph<WfNodeState> stateGraph, String stateGraphNodeUuid) throws GraphStateException {
        List<StateGraph<WfNodeState>> stateGraphList = stateGraphNodes.computeIfAbsent(stateGraphNodeUuid, k -> new ArrayList<>());
        boolean exist = stateGraphList.stream().anyMatch(item -> item == stateGraph);
        if (exist) {
            log.info("state graph node exist,stateGraphNodeUuid:{}", stateGraphNodeUuid);
            return;
        }

        log.info("addNodeToStateGraph,node uuid:{}", stateGraphNodeUuid);
        AiWorkflowNodeVo wfNode = getNodeByUuid(stateGraphNodeUuid);
        stateGraph.addNode(stateGraphNodeUuid, node_async((state) -> runNode(wfNode, state)));
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

    private void addEdgeToStateGraph(StateGraph<WfNodeState> stateGraph, String source, String target) throws GraphStateException {
        String key = source + "_" + target;
        List<StateGraph<WfNodeState>> stateGraphList = stateGraphEdges.computeIfAbsent(key, k -> new ArrayList<>());
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

    private void appendToNextNodes(CompileNode compileNode, CompileNode newNode) {
        boolean exist = compileNode.getNextNodes().stream().anyMatch(item -> item.getId().equals(newNode.getId()));
        if (!exist) {
            compileNode.getNextNodes().add(newNode);
        }
    }

    private String getHumanFeedbackTip(String nextNode) {
        return wfNodes.stream()
                .filter(node -> node.getUuid().equals(nextNode))
                .findFirst()
                .map(node -> "等待用户输入: " + node.getTitle())
                .orElse("等待用户输入");
    }

    // 发送节点输出块（用于流式输出）
    private void sendNodeChunk(String nodeUuid, String chunk) {
        try {
            streamHandler.sendNodeChunk(nodeUuid, chunk);
        } catch (Exception e) {
            log.error("发送节点输出块失败,nodeUuid:{}", nodeUuid, e);
        }
    }

    public CompiledGraph<WfNodeState> getApp() {
        return app;
    }
}
