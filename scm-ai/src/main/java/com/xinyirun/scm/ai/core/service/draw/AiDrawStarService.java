package com.xinyirun.scm.ai.core.service.draw;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.draw.AiDrawStarEntity;
import com.xinyirun.scm.ai.core.mapper.draw.AiDrawStarMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AI绘图点赞服务
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiDrawStarService extends ServiceImpl<AiDrawStarMapper, AiDrawStarEntity> {

    /**
     * 判断是否已点赞
     *
     * @param drawId 绘图ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    public boolean isStarred(Long drawId, Long userId) {
        return baseMapper.selectCount(
                lambdaQuery()
                        .eq(AiDrawStarEntity::getDrawId, drawId)
                        .eq(AiDrawStarEntity::getUserId, userId)
                        .eq(AiDrawStarEntity::getIsDeleted, false)
                        .getWrapper()
        ) > 0;
    }

    /**
     * 切换点赞状态
     *
     * @param drawId 绘图ID
     * @param userId 用户ID
     * @return 当前是否已点赞
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggle(Long drawId, Long userId) {
        AiDrawStarEntity star = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawStarEntity::getDrawId, drawId)
                        .eq(AiDrawStarEntity::getUserId, userId)
                        .getWrapper()
        );

        if (star == null) {
            // 新增点赞
            star = new AiDrawStarEntity();
            star.setDrawId(drawId);
            star.setUserId(userId);
            star.setIsDeleted(false);
            baseMapper.insert(star);
            return true;
        } else {
            // 切换状态
            Boolean newStatus = !Boolean.TRUE.equals(star.getIsDeleted());
            lambdaUpdate()
                    .eq(AiDrawStarEntity::getId, star.getId())
                    .set(AiDrawStarEntity::getIsDeleted, newStatus)
                    .update();
            return !newStatus;
        }
    }
}
