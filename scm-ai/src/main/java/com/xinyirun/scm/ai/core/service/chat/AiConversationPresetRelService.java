package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationPresetRelEntity;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationPresetRelMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI对话预设关系服务类
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Slf4j
@Service
public class AiConversationPresetRelService extends ServiceImpl<AiConversationPresetRelMapper, AiConversationPresetRelEntity> {

    @Resource
    private AiConversationPresetRelMapper presetRelMapper;

    /**
     * 创建预设与对话的关联关系
     *
     * @param presetConvId 预设对话ID（ai_conversation_preset.id）
     * @param userConvId 用户对话ID（ai_conversation.id）
     * @param userId 用户ID
     * @param customModifications 用户自定义修改（JSON格式）
     * @return 关联关系实体
     */
    public AiConversationPresetRelEntity createRelation(String presetConvId, String userConvId,
                                                         Long userId, String customModifications) {
        AiConversationPresetRelEntity entity = new AiConversationPresetRelEntity();
        entity.setUuid(UuidUtil.createShort());
        entity.setPresetConvId(presetConvId);
        entity.setUserConvId(userConvId);
        entity.setUserId(userId);
        entity.setCustomModifications(customModifications);

        boolean success = this.save(entity);
        log.info("创建预设关联关系，presetConvId: {}, userConvId: {}", presetConvId, userConvId);

        return success ? entity : null;
    }

    /**
     * 根据对话ID删除预设关系
     *
     * @param conversationId 对话ID
     * @return 删除数量
     */
    public int deleteByConversationId(String conversationId) {
        int count = presetRelMapper.deleteByConversationId(conversationId);
        log.info("删除对话的预设关系，conversationId: {}, 删除数量: {}", conversationId, count);
        return count;
    }

    /**
     * 根据预设ID删除预设关系
     *
     * @param presetId 预设ID
     * @return 删除数量
     */
    public int deleteByPresetId(String presetId) {
        int count = presetRelMapper.deleteByPresetId(presetId);
        log.info("删除预设的关联关系，presetId: {}, 删除数量: {}", presetId, count);
        return count;
    }

    /**
     * 查询对话关联的预设
     *
     * @param conversationId 对话ID
     * @return 预设关系实体
     */
    public AiConversationPresetRelEntity getByConversationId(String conversationId) {
        return this.lambdaQuery()
                .eq(AiConversationPresetRelEntity::getUserConvId, conversationId)
                .one();
    }
}
