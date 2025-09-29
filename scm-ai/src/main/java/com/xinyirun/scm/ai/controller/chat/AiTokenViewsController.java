package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.ai.bean.vo.statistics.*;
import com.xinyirun.scm.ai.service.statistics.AiTokenViewsService;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * AI Token统计视图查询控制器
 * 提供基于数据库的统计查询接口，专用于报表和分析功能
 * 使用标准MVC架构，通过Service层处理业务逻辑
 *
 * @author SCM-AI重构团队
 * @since 2025-09-29
 */
@Tag(name = "AI Token统计视图")
@RestController
@RequestMapping("/api/v1/ai/statistics/views")
@Slf4j
public class AiTokenViewsController {

    @Resource
    private AiTokenViewsService aiTokenViewsService;

    /**
     * 查询用户Token使用汇总
     */
    @Operation(summary = "查询用户Token使用汇总")
    @GetMapping("/user-summary")
    @SysLogAnnotion("查询用户Token汇总")
    public ResponseEntity<List<UserTokenSummaryVo>> getUserTokenSummary(
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId,
            @Parameter(description = "限制返回条数") @RequestParam(defaultValue = "100") Integer limit) {

        try {
            List<UserTokenSummaryVo> summaries = aiTokenViewsService.getUserTokenSummary(userId, limit);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("查询用户Token使用汇总失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 查询模型使用统计
     */
    @Operation(summary = "查询模型使用统计")
    @GetMapping("/model-stats")
    @SysLogAnnotion("查询模型使用统计")
    public ResponseEntity<List<ModelUsageStatsVo>> getModelUsageStats(
            @Parameter(description = "AI提供商") @RequestParam(required = false) String aiProvider,
            @Parameter(description = "限制返回条数") @RequestParam(defaultValue = "50") Integer limit) {

        try {
            List<ModelUsageStatsVo> stats = aiTokenViewsService.getModelUsageStats(aiProvider, limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("查询模型使用统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 查询日期统计汇总
     */
    @Operation(summary = "查询日期统计汇总")
    @GetMapping("/daily-summary")
    @SysLogAnnotion("查询日期统计汇总")
    public ResponseEntity<List<DailyTokenSummaryVo>> getDailyTokenSummary(
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "限制返回条数") @RequestParam(defaultValue = "30") Integer limit) {

        try {
            List<DailyTokenSummaryVo> summaries = aiTokenViewsService.getDailyTokenSummary(startDate, endDate, limit);
            return ResponseEntity.ok(summaries);
        } catch (Exception e) {
            log.error("查询日期统计汇总失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取Token使用趋势数据
     */
    @Operation(summary = "获取Token使用趋势")
    @GetMapping("/usage-trend")
    @SysLogAnnotion("获取使用趋势")
    public ResponseEntity<List<TokenUsageTrendVo>> getTokenUsageTrend(
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam LocalDate endDate) {

        try {
            List<TokenUsageTrendVo> trends = aiTokenViewsService.getTokenUsageTrend(startDate, endDate);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            log.error("获取Token使用趋势失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取用户排行榜
     */
    @Operation(summary = "获取用户Token使用排行榜")
    @GetMapping("/user-ranking")
    @SysLogAnnotion("获取用户排行")
    public ResponseEntity<List<UserRankingVo>> getUserRanking(
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "排行榜数量") @RequestParam(defaultValue = "10") Integer topN) {

        try {
            List<UserRankingVo> rankings = aiTokenViewsService.getUserRanking(startDate, endDate, topN);
            return ResponseEntity.ok(rankings);
        } catch (Exception e) {
            log.error("获取用户排行榜失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}