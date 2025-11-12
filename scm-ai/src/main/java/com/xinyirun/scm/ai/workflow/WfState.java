package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.WORKFLOW_PROCESS_STATUS_READY;

/**
 * 工作流实例状态
 *
 * @author zxh
 * @since 2025-10-21
 */
@Setter
@Getter
public class WfState {

    private String uuid;
    private Long userId;
    private String tenantCode;
    private String conversationId;
    private String processingNodeUuid;

    /**
     * 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     */
    private WorkflowCallSource callSource;

    /**
     * 工作流流式处理器（用于发送 SSE 事件）
     */
    private WorkflowStreamHandler streamHandler;

    /**
     * Source node uuid => target node uuid list
     */
    private Map<String, List<String>> edges = new HashMap<>();
    private Map<String, List<String>> conditionalEdges = new HashMap<>();

    /**
     * 已运行节点列表
     */
    private List<AbstractWfNode> completedNodes = new LinkedList<>();

    private List<AiWorkflowRuntimeNodeVo> runtimeNodes = new ArrayList<>();

    /**
     * 工作流接收到的输入（也是开始节点的输入参数）
     */
    private List<NodeIOData> input;

    /**
     * 工作流执行结束后的输出
     */
    private List<NodeIOData> output = new ArrayList<>();
    private Integer processStatus = WORKFLOW_PROCESS_STATUS_READY;

    /**
     * 人机交互节点
     */
    private Set<String> interruptNodes = new HashSet<>();

    /**
     * 执行栈：用于检测子工作流循环依赖
     */
    private Set<String> executionStack = new HashSet<>();

    public WfState(Long userId, List<NodeIOData> input, String uuid) {
        this.input = input;
        this.userId = userId;
        this.uuid = uuid;
    }

    public WfState(Long userId, List<NodeIOData> input, String uuid, String tenantCode, String conversationId) {
        this.input = input;
        this.userId = userId;
        this.uuid = uuid;
        this.tenantCode = tenantCode;
        this.conversationId = conversationId;
    }

    /**
     * 构造函数（支持传入父工作流的执行栈）
     *
     * @param userId 用户ID
     * @param input 工作流输入
     * @param uuid 运行时UUID
     * @param tenantCode 租户编码
     * @param conversationId 对话ID
     * @param parentExecutionStack 父工作流的执行栈
     */
    public WfState(Long userId, List<NodeIOData> input, String uuid, String tenantCode, String conversationId, Set<String> parentExecutionStack) {
        this.input = input;
        this.userId = userId;
        this.uuid = uuid;
        this.tenantCode = tenantCode;
        this.conversationId = conversationId;
        if (parentExecutionStack != null) {
            this.executionStack = new HashSet<>(parentExecutionStack);
        }
    }

    /**
     * 获取最新的输出结果
     *
     * @return 参数列表
     */
    public List<NodeIOData> getLatestOutputs() {
        WfNodeState upstreamState = completedNodes.get(completedNodes.size() - 1).getState();
        return upstreamState.getOutputs();
    }

    public Optional<WfNodeState> getNodeStateByNodeUuid(String nodeUuid) {
        return this.completedNodes.stream()
                .filter(item -> item.getNode().getUuid().equals(nodeUuid))
                .map(AbstractWfNode::getState)
                .findFirst();
    }

    /**
     * 新增一条边
     * 并行执行分支的情况下会出现一个 source node 对应多个 target node
     *
     * @param sourceNodeUuid 开始节点
     * @param targetNodeUuid 目标节点
     */
    public void addEdge(String sourceNodeUuid, String targetNodeUuid) {
        List<String> targetNodeUuids = edges.computeIfAbsent(sourceNodeUuid, k -> new ArrayList<>());
        targetNodeUuids.add(targetNodeUuid);
    }

    /**
     * 新增一条边
     * 按条件执行的分支会出现一个 source node 对应多个 target node 的情况
     *
     * @param sourceNodeUuid 开始节点
     * @param targetNodeUuid 目标节点
     */
    public void addConditionalEdge(String sourceNodeUuid, String targetNodeUuid) {
        List<String> targetNodeUuids = conditionalEdges.computeIfAbsent(sourceNodeUuid, k -> new ArrayList<>());
        targetNodeUuids.add(targetNodeUuid);
    }

    public List<NodeIOData> getIOByNodeUuid(String nodeUuid) {
        List<NodeIOData> result = new ArrayList<>();
        Optional<AbstractWfNode> optional = completedNodes.stream()
                .filter(node -> nodeUuid.equals(node.getNode().getUuid()))
                .findFirst();
        if (optional.isEmpty()) {
            return result;
        }
        result.addAll(optional.get().getState().getInputs());
        result.addAll(optional.get().getState().getOutputs());
        return result;
    }

    public AiWorkflowRuntimeNodeVo getRuntimeNodeByNodeUuid(String wfNodeUuid) {
        AiWorkflowNodeVo wfNode = getCompletedNodes().stream()
                .map(AbstractWfNode::getNode)
                .filter(node -> node.getUuid().equals(wfNodeUuid))
                .findFirst()
                .orElse(null);
        if (null == wfNode) {
            return null;
        }
        return getRuntimeNodes().stream()
                .filter(item -> item.getNodeId().equals(wfNode.getId()))
                .findFirst()
                .orElse(null);
    }

    public void addInterruptNode(String nodeUuid) {
        this.interruptNodes.add(nodeUuid);
    }

    /**
     * 检查工作流UUID是否在执行栈中（用于检测循环依赖）
     *
     * @param workflowUuid 工作流UUID
     * @return true-存在循环依赖，false-不存在
     */
    public boolean isInExecutionStack(String workflowUuid) {
        return executionStack.contains(workflowUuid);
    }

    /**
     * 获取执行栈副本（用于子工作流）
     *
     * @return 执行栈副本
     */
    public Set<String> getExecutionStack() {
        return new HashSet<>(executionStack);
    }

    /**
     * 添加工作流到执行栈
     *
     * @param workflowUuid 工作流UUID
     */
    public void addToExecutionStack(String workflowUuid) {
        this.executionStack.add(workflowUuid);
    }

    /**
     * 获取输出数据的JSON字符串表示
     * 用于子工作流返回输出结果
     *
     * @return JSON格式的输出数据
     */
    public String getOutputAsJsonString() {
        JSONObject outputNode = new JSONObject();
        if (this.output != null) {
            for (NodeIOData data : this.output) {
                outputNode.put(data.getName(), data.getContent());
            }
        }
        return outputNode.toJSONString();
    }
}
