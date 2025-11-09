package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.*;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeVo;
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
    private final WorkflowStreamHandler streamHandler;
    private final List<AiWorkflowComponentEntity> components;
    private final List<AiWorkflowNodeVo> wfNodes;
    private final List<AiWorkflowEdgeEntity> wfEdges;
    private final AiWorkflowRuntimeService workflowRuntimeService;
    private final AiWorkflowRuntimeNodeService workflowRuntimeNodeService;

    private final ObjectStreamStateSerializer<WfNodeState> stateSerializer = new ObjectStreamStateSerializer<>(WfNodeState::new);
    private final Map<String, List<StateGraph<WfNodeState>>> stateGraphNodes = new HashMap<>();
    private final Map<String, List<StateGraph<WfNodeState>>> stateGraphEdges = new HashMap<>();
    private final Map<String, String> rootToSubGraph = new HashMap<>();
    private final Map<String, GraphCompileNode> nodeToParallelBranch = new HashMap<>();

    private Long userId;
    private String tenantCode;
    private WfState wfState;
    private AiWorkflowRuntimeVo wfRuntimeResp;

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
     * 构造函数（顶层工作流）
     * 使用 WorkflowStreamHandler 实现事件驱动的流式输出
     */
    public WorkflowEngine(
            AiWorkflowEntity workflow,
            WorkflowStreamHandler streamHandler,
            List<AiWorkflowComponentEntity> components,
            List<AiWorkflowNodeVo> nodes,
            List<AiWorkflowEdgeEntity> wfEdges,
            AiWorkflowRuntimeService workflowRuntimeService,
            AiWorkflowRuntimeNodeService workflowRuntimeNodeService) {
        this(workflow, streamHandler, components, nodes, wfEdges,
            workflowRuntimeService, workflowRuntimeNodeService, null);
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
            AiWorkflowRuntimeService workflowRuntimeService,
            AiWorkflowRuntimeNodeService workflowRuntimeNodeService,
            String parentRuntimeUuid) {
        this.workflow = workflow;
        this.streamHandler = streamHandler;
        this.components = components;
        this.wfNodes = nodes;
        this.wfEdges = wfEdges;
        this.workflowRuntimeService = workflowRuntimeService;
        this.workflowRuntimeNodeService = workflowRuntimeNodeService;
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
            // 设置流式处理器，供节点使用（如 LLM 流式响应）
            this.wfState.setStreamHandler(streamHandler);

            // 添加需要中断的节点（HumanFeedbackNode）
            for (String humanFeedbackNodeUuid : humanFeedbackNodeUuids) {
                this.wfState.addInterruptNode(humanFeedbackNodeUuid);
            }

            // 只有顶层工作流才更新runtime记录
            if (this.wfRuntimeResp != null) {
                workflowRuntimeService.updateInput(this.wfRuntimeResp.getId(), wfState);
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
            } else {
                // 子工作流：从wfState中获取输出
                outputStr = wfState.getOutputAsJsonString();
            }

            if (StringUtils.isBlank(outputStr)) {
                outputStr = "{}";
            }

            // 发送完成事件
            streamHandler.sendComplete(outputStr);
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
                // 顶层工作流：直接使用wfRuntimeResp
                runtimeId = this.wfRuntimeResp.getId();
            } else {
                // 子工作流：通过wfState.getUuid()查询获取runtime_id（复用父工作流的runtime记录）
                AiWorkflowRuntimeEntity runtime = workflowRuntimeService.getByUuid(wfState.getUuid());
                runtimeId = runtime.getId();
            }

            AiWorkflowRuntimeNodeVo runtimeNodeVo = workflowRuntimeNodeService.createByState(
                    userId, wfNode.getId(), runtimeId, nodeState);
            wfState.getRuntimeNodes().add(runtimeNodeVo);

            // 4. 发送节点运行开始消息
            streamHandler.sendNodeRun(wfNode.getUuid(), JSONObject.toJSONString(runtimeNodeVo));

            // 5. 执行节点，带输入输出回调
            NodeProcessResult processResult = abstractWfNode.process(
                    // 输入回调
                    (is) -> {
                        // 在回调中设置租户上下文，防止异步执行时上下文丢失
                        DataSourceHelper.use(this.tenantCode);
                        workflowRuntimeNodeService.updateInput(runtimeNodeVo.getId(), nodeState);
                        for (NodeIOData input : nodeState.getInputs()) {
                            streamHandler.sendNodeInput(wfNode.getUuid(), JSONObject.toJSONString(input));
                        }
                    },
                    // 输出回调
                    (is) -> {
                        // 在回调中设置租户上下文，防止异步执行时上下文丢失
                        DataSourceHelper.use(this.tenantCode);
                        workflowRuntimeNodeService.updateOutput(runtimeNodeVo.getId(), nodeState);

                        // 并行节点内部的节点执行结束后，需要主动向客户端发送输出结果
                        String nodeUuid = wfNode.getUuid();
                        List<NodeIOData> nodeOutputs = nodeState.getOutputs();
                        for (NodeIOData output : nodeOutputs) {
                            streamHandler.sendNodeOutput(nodeUuid, JSONObject.toJSONString(output));
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
                    AiWorkflowRuntimeNodeVo runtimeNodeVo = wfState.getRuntimeNodeByNodeUuid(out.node());
                    if (null != runtimeNodeVo) {
                        // 更新运行时节点的输出
                        workflowRuntimeNodeService.updateOutput(runtimeNodeVo.getId(), abstractWfNode.getState());
                        wfState.setOutput(abstractWfNode.getState().getOutputs());
                    } else {
                        log.warn("Can not find runtime node, node uuid:{}", out.node());
                    }
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
