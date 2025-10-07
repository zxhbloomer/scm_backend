package com.xinyirun.scm.ai.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseStarEntity;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseStarMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 知识库收藏记录服务类
 *
 * <p>对应 aideepin 服务：KnowledgeBaseStarService</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeBaseStarService extends ServiceImpl<AiKnowledgeBaseStarMapper, AiKnowledgeBaseStarEntity> {

    /**
     * 获取用户对某个知识库的收藏记录
     *
     * <p>对应 aideepin 方法：getRecord</p>
     *
     * @param userId 用户ID
     * @param kbId 知识库ID
     * @return 收藏记录，如果不存在则返回null
     */
    public AiKnowledgeBaseStarEntity getRecord(String userId, String kbId) {
        LambdaQueryWrapper<AiKnowledgeBaseStarEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseStarEntity::getUserId, userId);
        wrapper.eq(AiKnowledgeBaseStarEntity::getKbId, kbId);
        return this.getOne(wrapper);
    }
}
