package com.xinyirun.scm.ai.core.service.draw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.draw.AiDrawCommentEntity;
import com.xinyirun.scm.ai.bean.vo.draw.AiDrawCommentVo;
import com.xinyirun.scm.ai.core.mapper.draw.AiDrawCommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * AI绘图评论服务
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiDrawCommentService extends ServiceImpl<AiDrawCommentMapper, AiDrawCommentEntity> {

    /**
     * 添加评论
     *
     * @param drawId  绘图ID
     * @param userId  用户ID
     * @param remark  评论内容
     * @return 评论VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiDrawCommentVo add(Long drawId, Long userId, String remark) {
        AiDrawCommentEntity comment = new AiDrawCommentEntity();
        comment.setDrawId(drawId);
        comment.setUserId(userId);
        comment.setRemark(remark);
        comment.setIsDeleted(0);

        baseMapper.insert(comment);

        AiDrawCommentEntity savedEntity = baseMapper.selectById(comment.getId());
        return AiDrawCommentVo.builder()
                .commentUuid(savedEntity.getCommentUuid())
                .drawUuid(savedEntity.getDrawUuid())
                .remark(savedEntity.getRemark())
                .cTime(savedEntity.getCTime())
                .build();
    }

    /**
     * 分页查询评论
     *
     * @param drawId      绘图ID
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 分页结果
     */
    public Page<AiDrawCommentVo> listByPage(Long drawId, Integer currentPage, Integer pageSize) {
        Page<AiDrawCommentEntity> page = baseMapper.selectPage(
                new Page<>(currentPage, pageSize),
                lambdaQuery()
                        .eq(AiDrawCommentEntity::getDrawId, drawId)
                        .eq(AiDrawCommentEntity::getIsDeleted, 0)
                        .orderByDesc(AiDrawCommentEntity::getId)
                        .getWrapper()
        );

        Page<AiDrawCommentVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AiDrawCommentVo> voList = new ArrayList<>();
        for (AiDrawCommentEntity entity : page.getRecords()) {
            AiDrawCommentVo vo = AiDrawCommentVo.builder()
                    .commentUuid(entity.getCommentUuid())
                    .drawUuid(entity.getDrawUuid())
                    .remark(entity.getRemark())
                    .cTime(entity.getCTime())
                    .build();
            voList.add(vo);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long commentId, Long userId) {
        AiDrawCommentEntity comment = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawCommentEntity::getId, commentId)
                        .eq(AiDrawCommentEntity::getUserId, userId)
                        .eq(AiDrawCommentEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        lambdaUpdate()
                .eq(AiDrawCommentEntity::getId, commentId)
                .set(AiDrawCommentEntity::getIsDeleted, 1)
                .update();

        return true;
    }
}
