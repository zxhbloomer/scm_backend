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
 * 参考 aideepin: com.moyz.adi.common.workflow.node.faqextractor.FaqExtractorNode
 *
 * 功能：
 * - 分析用户输入内容，提取常见问题（FAQ）及其对应的答案
 * - 使用LLM进行智能提取
 * - 输出格式化的问答对
 *
 * 转换说明：
 * - ObjectNode → JSONObject (Fastjson2)
 * - BaseException → RuntimeException
 * - 使用 WorkflowUtil.streamingInvokeLLM 调用LLM
 *
 * @author SCM AI Team
 * @since 2025-10-27
 */
@Slf4j
public class FaqExtractorNode extends AbstractWfNode {

    public FaqExtractorNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    /**
     * 节点处理逻辑
     * 参考 aideepin FaqExtractorNode.onProcess() 45-70行
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
        log.info("FaqExtractorNode prompt: {}", prompt);

        // 6. 调用LLM进行流式处理
        // 参考 aideepin 第68行: WorkflowUtil.streamingInvokeLLM(wfState, state, node, nodeConfigObj.getModelName(), llmMessages)
        // scm-ai版本直接传入prompt字符串
        WorkflowUtil.streamingInvokeLLM(wfState, state, node, nodeConfigObj.getModelName(), prompt);

        return new NodeProcessResult();
    }
}
