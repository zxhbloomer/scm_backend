package com.xinyirun.scm.ai.workflow.node.knowledgeretrieval;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchResultVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.service.elasticsearch.VectorRetrievalService;
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
 * 使用向量检索技术，从Elasticsearch中检索与用户问题最相似的文本段。
 *
 * 核心流程：
 * 1. 从配置中获取知识库UUID、检索参数
 * 2. 调用VectorRetrievalService进行向量检索
 * 3. 将检索结果拼接成字符串返回
 * 4. 如果严格模式且检索结果为空，抛出异常
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

        // 从配置中获取检索参数
        int topN = nodeConfig.getTopN() != null ? nodeConfig.getTopN() : 5;
        double minScore = nodeConfig.getScore() != null ? nodeConfig.getScore() : 0.3;
        boolean isStrict = Boolean.TRUE.equals(nodeConfig.getIsStrict());

        log.info("开始知识库检索，知识库UUID: {}, 查询内容: {}, topN: {}, minScore: {}, isStrict: {}",
                kbUuid, textInput, topN, minScore, isStrict);

        StringBuilder resp = new StringBuilder();

        try {
            // 获取VectorRetrievalService实例
            VectorRetrievalService vectorRetrievalService = SpringUtil.getBean(VectorRetrievalService.class);

            // 1. 调用向量检索服务
            List<VectorSearchResultVo> searchResults = vectorRetrievalService.searchSimilarDocuments(
                    textInput, kbUuid, topN, minScore
            );

            log.info("向量检索完成，结果数: {}", searchResults.size());

            // 2. 检查检索结果
            boolean vectorEmpty = searchResults == null || searchResults.isEmpty();

            if (vectorEmpty) {
                log.warn("知识库检索结果为空，kbUuid: {}, 查询内容: {}", kbUuid, textInput);

                // 严格模式下检索结果为空时抛出异常
                if (isStrict) {
                    throw new BusinessException("严格模式：知识库中未找到相关答案，请补充知识库内容或调整检索参数");
                }

                // 非严格模式下使用默认响应或返回空
                log.info("非严格模式：检索结果为空，返回默认响应");
            }

            // 3. 拼接向量检索结果
            if (!vectorEmpty) {
                log.info("拼接向量检索结果，数量: {}", searchResults.size());
                resp.append("=== 向量检索结果 ===\n");
                for (int i = 0; i < searchResults.size(); i++) {
                    VectorSearchResultVo result = searchResults.get(i);
                    resp.append("[").append(i + 1).append("] ")
                            .append(result.getContent())
                            .append(" (相似度: ").append(String.format("%.2f", result.getScore()))
                            .append(")\n\n");
                }
            }

            log.info("检索结果处理完成，输出文本长度: {}", resp.length());

        } catch (BusinessException e) {
            // 业务异常直接向上抛出
            throw e;
        } catch (Exception e) {
            log.error("知识检索异常: {}", e.getMessage(), e);
            throw new BusinessException("知识检索失败: " + e.getMessage());
        }

        // 获取最终响应文本
        String respText = resp.toString();
        if (StringUtils.isBlank(respText) && StringUtils.isNotBlank(nodeConfig.getDefaultResponse())) {
            respText = nodeConfig.getDefaultResponse();
        }

        log.info("知识检索节点处理完成，返回文本长度: {}", respText.length());

        return NodeProcessResult.builder()
                .content(List.of(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", respText)))
                .build();
    }
}
