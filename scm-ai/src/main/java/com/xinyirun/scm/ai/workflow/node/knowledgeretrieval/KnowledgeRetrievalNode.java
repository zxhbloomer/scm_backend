package com.xinyirun.scm.ai.workflow.node.knowledgeretrieval;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
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

    public KnowledgeRetrievalNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity nodeDef, WfState wfState, WfNodeState nodeState) {
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

        // 从Spring容器获取RAG服务
        // TODO: 实现知识检索逻辑，需要获取RAG服务并执行检索
        // Object ragService = SpringUtil.getBean("ragService");
        StringBuilder resp = new StringBuilder();

        try {
            // 调用知识库检索接口
            // List<Content> contents = retriever.retrieve(Query.from(textInput));
            // for (Content content : contents) {
            //     resp.append(content.textSegment().text());
            // }

            // 暂时返回空，实际实现需要根据SCM-AI的RAG服务调整
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
