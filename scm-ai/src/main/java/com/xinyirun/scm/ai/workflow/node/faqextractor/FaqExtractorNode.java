package com.xinyirun.scm.ai.workflow.node.faqextractor;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 工作流FAQ提取节点
 *
 * 功能：
 * - 分析用户输入内容,提取常见问题(FAQ)及其对应的答案
 * - 使用LLM进行智能提取
 * - 输出格式化的问答对
 *
 * @author zxh
 * @since 2025-10-27
 */
@Slf4j
public class FaqExtractorNode extends AbstractWfNode {

    public FaqExtractorNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    /**
     * 节点处理逻辑
     *
     * nodeConfig格式：
     * {"top_n": 10, "model_name": "deepseek-chat"}
     *
     * @return LLM的返回内容（通过流式输出到nodeState）
     */
    @Override
    public NodeProcessResult onProcess() {
        // 1. 获取节点配置
        JSONObject objectConfig = node.getNodeConfig();
        if (objectConfig == null || objectConfig.isEmpty()) {
            throw new RuntimeException("找不到FAQ提取节点的配置");
        }

        // 2. 解析配置对象
        FaqExtractorNodeConfig nodeConfigObj = checkAndGetConfig(FaqExtractorNodeConfig.class);
        if (nodeConfigObj == null || StringUtils.isBlank(nodeConfigObj.getModelName())) {
            log.warn("找不到FAQ提取节点的配置");
            throw new RuntimeException("FAQ提取节点配置错误");
        }

        log.info("FaqExtractorNode config: {}", nodeConfigObj);

        // 3. 检查输入参数
        if (state.getInputs().isEmpty()) {
            log.warn("FaqExtractorNode inputs is empty");
            return new NodeProcessResult();
        }

        // 4. 获取用户输入文本
        String userInput = getFirstInputText();

        // 5. 生成提示词
        String prompt = FaqExtractorPrompt.getPrompt(nodeConfigObj.getTopN(), userInput);
        log.info("关于常见问题提取生成的提示词: {}", prompt);

        // 6. 调用LLM进行流式处理
        WorkflowUtil.streamingInvokeLLM(wfState, state, node, nodeConfigObj.getModelName(), prompt);

        return new NodeProcessResult();
    }
}
