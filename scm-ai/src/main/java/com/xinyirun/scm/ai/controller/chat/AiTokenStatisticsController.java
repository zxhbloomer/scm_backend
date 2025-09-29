package com.xinyirun.scm.ai.controller.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.ai.bean.vo.statistics.AiTokenStatisticsVo;
import com.xinyirun.scm.ai.bean.vo.statistics.AiTokenUsageVo;
import com.xinyirun.scm.ai.bean.vo.statistics.AiUserQuotaVo;
import com.xinyirun.scm.ai.service.AiTokenStatisticsService;
import com.xinyirun.scm.ai.service.AiTokenUsageService;
import com.xinyirun.scm.ai.service.AiUserQuotaService;
import com.xinyirun.scm.ai.service.AiConfigService;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI Token统计控制器
 * 提供Token使用统计、配额查询和管理功能的REST API接口
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Tag(name = "AI Token统计")
@RestController
@RequestMapping(value = "/api/v1/ai/statistics")
public class AiTokenStatisticsController {

    @Resource
    private AiTokenStatisticsService aiTokenStatisticsService;

    @Resource
    private AiTokenUsageService aiTokenUsageService;

    @Resource
    private AiUserQuotaService aiUserQuotaService;

    @Resource
    private AiConfigService aiConfigService;

    /**
     * 获取当前用户的Token配额信息
     */
    @GetMapping(value = "/quota")
    @Operation(summary = "获取用户Token配额")
    @SysLogAnnotion("获取Token配额")
    public ResponseEntity<AiUserQuotaVo> getUserQuota() {
        Long operatorId = SecurityUtil.getStaff_id();
        String userId = operatorId != null ? operatorId.toString() : null;
        String tenant = getCurrentTenant();

        AiUserQuotaVo result = aiUserQuotaService.getByUserId(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取指定用户的Token配额信息
     */
    @GetMapping(value = "/quota/{userId}")
    @Operation(summary = "获取指定用户Token配额")
    @SysLogAnnotion("查询用户配额")
    public ResponseEntity<AiUserQuotaVo> getUserQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId) {
        String tenant = getCurrentTenant();

        AiUserQuotaVo result = aiUserQuotaService.getByUserId(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 创建用户Token配额
     */
    @PostMapping(value = "/quota/{userId}")
    @Operation(summary = "创建用户Token配额")
    @SysLogAnnotion("创建用户配额")
    public ResponseEntity<AiUserQuotaVo> createUserQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "总配额")
            @RequestParam Long totalQuota) {

        Long operatorId = SecurityUtil.getStaff_id();
        String tenant = getCurrentTenant();

        AiUserQuotaVo quotaVo = new AiUserQuotaVo();
        quotaVo.setUser_id(userId);
        quotaVo.setTotal_quota(totalQuota);

        AiUserQuotaVo result = aiUserQuotaService.createQuota(quotaVo, operatorId);
        return ResponseEntity.ok(result);
    }

    /**
     * 重置用户配额
     */
    @PostMapping(value = "/quota/{userId}/reset")
    @Operation(summary = "重置用户配额")
    @SysLogAnnotion("重置用户配额")
    public ResponseEntity<Boolean> resetUserQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "新配额")
            @RequestParam Long newQuota) {

        Long operatorId = SecurityUtil.getStaff_id();
        String tenant = getCurrentTenant();

        boolean result = aiUserQuotaService.resetQuota(userId, newQuota, tenant, operatorId);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查用户配额是否充足
     */
    @GetMapping(value = "/quota/{userId}/check")
    @Operation(summary = "检查用户配额")
    @SysLogAnnotion("检查配额充足")
    public ResponseEntity<Boolean> checkUserQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "预估Token数")
            @RequestParam(defaultValue = "1000") Long estimatedTokens) {

        String tenant = getCurrentTenant();
        boolean result = aiUserQuotaService.checkQuotaSufficient(userId, estimatedTokens, tenant);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取Token使用记录
     */
    @GetMapping(value = "/usage")
    @Operation(summary = "获取Token使用记录")
    @SysLogAnnotion("获取使用记录")
    public ResponseEntity<IPage<AiTokenUsageVo>> getTokenUsage(
            @Parameter(description = "用户ID") @RequestParam(required = false) String userId,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") int pageSize) {

        String tenant = getCurrentTenant();

        // 如果没有指定用户ID，使用当前用户
        if (userId == null) {
            Long operatorId = SecurityUtil.getStaff_id();
            userId = operatorId != null ? operatorId.toString() : null;
        }

        IPage<AiTokenUsageVo> result = aiTokenUsageService.getByUserAndTimeRange(
                userId, startTime, endTime, tenant, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取对话Token使用记录
     */
    @GetMapping(value = "/usage/conversation/{conversationId}")
    @Operation(summary = "获取对话Token使用记录")
    @SysLogAnnotion("获取对话使用记录")
    public ResponseEntity<IPage<AiTokenUsageVo>> getConversationTokenUsage(
            @Parameter(description = "对话ID", required = true)
            @PathVariable Integer conversationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") int pageSize) {

        IPage<AiTokenUsageVo> result = aiTokenUsageService.getByConversationId(conversationId, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户每日统计数据
     */
    @GetMapping(value = "/daily/{userId}")
    @Operation(summary = "获取用户每日统计")
    @SysLogAnnotion("获取每日统计")
    public ResponseEntity<List<Map<String, Object>>> getUserDailyStatistics(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "开始日期") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String tenant = getCurrentTenant();
        List<Map<String, Object>> result = aiTokenStatisticsService.getUserDailyStatistics(
                userId, startDate, endDate, tenant);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取模型使用排行榜
     */
    @GetMapping(value = "/ranking/models")
    @Operation(summary = "获取模型使用排行榜")
    @SysLogAnnotion("获取模型排行")
    public ResponseEntity<List<Map<String, Object>>> getModelRanking(
            @Parameter(description = "开始日期") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {

        String tenant = getCurrentTenant();
        List<Map<String, Object>> result = aiTokenStatisticsService.getModelRanking(
                startDate, endDate, tenant, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户使用排行榜
     */
    @GetMapping(value = "/ranking/users")
    @Operation(summary = "获取用户使用排行榜")
    @SysLogAnnotion("获取用户排行")
    public ResponseEntity<List<Map<String, Object>>> getUserRanking(
            @Parameter(description = "开始日期") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束日期") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {

        String tenant = getCurrentTenant();
        List<Map<String, Object>> result = aiTokenStatisticsService.getUserRanking(
                startDate, endDate, tenant, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取配额即将用尽的用户
     */
    @GetMapping(value = "/quota/warning")
    @Operation(summary = "获取配额预警用户")
    @SysLogAnnotion("获取配额预警")
    public ResponseEntity<IPage<AiUserQuotaVo>> getQuotaWarningUsers(
            @Parameter(description = "阈值百分比") @RequestParam(defaultValue = "0.8") Double threshold,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") int pageSize) {

        String tenant = getCurrentTenant();
        IPage<AiUserQuotaVo> result = aiUserQuotaService.getUsersNearQuotaLimit(threshold, tenant, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * 消费用户配额
     */
    @PostMapping(value = "/quota/{userId}/consume")
    @Operation(summary = "消费用户配额")
    @SysLogAnnotion("消费用户配额")
    public ResponseEntity<Boolean> consumeQuota(
            @Parameter(description = "用户ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "消费Token数量")
            @RequestParam Long consumeTokens) {

        Long operatorId = SecurityUtil.getStaff_id();
        String tenant = getCurrentTenant();

        boolean result = aiUserQuotaService.consumeQuota(userId, consumeTokens, tenant, operatorId);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前租户ID
     * TODO: 实现获取当前租户的逻辑
     */
    private String getCurrentTenant() {
        // 这里需要根据实际的租户获取逻辑来实现
        // 可能从ThreadLocal、Session、请求头等获取
        return "default";
    }
}