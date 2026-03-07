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
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;
import static com.xinyirun.scm.ai.workflow.WorkflowConstants.NODE_OUTPUT_KEY_PREFIX;

/**
 * 工作流知识检索节点
 *
 * 支持单个或多个知识库的向量检索和图谱检索。
 * 多知识库时使用CompletableFuture并行检索，按score降序合并结果。
 */
@Slf4j
public class KnowledgeRetrievalNode extends AbstractWfNode {

    private static final int RETRIEVAL_TIMEOUT_SECONDS = 30;

    public KnowledgeRetrievalNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    public NodeProcessResult onProcess() {
        KnowledgeRetrievalNodeConfig nodeConfig = checkAndGetConfig(KnowledgeRetrievalNodeConfig.class);

        // 1. 获取知识库UUID列表（兼容新旧格式）
        List<String> kbUuids = resolveKbUuids(nodeConfig);
        if (kbUuids.isEmpty()) {
            throw new BusinessException("知识库UUID不能为空");
        }

        // 2. 获取查询关键词
        String textInput = resolveQueryText(nodeConfig);
        if (StringUtils.isBlank(textInput)) {
            log.warn("知识检索节点输入内容为空");
            return NodeProcessResult.builder()
                    .content(List.of(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", "")))
                    .build();
        }

        // 3. 获取检索参数
        int topN = nodeConfig.getTopN() != null ? nodeConfig.getTopN() : 5;
        double minScore = nodeConfig.getScore() != null ? nodeConfig.getScore() : 0.3;
        boolean isStrict = Boolean.TRUE.equals(nodeConfig.getIsStrict());
        boolean enableGraph = Boolean.TRUE.equals(nodeConfig.getEnableGraphRetrieval());
        String graphModelName = nodeConfig.getGraphModelName();

        if (enableGraph && StringUtils.isBlank(graphModelName)) {
            throw new BusinessException("启用图谱检索时必须配置图谱检索模型。请在节点属性中选择模型，或关闭图谱检索开关。");
        }

        log.info("开始知识库检索，知识库数量: {}, UUIDs: {}, 查询内容: {}, topN: {}, minScore: {}", kbUuids.size(), kbUuids, textInput, topN, minScore);

        StringBuilder resp = new StringBuilder();

        try {
            MilvusVectorRetrievalService vectorRetrievalService = SpringUtil.getBean(MilvusVectorRetrievalService.class);

            // 4. 并行向量检索所有知识库
            List<VectorSearchResultVo> searchResults = parallelVectorSearch(vectorRetrievalService, textInput, kbUuids, topN, minScore);
            log.info("向量检索完成，合并后结果数: {}", searchResults.size());

            // 5. 并行图谱检索（若启用）
            List<GraphSearchResultVo> graphResults = new ArrayList<>();
            if (enableGraph) {
                graphResults = parallelGraphSearch(textInput, kbUuids, topN, graphModelName);
                log.info("图谱检索完成，合并后结果数: {}", graphResults.size());
            }

            // 6. 检查检索结果
            boolean vectorEmpty = searchResults.isEmpty();
            boolean graphEmpty = !enableGraph || graphResults.isEmpty();

            if (vectorEmpty && graphEmpty) {
                log.warn("知识库检索结果为空，查询内容: {}", textInput);
                if (isStrict) {
                    throw new BusinessException("严格模式：知识库中未找到相关答案，请补充知识库内容或调整检索参数");
                }
            }

            // 7. 拼接向量检索结果
            if (!vectorEmpty) {
                resp.append("=== 向量检索结果 ===\n");
                for (int i = 0; i < searchResults.size(); i++) {
                    VectorSearchResultVo result = searchResults.get(i);
                    resp.append("[").append(i + 1).append("] ")
                            .append(result.getContent())
                            .append(" (相似度: ").append(String.format("%.2f", result.getScore()))
                            .append(")\n\n");
                }
            }

            // 8. 拼接图谱检索结果
            if (!graphEmpty) {
                resp.append("=== 图谱检索结果 ===\n");
                resp.append("实体：");
                String entities = graphResults.stream()
                        .map(GraphSearchResultVo::getEntityName)
                        .distinct()
                        .collect(Collectors.joining("、"));
                resp.append(entities).append("\n");

                resp.append("关系：\n");
                for (GraphSearchResultVo result : graphResults) {
                    if (result.getRelations() != null) {
                        for (GraphRelationVo relation : result.getRelations()) {
                            resp.append("  - ")
                                    .append(relation.getSourceEntityName())
                                    .append(" [").append(relation.getRelationType()).append("] ")
                                    .append(relation.getTargetEntityName())
                                    .append("\n");
                        }
                    }
                }
                resp.append("\n");
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("知识检索异常: {}", e.getMessage(), e);
            throw new BusinessException("知识检索失败: " + e.getMessage());
        }

        String respText = resp.toString();
        if (StringUtils.isBlank(respText) && StringUtils.isNotBlank(nodeConfig.getDefaultResponse())) {
            respText = nodeConfig.getDefaultResponse();
        }

        log.info("知识检索节点处理完成，返回文本长度: {}", respText.length());

        return NodeProcessResult.builder()
                .content(List.of(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", respText)))
                .build();
    }

    /**
     * 解析知识库UUID列表，兼容新格式（多知识库数组）和旧格式（单知识库字符串）
     */
    private List<String> resolveKbUuids(KnowledgeRetrievalNodeConfig nodeConfig) {
        List<String> kbUuids = new ArrayList<>();

        // 新格式：多知识库列表
        if (nodeConfig.getKnowledgeBaseList() != null && !nodeConfig.getKnowledgeBaseList().isEmpty()) {
            for (KnowledgeRetrievalNodeConfig.KbItem item : nodeConfig.getKnowledgeBaseList()) {
                String uuid = resolveOneKbUuid(item.getUuid(), item.getIsTemp(), item.getTempNodeUuid());
                if (StringUtils.isNotBlank(uuid)) {
                    kbUuids.add(uuid);
                }
            }
            log.info("使用多知识库模式，解析后UUID数量: {}", kbUuids.size());
            return kbUuids;
        }

        // 旧格式：单知识库字符串
        if (StringUtils.isNotBlank(nodeConfig.getKnowledgeBaseUuid())) {
            String uuid = resolveOneKbUuid(
                    nodeConfig.getKnowledgeBaseUuid(),
                    nodeConfig.getIsTempKb(),
                    nodeConfig.getTempKbNodeUuid()
            );
            if (StringUtils.isNotBlank(uuid)) {
                kbUuids.add(uuid);
            }
            log.info("使用单知识库模式（向后兼容），UUID: {}", uuid);
        }

        return kbUuids;
    }

    /**
     * 解析单个知识库UUID，处理临时知识库和变量引用
     */
    private String resolveOneKbUuid(String rawUuid, Boolean isTemp, String tempKbNodeUuid) {
        String kbUuid = rawUuid;

        // 临时知识库：从上游节点输出中提取kbUuid
        if (Boolean.TRUE.equals(isTemp) && StringUtils.isNotBlank(tempKbNodeUuid)) {
            String outputKey = NODE_OUTPUT_KEY_PREFIX + tempKbNodeUuid;
            Object tempKbOutput = state.data().get(outputKey);

            if (tempKbOutput instanceof List) {
                @SuppressWarnings("unchecked")
                List<NodeIOData> outputs = (List<NodeIOData>) tempKbOutput;
                if (!outputs.isEmpty()) {
                    try {
                        com.alibaba.fastjson2.JSONObject jsonObj = com.alibaba.fastjson2.JSON.parseObject(outputs.get(0).valueToString());
                        String extracted = jsonObj.getString("kbUuid");
                        if (StringUtils.isNotBlank(extracted)) {
                            kbUuid = extracted;
                            log.info("从临时知识库节点提取kbUuid: {}", kbUuid);
                        }
                    } catch (Exception e) {
                        log.warn("解析临时知识库节点输出失败: {}", e.getMessage());
                    }
                }
            }
        }

        // 变量引用格式兼容
        if (kbUuid != null && kbUuid.startsWith("{") && kbUuid.endsWith("}")) {
            kbUuid = WorkflowUtil.renderTemplate(kbUuid, state.getInputs());
            if (StringUtils.isBlank(kbUuid) || (kbUuid.startsWith("{") && kbUuid.endsWith("}"))) {
                log.warn("知识库UUID变量引用解析失败: {}", rawUuid);
                return null;
            }
        }

        return kbUuid;
    }

    /**
     * 解析查询关键词
     */
    private String resolveQueryText(KnowledgeRetrievalNodeConfig nodeConfig) {
        if (StringUtils.isNotBlank(nodeConfig.getQueryTemplate())) {
            String text = WorkflowUtil.renderTemplate(nodeConfig.getQueryTemplate(), state.getInputs());
            log.info("使用查询模板，渲染后: {}", text);
            return text;
        }
        String text = getFirstInputText();
        log.info("使用上游节点输出作为查询关键词: {}", text);
        return text;
    }

    /**
     * 并行向量检索多个知识库，合并结果按score降序排列
     */
    private List<VectorSearchResultVo> parallelVectorSearch(
            MilvusVectorRetrievalService service, String question, List<String> kbUuids, int topN, double minScore) {

        // 单个知识库直接调用，无需并行
        if (kbUuids.size() == 1) {
            return service.searchSimilarDocuments(question, kbUuids.get(0), topN, minScore);
        }

        // 多个知识库并行检索
        List<CompletableFuture<List<VectorSearchResultVo>>> futures = kbUuids.stream()
                .map(kbUuid -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return service.searchSimilarDocuments(question, kbUuid, topN, minScore);
                    } catch (Exception e) {
                        log.warn("知识库 {} 向量检索失败: {}", kbUuid, e.getMessage());
                        return List.<VectorSearchResultVo>of();
                    }
                }))
                .collect(Collectors.toList());

        // 收集所有结果
        List<VectorSearchResultVo> allResults = new ArrayList<>();
        for (CompletableFuture<List<VectorSearchResultVo>> future : futures) {
            try {
                allResults.addAll(future.get(RETRIEVAL_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            } catch (Exception e) {
                log.warn("向量检索超时或异常: {}", e.getMessage());
            }
        }

        // 按score降序排列，限制topN
        return allResults.stream()
                .sorted(Comparator.comparingDouble(VectorSearchResultVo::getScore).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * 并行图谱检索多个知识库
     */
    private List<GraphSearchResultVo> parallelGraphSearch(
            String question, List<String> kbUuids, int topN, String graphModelName) {

        GraphRetrievalService graphService = SpringUtil.getBean(GraphRetrievalService.class);

        List<CompletableFuture<List<GraphSearchResultVo>>> futures = kbUuids.stream()
                .map(kbUuid -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String tenantCode = kbUuid.split("::", 2)[0];
                        return graphService.searchRelatedEntities(question, kbUuid, tenantCode, topN, graphModelName);
                    } catch (Exception e) {
                        log.warn("知识库 {} 图谱检索失败: {}", kbUuid, e.getMessage());
                        return List.<GraphSearchResultVo>of();
                    }
                }))
                .collect(Collectors.toList());

        List<GraphSearchResultVo> allResults = new ArrayList<>();
        for (CompletableFuture<List<GraphSearchResultVo>> future : futures) {
            try {
                allResults.addAll(future.get(RETRIEVAL_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            } catch (Exception e) {
                log.warn("图谱检索超时或异常: {}", e.getMessage());
            }
        }

        return allResults;
    }
}
