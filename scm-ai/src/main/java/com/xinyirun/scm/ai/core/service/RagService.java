package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.rag.*;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.service.elasticsearch.VectorRetrievalService;
import com.xinyirun.scm.ai.core.service.model.AiModelSourceService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefEmbeddingService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefGraphService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaService;
import com.xinyirun.scm.ai.core.util.TokenCalculator;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG服务
 *
 * <p>对应 aideepin 服务：KnowledgeBaseService（RAG相关方法）</p>
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Slf4j
@Service
public class RagService {

    @Resource
    private AiKnowledgeBaseQaService qaService;
    @Resource
    private KnowledgeBaseService knowledgeBaseService;
    @Resource
    private VectorRetrievalService vectorRetrievalService;
    @Resource
    private GraphRetrievalService graphRetrievalService;
    @Resource
    private AiKnowledgeBaseQaRefEmbeddingService qaRefEmbeddingService;
    @Resource
    private AiKnowledgeBaseQaRefGraphService qaRefGraphService;
    @Resource
    private AiModelSourceService aiModelSourceService;

    @Autowired
    private AiModelProvider aiModelProvider;


    /**
     * SSE流式问答（核心RAG问答方法）
     *
     * <p>对应 aideepin 方法：KnowledgeBaseService.retrieveAndPushToLLM() + CompositeRAG.ragChat()</p>
     *
     * <p>核心流程（严格对应aideepin）：</p>
     * <ol>
     *   <li>查询QA记录和知识库配置</li>
     *   <li>向量检索 - VectorRetrievalService.searchSimilarDocuments()</li>
     *   <li>图谱检索 - GraphRetrievalService.searchRelatedEntities()</li>
     *   <li>构建RAG增强Prompt - 将检索结果注入到提示词中</li>
     *   <li>ChatModel流式生成 - Spring AI ChatModel.stream()</li>
     *   <li>保存完整答案、引用记录、Token统计</li>
     *   <li>清除检索缓存</li>
     * </ol>
     *
     * <p>参考代码：</p>
     * aideepin: KnowledgeBaseService.retrieveAndPushToLLM() 第378-453行
     * aideepin: CompositeRAG.ragChat() 第77-99行
     *
     * @param qaUuid 问答记录UUID
     * @param userId 用户ID
     * @param tenantCode 租户tenantCode
     * @param maxResults 最大检索结果数（默认3）
     * @param minScore 最小相似度分数（默认0.3）
     * @return SSE流式响应
     */
    public Flux<ChatResponseVo> sseAsk(String qaUuid, Long userId, String tenantCode,
                                        Integer maxResults, Double minScore) {
        log.info("RAG流式问答开始，qaUuid: {}, userId: {}, maxResults: {}, minScore: {}",
                qaUuid, userId, maxResults, minScore);

        // 参数默认值
        if (maxResults == null || maxResults <= 0) {
            maxResults = 3;
        }
        if (minScore == null || minScore < 0) {
            minScore = 0.3;
        }

        final int finalMaxResults = maxResults;
        final double finalMinScore = minScore;

        // 使用Flux.create异步处理（参考AiConversationController.chatStream）
        return Flux.<ChatResponseVo>create(fluxSink -> {
            try {
                // 【多租户支持】在异步线程中设置数据源
                // 由于subscribeOn使用了弹性线程池，必须在Flux.create内部显式设置租户数据源
                if (StringUtils.isNotBlank(tenantCode)) {
                    DataSourceHelper.use(tenantCode);
                    log.debug("RAG流式问答 - 已设置租户数据源: {}", tenantCode);
                }

                // 1. 查询QA记录（对应aideepin第380行）
                AiKnowledgeBaseQaEntity qaRecord = qaService.getByQaUuid(qaUuid);
                if (qaRecord == null) {
                    log.error("问答记录不存在，qaUuid: {}", qaUuid);
                    fluxSink.error(new RuntimeException("问答记录不存在"));
                    return;
                }

                String question = qaRecord.getQuestion();
                String kbUuid = qaRecord.getKbUuid();

                log.info("QA记录查询成功，question: {}, kbUuid: {}", question, kbUuid);

                // 2. 查询知识库配置（对应aideepin第381行）
                AiKnowledgeBaseVo knowledgeBase = knowledgeBaseService.getByUuid(kbUuid);
                if (knowledgeBase == null) {
                    log.error("知识库不存在，kbUuid: {}", kbUuid);
                    fluxSink.error(new RuntimeException("知识库不存在"));
                    return;
                }

                // 2.5. 【严格模式判断点1】查询AI模型配置（获取maxInputTokens）
                // 对应aideepin：KnowledgeBaseService.retrieveAndPushToLLM() 第382行
                // 兼容旧数据：空值、"default"字符串都使用知识库默认模型
                String aiModelId = qaRecord.getAiModelId();
                if (StringUtils.isBlank(aiModelId) || "default".equals(aiModelId)) {
                    aiModelId = knowledgeBase.getIngestModelId();
                    log.debug("QA记录AI模型ID无效({}), 使用知识库默认模型: {}",
                        qaRecord.getAiModelId(), aiModelId);

                    if (StringUtils.isBlank(aiModelId)) {
                        log.error("知识库未配置默认AI模型，kbUuid: {}", kbUuid);
                        fluxSink.error(new RuntimeException("知识库未配置AI模型，请先配置知识库的AI模型"));
                        return;
                    }
                }

                AiModelSourceVo aiModel = aiModelSourceService.getByIdOrThrow(aiModelId);
                int maxInputTokens = aiModel.getMax_input_tokens();
                log.debug("AI模型配置：id={}, max_input_tokens={}", aiModel.getId(), maxInputTokens);

                // 2.6. 【严格模式判断点1】Token验证（用户问题是否超限）
                // 对标aideepin：InputAdaptor.isQuestionValid()
                InputAdaptorMsg validationResult = TokenCalculator.isQuestionValid(question, maxInputTokens);

                // 2.7. 计算maxResults（考虑token空间）
                // 对标aideepin：KnowledgeBaseService.retrieveAndPushToLLM() 第386-389行
                int computedMaxResults = knowledgeBase.getRetrieveMaxResults();
                if (validationResult.getTokenTooMuch() == InputAdaptorMsg.TOKEN_TOO_MUCH_QUESTION) {
                    // 用户问题Token过长，无空间检索文档
                    computedMaxResults = 0;
                    log.warn("用户问题Token过长：questionTokens={}, maxInputTokens={}, maxResults设为0",
                        validationResult.getUserQuestionTokenCount(), maxInputTokens);
                }

                // 2.8. 【严格模式判断点1】严格模式下问题过长直接返回错误
                // 对标aideepin: KnowledgeBaseService.java第410-413行严格模式逻辑
                if (Integer.valueOf(1).equals(knowledgeBase.getIsStrict()) && computedMaxResults == 0) {
                    String errorMsg = String.format(
                        "严格模式：用户问题过长，超过模型输入限制（questionTokens=%d, maxInputTokens=%d）",
                        validationResult.getUserQuestionTokenCount(), maxInputTokens
                    );
                    log.error(errorMsg);

                    // ✅ 使用工厂方法(等价于aideepin的sseEmitterHelper.sendErrorAndComplete)
                    ChatResponseVo errorResponse = ChatResponseVo.createErrorResponse("【知识库严格模式】" + errorMsg);

                    fluxSink.next(errorResponse);
                    fluxSink.complete();
                    return;
                }

                // 使用计算后的maxResults（替换原有的finalMaxResults）
                final int effectiveMaxResults = computedMaxResults;

                // 3. RAG检索阶段（对应aideepin第437行的createRetriever）
                log.info("开始RAG检索，effectiveMaxResults: {}, minScore: {}", effectiveMaxResults, finalMinScore);

                // 3.1 向量检索（对应aideepin的EmbeddingRAG）
                List<VectorSearchResultVo> vectorResults = vectorRetrievalService.searchSimilarDocuments(
                        question, kbUuid, effectiveMaxResults, finalMinScore);
                log.info("向量检索完成，结果数: {}", vectorResults.size());

                // 3.2 图谱检索（对应aideepin的GraphRAG）
                List<GraphSearchResultVo> graphResults = graphRetrievalService.searchRelatedEntities(
                        question, kbUuid, tenantCode, effectiveMaxResults);
                log.info("图谱检索完成，结果数: {}", graphResults.size());

                // 3.3. 【严格模式判断点2】检索结果为空检查（对应aideepin的isEmpty检查）
                boolean vectorEmpty = vectorResults == null || vectorResults.isEmpty();
                boolean graphEmpty = graphResults == null || graphResults.isEmpty();

                if (vectorEmpty && graphEmpty) {
                    log.warn("RAG检索结果为空：向量检索={}, 图谱检索={}", vectorResults != null ? vectorResults.size() : 0,
                            graphResults != null ? graphResults.size() : 0);

                    // 严格模式下直接返回错误(对标aideepin严格模式逻辑)
                    if (Integer.valueOf(1).equals(knowledgeBase.getIsStrict())) {
                        String errorMsg = "严格模式：知识库中未找到相关答案，请补充知识库内容或调整检索参数";
                        log.error(errorMsg);

                        // ✅ 使用工厂方法(等价于aideepin的sseEmitterHelper.sendErrorAndComplete)
                        ChatResponseVo errorResponse = ChatResponseVo.createErrorResponse("【知识库严格模式】" + errorMsg);

                        fluxSink.next(errorResponse);
                        fluxSink.complete();
                        return;
                    }

                    // 非严格模式下警告并继续（由LLM直接回答）
                    log.warn("非严格模式：检索结果为空，将由LLM直接回答问题");
                }

                // 4. 构建RAG增强的Prompt（对应aideepin第438行的ragChat）
                String ragPrompt = buildRagPrompt(question, vectorResults, graphResults, knowledgeBase);
                log.info("RAG Prompt构建完成，长度: {} 字符", ragPrompt.length());

                // 5. 流式生成（对应aideepin第153-160行的tokenStream处理）
                StringBuilder completeAnswer = new StringBuilder();
                final Usage[] finalUsage = new Usage[1];

                // 调用ChatModel流式生成（参考chatWithMemoryStream的模式）
                aiModelProvider.getChatModel().stream(new Prompt(ragPrompt))
                        .doOnNext(chatResponse -> {
                            // 获取内容片段（对应aideepin第154行onPartialResponse）
                            String content = chatResponse.getResult().getOutput().getText();
                            completeAnswer.append(content);

                            // 发送内容块
                            fluxSink.next(ChatResponseVo.createContentChunk(content));

                            // 保存最后一个响应的Usage信息
                            if (chatResponse.getMetadata() != null &&
                                chatResponse.getMetadata().getUsage() != null) {
                                finalUsage[0] = chatResponse.getMetadata().getUsage();
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                // ✅ 在异步回调中重新设置租户数据源（对标AiConversationService.chatStreamWithCallback第107行）
                                // 原因：doOnComplete可能在不同线程执行，ThreadLocal的租户上下文会丢失
                                // kbUuid格式：scm_tenant_20250519_001::8f88cf1bec2248aca0e185dac7f77101
                                String extractedTenantCode = kbUuid.split("::", 2)[0];
                                if (StringUtils.isNotBlank(extractedTenantCode)) {
                                    DataSourceHelper.use(extractedTenantCode);
                                    log.debug("RAG异步回调 - 已重新设置租户数据源: {}", extractedTenantCode);
                                }

                                // 6. 保存完整答案和统计信息（对应aideepin第455-477行updateQaRecord）
                                String fullAnswer = completeAnswer.toString();
                                log.info("AI生成完成，答案长度: {} 字符", fullAnswer.length());

                                // 6.1 计算token（对应aideepin第457行）
                                Integer promptTokens = 0;
                                Integer answerTokens = 0;
                                if (finalUsage[0] != null) {
                                    promptTokens = finalUsage[0].getPromptTokens();
                                    answerTokens = finalUsage[0].getTotalTokens();
                                }

                                // 6.2 更新QA记录（对应aideepin第462-468行）
                                qaRecord.setPrompt(ragPrompt);
                                qaRecord.setPromptTokens(promptTokens);
                                qaRecord.setAnswer(fullAnswer);
                                qaRecord.setAnswerTokens(answerTokens);
                                qaService.updateById(qaRecord);

                                log.info("QA记录已更新，promptTokens: {}, answerTokens: {}",
                                        promptTokens, answerTokens);

                                // 6.3 保存embedding引用记录（对应aideepin第470行createRef中的createEmbeddingRefs）
                                Map<String, Double> embeddingScores = vectorRetrievalService.getAllCachedScores();
                                if (!embeddingScores.isEmpty()) {
                                    qaRefEmbeddingService.saveRefEmbeddings(qaUuid, embeddingScores, userId);
                                    log.info("保存embedding引用记录，数量: {}", embeddingScores.size());
                                }

                                // 6.4 保存graph引用记录（对应aideepin第470行createRef中的createGraphRefs）
                                if (!graphResults.isEmpty()) {
                                    RefGraphVo graphRef = buildRefGraphVo(graphResults);
                                    qaRefGraphService.saveRefGraphs(qaUuid, graphRef, userId);
                                    log.info("保存graph引用记录，vertices: {}, edges: {}",
                                            graphRef.getVertices().size(), graphRef.getEdges().size());
                                }

                                // 7. 清除检索缓存（避免内存泄漏）
                                vectorRetrievalService.clearScoreCache();
                                graphRetrievalService.clearScoreCache();

                                // 发送完成响应
                                fluxSink.next(ChatResponseVo.createCompleteResponse(fullAnswer, "rag-model"));
                                fluxSink.complete();

                                log.info("RAG流式问答完成，qaUuid: {}", qaUuid);

                            } catch (Exception e) {
                                log.error("RAG后处理失败", e);
                                fluxSink.error(e);
                            }
                        })
                        .doOnError(error -> {
                            log.error("RAG流式生成失败", error);
                            fluxSink.error(error);
                        })
                        .subscribe();

            } catch (Exception e) {
                log.error("RAG流式问答失败", e);
                fluxSink.error(e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic()) // 在弹性线程池中执行
        .doFinally(signalType -> {
            // 【多租户支持】清理数据源连接，防止连接泄漏
            // 无论成功、失败还是取消，都必须清理数据源
            DataSourceHelper.close();
            log.debug("RAG流式问答 - 已清理租户数据源连接");
        });
    }

    /**
     * 构建RAG增强的Prompt
     *
     * <p>对应 aideepin 的 RetrievalAugmentor 逻辑</p>
     *
     * <p>Prompt结构：</p>
     * <pre>
     * 系统指令：你是一个基于知识库的AI助手...
     *
     * 【知识库上下文】
     * === 向量检索结果 ===
     * [1] 文本段内容1...
     * [2] 文本段内容2...
     *
     * === 图谱检索结果 ===
     * 实体：实体1、实体2...
     * 关系：实体1-关系-实体2...
     *
     * 【用户问题】
     * 问题内容...
     * </pre>
     *
     * @param question 用户问题
     * @param vectorResults 向量检索结果
     * @param graphResults 图谱检索结果
     * @param knowledgeBase 知识库配置
     * @return RAG增强后的完整Prompt
     */
    private String buildRagPrompt(String question, List<VectorSearchResultVo> vectorResults,
                                   List<GraphSearchResultVo> graphResults,
                                   AiKnowledgeBaseVo knowledgeBase) {
        StringBuilder promptBuilder = new StringBuilder();

        // 系统指令（如果知识库配置了自定义系统消息，则使用）
        String systemMessage = knowledgeBase.getRemark();
        if (StringUtils.isBlank(systemMessage)) {
            systemMessage = "你是一个基于知识库的AI助手，请根据提供的知识库上下文回答用户问题。" +
                    "如果上下文中没有相关信息，请诚实地告诉用户你不知道答案。";
        }
        promptBuilder.append(systemMessage).append("\n\n");

        // 知识库上下文
        promptBuilder.append("【知识库上下文】\n\n");

        // 向量检索结果
        if (!vectorResults.isEmpty()) {
            promptBuilder.append("=== 向量检索结果 ===\n");
            for (int i = 0; i < vectorResults.size(); i++) {
                VectorSearchResultVo result = vectorResults.get(i);
                promptBuilder.append("[").append(i + 1).append("] ")
                        .append(result.getContent())
                        .append(" (相似度: ").append(String.format("%.2f", result.getScore()))
                        .append(")\n\n");
            }
        }

        // 图谱检索结果
        if (!graphResults.isEmpty()) {
            promptBuilder.append("=== 图谱检索结果 ===\n");

            // 提取实体
            promptBuilder.append("实体：");
            String entities = graphResults.stream()
                    .map(GraphSearchResultVo::getEntityName)
                    .distinct()
                    .collect(Collectors.joining("、"));
            promptBuilder.append(entities).append("\n");

            // 提取关系
            promptBuilder.append("关系：\n");
            for (GraphSearchResultVo result : graphResults) {
                if (result.getRelations() != null && !result.getRelations().isEmpty()) {
                    for (GraphRelationVo relation : result.getRelations()) {
                        promptBuilder.append("  - ")
                                .append(relation.getSourceEntityName())
                                .append(" [").append(relation.getRelationType()).append("] ")
                                .append(relation.getTargetEntityName())
                                .append("\n");
                    }
                }
            }
            promptBuilder.append("\n");
        }

        // 用户问题
        promptBuilder.append("【用户问题】\n").append(question).append("\n");

        return promptBuilder.toString();
    }

    /**
     * 从图谱检索结果构建RefGraphVo（用于保存引用记录）
     *
     * <p>对应 aideepin 的 GraphStoreContentRetriever.getGraphRef()</p>
     *
     * @param graphResults 图谱检索结果
     * @return RefGraphVo对象
     */
    private RefGraphVo buildRefGraphVo(List<GraphSearchResultVo> graphResults) {
        // 提取所有实体（去重）
        Map<String, RefGraphVo.GraphVertexVo> vertexMap = new HashMap<>();
        for (GraphSearchResultVo result : graphResults) {
            if (!vertexMap.containsKey(result.getEntityId())) {
                RefGraphVo.GraphVertexVo vertex = RefGraphVo.GraphVertexVo.builder()
                        .id(result.getEntityId())
                        .name(result.getEntityName())
                        .type(result.getEntityType())
                        .description(result.getDescription())
                        .build();
                vertexMap.put(result.getEntityId(), vertex);
            }
        }

        // 提取所有关系（去重）
        Map<String, RefGraphVo.GraphEdgeVo> edgeMap = new HashMap<>();
        for (GraphSearchResultVo result : graphResults) {
            if (result.getRelations() != null) {
                for (GraphRelationVo relation : result.getRelations()) {
                    String edgeKey = relation.getSourceEntityId() + "-" + relation.getTargetEntityId();
                    if (!edgeMap.containsKey(edgeKey)) {
                        RefGraphVo.GraphEdgeVo edge = RefGraphVo.GraphEdgeVo.builder()
                                .id(relation.getRelationId())
                                .name(relation.getRelationType())
                                .description(relation.getDescription())
                                .source(relation.getSourceEntityId())
                                .target(relation.getTargetEntityId())
                                .build();
                        edgeMap.put(edgeKey, edge);
                    }
                }
            }
        }

        // 提取从问题中识别出的实体名称（从图谱检索结果推断）
        List<String> entitiesFromQuestion = graphResults.stream()
                .filter(r -> r.getScore() != null && r.getScore() >= 0.8)
                .map(GraphSearchResultVo::getEntityName)
                .distinct()
                .collect(Collectors.toList());

        return RefGraphVo.builder()
                .vertices(vertexMap.values().stream().collect(Collectors.toList()))
                .edges(edgeMap.values().stream().collect(Collectors.toList()))
                .entitiesFromQuestion(entitiesFromQuestion)
                .build();
    }
}
