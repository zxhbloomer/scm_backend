package com.xinyirun.scm.ai.workflow.node.knowledgeretrieval;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流知识检索节点
 *
 * 此节点负责从知识库中检索相关文档内容。
 */
@Slf4j
public class KnowledgeRetrievalNode extends AbstractWfNode {

    public KnowledgeRetrievalNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    public NodeProcessResult onProcess() {
        KnowledgeRetrievalNodeConfig nodeConfig = checkAndGetConfig(KnowledgeRetrievalNodeConfig.class);

        if (StringUtils.isBlank(nodeConfig.getKnowledgeBaseUuid())) {
            log.warn("知识检索节点缺少知识库UUID");
            throw new BusinessException("知识库UUID不能为空");
        }

        String kbUuid = nodeConfig.getKnowledgeBaseUuid();
        log.info("KnowledgeRetrievalNode config: {}", nodeConfig);

        // 获取输入文本
        String textInput = getFirstInputText();
        if (StringUtils.isBlank(textInput)) {
            log.warn("知识检索节点输入内容为空");
            return NodeProcessResult.builder()
                    .content(List.of(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", "")))
                    .build();
        }

        // 调用知识库检索服务
        // 注意：需要根据实际RAG服务实现调整此处代码
        StringBuilder resp = new StringBuilder();

        try {
            // 实现知识库检索逻辑
            log.info("开始知识库检索，知识库UUID: {}, 查询内容: {}", kbUuid, textInput);

            // 此处应调用具体的RAG服务接口
            // 暂时返回空结果
        } catch (Exception e) {
            log.error("知识检索异常: {}", e.getMessage(), e);
            throw new BusinessException("知识检索失败: " + e.getMessage());
        }

        String respText = resp.toString();
        if (StringUtils.isBlank(respText) && StringUtils.isNotBlank(nodeConfig.getDefaultResponse())) {
            respText = nodeConfig.getDefaultResponse();
        }

        return NodeProcessResult.builder()
                .content(List.of(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", respText)))
                .build();
    }
}
