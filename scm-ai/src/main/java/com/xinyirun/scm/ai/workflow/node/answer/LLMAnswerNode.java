package com.xinyirun.scm.ai.workflow.node.answer;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 工作流LLM大模型节点
 *
 * 此节点负责调用LLM模型生成大模型回答。
 * 支持模板化提示词和流式回复。
 */
@Slf4j
public class LLMAnswerNode extends AbstractWfNode {

    public LLMAnswerNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
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
        // show_process_output=true 时用同步模式，确保 node_complete 时 outputs 已就绪
        boolean silentMode = Boolean.TRUE.equals(nodeConfig.getShowProcessOutput());
        Flux<ChatResponse> streamingFlux = WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, prompt, silentMode);

        if (silentMode) {
            // 同步模式：outputs 已写入，直接返回，不需要 streamingFlux
            return NodeProcessResult.builder().build();
        }
        return NodeProcessResult.builder().streamingFlux(streamingFlux).build();
    }
}
