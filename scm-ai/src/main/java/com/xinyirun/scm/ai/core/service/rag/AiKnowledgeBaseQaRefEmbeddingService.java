package com.xinyirun.scm.ai.core.service.rag;

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
 * 
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Slf4j
@Service
public class AiKnowledgeBaseQaRefEmbeddingService extends ServiceImpl<AiKnowledgeBaseQaRefEmbeddingMapper, AiKnowledgeBaseQaRefEmbeddingEntity> {

    /**
     * 批量保存QA问答的向量引用记录
     *
     * <p>将向量检索结果保存到MySQL引用表，记录问答使用了哪些embedding</p>
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
        List<AiKnowledgeBaseQaRefEmbeddingEntity> entities = new java.util.ArrayList<>();
        for (Map.Entry<String, Double> entry : embeddingScores.entrySet()) {
            AiKnowledgeBaseQaRefEmbeddingEntity entity = new AiKnowledgeBaseQaRefEmbeddingEntity();
            entity.setQaRecordId(qaRecordId);
            entity.setEmbeddingId(entry.getKey());
            entity.setScore(entry.getValue());
            entity.setUserId(userId);
            entities.add(entity);
        }

        // 批量保存
        boolean success = this.saveBatch(entities);

        int savedCount = success ? entities.size() : 0;
        log.info("保存QA引用记录完成，qaRecordId: {}, 保存数量: {}",
                qaRecordId, savedCount);

        return savedCount;
    }

    /**
     * 根据问答记录UUID查询向量引用列表
     *
     * 
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
