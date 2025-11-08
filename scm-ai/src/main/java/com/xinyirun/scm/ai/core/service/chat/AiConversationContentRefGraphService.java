package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentRefGraphEntity;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentRefGraphMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * AI对话消息-图谱引用服务类
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Slf4j
@Service
public class AiConversationContentRefGraphService extends ServiceImpl<AiConversationContentRefGraphMapper, AiConversationContentRefGraphEntity> {

    @Resource
    private AiConversationContentRefGraphMapper refGraphMapper;

    /**
     * 保存对话消息的图谱引用记录
     *
     * @param messageId 消息ID（ai_conversation_content.message_id）
     * @param kbId 知识库ID
     * @param entitiesFromQuestion 从问题提取的实体（JSON字符串）
     * @param graphFromStore Neo4j图谱数据（JSON字符串）
     * @param entityCount 实体数量
     * @param relationCount 关系数量
     * @param userId 用户ID
     * @return 保存的记录ID
     */
    public Long saveRefGraph(String messageId, String kbId, String entitiesFromQuestion,
                             String graphFromStore, Integer entityCount, Integer relationCount, Long userId) {
        AiConversationContentRefGraphEntity entity = new AiConversationContentRefGraphEntity();
        entity.setMessageId(messageId);
        entity.setKbId(kbId);
        entity.setEntitiesFromQuestion(entitiesFromQuestion);
        entity.setGraphFromStore(graphFromStore);
        entity.setEntityCount(entityCount);
        entity.setRelationCount(relationCount);
        entity.setUserId(userId);

        boolean success = this.save(entity);
        log.info("保存对话图谱引用完成，messageId: {}, 实体数: {}, 关系数: {}",
                messageId, entityCount, relationCount);

        return success ? entity.getId() : null;
    }

    /**
     * 根据消息ID列表批量删除图谱引用
     *
     * @param messageIds 消息ID列表
     * @return 删除数量
     */
    public int deleteByMessageIds(List<String> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return 0;
        }

        int count = refGraphMapper.deleteByMessageIds(messageIds);
        log.info("批量删除对话图谱引用，messageIds数量: {}, 删除数量: {}", messageIds.size(), count);
        return count;
    }
}
