package com.xinyirun.scm.ai.workflow.node.answer;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 工作流LLM回答节点
 *
 * 此节点负责调用LLM模型生成回答。
 * 支持模板化提示词和流式回复。
 */
@Slf4j
public class LLMAnswerNode extends AbstractWfNode {

    public LLMAnswerNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    public NodeProcessResult onProcess() {
        LLMAnswerNodeConfig nodeConfig = checkAndGetConfig(LLMAnswerNodeConfig.class);
        String inputText = getFirstInputText();
        log.info("LLM answer node config: {}", nodeConfig);

        // 构建最终的提示词
        String prompt = inputText;
        if (StringUtils.isNotBlank(nodeConfig.getPrompt())) {
            prompt = WorkflowUtil.renderTemplate(nodeConfig.getPrompt(), state.getInputs());
        }
        log.info("LLM prompt: {}", prompt);

        String modelName = nodeConfig.getModelName();
        // 调用LLM接口
        WorkflowUtil.invokeLLM(wfState, state, node, modelName, prompt);

        return new NodeProcessResult();
    }
}
