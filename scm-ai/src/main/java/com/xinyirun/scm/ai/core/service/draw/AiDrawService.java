package com.xinyirun.scm.ai.core.service.draw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.draw.AiDrawEntity;
import com.xinyirun.scm.ai.bean.vo.draw.AiDrawVo;
import com.xinyirun.scm.ai.core.mapper.draw.AiDrawMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * AI绘图服务
 *
 * <p>基于AIDeepin DrawService实现</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Service
public class AiDrawService extends ServiceImpl<AiDrawMapper, AiDrawEntity> {

    @Lazy
    @Resource
    private AiDrawService self;

    @Resource
    private AiDrawStarService aiDrawStarService;

    /**
     * 文本生成图片
     *
     * @param userId          用户ID
     * @param prompt          正向提示词
     * @param negativePrompt  反向提示词
     * @param modelId         模型ID
     * @param size            尺寸
     * @param quality         质量
     * @param number          数量
     * @param seed            种子
     * @return 绘图UUID
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateByPrompt(Long userId, String prompt, String negativePrompt,
                                    Long modelId, String size, String quality,
                                    Integer number, Long seed) {
        String drawUuid = UuidUtil.createShort();

        AiDrawEntity draw = new AiDrawEntity();
        draw.setDrawUuid(drawUuid);
        draw.setUserId(userId);
        draw.setAiModelId(modelId);
        draw.setPrompt(prompt);
        draw.setNegativePrompt(negativePrompt);
        draw.setGenerateSize(size);
        draw.setGenerateQuality(quality);
        draw.setGenerateNumber(number);
        draw.setGenerateSeed(seed);
        draw.setInteractingMethod(1); // 1-文本生图
        draw.setProcessStatus(1); // 1-处理中
        draw.setIsPublic(0);
        draw.setWithWatermark(0);
        draw.setStarCount(0);
        draw.setCommentCount(0);
        draw.setIsDeleted(0);

        baseMapper.insert(draw);

        // TODO: 异步调用图片生成服务
        // self.asyncGenerate(draw);

        return drawUuid;
    }

    /**
     * 编辑图片
     *
     * @param userId            用户ID
     * @param originalDrawUuid  原始绘图UUID
     * @param maskImgUrl        遮罩图URL
     * @param prompt            提示词
     * @param modelId           模型ID
     * @param size              尺寸
     * @param number            数量
     * @return 绘图UUID
     */
    @Transactional(rollbackFor = Exception.class)
    public String editImage(Long userId, String originalDrawUuid, String maskImgUrl,
                            String prompt, Long modelId, String size, Integer number) {
        String drawUuid = UuidUtil.createShort();

        AiDrawEntity draw = new AiDrawEntity();
        draw.setDrawUuid(drawUuid);
        draw.setUserId(userId);
        draw.setAiModelId(modelId);
        draw.setOriginalDrawUuid(originalDrawUuid);
        draw.setMaskImgUrl(maskImgUrl);
        draw.setPrompt(prompt);
        draw.setGenerateSize(size);
        draw.setGenerateNumber(number);
        draw.setInteractingMethod(2); // 2-编辑
        draw.setProcessStatus(1); // 1-处理中
        draw.setIsPublic(0);
        draw.setWithWatermark(0);
        draw.setStarCount(0);
        draw.setCommentCount(0);
        draw.setIsDeleted(0);

        baseMapper.insert(draw);

        // TODO: 异步调用图片编辑服务
        // self.asyncEditImage(draw);

        return drawUuid;
    }

    /**
     * 图片变体(图生图)
     *
     * @param userId            用户ID
     * @param originalDrawUuid  原始绘图UUID
     * @param modelId           模型ID
     * @param number            数量
     * @return 绘图UUID
     */
    @Transactional(rollbackFor = Exception.class)
    public String variationImage(Long userId, String originalDrawUuid, Long modelId, Integer number) {
        String drawUuid = UuidUtil.createShort();

        AiDrawEntity draw = new AiDrawEntity();
        draw.setDrawUuid(drawUuid);
        draw.setUserId(userId);
        draw.setAiModelId(modelId);
        draw.setOriginalDrawUuid(originalDrawUuid);
        draw.setGenerateNumber(number);
        draw.setInteractingMethod(3); // 3-图生图
        draw.setProcessStatus(1); // 1-处理中
        draw.setIsPublic(0);
        draw.setWithWatermark(0);
        draw.setStarCount(0);
        draw.setCommentCount(0);
        draw.setIsDeleted(0);

        baseMapper.insert(draw);

        // TODO: 异步调用图片变体服务
        // self.asyncVariationImage(draw);

        return drawUuid;
    }

    /**
     * 重新生成失败的图片
     *
     * @param drawUuid 绘图UUID
     * @param userId   用户ID
     */
    public void regenerate(String drawUuid, Long userId) {
        AiDrawEntity draw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getProcessStatus, 2) // 2-失败
                        .getWrapper()
        );

        if (draw == null) {
            throw new RuntimeException("未找到失败的绘图任务");
        }

        // 重置为处理中状态
        lambdaUpdate()
                .eq(AiDrawEntity::getId, draw.getId())
                .set(AiDrawEntity::getProcessStatus, 1)
                .set(AiDrawEntity::getFailRemark, null)
                .update();

        // TODO: 异步重新生成
        // self.asyncRegenerate(draw);
    }

    /**
     * 获取我的绘图列表
     *
     * @param userId      用户ID
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 分页结果
     */
    public Page<AiDrawVo> listMine(Long userId, Integer currentPage, Integer pageSize) {
        Page<AiDrawEntity> page = baseMapper.selectPage(
                new Page<>(currentPage, pageSize),
                lambdaQuery()
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .orderByDesc(AiDrawEntity::getId)
                        .getWrapper()
        );

        Page<AiDrawVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AiDrawVo> voList = new ArrayList<>();
        for (AiDrawEntity entity : page.getRecords()) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(entity, vo);
            // 判断是否已点赞
            if (userId != null) {
                boolean isStarred = aiDrawStarService.isStarred(entity.getId(), userId);
                vo.setIsStar(isStarred);
            }
            voList.add(vo);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取公开的绘图列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 分页结果
     */
    public Page<AiDrawVo> listPublic(Integer currentPage, Integer pageSize) {
        Page<AiDrawEntity> page = baseMapper.selectPage(
                new Page<>(currentPage, pageSize),
                lambdaQuery()
                        .eq(AiDrawEntity::getIsPublic, 1)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .orderByDesc(AiDrawEntity::getId)
                        .getWrapper()
        );

        Page<AiDrawVo> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<AiDrawVo> voList = new ArrayList<>();
        for (AiDrawEntity entity : page.getRecords()) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(entity, vo);
            voList.add(vo);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 获取绘图详情
     *
     * @param drawUuid 绘图UUID
     * @param userId   用户ID(可为空)
     * @return 绘图VO
     */
    public AiDrawVo getDetail(String drawUuid, Long userId) {
        AiDrawEntity draw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (draw == null) {
            return null;
        }

        // 公开的图片或者自己的图片,都可以获取到
        if (draw.getIsPublic() == 1 || (userId != null && userId.equals(draw.getUserId()))) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(draw, vo);
            // 判断是否已点赞
            if (userId != null) {
                boolean isStarred = aiDrawStarService.isStarred(draw.getId(), userId);
                vo.setIsStar(isStarred);
            }
            return vo;
        }

        return null;
    }

    /**
     * 获取下一条公开图片
     *
     * @param drawUuid 当前绘图UUID
     * @return 下一条绘图VO
     */
    public AiDrawVo newerPublic(String drawUuid) {
        AiDrawEntity currentDraw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getIsPublic, 1)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (currentDraw == null) {
            return null;
        }

        AiDrawEntity nextDraw = baseMapper.selectOne(
                lambdaQuery()
                        .gt(AiDrawEntity::getId, currentDraw.getId())
                        .eq(AiDrawEntity::getIsPublic, 1)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .orderByAsc(AiDrawEntity::getId)
                        .last("LIMIT 1")
                        .getWrapper()
        );

        if (nextDraw != null) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(nextDraw, vo);
            return vo;
        }
        return null;
    }

    /**
     * 获取上一条公开图片
     *
     * @param drawUuid 当前绘图UUID
     * @return 上一条绘图VO
     */
    public AiDrawVo olderPublic(String drawUuid) {
        AiDrawEntity currentDraw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getIsPublic, 1)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (currentDraw == null) {
            return null;
        }

        AiDrawEntity prevDraw = baseMapper.selectOne(
                lambdaQuery()
                        .lt(AiDrawEntity::getId, currentDraw.getId())
                        .eq(AiDrawEntity::getIsPublic, 1)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .orderByDesc(AiDrawEntity::getId)
                        .last("LIMIT 1")
                        .getWrapper()
        );

        if (prevDraw != null) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(prevDraw, vo);
            return vo;
        }
        return null;
    }

    /**
     * 获取我的下一条图片
     *
     * @param drawUuid 当前绘图UUID
     * @param userId   用户ID
     * @return 下一条绘图VO
     */
    public AiDrawVo newerMine(String drawUuid, Long userId) {
        AiDrawEntity currentDraw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (currentDraw == null) {
            return null;
        }

        AiDrawEntity nextDraw = baseMapper.selectOne(
                lambdaQuery()
                        .gt(AiDrawEntity::getId, currentDraw.getId())
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .orderByAsc(AiDrawEntity::getId)
                        .last("LIMIT 1")
                        .getWrapper()
        );

        if (nextDraw != null) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(nextDraw, vo);
            // 判断是否已点赞
            if (userId != null) {
                boolean isStarred = aiDrawStarService.isStarred(nextDraw.getId(), userId);
                vo.setIsStar(isStarred);
            }
            return vo;
        }
        return null;
    }

    /**
     * 获取我的上一条图片
     *
     * @param drawUuid 当前绘图UUID
     * @param userId   用户ID
     * @return 上一条绘图VO
     */
    public AiDrawVo olderMine(String drawUuid, Long userId) {
        AiDrawEntity currentDraw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (currentDraw == null) {
            return null;
        }

        AiDrawEntity prevDraw = baseMapper.selectOne(
                lambdaQuery()
                        .lt(AiDrawEntity::getId, currentDraw.getId())
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .orderByDesc(AiDrawEntity::getId)
                        .last("LIMIT 1")
                        .getWrapper()
        );

        if (prevDraw != null) {
            AiDrawVo vo = new AiDrawVo();
            BeanUtils.copyProperties(prevDraw, vo);
            // 判断是否已点赞
            if (userId != null) {
                boolean isStarred = aiDrawStarService.isStarred(prevDraw.getId(), userId);
                vo.setIsStar(isStarred);
            }
            return vo;
        }
        return null;
    }

    /**
     * 设置公开/私有
     *
     * @param drawUuid      绘图UUID
     * @param userId        用户ID
     * @param isPublic      是否公开
     * @param withWatermark 是否带水印
     * @return 更新后的VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiDrawVo setPublic(String drawUuid, Long userId, Boolean isPublic, Boolean withWatermark) {
        AiDrawEntity draw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (draw == null) {
            throw new RuntimeException("绘图任务不存在");
        }

        // TODO: 如果需要水印,生成水印图片
        // if (Boolean.TRUE.equals(withWatermark)) {
        //     generateWatermarkImage(draw);
        // }

        lambdaUpdate()
                .eq(AiDrawEntity::getId, draw.getId())
                .set(AiDrawEntity::getIsPublic, isPublic ? 1 : 0)
                .set(withWatermark != null, AiDrawEntity::getWithWatermark, withWatermark ? 1 : 0)
                .update();

        draw.setIsPublic(isPublic ? 1 : 0);
        if (withWatermark != null) {
            draw.setWithWatermark(withWatermark ? 1 : 0);
        }

        AiDrawVo vo = new AiDrawVo();
        BeanUtils.copyProperties(draw, vo);
        // 判断是否已点赞
        if (userId != null) {
            boolean isStarred = aiDrawStarService.isStarred(draw.getId(), userId);
            vo.setIsStar(isStarred);
        }
        return vo;
    }

    /**
     * 删除绘图任务
     *
     * @param drawUuid 绘图UUID
     * @param userId   用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(String drawUuid, Long userId) {
        AiDrawEntity draw = baseMapper.selectOne(
                lambdaQuery()
                        .eq(AiDrawEntity::getDrawUuid, drawUuid)
                        .eq(AiDrawEntity::getUserId, userId)
                        .eq(AiDrawEntity::getIsDeleted, 0)
                        .getWrapper()
        );

        if (draw == null) {
            throw new RuntimeException("绘图任务不存在");
        }

        // TODO: 删除关联的图片文件
        // if (StringUtils.isNotBlank(draw.getImgUrl())) {
        //     fileService.deleteFile(draw.getImgUrl());
        // }

        lambdaUpdate()
                .eq(AiDrawEntity::getId, draw.getId())
                .set(AiDrawEntity::getIsDeleted, 1)
                .update();

        return true;
    }
}
