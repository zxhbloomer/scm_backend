package com.xinyirun.scm.ai.workflow.node.humanfeedback;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;
import static com.xinyirun.scm.ai.workflow.WorkflowConstants.HUMAN_FEEDBACK_KEY;

/**
 * 工作流人机交互节点
 *
 * 此节点负责等待用户输入，并将用户反馈转换为输出。
 */
@Slf4j
public class HumanFeedbackNode extends AbstractWfNode {

    public HumanFeedbackNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity node, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        HumanFeedbackNodeConfig nodeConfig = checkAndGetConfig(HumanFeedbackNodeConfig.class);
        log.info("HumanFeedbackNode config: {}", nodeConfig);

        // 获取用户反馈输入
        Object feedbackData = state.data().get(HUMAN_FEEDBACK_KEY);
        if (feedbackData == null) {
            log.error("人机交互节点未获取到用户反馈，nodeUuid: {}", node.getNodeUuid());
            throw new BusinessException("未获取到用户反馈");
        }

        String userInput = feedbackData.toString();
        log.info("用户反馈输入: {}", userInput);

        List<NodeIOData> result = List.of(
            NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput)
        );

        return NodeProcessResult.builder().content(result).build();
    }

    /**
     * 获取人机交互节点的提示文本
     *
     * @param feedbackNode 人机交互节点
     * @return 提示文本
     */
    public static String getTip(AiWorkflowNodeEntity feedbackNode) {
        try {
            Object configObj = feedbackNode.getNodeConfig();
            if (configObj == null) {
                return "";
            }

            HumanFeedbackNodeConfig nodeConfig;
            if (configObj instanceof String) {
                nodeConfig = JsonUtil.fromJson((String) configObj, HumanFeedbackNodeConfig.class);
            } else {
                nodeConfig = JsonUtil.fromJson(JsonUtil.toJson(configObj), HumanFeedbackNodeConfig.class);
            }

            if (nodeConfig == null) {
                return "";
            }

            return nodeConfig.getTip();
        } catch (Exception e) {
            log.warn("获取人机交互节点提示失败: {}", e.getMessage());
            return "";
        }
    }
}
