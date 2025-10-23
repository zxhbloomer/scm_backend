package com.xinyirun.scm.ai.workflow.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流结束节点
 *
 * 此节点是工作流执行的最后一个节点，负责：
 * - 将工作流执行结果渲染成最终输出格式
 * - 支持模板化结果输出
 */
@Slf4j
public class EndNode extends AbstractWfNode {

    public EndNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        List<NodeIOData> result = new ArrayList<>();

        Object nodeConfigObj = node.getNodeConfig();
        String output = "";

        if (null == nodeConfigObj) {
            log.warn("EndNode result config is empty, nodeUuid: {}, title: {}", node.getNodeUuid(), node.getName());
        } else {
            // 从节点配置中获取结果模板
            String resultTemplate = extractResultTemplate(nodeConfigObj);
            if (null != resultTemplate) {
                // 将输入参数渲染到模板中
                output = renderTemplate(resultTemplate, state.getInputs());
            }
        }

        result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", output));
        return NodeProcessResult.builder().content(result).build();
    }

    /**
     * 从节点配置中提取结果模板
     *
     * @param configObj 节点配置对象
     * @return 结果模板字符串
     */
    private String extractResultTemplate(Object configObj) {
        try {
            if (configObj instanceof String) {
                JsonNode jsonNode = JsonUtil.fromJson((String) configObj, JsonNode.class);
                JsonNode resultNode = jsonNode.get("result");
                if (resultNode != null) {
                    return resultNode.asText();
                }
            } else if (configObj instanceof JsonNode) {
                JsonNode jsonNode = (JsonNode) configObj;
                JsonNode resultNode = jsonNode.get("result");
                if (resultNode != null) {
                    return resultNode.asText();
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract result template from node config, nodeUuid: {}", node.getNodeUuid(), e);
        }
        return null;
    }

    /**
     * 将输入参数渲染到模板中
     *
     * 此方法使用简单的变量替换逻辑，将输入参数中的值替换到模板字符串中
     *
     * @param template 模板字符串
     * @param inputs   输入参数列表
     * @return 渲染后的输出字符串
     */
    private String renderTemplate(String template, List<NodeIOData> inputs) {
        if (null == template || template.isEmpty()) {
            return "";
        }

        String result = template;
        for (NodeIOData input : inputs) {
            String placeholder = "${" + input.getName() + "}";
            String value = input.valueToString();
            result = result.replace(placeholder, value != null ? value : "");
        }

        return result;
    }
}
