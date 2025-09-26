package com.xinyirun.scm.ai.config.scheduler;

import com.xinyirun.scm.ai.core.service.chat.AiConfigService;
import com.xinyirun.scm.ai.core.service.chat.AiUserQuotaService;
import com.xinyirun.scm.ai.common.util.LogUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 配额重置定时任务调度器
 * 负责自动重置用户的日配额和月配额
 *
 * @author zxh
 * @createTime 2025-09-25
 */
@Component
@EnableScheduling
@ConditionalOnProperty(name = "scm.ai.scheduler.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class QuotaResetScheduler {

    @Resource
    private AiUserQuotaService aiUserQuotaService;

    @Resource
    private AiConfigService aiConfigService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 每日凌晨1点重置所有用户的日配额
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void resetDailyQuotas() {
        try {
            // 检查是否启用配额管理
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                LogUtils.debug("Token配额检查已禁用，跳过日配额重置");
                return;
            }

            LogUtils.info("开始执行日配额重置任务");
            long startTime = System.currentTimeMillis();

            // 调用服务层重置所有用户日配额
            aiUserQuotaService.resetAllUsersDailyQuota();

            long endTime = System.currentTimeMillis();
            LogUtils.info("日配额重置任务完成，耗时: {}ms", endTime - startTime);

        } catch (Exception e) {
            LogUtils.error("日配额重置任务执行失败", e);
        }
    }

    /**
     * 每月1日凌晨2点重置所有用户的月配额
     * cron表达式：每月1日凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void resetMonthlyQuotas() {
        try {
            // 检查是否启用配额管理
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                LogUtils.debug("Token配额检查已禁用，跳过月配额重置");
                return;
            }

            LogUtils.info("开始执行月配额重置任务");
            long startTime = System.currentTimeMillis();

            // 调用服务层重置所有用户月配额
            aiUserQuotaService.resetAllUsersMonthlyQuota();

            long endTime = System.currentTimeMillis();
            LogUtils.info("月配额重置任务完成，耗时: {}ms", endTime - startTime);

        } catch (Exception e) {
            LogUtils.error("月配额重置任务执行失败", e);
        }
    }

    /**
     * 每小时检查并重置需要重置的用户配额
     * 处理因系统故障等原因错过重置的用户
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void checkAndResetExpiredQuotas() {
        try {
            // 检查是否启用配额管理
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                return;
            }

            LogUtils.debug("开始检查需要重置的过期配额");

            // 检查并重置过期的日配额
            checkAndResetExpiredDailyQuotas();

            // 检查并重置过期的月配额
            checkAndResetExpiredMonthlyQuotas();

        } catch (Exception e) {
            LogUtils.error("检查过期配额任务执行失败", e);
        }
    }

    /**
     * 检查并重置过期的日配额
     */
    private void checkAndResetExpiredDailyQuotas() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String sql = """
                SELECT user_id, tenant
                FROM ai_user_quota
                WHERE daily_reset_date != ? OR daily_reset_date IS NULL
                """;

            List<Map<String, Object>> expiredUsers = jdbcTemplate.queryForList(sql, today);

            if (!expiredUsers.isEmpty()) {
                LogUtils.info("发现{}个用户的日配额需要重置", expiredUsers.size());

                for (Map<String, Object> user : expiredUsers) {
                    String userId = (String) user.get("user_id");
                    String tenant = (String) user.get("tenant");

                    try {
                        aiUserQuotaService.resetUserDailyQuota(userId, tenant);
                        LogUtils.debug("重置用户日配额成功: userId={}, tenant={}", userId, tenant);
                    } catch (Exception e) {
                        LogUtils.error("重置用户日配额失败: ", e);
                    }
                }
            } else {
                LogUtils.debug("所有用户的日配额都是最新的");
            }

        } catch (Exception e) {
            LogUtils.error("检查过期日配额失败", e);
        }
    }

    /**
     * 检查并重置过期的月配额
     */
    private void checkAndResetExpiredMonthlyQuotas() {
        try {
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

            String sql = """
                SELECT user_id, tenant
                FROM ai_user_quota
                WHERE monthly_reset_date < ? OR monthly_reset_date IS NULL
                """;

            // 检查是否需要重置月配额（如果最后重置时间不是本月）
            String firstDayOfMonth = currentMonth + "-01";
            List<Map<String, Object>> expiredUsers = jdbcTemplate.queryForList(sql, firstDayOfMonth);

            if (!expiredUsers.isEmpty()) {
                LogUtils.info("发现{}个用户的月配额需要重置", expiredUsers.size());

                for (Map<String, Object> user : expiredUsers) {
                    String userId = (String) user.get("user_id");
                    String tenant = (String) user.get("tenant");

                    try {
                        aiUserQuotaService.resetUserMonthlyQuota(userId, tenant);
                        LogUtils.debug("重置用户月配额成功: userId={}, tenant={}", userId, tenant);
                    } catch (Exception e) {
                        LogUtils.error("重置用户月配额失败:",  e);
                    }
                }
            } else {
                LogUtils.debug("所有用户的月配额都是最新的");
            }

        } catch (Exception e) {
            LogUtils.error("检查过期月配额失败", e);
        }
    }

    /**
     * 每天凌晨3点清理过期的Token使用记录
     * 保留最近30天的详细记录，删除更早的记录以节省存储空间
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredTokenUsage() {
        try {
            // 检查是否启用数据清理
            Boolean cleanupEnabled = aiConfigService.getBooleanConfig("token.cleanup.enabled", false);
            if (!cleanupEnabled) {
                LogUtils.debug("Token使用记录清理已禁用");
                return;
            }

            LogUtils.info("开始清理过期的Token使用记录");
            long startTime = System.currentTimeMillis();

            // 获取保留天数配置
            Integer retentionDays = aiConfigService.getIntegerConfig("token.retention.days", 30);

            // 计算截止时间戳（保留天数之前的记录将被删除）
            long cutoffTimestamp = System.currentTimeMillis() - (retentionDays * 24L * 60L * 60L * 1000L);

            // 执行清理操作
            String deleteSql = """
                DELETE FROM ai_token_usage
                WHERE create_time < ?
                """;

            int deletedCount = jdbcTemplate.update(deleteSql, cutoffTimestamp);

            if (deletedCount > 0) {
                LogUtils.info("清理了{}条过期的Token使用记录，保留天数: {}", deletedCount, retentionDays);
            } else {
                LogUtils.debug("没有需要清理的过期Token使用记录");
            }

            long endTime = System.currentTimeMillis();
            LogUtils.info("Token使用记录清理完成，耗时: {}ms", endTime - startTime);

        } catch (Exception e) {
            LogUtils.error("Token使用记录清理任务执行失败", e);
        }
    }

    /**
     * 每周日凌晨4点生成Token使用统计报告
     */
    @Scheduled(cron = "0 0 4 ? * SUN")
    public void generateWeeklyReport() {
        try {
            // 检查是否启用报告生成
            Boolean reportEnabled = aiConfigService.getBooleanConfig("token.report.enabled", true);
            if (!reportEnabled) {
                LogUtils.debug("Token统计报告生成已禁用");
                return;
            }

            LogUtils.info("开始生成周度Token使用统计报告");
            long startTime = System.currentTimeMillis();

            // 生成周度统计报告
            generateAndLogWeeklyStatistics();

            long endTime = System.currentTimeMillis();
            LogUtils.info("周度Token使用统计报告生成完成，耗时: {}ms", endTime - startTime);

        } catch (Exception e) {
            LogUtils.error("周度统计报告生成任务执行失败", e);
        }
    }

    /**
     * 系统启动时的初始化检查
     * 检查是否有需要立即处理的配额重置
     */
    @Scheduled(initialDelay = 60000, fixedDelay = Long.MAX_VALUE) // 启动1分钟后执行一次
    public void initialQuotaCheck() {
        try {
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                return;
            }

            LogUtils.info("执行系统启动时的配额检查");

            // 检查并处理启动时可能遗漏的配额重置
            checkAndResetExpiredQuotas();

            LogUtils.info("系统启动配额检查完成");

        } catch (Exception e) {
            LogUtils.error("系统启动配额检查失败", e);
        }
    }

    /**
     * 生成并记录周度统计信息
     */
    private void generateAndLogWeeklyStatistics() {
        try {
            // 获取本周的开始和结束时间
            LocalDate now = LocalDate.now();
            LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
            LocalDate weekEnd = weekStart.plusDays(6);

            long weekStartTimestamp = weekStart.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            long weekEndTimestamp = weekEnd.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();

            // 查询本周统计数据
            String statisticsSql = """
                SELECT
                    COUNT(*) as total_requests,
                    SUM(CASE WHEN is_success = 1 THEN 1 ELSE 0 END) as success_requests,
                    SUM(total_tokens) as total_tokens,
                    SUM(cost) as total_cost,
                    COUNT(DISTINCT user_id) as active_users,
                    COUNT(DISTINCT ai_model_type) as used_models,
                    AVG(response_time_ms) as avg_response_time
                FROM ai_token_usage
                WHERE create_time >= ? AND create_time < ?
                """;

            Map<String, Object> weekStats = jdbcTemplate.queryForMap(statisticsSql, weekStartTimestamp, weekEndTimestamp);

            // 查询模型使用分布
            String modelDistributionSql = """
                SELECT
                    ai_model_type,
                    COUNT(*) as request_count,
                    SUM(total_tokens) as tokens,
                    SUM(cost) as cost
                FROM ai_token_usage
                WHERE create_time >= ? AND create_time < ?
                GROUP BY ai_model_type
                ORDER BY request_count DESC
                """;

            List<Map<String, Object>> modelStats = jdbcTemplate.queryForList(modelDistributionSql, weekStartTimestamp, weekEndTimestamp);

            // 记录周度报告到日志
            LogUtils.info("=== 周度Token使用统计报告 ({} - {}) ===", weekStart, weekEnd);
            LogUtils.info("总请求数: {}", weekStats.get("total_requests"));
            LogUtils.info("成功请求数: {}", weekStats.get("success_requests"));
            LogUtils.info("总Token数: {}", weekStats.get("total_tokens"));
            LogUtils.info("总成本: {} 美元", weekStats.get("total_cost"));
            LogUtils.info("活跃用户数: {}", weekStats.get("active_users"));
            LogUtils.info("使用模型数: {}", weekStats.get("used_models"));
            LogUtils.info("平均响应时间: {} ms", weekStats.get("avg_response_time"));

            LogUtils.info("--- 模型使用分布 ---");
            for (Map<String, Object> modelStat : modelStats) {
                LogUtils.info("模型: {}, 请求数: {}, Token数: {}, 成本: {} 美元",
                    modelStat.get("ai_model_type"),
                    modelStat.get("request_count"),
                    modelStat.get("tokens"),
                    modelStat.get("cost"));
            }

            // 更新统计表（可选，用于历史数据保存）
            updateWeeklyStatisticsTable(weekStart, weekEnd, weekStats, modelStats);

        } catch (Exception e) {
            LogUtils.error("生成周度统计报告失败", e);
        }
    }

    /**
     * 更新周度统计表
     */
    private void updateWeeklyStatisticsTable(LocalDate weekStart, LocalDate weekEnd,
                                           Map<String, Object> weekStats,
                                           List<Map<String, Object>> modelStats) {
        try {
            // 构造统计期间标识
            String periodKey = weekStart.format(DateTimeFormatter.ofPattern("yyyy-ww")) + "_WEEK";

            // 插入或更新周度统计记录
            String upsertSql = """
                INSERT INTO ai_token_statistics (
                    id, period_type, period_key, period_start, period_end,
                    total_requests, success_requests, total_tokens, total_cost,
                    unique_users, unique_models, avg_response_time, create_time
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    total_requests = VALUES(total_requests),
                    success_requests = VALUES(success_requests),
                    total_tokens = VALUES(total_tokens),
                    total_cost = VALUES(total_cost),
                    unique_users = VALUES(unique_users),
                    unique_models = VALUES(unique_models),
                    avg_response_time = VALUES(avg_response_time)
                """;

            String statisticsId = java.util.UUID.randomUUID().toString();
            long periodStartTimestamp = weekStart.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            long periodEndTimestamp = weekEnd.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();

            jdbcTemplate.update(upsertSql,
                statisticsId, "WEEK", periodKey, periodStartTimestamp, periodEndTimestamp,
                weekStats.get("total_requests"), weekStats.get("success_requests"),
                weekStats.get("total_tokens"), weekStats.get("total_cost"),
                weekStats.get("active_users"), weekStats.get("used_models"),
                weekStats.get("avg_response_time"), System.currentTimeMillis());

            LogUtils.debug("周度统计数据已保存到数据库，统计ID: {}", statisticsId);

        } catch (Exception e) {
            LogUtils.error("保存周度统计数据失败", e);
        }
    }
}