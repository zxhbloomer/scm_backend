package com.xinyirun.scm.ai.workflow.node.start;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WfNodeIODataUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流开始节点
 *
 * 此节点是工作流执行的起点,负责：
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

        // 日志：开始节点的输入参数
        log.debug("开始节点执行 - nodeTitle: {}, nodeUuid: {}, inputs数量: {}",
                node.getTitle(), node.getUuid(), state.getInputs().size());
        for (int i = 0; i < state.getInputs().size(); i++) {
            NodeIOData input = state.getInputs().get(i);
            log.debug("  开始节点输入参数#{} - name: {}, type: {}, value: {}",
                    i + 1,
                    input.getName(),
                    input.getContent().getType(),
                    input.getContent().getValue());
        }

        List<NodeIOData> result = new ArrayList<>();

        // 如果配置了开场白,则使用开场白作为输出
        if (StringUtils.isNotBlank(nodeConfig.getPrologue())) {
            log.debug("开始节点使用开场白作为输出: {}", nodeConfig.getPrologue());
            result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", nodeConfig.getPrologue()));
        } else {
            // 否则使用标准工具类方法转换输入为输出
            log.debug("开始节点直接传递输入参数作为输出");
            result.addAll(WfNodeIODataUtil.changeInputsToOutputs(state.getInputs()));
        }

        // AI_CHAT场景: 记录LLM选择的workflow信息
        if (wfState.getCallSource() == WorkflowCallSource.AI_CHAT) {
            Map<String, Object> selectionInfo = new HashMap<>();
            selectionInfo.put("workflow_uuid", wfState.getWorkflowUuid());
            selectionInfo.put("workflow_title", wfState.getWorkflowTitle());
            selectionInfo.put("selected_by", "LLM智能路由");
            selectionInfo.put("selected_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            selectionInfo.put("call_source", wfState.getCallSource().name());

            NodeIOData workflowInfo = NodeIOData.createByOptions(
                "workflow_selection",
                "工作流选择",
                selectionInfo
            );
            result.add(workflowInfo);

            log.info("AI_CHAT场景 - 记录workflow选择: workflow_uuid={}, workflow_title={}",
                    wfState.getWorkflowUuid(), wfState.getWorkflowTitle());
        }

        return NodeProcessResult.builder().content(result).build();
    }
}
