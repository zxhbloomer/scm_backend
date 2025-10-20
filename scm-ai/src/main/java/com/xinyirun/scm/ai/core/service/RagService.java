package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.rag.*;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.service.elasticsearch.VectorRetrievalService;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefEmbeddingService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefGraphService;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaService;
import com.xinyirun.scm.ai.core.util.TokenCalculator;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG服务
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
    private AiModelConfigService aiModelConfigService;

    @Autowired
    private AiModelProvider aiModelProvider;


    /**
     * SSE流式问答（核心RAG问答方法）
     *
     * <p>核心流程：</p>
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
     * @param qaUuid 问答记录UUID
     * @param userId 用户ID
     * @param tenantCode 租户tenantCode
     * @param maxResults 最大检索结果数（已废弃，实际使用知识库配置的retrieveMaxResults）
     * @param minScore 最小相似度分数（已废弃，实际使用知识库配置的retrieveMinScore）
     * @return SSE流式响应
     */
    public Flux<ChatResponseVo> sseAsk(String qaUuid, Long userId, String tenantCode,
                                        Integer maxResults, Double minScore) {
        log.info("RAG流式问答开始，qaUuid: {}, userId: {}", qaUuid, userId);

        // 使用Flux.create异步处理
        return Flux.<ChatResponseVo>create(fluxSink -> {
            try {
                // 【多租户支持】在异步线程中设置数据源
                // 由于subscribeOn使用了弹性线程池，必须在Flux.create内部显式设置租户数据源
                if (StringUtils.isNotBlank(tenantCode)) {
                    DataSourceHelper.use(tenantCode);
                    log.debug("RAG流式问答 - 已设置租户数据源: {}", tenantCode);
                }

                // 1. 查询QA记录
                AiKnowledgeBaseQaEntity qaRecord = qaService.getByQaUuid(qaUuid);
                if (qaRecord == null) {
                    log.error("问答记录不存在，qaUuid: {}", qaUuid);
                    fluxSink.error(new RuntimeException("问答记录不存在"));
                    return;
                }

                String question = qaRecord.getQuestion();
                String kbUuid = qaRecord.getKbUuid();

                log.info("QA记录查询成功，question: {}, kbUuid: {}", question, kbUuid);

                // 2. 查询知识库配置
                AiKnowledgeBaseVo knowledgeBase = knowledgeBaseService.getByUuid(kbUuid);
                if (knowledgeBase == null) {
                    log.error("知识库不存在，kbUuid: {}", kbUuid);
                    fluxSink.error(new RuntimeException("知识库不存在"));
                    return;
                }

                // 2.5. 【严格模式判断点1】查询AI模型配置（获取maxInputTokens）
                // 获取默认LLM模型配置（RAG场景使用LLM）
                AiModelConfigVo aiModel = aiModelConfigService.getDefaultModelConfigWithKey("LLM");

                // 从maxTokens推算maxInputTokens（通常maxInputTokens是maxTokens的70-80%）
                // 如果没有配置maxTokens,使用默认值4096
                int maxInputTokens = aiModel.getMaxTokens() != null ?
                    (int)(aiModel.getMaxTokens() * 0.75) : 4096;

                log.debug("AI模型配置：id={}, maxTokens={}, 推算maxInputTokens={}",
                    aiModel.getId(), aiModel.getMaxTokens(), maxInputTokens);

                // 2.6. 【严格模式判断点1】Token验证（用户问题是否超限）
                InputAdaptorMsg validationResult = TokenCalculator.isQuestionValid(question, maxInputTokens);

                // 2.7. 从知识库配置读取检索参数
                int computedMaxResults = knowledgeBase.getRetrieveMaxResults();
                double computedMinScore = knowledgeBase.getRetrieveMinScore().doubleValue();

                // 2.7.5. 根据token空间动态调整maxResults
                if (validationResult.getTokenTooMuch() == InputAdaptorMsg.TOKEN_TOO_MUCH_QUESTION) {
                    // 用户问题Token过长，无空间检索文档
                    computedMaxResults = 0;
                    log.warn("用户问题Token过长：questionTokens={}, maxInputTokens={}, maxResults设为0",
                        validationResult.getUserQuestionTokenCount(), maxInputTokens);
                }

                // 2.8. 【严格模式判断点1】严格模式下问题过长直接返回错误
                if (Integer.valueOf(1).equals(knowledgeBase.getIsStrict()) && computedMaxResults == 0) {
                    String errorMsg = String.format(
                        "严格模式：用户问题过长，超过模型输入限制（questionTokens=%d, maxInputTokens=%d）",
                        validationResult.getUserQuestionTokenCount(), maxInputTokens
                    );
                    log.error(errorMsg);

                    // 使用工厂方法创建错误响应
                    ChatResponseVo errorResponse = ChatResponseVo.createErrorResponse("【知识库严格模式】" + errorMsg);

                    fluxSink.next(errorResponse);
                    fluxSink.complete();
                    return;
                }

                // 使用从知识库配置读取的参数
                final int effectiveMaxResults = computedMaxResults;
                final double effectiveMinScore = computedMinScore;

                // 3. RAG检索阶段
                log.info("开始RAG检索，kbUuid: {}, effectiveMaxResults: {}, effectiveMinScore: {}",
                        kbUuid, effectiveMaxResults, effectiveMinScore);

                // 3.1 向量检索
                List<VectorSearchResultVo> vectorResults = vectorRetrievalService.searchSimilarDocuments(
                        question, kbUuid, effectiveMaxResults, effectiveMinScore);
                log.info("向量检索完成，结果数: {}", vectorResults.size());

                // 3.2 图谱检索
                List<GraphSearchResultVo> graphResults = graphRetrievalService.searchRelatedEntities(
                        question, kbUuid, tenantCode, effectiveMaxResults);
                log.info("图谱检索完成，结果数: {}", graphResults.size());

                // 3.3. 【严格模式判断点2】检索结果为空检查
                boolean vectorEmpty = vectorResults == null || vectorResults.isEmpty();
                boolean graphEmpty = graphResults == null || graphResults.isEmpty();

                if (vectorEmpty && graphEmpty) {
                    log.warn("RAG检索结果为空：向量检索={}, 图谱检索={}", vectorResults != null ? vectorResults.size() : 0,
                            graphResults != null ? graphResults.size() : 0);

                    // 严格模式下直接返回错误
                    if (Integer.valueOf(1).equals(knowledgeBase.getIsStrict())) {
                        String errorMsg = "严格模式：知识库中未找到相关答案，请补充知识库内容或调整检索参数";
                        log.error(errorMsg);

                        // 使用工厂方法创建错误响应
                        ChatResponseVo errorResponse = ChatResponseVo.createErrorResponse("【知识库严格模式】" + errorMsg);

                        fluxSink.next(errorResponse);
                        fluxSink.complete();
                        return;
                    }

                    // 非严格模式下警告并继续（由LLM直接回答）
                    log.warn("非严格模式：检索结果为空，将由LLM直接回答问题");
                }

                // 4. 构建RAG增强的Messages
                List<Message> ragMessages = buildRagMessages(question, vectorResults, graphResults, knowledgeBase);
                log.info("RAG Messages构建完成，消息数: {}", ragMessages.size());

                // 4.5. ✅ 添加温度参数支持
                Double temperature = 0.0; // 默认值：完全确定性
                if (knowledgeBase.getQueryLlmTemperature() != null) {
                    temperature = knowledgeBase.getQueryLlmTemperature().doubleValue();
                }

                ChatOptions chatOptions = OpenAiChatOptions.builder()
                        .temperature(temperature)
                        .build();

                log.info("ChatOptions配置完成，temperature: {}", temperature);

                // 5. 流式生成
                StringBuilder completeAnswer = new StringBuilder();
                final Usage[] finalUsage = new Usage[1];

                // 保存完整prompt用于记录（将messages转换为字符串）
                final String ragPromptForRecord = ragMessages.stream()
                        .map(msg -> msg.getMessageType() + ": " + msg.getText())
                        .collect(Collectors.joining("\n\n"));

                // 调用ChatModel流式生成（使用结构化Messages + ChatOptions）
                aiModelProvider.getChatModel().stream(new Prompt(ragMessages, chatOptions))
                        .doOnNext(chatResponse -> {
                            // 获取内容片段
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
                                // ✅ 在异步回调中重新设置租户数据源
                                // 原因：doOnComplete可能在不同线程执行，ThreadLocal的租户上下文会丢失
                                // kbUuid格式：scm_tenant_20250519_001::8f88cf1bec2248aca0e185dac7f77101
                                String extractedTenantCode = kbUuid.split("::", 2)[0];
                                if (StringUtils.isNotBlank(extractedTenantCode)) {
                                    DataSourceHelper.use(extractedTenantCode);
                                    log.debug("RAG异步回调 - 已重新设置租户数据源: {}", extractedTenantCode);
                                }

                                // 6. 保存完整答案和统计信息
                                String fullAnswer = completeAnswer.toString();
                                log.info("AI生成完成，答案长度: {} 字符", fullAnswer.length());

                                // 6.1 计算token
                                Integer promptTokens = 0;
                                Integer answerTokens = 0;
                                if (finalUsage[0] != null) {
                                    promptTokens = finalUsage[0].getPromptTokens();
                                    answerTokens = finalUsage[0].getTotalTokens();
                                }

                                // 6.2 更新QA记录
                                qaRecord.setPrompt(ragPromptForRecord);
                                qaRecord.setPromptTokens(promptTokens);
                                qaRecord.setAnswer(fullAnswer);
                                qaRecord.setAnswerTokens(answerTokens);
                                qaService.updateById(qaRecord);

                                log.info("QA记录已更新，promptTokens: {}, answerTokens: {}",
                                        promptTokens, answerTokens);

                                // 6.3 保存embedding引用记录
                                Map<String, Double> embeddingScores = vectorRetrievalService.getAllCachedScores();
                                if (!embeddingScores.isEmpty()) {
                                    qaRefEmbeddingService.saveRefEmbeddings(qaUuid, embeddingScores, userId);
                                    log.info("保存embedding引用记录，数量: {}", embeddingScores.size());
                                }

                                // 6.4 保存graph引用记录
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
     * 构建RAG增强的Messages
     *
     * <p>消息结构：</p>
     * <pre>
     * SystemMessage: 知识库配置的系统提示词（querySystemMessage字段）
     * UserMessage: 知识库上下文 + 用户问题
     *
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
     * @return 结构化的消息列表（SystemMessage + UserMessage）
     */
    private List<Message> buildRagMessages(String question, List<VectorSearchResultVo> vectorResults,
                                            List<GraphSearchResultVo> graphResults,
                                            AiKnowledgeBaseVo knowledgeBase) {
        List<Message> messages = new ArrayList<>();

        // 使用知识库配置的系统提示词
        String systemMessage = knowledgeBase.getQuerySystemMessage();
        if (StringUtils.isBlank(systemMessage)) {
            systemMessage = "你是一个基于知识库的AI助手，请根据提供的知识库上下文回答用户问题。" +
                    "如果上下文中没有相关信息，请诚实地告诉用户你不知道答案。";
        }

        // 使用SystemMessage而不是拼接到prompt
        messages.add(new SystemMessage(systemMessage));

        // 构建用户消息：知识库上下文 + 用户问题
        StringBuilder userMessageBuilder = new StringBuilder();

        // 知识库上下文
        userMessageBuilder.append("【知识库上下文】\n\n");

        // 向量检索结果
        if (!vectorResults.isEmpty()) {
            userMessageBuilder.append("=== 向量检索结果 ===\n");
            for (int i = 0; i < vectorResults.size(); i++) {
                VectorSearchResultVo result = vectorResults.get(i);
                userMessageBuilder.append("[").append(i + 1).append("] ")
                        .append(result.getContent())
                        .append(" (相似度: ").append(String.format("%.2f", result.getScore()))
                        .append(")\n\n");
            }
        }

        // 图谱检索结果
        if (!graphResults.isEmpty()) {
            userMessageBuilder.append("=== 图谱检索结果 ===\n");

            // 提取实体
            userMessageBuilder.append("实体：");
            String entities = graphResults.stream()
                    .map(GraphSearchResultVo::getEntityName)
                    .distinct()
                    .collect(Collectors.joining("、"));
            userMessageBuilder.append(entities).append("\n");

            // 提取关系
            userMessageBuilder.append("关系：\n");
            for (GraphSearchResultVo result : graphResults) {
                if (result.getRelations() != null && !result.getRelations().isEmpty()) {
                    for (GraphRelationVo relation : result.getRelations()) {
                        userMessageBuilder.append("  - ")
                                .append(relation.getSourceEntityName())
                                .append(" [").append(relation.getRelationType()).append("] ")
                                .append(relation.getTargetEntityName())
                                .append("\n");
                    }
                }
            }
            userMessageBuilder.append("\n");
        }

        // 用户问题
        userMessageBuilder.append("【用户问题】\n").append(question).append("\n");

        messages.add(new UserMessage(userMessageBuilder.toString()));

        return messages;
    }

    /**
     * 从图谱检索结果构建RefGraphVo（用于保存引用记录）
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
