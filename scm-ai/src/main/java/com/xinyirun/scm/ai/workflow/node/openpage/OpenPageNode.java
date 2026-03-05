package com.xinyirun.scm.ai.workflow.node.openpage;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;

/**
 * 打开前端页面节点
 *
 * 根据节点配置的提示词调用LLM生成业务JSON数据，
 * 将结果存入侧通道(wfState.ai_open_dialog_para)，
 * 供前端自动打开业务弹窗和渲染"打开页面"按钮。
 * 本节点使用非流式LLM调用，生成结果不显示在聊天窗口。
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
        String prompt = WorkflowUtil.renderTemplate(nodeConfig.getPrompt(), state.getInputs());
        log.info("OpenPage节点开始调用LLM，prompt长度: {}", prompt != null ? prompt.length() : 0);

        NodeIOData output = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
        state.getOutputs().add(output);

        String result = output.valueToString();
        wfState.setAi_open_dialog_para(result);
        log.info("OpenPage节点完成，LLM输出长度: {}", result != null ? result.length() : 0);

        return new NodeProcessResult();
    }
}
