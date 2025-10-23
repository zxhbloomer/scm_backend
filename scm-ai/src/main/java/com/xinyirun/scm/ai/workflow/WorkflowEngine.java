package com.xinyirun.scm.ai.workflow;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xinyirun.scm.ai.bean.entity.workflow.*;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeVo;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import org.bsc.langgraph4j.state.StateSnapshot;
import org.bsc.langgraph4j.streaming.StreamingOutput;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
public class WorkflowEngine {
    private CompiledGraph<WfNodeState> app;
    private final AiWorkflowEntity workflow;
    private final List<AiWorkflowComponentEntity> components;
    private final List<AiWorkflowNodeEntity> wfNodes;
    private final List<AiWorkflowEdgeEntity> wfEdges;
    private final AiWorkflowRuntimeService workflowRuntimeService;
    private final AiWorkflowRuntimeNodeService workflowRuntimeNodeService;

    private final ObjectStreamStateSerializer<WfNodeState> stateSerializer = new ObjectStreamStateSerializer<>(WfNodeState::new);
    private final Map<String, List<StateGraph<WfNodeState>>> stateGraphNodes = new HashMap<>();
    private final Map<String, List<StateGraph<WfNodeState>>> stateGraphEdges = new HashMap<>();
    private final Map<String, String> rootToSubGraph = new HashMap<>();
    private final Map<String, GraphCompileNode> nodeToParallelBranch = new HashMap<>();

    private SseEmitter sseEmitter;
    private Long userId;
    private WfState wfState;
    private AiWorkflowRuntimeVo wfRuntimeResp;

    public WorkflowEngine(
            AiWorkflowEntity workflow,
            List<AiWorkflowComponentEntity> components,
            List<AiWorkflowNodeEntity> nodes,
            List<AiWorkflowEdgeEntity> wfEdges,
            AiWorkflowRuntimeService workflowRuntimeService,
            AiWorkflowRuntimeNodeService workflowRuntimeNodeService) {
        this.workflow = workflow;
        this.components = components;
        this.wfNodes = nodes;
        this.wfEdges = wfEdges;
        this.workflowRuntimeService = workflowRuntimeService;
        this.workflowRuntimeNodeService = workflowRuntimeNodeService;
    }

    public void run(Long userId, List<ObjectNode> userInputs, SseEmitter sseEmitter) {
        this.userId = userId;
        this.sseEmitter = sseEmitter;
        log.info("WorkflowEngine run,userId:{},workflowUuid:{},userInputs:{}", userId, workflow.getWorkflowUuid(), userInputs);

        if (workflow.getIsEnable() == null || workflow.getIsEnable() == 0) {
            sendErrorAndComplete("工作流已禁用");
            throw new RuntimeException("工作流已禁用");
        }

        Long workflowId = this.workflow.getId();
        this.wfRuntimeResp = workflowRuntimeService.create(userId, workflowId);
        sendSseStart();

        String runtimeUuid = this.wfRuntimeResp.getUuid();
        try {
            Pair<AiWorkflowNodeEntity, Set<AiWorkflowNodeEntity>> startAndEnds = findStartAndEndNode();
            AiWorkflowNodeEntity startNode = startAndEnds.getLeft();
            List<NodeIOData> wfInputs = getAndCheckUserInput(userInputs, startNode);

            // 工作流运行实例状态
            this.wfState = new WfState(userId, wfInputs, runtimeUuid);
            workflowRuntimeService.updateInput(this.wfRuntimeResp.getId(), wfState);

            CompileNode rootCompileNode = new CompileNode();
            rootCompileNode.setId(startNode.getNodeUuid());

            // 构建整棵树
            buildCompileNode(rootCompileNode, startNode);

            // 主状态图
            StateGraph<WfNodeState> mainStateGraph = new StateGraph<>(stateSerializer);
            this.wfState.addEdge(START, startNode.getNodeUuid());

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
    }

    private void exe(RunnableConfig invokeConfig, boolean resume) {
        // 不使用langgraph4j state的update相关方法，无需传入input
        AsyncGenerator<NodeOutput<WfNodeState>> outputs = app.stream(resume ? null : Map.of(), invokeConfig);
        streamingResult(wfState, outputs, sseEmitter);

        StateSnapshot<WfNodeState> stateSnapshot = app.getState(invokeConfig);
        String nextNode = stateSnapshot.config().nextNode().orElse("");

        // 还有下个节点，表示进入中断状态，等待用户输入后继续执行
        if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
            String intTip = getHumanFeedbackTip(nextNode);
            // 将等待输入信息[事件与提示词]发送到到客户端
            sendSseMessage("[NODE_WAIT_FEEDBACK_BY_" + nextNode + "]", intTip);
            InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);

            // 更新状态
            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_WAITING_INPUT);
            workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
        } else {
            AiWorkflowRuntimeEntity updatedRuntime = workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
            sendSseComplete(updatedRuntime.getOutput());
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
        sendErrorAndComplete(errorMsg);
        workflowRuntimeService.updateStatus(wfRuntimeResp.getId(), WORKFLOW_PROCESS_STATUS_FAIL, errorMsg);
    }

    private Map<String, Object> runNode(AiWorkflowNodeEntity wfNode, WfNodeState nodeState) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // TODO: 实现节点执行逻辑
            // 1. 找到对应的WorkflowComponent
            // 2. 通过WfNodeFactory创建AbstractWfNode实例
            // 3. 创建运行时节点记录
            // 4. 执行节点process方法
            // 5. 处理流式生成器(如果有)

            throw new RuntimeException("节点类型执行逻辑暂未实现,需要先实现AbstractWfNode及其子类");
        } catch (Exception e) {
            log.error("Node run error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 流式输出结果
     *
     * @param wfState    工作流状态
     * @param outputs    输出
     * @param sseEmitter sse emitter
     */
    private void streamingResult(WfState wfState, AsyncGenerator<NodeOutput<WfNodeState>> outputs, SseEmitter sseEmitter) {
        for (NodeOutput<WfNodeState> out : outputs) {
            if (out instanceof StreamingOutput<WfNodeState> streamingOutput) {
                String node = streamingOutput.node();
                String chunk = streamingOutput.chunk();
                log.info("node:{},chunk:{}", node, chunk);
                sendSseMessage("[NODE_CHUNK_" + node + "]", chunk);
            } else {
                // TODO: 处理节点输出更新
                // 找到对应的abstractWfNode,更新运行时节点的输出
                log.info("Node output: {}", out.node());
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
    private List<NodeIOData> getAndCheckUserInput(List<ObjectNode> userInputs, AiWorkflowNodeEntity startNode) {
        // TODO: 实现用户输入校验
        // 1. 解析startNode的inputConfig
        // 2. 验证用户输入是否满足定义的要求
        // 3. 转换为NodeIOData列表

        List<NodeIOData> wfInputs = new ArrayList<>();
        // 临时实现:直接创建一个默认输入
        if (!userInputs.isEmpty()) {
            ObjectNode firstInput = userInputs.get(0);
            if (firstInput.has("name") && firstInput.has("value")) {
                String name = firstInput.get("name").asText();
                String value = firstInput.get("value").asText();
                wfInputs.add(NodeIOData.createByText(name, name, value));
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
    public Pair<AiWorkflowNodeEntity, Set<AiWorkflowNodeEntity>> findStartAndEndNode() {
        AiWorkflowNodeEntity startNode = null;
        Set<AiWorkflowNodeEntity> endNodes = new HashSet<>();

        for (AiWorkflowNodeEntity node : wfNodes) {
            Optional<AiWorkflowComponentEntity> wfComponent = components.stream()
                    .filter(item -> item.getId().equals(node.getWorkflowComponentId()))
                    .findFirst();

            if (wfComponent.isPresent() && "start".equals(wfComponent.get().getName())) {
                if (null != startNode) {
                    throw new RuntimeException("工作流中存在多个开始节点");
                }
                startNode = node;
            } else if (wfComponent.isPresent() && "end".equals(wfComponent.get().getName())) {
                endNodes.add(node);
            }
        }

        if (null == startNode) {
            log.error("没有开始节点,workflowId:{}", wfNodes.get(0).getWorkflowId());
            throw new RuntimeException("未找到开始节点");
        }

        // Find all end nodes (没有出边的节点也是结束节点)
        wfNodes.forEach(item -> {
            String nodeUuid = item.getNodeUuid();
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

    private void buildCompileNode(CompileNode parentNode, AiWorkflowNodeEntity node) {
        log.info("buildByNode, parentNode:{}, node:{},name:{}", parentNode.getId(), node.getNodeUuid(), node.getName());
        CompileNode newNode;
        List<String> upstreamNodeUuids = getUpstreamNodeUuids(node.getNodeUuid());

        if (upstreamNodeUuids.isEmpty()) {
            log.error("节点{}没有上游节点", node.getNodeUuid());
            newNode = parentNode;
        } else if (upstreamNodeUuids.size() == 1) {
            String upstreamUuid = upstreamNodeUuids.get(0);
            boolean pointToParallel = pointToParallelBranch(upstreamUuid);

            if (pointToParallel) {
                String rootId = node.getNodeUuid();
                GraphCompileNode graphCompileNode = getOrCreateGraphCompileNode(rootId);
                appendToNextNodes(parentNode, graphCompileNode);
                newNode = graphCompileNode;
            } else if (parentNode instanceof GraphCompileNode graphCompileNode) {
                newNode = CompileNode.builder().id(node.getNodeUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
                graphCompileNode.appendToLeaf(newNode);
            } else {
                newNode = CompileNode.builder().id(node.getNodeUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
                appendToNextNodes(parentNode, newNode);
            }
        } else {
            newNode = CompileNode.builder().id(node.getNodeUuid()).conditional(false).nextNodes(new ArrayList<>()).build();
            GraphCompileNode parallelBranch = nodeToParallelBranch.get(parentNode.getId());
            appendToNextNodes(Objects.requireNonNullElse(parallelBranch, parentNode), newNode);
        }

        if (null == newNode) {
            log.error("节点{}不存在", node.getNodeUuid());
            return;
        }

        List<String> downstreamUuids = getDownstreamNodeUuids(node.getNodeUuid());
        for (String downstream : downstreamUuids) {
            Optional<AiWorkflowNodeEntity> n = wfNodes.stream()
                    .filter(item -> item.getNodeUuid().equals(downstream))
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
        AiWorkflowNodeEntity wfNode = getNodeByUuid(stateGraphNodeUuid);
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

    private AiWorkflowNodeEntity getNodeByUuid(String nodeUuid) {
        return wfNodes.stream()
                .filter(item -> item.getNodeUuid().equals(nodeUuid))
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
                .filter(node -> node.getNodeUuid().equals(nextNode))
                .findFirst()
                .map(node -> "等待用户输入: " + node.getName())
                .orElse("等待用户输入");
    }

    // SSE辅助方法
    private void sendSseStart() {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("start")
                    .data("{\"status\":\"started\",\"runtimeUuid\":\"" + wfRuntimeResp.getUuid() + "\"}"));
        } catch (Exception e) {
            log.error("发送SSE start失败", e);
        }
    }

    private void sendSseMessage(String event, String data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(event)
                    .data(data));
        } catch (Exception e) {
            log.error("发送SSE消息失败,event:{}", event, e);
        }
    }

    private void sendSseComplete(String data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("complete")
                    .data(data));
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("发送SSE complete失败", e);
        }
    }

    private void sendErrorAndComplete(String errorMsg) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("error")
                    .data(errorMsg));
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("发送SSE错误失败", e);
        }
    }

    public CompiledGraph<WfNodeState> getApp() {
        return app;
    }
}
