package com.xinyirun.scm.ai.workflow.node.humanfeedback;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;
import static com.xinyirun.scm.ai.workflow.WorkflowConstants.HUMAN_FEEDBACK_KEY;

/**
 * 工作流人机交互节点
 *
 * 此节点负责等待用户输入，并将用户反馈转换为多个输出参数。
 * 支持4种交互类型: text/confirm/select/form
 */
@Slf4j
public class HumanFeedbackNode extends AbstractWfNode {

    public HumanFeedbackNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo node, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        HumanFeedbackNodeConfig nodeConfig = checkAndGetConfig(HumanFeedbackNodeConfig.class);
        log.info("HumanFeedbackNode config: {}", nodeConfig);

        // 获取用户反馈输入
        Object feedbackData = state.data().get(HUMAN_FEEDBACK_KEY);
        if (feedbackData == null) {
            log.info("人机交互节点等待用户输入，设置等待标志, nodeUuid: {}", node.getUuid());
            // 暂存已初始化的 inputs，供 resolveSelectOptions 读取（此时节点尚未进入 completedNodes）
            wfState.savePendingNodeInputs(node.getUuid(), state.getInputs());
            wfState.setWaitingInteraction(true);
            return NodeProcessResult.builder().content(List.of()).build();
        }

        String userInput = feedbackData.toString();
        log.info("用户反馈输入: {}", userInput);

        // 解析用户反馈，输出多个NodeIOData
        List<NodeIOData> result = parseUserFeedback(userInput);
        return NodeProcessResult.builder().content(result).build();
    }

    /**
     * 解析用户反馈JSON，输出多个NodeIOData参数
     * 向后兼容：JSON解析失败时当纯文本处理
     */
    private List<NodeIOData> parseUserFeedback(String userInput) {
        List<NodeIOData> result = new ArrayList<>();

        try {
            JSONObject feedback = JSONObject.parseObject(userInput);
            if (feedback == null) {
                return buildTextResult(userInput);
            }

            String action = feedback.getString("action");
            if (action == null || action.isEmpty()) {
                return buildTextResult(userInput);
            }

            // 输出action参数（所有类型都有）
            result.add(NodeIOData.createByText("action", "操作类型", action));

            // 根据action类型输出不同参数
            switch (action) {
                case "confirm":
                case "reject":
                    String confirmedStr = feedback.containsKey("confirmed")
                        ? String.valueOf(feedback.getBoolean("confirmed")) : String.valueOf("confirm".equals(action));
                    result.add(NodeIOData.createByText("output", "操作结果",
                        "confirm".equals(action) ? "用户确认" : "用户驳回"));
                    result.add(NodeIOData.createByText("confirmed", "是否确认", confirmedStr));
                    break;

                case "select_record":
                    // 前端发送结构: { action: "select_record", data: { key, label, ...业务数据 } }
                    JSONObject selectData = feedback.getJSONObject("data");
                    String selectedKey = selectData != null ? selectData.getString("key") : null;
                    String selectedLabel = selectData != null ? selectData.getString("label") : null;
                    result.add(NodeIOData.createByText("selectedKey", "选中项Key",
                        selectedKey != null ? selectedKey : ""));
                    result.add(NodeIOData.createByText("output", "操作结果",
                        "用户选择: " + (selectedLabel != null ? selectedLabel : selectedKey)));
                    // 输出完整业务数据，供下游节点（如OpenPage预填）使用
                    if (selectData != null) {
                        result.add(NodeIOData.createByText("selectedData", "选中项完整数据",
                            selectData.toJSONString()));
                    }
                    break;

                case "form_submit":
                    JSONObject formData = feedback.getJSONObject("data");
                    result.add(NodeIOData.createByText("formData", "表单数据",
                        formData != null ? formData.toJSONString() : "{}"));
                    result.add(NodeIOData.createByText("output", "操作结果", "用户提交表单"));
                    break;

                case "text_input":
                    String text = feedback.getString("text");
                    result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default",
                        text != null ? text : ""));
                    result.add(NodeIOData.createByText("output", "操作结果",
                        text != null ? text : ""));
                    break;

                default:
                    result.add(NodeIOData.createByText("output", "操作结果", userInput));
                    break;
            }

        } catch (Exception e) {
            // JSON解析失败，当纯文本处理（向后兼容）
            log.debug("用户反馈非JSON格式，当纯文本处理: {}", e.getMessage());
            return buildTextResult(userInput);
        }

        return result;
    }

    /**
     * 纯文本反馈的输出（向后兼容）
     */
    private List<NodeIOData> buildTextResult(String userInput) {
        return List.of(
            NodeIOData.createByText("action", "操作类型", "text_input"),
            NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput),
            NodeIOData.createByText("output", "操作结果", userInput)
        );
    }

    /**
     * 获取人机交互节点的完整配置
     */
    public static HumanFeedbackNodeConfig getConfig(AiWorkflowNodeVo feedbackNode) {
        try {
            com.alibaba.fastjson2.JSONObject configObj = feedbackNode.getNodeConfig();
            if (configObj == null || configObj.isEmpty()) {
                return new HumanFeedbackNodeConfig();
            }
            HumanFeedbackNodeConfig config = configObj.toJavaObject(HumanFeedbackNodeConfig.class);
            return config != null ? config : new HumanFeedbackNodeConfig();
        } catch (Exception e) {
            log.warn("获取人机交互节点配置失败: {}", e.getMessage());
            return new HumanFeedbackNodeConfig();
        }
    }

    /**
     * 获取人机交互节点的提示文本（向后兼容）
     */
    public static String getTip(AiWorkflowNodeVo feedbackNode) {
        HumanFeedbackNodeConfig config = getConfig(feedbackNode);
        return config.getTip() != null ? config.getTip() : "";
    }
}
