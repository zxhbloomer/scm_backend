package com.xinyirun.scm.ai.controller.draw;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.vo.draw.AiDrawCommentVo;
import com.xinyirun.scm.ai.bean.vo.draw.AiDrawVo;
import com.xinyirun.scm.ai.core.service.draw.AiDrawCommentService;
import com.xinyirun.scm.ai.core.service.draw.AiDrawService;
import com.xinyirun.scm.ai.core.service.draw.AiDrawStarService;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI绘图Controller
 *
 * 提供AI图片生成、编辑、评论等功能的REST API
 *
 * @author SCM-AI Team
 */
@Slf4j
@Tag(name = "AI绘图管理")
@RestController
@RequestMapping("/api/v1/ai/draw")
@Validated
public class DrawController {

    @Resource
    private AiDrawService drawService;

    @Resource
    private AiDrawStarService drawStarService;

    @Resource
    private AiDrawCommentService drawCommentService;

    /**
     * 文本生成图片
     *
     * @param prompt         正向提示词
     * @param negativePrompt 反向提示词
     * @param modelId        模型ID
     * @param size           尺寸
     * @param quality        质量
     * @param number         生成数量
     * @param seed           种子
     * @param userId         用户ID
     * @return 绘图UUID
     */
    @Operation(summary = "文本生成图片")
    @PostMapping("/generation")
    @SysLogAnnotion("AI文本生成图片")
    public ResponseEntity<JsonResultAo<Map<String, String>>> generation(
            @RequestParam @NotBlank String prompt,
            @RequestParam(required = false) String negativePrompt,
            @RequestParam @NotNull Long modelId,
            @RequestParam(defaultValue = "1024x1024") String size,
            @RequestParam(defaultValue = "standard") String quality,
            @RequestParam(defaultValue = "1") @Min(1) Integer number,
            @RequestParam(required = false) Long seed,
            @RequestParam @NotNull Long userId) {
        String drawUuid = drawService.generateByPrompt(userId, prompt, negativePrompt, modelId, size, quality, number, seed);
        return ResponseEntity.ok().body(ResultUtil.OK(Map.of("uuid", drawUuid)));
    }

    /**
     * 重新生成失败的图片
     *
     * @param drawUuid 绘图UUID
     * @param userId   用户ID
     * @return 成功响应
     */
    @Operation(summary = "重新生成失败的图片")
    @PostMapping("/regenerate/{drawUuid}")
    @SysLogAnnotion("重新生成AI图片")
    public ResponseEntity<JsonResultAo<Boolean>> regenerate(
            @PathVariable @NotBlank String drawUuid,
            @RequestParam @NotNull Long userId) {
        drawService.regenerate(drawUuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 编辑图片
     *
     * @param originalDrawUuid 原始绘图UUID
     * @param maskImgUrl       遮罩图URL
     * @param prompt           提示词
     * @param modelId          模型ID
     * @param size             尺寸
     * @param number           生成数量
     * @param userId           用户ID
     * @return 绘图UUID
     */
    @Operation(summary = "编辑图片")
    @PostMapping("/edit")
    @SysLogAnnotion("编辑AI图片")
    public ResponseEntity<JsonResultAo<Map<String, String>>> edit(
            @RequestParam @NotBlank String originalDrawUuid,
            @RequestParam(required = false) String maskImgUrl,
            @RequestParam @NotBlank String prompt,
            @RequestParam @NotNull Long modelId,
            @RequestParam(defaultValue = "1024x1024") String size,
            @RequestParam(defaultValue = "1") @Min(1) Integer number,
            @RequestParam @NotNull Long userId) {
        String drawUuid = drawService.editImage(userId, originalDrawUuid, maskImgUrl, prompt, modelId, size, number);
        return ResponseEntity.ok().body(ResultUtil.OK(Map.of("uuid", drawUuid)));
    }

    /**
     * 图片变体(图生图)
     *
     * @param originalDrawUuid 原始绘图UUID
     * @param modelId          模型ID
     * @param number           生成数量
     * @param userId           用户ID
     * @return 绘图UUID
     */
    @Operation(summary = "图片变体")
    @PostMapping("/variation")
    @SysLogAnnotion("AI图片变体生成")
    public ResponseEntity<JsonResultAo<Map<String, String>>> variation(
            @RequestParam @NotBlank String originalDrawUuid,
            @RequestParam @NotNull Long modelId,
            @RequestParam(defaultValue = "1") @Min(1) Integer number,
            @RequestParam @NotNull Long userId) {
        String drawUuid = drawService.variationImage(userId, originalDrawUuid, modelId, number);
        return ResponseEntity.ok().body(ResultUtil.OK(Map.of("uuid", drawUuid)));
    }

    /**
     * 获取我的绘图列表
     *
     * @param userId      用户ID
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 绘图列表
     */
    @Operation(summary = "获取我的绘图列表")
    @GetMapping("/list")
    @SysLogAnnotion("获取我的绘图列表")
    public ResponseEntity<JsonResultAo<Page<AiDrawVo>>> list(
            @RequestParam @NotNull Long userId,
            @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        Page<AiDrawVo> page = drawService.listMine(userId, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 获取公开的绘图列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 绘图列表
     */
    @Operation(summary = "获取公开的绘图列表")
    @GetMapping("/public/list")
    @SysLogAnnotion("获取公开的绘图列表")
    public ResponseEntity<JsonResultAo<Page<AiDrawVo>>> publicList(
            @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        Page<AiDrawVo> page = drawService.listPublic(currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 获取绘图详情
     *
     * @param drawUuid 绘图UUID
     * @param userId   用户ID(可选)
     * @return 绘图详情
     */
    @Operation(summary = "获取绘图详情")
    @GetMapping("/detail/{drawUuid}")
    @SysLogAnnotion("获取绘图详情")
    public ResponseEntity<JsonResultAo<AiDrawVo>> getDetail(
            @PathVariable @NotBlank String drawUuid,
            @RequestParam(required = false) Long userId) {
        AiDrawVo vo = drawService.getDetail(drawUuid, userId);
        if (vo == null) {
            throw new RuntimeException("绘图任务不存在或无权访问");
        }
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 获取下一条公开图片
     *
     * @param drawUuid 当前绘图UUID
     * @return 下一条图片
     */
    @Operation(summary = "获取下一条公开图片")
    @GetMapping("/detail/newer-public/{drawUuid}")
    @SysLogAnnotion("获取下一条公开图片")
    public ResponseEntity<JsonResultAo<AiDrawVo>> newerPublic(@PathVariable @NotBlank String drawUuid) {
        AiDrawVo vo = drawService.newerPublic(drawUuid);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 获取上一条公开图片
     *
     * @param drawUuid 当前绘图UUID
     * @return 上一条图片
     */
    @Operation(summary = "获取上一条公开图片")
    @GetMapping("/detail/older-public/{drawUuid}")
    @SysLogAnnotion("获取上一条公开图片")
    public ResponseEntity<JsonResultAo<AiDrawVo>> olderPublic(@PathVariable @NotBlank String drawUuid) {
        AiDrawVo vo = drawService.olderPublic(drawUuid);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 获取我的下一条图片
     *
     * @param drawUuid 当前绘图UUID
     * @param userId   用户ID
     * @return 下一条图片
     */
    @Operation(summary = "获取我的下一条图片")
    @GetMapping("/detail/newer-mine/{drawUuid}")
    @SysLogAnnotion("获取我的下一条图片")
    public ResponseEntity<JsonResultAo<AiDrawVo>> newerMine(
            @PathVariable @NotBlank String drawUuid,
            @RequestParam @NotNull Long userId) {
        AiDrawVo vo = drawService.newerMine(drawUuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 获取我的上一条图片
     *
     * @param drawUuid 当前绘图UUID
     * @param userId   用户ID
     * @return 上一条图片
     */
    @Operation(summary = "获取我的上一条图片")
    @GetMapping("/detail/older-mine/{drawUuid}")
    @SysLogAnnotion("获取我的上一条图片")
    public ResponseEntity<JsonResultAo<AiDrawVo>> olderMine(
            @PathVariable @NotBlank String drawUuid,
            @RequestParam @NotNull Long userId) {
        AiDrawVo vo = drawService.olderMine(drawUuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 设置公开/私有
     *
     * @param drawUuid      绘图UUID
     * @param isPublic      是否公开
     * @param withWatermark 是否带水印
     * @param userId        用户ID
     * @return 更新后的绘图信息
     */
    @Operation(summary = "设置公开/私有")
    @PostMapping("/set-public/{drawUuid}")
    @SysLogAnnotion("设置绘图公开/私有")
    public ResponseEntity<JsonResultAo<AiDrawVo>> setPublic(
            @PathVariable @NotBlank String drawUuid,
            @RequestParam(defaultValue = "false") Boolean isPublic,
            @RequestParam(required = false) Boolean withWatermark,
            @RequestParam @NotNull Long userId) {
        AiDrawVo vo = drawService.setPublic(drawUuid, userId, isPublic, withWatermark);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 删除绘图任务
     *
     * @param drawUuid 绘图UUID
     * @param userId   用户ID
     * @return 是否成功
     */
    @Operation(summary = "删除绘图任务")
    @PostMapping("/del/{drawUuid}")
    @SysLogAnnotion("删除绘图任务")
    public ResponseEntity<JsonResultAo<Boolean>> delete(
            @PathVariable @NotBlank String drawUuid,
            @RequestParam @NotNull Long userId) {
        boolean success = drawService.delete(drawUuid, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }

    /**
     * 切换点赞状态
     *
     * @param drawId 绘图ID
     * @param userId 用户ID
     * @return 当前是否已点赞
     */
    @Operation(summary = "切换点赞状态")
    @PostMapping("/star/toggle")
    @SysLogAnnotion("切换绘图点赞状态")
    public ResponseEntity<JsonResultAo<Boolean>> toggleStar(
            @RequestParam @NotNull Long drawId,
            @RequestParam @NotNull Long userId) {
        boolean isStarred = drawStarService.toggle(drawId, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(isStarred));
    }

    /**
     * 添加评论
     *
     * @param drawId 绘图ID
     * @param userId 用户ID
     * @param remark 评论内容
     * @return 评论信息
     */
    @Operation(summary = "添加评论")
    @PostMapping("/comment/add")
    @SysLogAnnotion("添加绘图评论")
    public ResponseEntity<JsonResultAo<AiDrawCommentVo>> addComment(
            @RequestParam @NotNull Long drawId,
            @RequestParam @NotNull Long userId,
            @RequestParam @NotBlank String remark) {
        AiDrawCommentVo vo = drawCommentService.add(drawId, userId, remark);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 分页查询评论
     *
     * @param drawId      绘图ID
     * @param currentPage 当前页
     * @param pageSize    每页数量
     * @return 评论列表
     */
    @Operation(summary = "分页查询评论")
    @GetMapping("/comment/list")
    @SysLogAnnotion("查询绘图评论列表")
    public ResponseEntity<JsonResultAo<Page<AiDrawCommentVo>>> listComments(
            @RequestParam @NotNull Long drawId,
            @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        Page<AiDrawCommentVo> page = drawCommentService.listByPage(drawId, currentPage, pageSize);
        return ResponseEntity.ok().body(ResultUtil.OK(page));
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     * @return 是否成功
     */
    @Operation(summary = "删除评论")
    @PostMapping("/comment/del/{commentId}")
    @SysLogAnnotion("删除绘图评论")
    public ResponseEntity<JsonResultAo<Boolean>> deleteComment(
            @PathVariable @NotNull Long commentId,
            @RequestParam @NotNull Long userId) {
        boolean success = drawCommentService.delete(commentId, userId);
        return ResponseEntity.ok().body(ResultUtil.OK(success));
    }
}
