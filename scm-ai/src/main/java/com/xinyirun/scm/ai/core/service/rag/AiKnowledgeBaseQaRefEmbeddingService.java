package com.xinyirun.scm.ai.core.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefEmbeddingEntity;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchResultVo;
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
     * 批量保存QA问答的向量引用记录（包含文本内容）
     *
     * <p>将向量检索结果保存到MySQL引用表，记录问答使用了哪些embedding及其文本内容</p>
     *
     * @param qaRecordId 问答记录ID（ai_knowledge_base_qa.id）
     * @param vectorResults 向量检索结果列表（包含embeddingId, score, content）
     * @param userId 用户ID
     * @return 保存的记录数
     */
    public int saveRefEmbeddings(String qaRecordId, List<VectorSearchResultVo> vectorResults, Long userId) {
        if (CollectionUtils.isEmpty(vectorResults)) {
            log.warn("vectorResults为空，无需保存QA引用记录，qaRecordId: {}", qaRecordId);
            return 0;
        }

        // 批量构建实体对象
        List<AiKnowledgeBaseQaRefEmbeddingEntity> entities = new java.util.ArrayList<>();
        for (VectorSearchResultVo result : vectorResults) {
            AiKnowledgeBaseQaRefEmbeddingEntity entity = new AiKnowledgeBaseQaRefEmbeddingEntity();
            entity.setQaRecordId(qaRecordId);
            entity.setEmbeddingId(result.getEmbeddingId());
            entity.setScore(result.getScore());
            entity.setContent(result.getContent());
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
     * <p>直接从MySQL返回引用记录（包含content），无需再查询Milvus</p>
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

        // 转换为VO，直接使用MySQL中的content
        return recordReferences.stream()
                .map(ref -> RefEmbeddingVo.builder()
                        .embeddingId(ref.getEmbeddingId())
                        .score(ref.getScore())
                        .text(ref.getContent())
                        .build())
                .toList();
    }

    /**
     * 根据问答记录UUID查询向量引用列表（返回完整信息）
     *
     * <p>直接从MySQL返回引用记录（包含content），无需再查询Milvus</p>
     * <p>用于Controller接口返回给前端展示</p>
     *
     * @param qaRecordId 问答记录ID
     * @return 向量引用列表（QaRefEmbeddingVo）
     */
    public List<QaRefEmbeddingVo> listRefEmbeddingsForDisplay(String qaRecordId) {
        // 查询该问答记录的所有向量引用
        LambdaQueryWrapper<AiKnowledgeBaseQaRefEmbeddingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaRefEmbeddingEntity::getQaRecordId, qaRecordId);
        List<AiKnowledgeBaseQaRefEmbeddingEntity> recordReferences = this.list(wrapper);

        if (CollectionUtils.isEmpty(recordReferences)) {
            return Collections.emptyList();
        }

        // 转换为QaRefEmbeddingVo，直接使用MySQL中的content
        return recordReferences.stream()
                .map(ref -> QaRefEmbeddingVo.builder()
                        .id(ref.getId())
                        .qaRecordId(ref.getQaRecordId())
                        .embeddingId(ref.getEmbeddingId())
                        .score(ref.getScore())
                        .content(ref.getContent())
                        .userId(ref.getUserId())
                        .build())
                .toList();
    }

    /**
     * 根据问答记录ID删除向量引用记录
     *
     * @param qaRecordId 问答记录ID（ai_knowledge_base_qa.uuid）
     * @return 删除的记录数
     */
    public int deleteByQaRecordId(String qaRecordId) {
        LambdaQueryWrapper<AiKnowledgeBaseQaRefEmbeddingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaRefEmbeddingEntity::getQaRecordId, qaRecordId);
        return baseMapper.delete(wrapper);
    }

    /**
     * 根据用户ID删除所有向量引用记录
     *
     * @param userId 用户ID
     * @return 删除的记录数
     */
    public int deleteByUserId(Long userId) {
        LambdaQueryWrapper<AiKnowledgeBaseQaRefEmbeddingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaRefEmbeddingEntity::getUserId, userId);
        int count = baseMapper.delete(wrapper);
        log.info("删除用户向量引用记录，userId: {}, 删除数量: {}", userId, count);
        return count;
    }
}
