package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentRefEmbeddingEntity;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentRefEmbeddingMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI对话消息-向量引用服务类
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Slf4j
@Service
public class AiConversationContentRefEmbeddingService extends ServiceImpl<AiConversationContentRefEmbeddingMapper, AiConversationContentRefEmbeddingEntity> {

    @Resource
    private AiConversationContentRefEmbeddingMapper refEmbeddingMapper;

    /**
     * 批量保存对话消息的向量引用记录
     *
     * @param messageId 消息ID（ai_conversation_content.message_id）
     * @param embeddingScores embeddingId到score的映射（从VectorRetrievalService获取）
     * @param userId 用户ID
     * @return 保存的记录数
     */
    public int saveRefEmbeddings(String messageId, Map<String, Double> embeddingScores, Long userId) {
        if (CollectionUtils.isEmpty(embeddingScores)) {
            log.warn("embeddingScores为空，跳过保存向量引用，messageId: {}", messageId);
            return 0;
        }

        List<AiConversationContentRefEmbeddingEntity> entities = new ArrayList<>();
        for (Map.Entry<String, Double> entry : embeddingScores.entrySet()) {
            AiConversationContentRefEmbeddingEntity entity = new AiConversationContentRefEmbeddingEntity();
            entity.setMessageId(messageId);
            entity.setEmbeddingId(entry.getKey());
            entity.setScore(BigDecimal.valueOf(entry.getValue()));
            entity.setUserId(userId);
            entities.add(entity);
        }

        boolean success = this.saveBatch(entities);
        int savedCount = success ? entities.size() : 0;

        log.info("保存对话向量引用完成，messageId: {}, 数量: {}", messageId, savedCount);
        return savedCount;
    }

    /**
     * 根据消息ID列表批量删除向量引用
     *
     * @param messageIds 消息ID列表
     * @return 删除数量
     */
    public int deleteByMessageIds(List<String> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return 0;
        }

        int count = refEmbeddingMapper.deleteByMessageIds(messageIds);
        log.info("批量删除对话向量引用，messageIds数量: {}, 删除数量: {}", messageIds.size(), count);
        return count;
    }
}
