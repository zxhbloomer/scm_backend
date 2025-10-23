package com.xinyirun.scm.ai.workflow.node.start;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流开始节点
 *
 * 此节点是工作流执行的起点，负责：
 * - 处理工作流的初始输入
 * - 可选地设置开场白(prologue)信息
 * - 将输入参数转换为后续节点的输入
 */
@Slf4j
public class StartNode extends AbstractWfNode {

    public StartNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        List<NodeIOData> result = new ArrayList<>();

        StartNodeConfig nodeConfig = checkAndGetConfig(StartNodeConfig.class);

        // 如果配置了开场白，则使用开场白作为输出
        if (StringUtils.isNotBlank(nodeConfig.getPrologue())) {
            result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", nodeConfig.getPrologue()));
        } else {
            // 否则直接使用工作流的初始输入作为输出
            result.addAll(convertInputsToOutputs(state.getInputs()));
        }

        return NodeProcessResult.builder().content(result).build();
    }

    /**
     * 将输入参数转换为输出参数
     *
     * @param inputs 输入参数列表
     * @return 输出参数列表
     */
    private List<NodeIOData> convertInputsToOutputs(List<NodeIOData> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return new ArrayList<>();
        }

        // 如果已存在output参数，直接返回
        for (NodeIOData input : inputs) {
            if (DEFAULT_OUTPUT_PARAM_NAME.equals(input.getName())) {
                return inputs;
            }
        }

        // 否则，将第一个参数的名称改为output
        if (!inputs.isEmpty()) {
            inputs.get(0).setName(DEFAULT_OUTPUT_PARAM_NAME);
        }

        return inputs;
    }
}
