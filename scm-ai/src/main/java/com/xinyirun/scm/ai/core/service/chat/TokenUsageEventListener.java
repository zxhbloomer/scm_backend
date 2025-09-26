package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.domain.AiTokenUsage;
import com.xinyirun.scm.ai.bean.event.LlmTokenUsageEvent;
import com.xinyirun.scm.ai.common.util.LogUtils;
import com.xinyirun.scm.ai.core.mapper.chat.AiTokenUsageMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Token使用事件监听器
 * 异步处理Token使用统计、配额管理和告警
 *
 * @author zxh
 * @createTime 2025-09-25
 */
@Component
@Slf4j
public class TokenUsageEventListener {

    @Resource
    private AiUserQuotaService aiUserQuotaService;

    @Resource
    private AiConfigService aiConfigService;

    @Resource
    private AiTokenUsageMapper aiTokenUsageMapper;

    /**
     * 处理Token使用事件
     * 异步执行，不影响主业务流程
     *
     * @param event Token使用事件
     */
    @EventListener
    @Async("aiAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRED)
    public void handleTokenUsageEvent(LlmTokenUsageEvent event) {
        try {
            LogUtils.debug("处理Token使用事件开始 - conversationId: {}, userId: {}",
                    event.getConversationId(), event.getUserId());

            // 1. 保存Token使用记录
            saveTokenUsageRecord(event);

            // 2. 更新用户配额
            updateUserQuota(event);

            // 3. 检查配额告警
            checkQuotaAlerts(event);

            // 4. 更新统计数据（可选，也可以通过定时任务处理）
            // updateStatistics(event);

            LogUtils.info("Token使用事件处理完成 - conversationId: {}, userId: {}, totalTokens: {}",
                    event.getConversationId(), event.getUserId(), event.getTotalTokens());

        } catch (Exception e) {
            LogUtils.error("处理Token使用事件失败", e);
            // 异步处理失败不抛出异常，记录日志即可
        }
    }

    /**
     * 保存Token使用记录到数据库
     */
    private void saveTokenUsageRecord(LlmTokenUsageEvent event) {
        try {
            AiTokenUsage tokenUsage = new AiTokenUsage();
            tokenUsage.setId(UUID.randomUUID().toString().replace("-", ""));
            tokenUsage.setConversationId(event.getConversationId());
            tokenUsage.setModelSourceId(event.getModelSourceId());
            tokenUsage.setUserId(event.getUserId());
            tokenUsage.setTenant(event.getTenant());
            tokenUsage.setAiProvider(event.getAiProvider());
            tokenUsage.setAiModelType(event.getAiModelType());
            tokenUsage.setPromptTokens(event.getPromptTokens() != null ? event.getPromptTokens() : 0L);
            tokenUsage.setCompletionTokens(event.getCompletionTokens() != null ? event.getCompletionTokens() : 0L);
            // total_tokens字段由数据库自动计算，不需要手动设置
            // 成本计算已移除，只保留Token统计功能
            tokenUsage.setSuccess(event.getSuccess() != null ? event.getSuccess() : true);
            tokenUsage.setResponseTime(event.getResponseTime() != null ? event.getResponseTime() : 0L);
            tokenUsage.setCreateTime(event.getCreateTime());

            // 保存到数据库
            aiTokenUsageMapper.insertSelective(tokenUsage);

            LogUtils.debug("Token使用记录已保存 - id: {}, totalTokens: {}",
                    tokenUsage.getId(), tokenUsage.getTotalTokens());

        } catch (Exception e) {
            LogUtils.error("保存Token使用记录失败", e);
            throw e;
        }
    }

    /**
     * 更新用户配额使用情况
     */
    private void updateUserQuota(LlmTokenUsageEvent event) {
        try {
            // 检查是否启用配额管理
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                LogUtils.debug("Token配额检查已禁用，跳过配额更新");
                return;
            }

            Long totalTokens = event.getTotalTokens();
            if (totalTokens == null || totalTokens <= 0) {
                LogUtils.debug("Token数量为0，跳过配额更新");
                return;
            }

            // 调用配额服务更新用户使用量
            aiUserQuotaService.updateTokenUsage(
                    event.getUserId(),
                    event.getTenant(),
                    totalTokens,
                    event.getCost()
            );

            LogUtils.debug("用户配额已更新 - userId: {}, tokens: {}, cost: {}",
                    new Object[]{event.getUserId(), totalTokens, event.getCost()});

        } catch (Exception e) {
            LogUtils.error("更新用户配额失败", e);
            throw e;
        }
    }

    /**
     * 检查配额告警
     */
    private void checkQuotaAlerts(LlmTokenUsageEvent event) {
        try {
            // 检查是否启用配额管理
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                return;
            }

            // 获取用户配额信息
            AiUserQuotaService.UserQuotaInfo quotaInfo = aiUserQuotaService.getUserQuotaInfo(
                    event.getUserId(), event.getTenant());

            if (quotaInfo == null) {
                return;
            }

            // 检查日配额告警（80%警告，90%严重警告）
            double dailyUsagePercent = quotaInfo.getDailyUsagePercent();
            if (dailyUsagePercent >= 90.0) {
                sendQuotaAlert(event.getUserId(), "DAILY_QUOTA_CRITICAL", dailyUsagePercent);
            } else if (dailyUsagePercent >= 80.0) {
                sendQuotaAlert(event.getUserId(), "DAILY_QUOTA_WARNING", dailyUsagePercent);
            }

            // 检查月配额告警
            double monthlyUsagePercent = quotaInfo.getMonthlyUsagePercent();
            if (monthlyUsagePercent >= 90.0) {
                sendQuotaAlert(event.getUserId(), "MONTHLY_QUOTA_CRITICAL", monthlyUsagePercent);
            } else if (monthlyUsagePercent >= 80.0) {
                sendQuotaAlert(event.getUserId(), "MONTHLY_QUOTA_WARNING", monthlyUsagePercent);
            }

        } catch (Exception e) {
            LogUtils.error("检查配额告警失败", e);
            // 告警检查失败不抛出异常
        }
    }

    /**
     * 发送配额告警
     */
    private void sendQuotaAlert(String userId, String alertType, double usagePercent) {
        try {
            // TODO: 实现告警发送逻辑
            // 可以发送邮件、短信、系统通知等
            LogUtils.warn("配额告警 - userId: {}, alertType: {}, usagePercent: {}%",
                    new Object[]{userId, alertType, String.format("%.2f", usagePercent)});

            // 示例：发送系统通知
            // notificationService.sendNotification(userId, alertType, usagePercent);

        } catch (Exception e) {
            LogUtils.error("发送配额告警失败", e);
        }
    }

    /**
     * 更新统计数据（可选实现）
     */
    private void updateStatistics(LlmTokenUsageEvent event) {
        try {
            // TODO: 更新ai_token_statistics表的聚合数据
            // 这里可以实现实时统计更新，或者通过定时任务批量处理

            LogUtils.debug("统计数据更新 - userId: {}, provider: {}, model: {}",
                    new Object[]{event.getUserId(), event.getAiProvider(), event.getAiModelType()});

        } catch (Exception e) {
            LogUtils.error("更新统计数据失败", e);
        }
    }
}