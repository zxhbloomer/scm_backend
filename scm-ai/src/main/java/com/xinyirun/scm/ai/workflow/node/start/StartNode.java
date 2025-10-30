package com.xinyirun.scm.ai.workflow.node.start;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WfNodeIODataUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流开始节点
 * 参考 aideepin: com.moyz.adi.common.workflow.node.start.StartNode
 *
 * 此节点是工作流执行的起点，负责：
 * - 处理工作流的初始输入
 * - 可选地设置开场白(prologue)信息
 * - 将输入参数转换为后续节点的输入
 */
@Slf4j
public class StartNode extends AbstractWfNode {

    public StartNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        StartNodeConfig nodeConfig = checkAndGetConfig(StartNodeConfig.class);
        List<NodeIOData> result;

        // 参考 aideepin StartNode.java Line 43-48
        // 如果配置了开场白，则使用开场白作为输出
        if (StringUtils.isNotBlank(nodeConfig.getPrologue())) {
            result = List.of(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", nodeConfig.getPrologue()));
        } else {
            // 否则使用标准工具类方法转换输入为输出
            // 参考 aideepin StartNode.java Line 46: WfNodeIODataUtil.changeInputsToOutputs(state.getInputs())
            result = WfNodeIODataUtil.changeInputsToOutputs(state.getInputs());
        }

        return NodeProcessResult.builder().content(result).build();
    }
}
