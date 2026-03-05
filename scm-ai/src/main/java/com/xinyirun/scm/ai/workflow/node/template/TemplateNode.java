package com.xinyirun.scm.ai.workflow.node.template;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WfNodeIODataUtil;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流模板转换节点
 *
 * 有模板时：使用 ${变量名} 语法渲染模板，输出自定义格式文本。
 * 无模板时：将所有输入变量自动构建为JSON对象输出，key为变量名，value为变量值。
 */
@Slf4j
public class TemplateNode extends AbstractWfNode {

    public TemplateNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo node, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        TemplateNodeConfig nodeConfig = checkAndGetConfig(TemplateNodeConfig.class);
        log.info("Template node config: {}", nodeConfig);

        // 将文件内容转换为Markdown格式
        WfNodeIODataUtil.changeFilesContentToMarkdown(state.getInputs());

        String template = nodeConfig.getTemplate();
        String content;
        if (StringUtils.isBlank(template)) {
            // 无模板：将所有输入变量构建为JSON对象输出
            JSONObject json = new JSONObject();
            for (NodeIOData input : state.getInputs()) {
                json.put(input.getName(), input.valueToString());
            }
            content = json.toJSONString();
        } else {
            content = WorkflowUtil.renderTemplate(template, state.getInputs());
        }

        NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", content);
        return NodeProcessResult.builder().content(List.of(output)).build();
    }
}
