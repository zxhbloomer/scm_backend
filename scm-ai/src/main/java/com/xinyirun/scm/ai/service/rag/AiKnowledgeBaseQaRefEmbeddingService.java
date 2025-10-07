package com.xinyirun.scm.ai.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefEmbeddingEntity;
import com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaRefEmbeddingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 知识库问答-向量引用服务类
 *
 * <p>对应 aideepin 服务：KnowledgeBaseQaRecordReferenceService</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Slf4j
@Service
public class AiKnowledgeBaseQaRefEmbeddingService extends ServiceImpl<AiKnowledgeBaseQaRefEmbeddingMapper, AiKnowledgeBaseQaRefEmbeddingEntity> {

    /**
     * 批量保存QA问答的向量引用记录
     * 对应aideepin在RAG查询后保存embeddingToScore缓存到数据库的逻辑
     *
     * <p>aideepin逻辑：</p>
     * <pre>
     * // 在retrieve()方法执行后，embeddingToScore缓存了所有embeddingId和score
     * // 然后保存到adi_knowledge_base_qa_record_reference表
     * embeddingToScore.forEach((embeddingId, score) -> {
     *     KnowledgeBaseQaRecordReference ref = new KnowledgeBaseQaRecordReference();
     *     ref.setQaRecordId(qaRecordId);
     *     ref.setEmbeddingId(embeddingId);
     *     ref.setScore(score);
     *     ref.setUserId(userId);
     *     save(ref);
     * });
     * </pre>
     *
     * @param qaRecordId 问答记录ID（ai_knowledge_base_qa.id）
     * @param embeddingScores embeddingId到score的映射（从VectorRetrievalService.embeddingToScore缓存获取）
     * @param userId 用户ID
     * @return 保存的记录数
     */
    public int saveRefEmbeddings(String qaRecordId, Map<String, Double> embeddingScores, Long userId) {
        if (CollectionUtils.isEmpty(embeddingScores)) {
            log.warn("embeddingScores为空，无需保存QA引用记录，qaRecordId: {}", qaRecordId);
            return 0;
        }

        // 批量构建实体对象
        List<AiKnowledgeBaseQaRefEmbeddingEntity> entities = embeddingScores.entrySet().stream()
                .map(entry -> {
                    AiKnowledgeBaseQaRefEmbeddingEntity entity = new AiKnowledgeBaseQaRefEmbeddingEntity();
                    entity.setQaRecordId(qaRecordId);
                    entity.setEmbeddingId(entry.getKey());
                    entity.setScore(entry.getValue());
                    entity.setUserId(userId);
                    return entity;
                })
                .toList();

        // 批量保存（对应aideepin的逐条save，这里优化为批量插入）
        boolean success = this.saveBatch(entities);

        int savedCount = success ? entities.size() : 0;
        log.info("保存QA引用记录完成，qaRecordId: {}, 保存数量: {}", qaRecordId, savedCount);

        return savedCount;
    }

    /**
     * 根据问答记录UUID查询向量引用列表
     *
     * <p>对应 aideepin 方法：listRefEmbeddings</p>
     *
     * @param qaRecordId 问答记录ID
     * @return 向量引用列表
     */
    public List<RefEmbeddingVo> listRefEmbeddings(String qaRecordId) {
        // 查询该问答记录的所有向量引用
        LambdaQueryWrapper<AiKnowledgeBaseQaRefEmbeddingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaRefEmbeddingEntity::getQaRecordId, qaRecordId);
        List<AiKnowledgeBaseQaRefEmbeddingEntity> recordReferences = this.list(wrapper);

        if (CollectionUtils.isEmpty(recordReferences)) {
            return Collections.emptyList();
        }

        // 转换为VO
        return recordReferences.stream()
                .map(ref -> RefEmbeddingVo.builder()
                        .embeddingId(ref.getEmbeddingId())
                        .score(ref.getScore())
                        .text(null)
                        .build())
                .toList();
    }
}
