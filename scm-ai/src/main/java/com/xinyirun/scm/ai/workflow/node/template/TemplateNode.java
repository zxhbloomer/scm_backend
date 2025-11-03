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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流模板节点
 *
 * 此节点负责使用指定的模板字符串来渲染工作流数据。
 * 模板支持 ${参数名} 的变量替换语法。
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

        // 使用工作流工具渲染模板
        String content = WorkflowUtil.renderTemplate(nodeConfig.getTemplate(), state.getInputs());
        NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", content);

        return NodeProcessResult.builder().content(List.of(output)).build();
    }
}
