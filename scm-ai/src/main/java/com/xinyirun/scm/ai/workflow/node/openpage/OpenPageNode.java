package com.xinyirun.scm.ai.workflow.node.openpage;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowInteractionEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowInteractionService;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 打开前端页面节点
 *
 * 支持两种模式：
 * 1. dialog模式（默认）：调用LLM生成业务JSON，通过ai_open_dialog_para触发前端弹窗
 * 2. route模式：构建导航指令，通过open_page_command触发前端RouterTab导航
 *    可选启用人机交互，通过interaction_request触发Chat交互组件
 */
@Slf4j
public class OpenPageNode extends AbstractWfNode {

    public OpenPageNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef,
                        WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    public NodeProcessResult onProcess() {
        OpenPageNodeConfig nodeConfig = checkAndGetConfig(OpenPageNodeConfig.class);
        String openMode = nodeConfig.getOpenMode();

        // 默认dialog模式，保持向后兼容
        if (openMode == null || "dialog".equals(openMode)) {
            return processDialogMode(nodeConfig);
        } else if ("route".equals(openMode)) {
            return processRouteMode(nodeConfig);
        }

        log.warn("OpenPage节点未知的open_mode: {}, 回退到dialog模式", openMode);
        return processDialogMode(nodeConfig);
    }

    /**
     * dialog模式：调用LLM生成JSON，触发前端弹窗（原有逻辑）
     */
    private NodeProcessResult processDialogMode(OpenPageNodeConfig nodeConfig) {
        String prompt = WorkflowUtil.renderTemplate(nodeConfig.getPrompt(), state.getInputs());
        log.info("OpenPage节点[dialog模式]开始调用LLM，prompt长度: {}", prompt != null ? prompt.length() : 0);

        NodeIOData output = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
        state.getOutputs().add(output);

        String result = output.valueToString();
        wfState.setAi_open_dialog_para(result);
        log.info("OpenPage节点[dialog模式]完成，LLM输出长度: {}", result != null ? result.length() : 0);

        return new NodeProcessResult();
    }

    /**
     * route模式：构建导航指令，可选启用人机交互
     */
    private NodeProcessResult processRouteMode(OpenPageNodeConfig nodeConfig) {
        log.info("OpenPage节点[route模式]开始，route={}, pageMode={}",
                nodeConfig.getRoute(), nodeConfig.getPageMode());

        // 构建open_page_command JSON
        JSONObject command = new JSONObject();
        command.put("route", nodeConfig.getRoute());
        command.put("page_mode", nodeConfig.getPageMode());

        // 从上游节点输入中提取动态参数
        JSONObject queryParams = extractInputAsJson("query_params");
        JSONObject formData = extractInputAsJson("form_data");
        String recordId = extractInputValue("record_id");

        command.put("query_params", queryParams);
        command.put("form_data", formData);
        command.put("record_id", recordId);

        String commandJson = command.toJSONString();
        wfState.setOpen_page_command(commandJson);
        log.info("OpenPage节点[route模式]导航指令: {}", commandJson);

        // 输出到节点output
        state.getOutputs().add(NodeIOData.createByText("open_page_command", "页面导航指令", commandJson));

        // 人机交互处理
        if (Boolean.TRUE.equals(nodeConfig.getInteractionEnabled())) {
            return processInteraction(nodeConfig);
        }

        return new NodeProcessResult();
    }

    /**
     * 处理人机交互：创建交互记录，设置侧通道
     */
    private NodeProcessResult processInteraction(OpenPageNodeConfig nodeConfig) {
        log.info("OpenPage节点[route模式]启用人机交互，type={}", nodeConfig.getInteractionType());

        AiWorkflowInteractionService interactionService =
                SpringUtil.getBean(AiWorkflowInteractionService.class);

        // 从上游输入中提取交互参数（如选项列表）
        String interactionParams = extractInputValue("interaction_params");
        if (interactionParams == null) {
            interactionParams = "{}";
        }

        // 创建交互记录
        AiWorkflowInteractionEntity interaction = interactionService.createInteraction(
                wfState.getConversationId(),
                wfState.getUuid(),
                node.getUuid(),
                nodeConfig.getInteractionType(),
                interactionParams,
                nodeConfig.getInteractionDescription(),
                nodeConfig.getTimeoutMinutes()
        );

        // 构建interaction_request JSON
        JSONObject request = new JSONObject();
        request.put("interaction_uuid", interaction.getInteractionUuid());
        request.put("type", nodeConfig.getInteractionType());
        request.put("description", nodeConfig.getInteractionDescription());
        try {
            request.put("params", JSONObject.parseObject(interactionParams));
        } catch (Exception e) {
            log.warn("解析交互参数JSON失败，使用空对象: {}", interactionParams);
            request.put("params", new JSONObject());
        }
        request.put("timeout_minutes", interaction.getTimeoutMinutes());
        request.put("timeout_at", interaction.getTimeoutAt() != null ? interaction.getTimeoutAt().toString() : null);

        String requestJson = request.toJSONString();
        wfState.setInteraction_request(requestJson);
        wfState.setWaitingInteraction(true);
        log.info("OpenPage节点[route模式]交互请求: {}", requestJson);

        state.getOutputs().add(NodeIOData.createByText("interaction_request", "交互请求", requestJson));

        return new NodeProcessResult();
    }

    /**
     * 从上游输入中提取指定名称的JSON对象
     */
    private JSONObject extractInputAsJson(String name) {
        for (NodeIOData input : state.getInputs()) {
            if (name.equals(input.getName())) {
                String value = input.valueToString();
                if (value != null && !value.isEmpty()) {
                    try {
                        return JSONObject.parseObject(value);
                    } catch (Exception e) {
                        log.warn("解析输入参数{}为JSON失败: {}", name, value);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从上游输入中提取指定名称的字符串值
     */
    private String extractInputValue(String name) {
        for (NodeIOData input : state.getInputs()) {
            if (name.equals(input.getName())) {
                return input.valueToString();
            }
        }
        return null;
    }
}
