package com.xinyirun.scm.ai.workflow.node.knowledgeretrieval;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.rag.GraphRelationVo;
import com.xinyirun.scm.ai.bean.vo.rag.GraphSearchResultVo;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchResultVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.service.GraphRetrievalService;
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorRetrievalService;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流知识检索节点
 *
 * 此节点负责从知识库中检索相关文档内容。
 * 使用向量检索技术，从Milvus中检索与用户问题最相似的文本段。
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
        boolean enableGraph = Boolean.TRUE.equals(nodeConfig.getEnableGraphRetrieval());
        String graphModelName = nodeConfig.getGraphModelName();

        // 图谱检索模型验证（与生成回答节点逻辑一致：不配置就报错，不使用默认模型）
        if (enableGraph && StringUtils.isBlank(graphModelName)) {
            log.error("❌ 启用图谱检索时必须配置图谱检索模型");
            throw new BusinessException("启用图谱检索时必须配置图谱检索模型。请在节点属性中选择模型，或关闭图谱检索开关。");
        }

        log.info("开始知识库检索，知识库UUID: {}, 查询内容: {}, topN: {}, minScore: {}, isStrict: {}, enableGraph: {}, graphModel: {}",
                kbUuid, textInput, topN, minScore, isStrict, enableGraph, graphModelName);

        StringBuilder resp = new StringBuilder();

        try {
            // 获取MilvusVectorRetrievalService实例
            MilvusVectorRetrievalService vectorRetrievalService = SpringUtil.getBean(MilvusVectorRetrievalService.class);

            // 1. 调用向量检索服务
            List<VectorSearchResultVo> searchResults = vectorRetrievalService.searchSimilarDocuments(
                    textInput, kbUuid, topN, minScore
            );

            log.info("向量检索完成，结果数: {}", searchResults.size());

            // 2. 图谱检索（仅在启用时调用）
            List<GraphSearchResultVo> graphResults = new ArrayList<>();

            if (enableGraph) {
                log.info("=== 图谱检索已启用 ===");
                log.info("图谱检索参数 - kbUuid: {}, 查询内容: {}, topN: {}", kbUuid, textInput, topN);

                try {
                    // 从kbUuid提取租户编码（格式：scm_tenant_20250519_001::uuid）
                    String tenantCode = kbUuid.split("::", 2)[0];
                    log.info("从kbUuid提取租户编码: {}", tenantCode);

                    // 获取GraphRetrievalService实例
                    GraphRetrievalService graphRetrievalService = SpringUtil.getBean(GraphRetrievalService.class);

                    // 调用图谱检索服务（使用指定模型）
                    log.info("===== 图谱检索调用信息 =====");
                    log.info("graphModelName 参数值: [{}]", graphModelName);
                    log.info("graphModelName 是否为空: {}", StringUtils.isBlank(graphModelName));
                    log.info("开始调用 GraphRetrievalService.searchRelatedEntities");
                    log.info("传递的参数: question=[{}], kbUuid=[{}], tenantCode=[{}], topN=[{}], modelName=[{}]",
                            textInput, kbUuid, tenantCode, topN, graphModelName);

                    graphResults = graphRetrievalService.searchRelatedEntities(
                            textInput, kbUuid, tenantCode, topN, graphModelName
                    );

                    log.info("===== 图谱检索调用完成 =====");

                    // 详细记录图谱检索结果
                    log.info("图谱检索完成，返回结果数: {}", graphResults.size());

                    if (!graphResults.isEmpty()) {
                        // 统计实体和关系数量
                        long entityCount = graphResults.stream()
                                .map(GraphSearchResultVo::getEntityName)
                                .distinct()
                                .count();
                        long relationCount = graphResults.stream()
                                .filter(r -> r.getRelations() != null)
                                .mapToLong(r -> r.getRelations().size())
                                .sum();

                        log.info("图谱检索统计 - 唯一实体数: {}, 关系数: {}", entityCount, relationCount);

                        // 记录实体名称
                        String entityNames = graphResults.stream()
                                .map(GraphSearchResultVo::getEntityName)
                                .distinct()
                                .collect(Collectors.joining("、"));
                        log.info("检索到的实体: {}", entityNames);
                    } else {
                        log.warn("图谱检索结果为空，可能原因：1)知识库未完成图谱化索引 2)问题中无可识别实体 3)图谱中无相关数据");
                    }

                } catch (Exception e) {
                    log.error("图谱检索失败，错误类型: {}, 错误信息: {}", e.getClass().getSimpleName(), e.getMessage(), e);
                    log.warn("图谱检索失败降级处理：继续使用向量检索结果");
                    // 图谱检索失败不影响向量检索结果，继续处理
                }
            } else {
                log.info("图谱检索未启用，仅使用向量检索结果");
            }

            // 3. 检查检索结果
            boolean vectorEmpty = searchResults == null || searchResults.isEmpty();
            boolean graphEmpty = !enableGraph || graphResults.isEmpty();

            if (vectorEmpty && graphEmpty) {
                log.warn("知识库检索结果为空，kbUuid: {}, 查询内容: {}", kbUuid, textInput);

                // 严格模式下检索结果为空时抛出异常
                if (isStrict) {
                    throw new BusinessException("严格模式：知识库中未找到相关答案，请补充知识库内容或调整检索参数");
                }

                // 非严格模式下使用默认响应或返回空
                log.info("非严格模式：检索结果为空，返回默认响应");
            }

            // 4. 合并向量和图谱结果
            log.info("=== 开始合并检索结果 ===");
            log.info("合并策略 - 向量结果: {}, 图谱结果: {}", !vectorEmpty ? "有" : "无", !graphEmpty ? "有" : "无");

            // 4.1 拼接向量检索结果
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

            // 4.2 拼接图谱检索结果
            if (!graphEmpty) {
                log.info("拼接图谱检索结果，数量: {}", graphResults.size());
                resp.append("=== 图谱检索结果 ===\n");

                // 提取实体
                resp.append("实体：");
                String entities = graphResults.stream()
                        .map(GraphSearchResultVo::getEntityName)
                        .distinct()
                        .collect(Collectors.joining("、"));
                resp.append(entities).append("\n");
                log.info("合并实体信息: {}", entities);

                // 提取关系
                resp.append("关系：\n");
                int relationCount = 0;
                for (GraphSearchResultVo result : graphResults) {
                    if (result.getRelations() != null && !result.getRelations().isEmpty()) {
                        for (GraphRelationVo relation : result.getRelations()) {
                            resp.append("  - ")
                                    .append(relation.getSourceEntityName())
                                    .append(" [").append(relation.getRelationType()).append("] ")
                                    .append(relation.getTargetEntityName())
                                    .append("\n");
                            relationCount++;
                        }
                    }
                }
                resp.append("\n");
                log.info("合并关系信息，数量: {}", relationCount);
            }

            log.info("检索结果合并完成，输出文本长度: {}", resp.length());

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
