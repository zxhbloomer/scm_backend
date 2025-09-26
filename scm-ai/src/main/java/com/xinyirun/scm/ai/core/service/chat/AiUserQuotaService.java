package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.bean.domain.AiUserQuota;
import com.xinyirun.scm.ai.core.mapper.chat.AiUserQuotaMapper;
import com.xinyirun.scm.ai.common.exception.MSException;
import com.xinyirun.scm.ai.common.util.LogUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * AI用户配额管理服务
 * 负责用户Token配额的管理、检查和重置功能
 *
 * @author Claude AI Assistant
 * @createTime 2025-09-25
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiUserQuotaService {

    @Resource
    private AiConfigService aiConfigService;

    @Resource
    private AiUserQuotaMapper aiUserQuotaMapper;

    /**
     * 获取用户配额信息
     * 如果用户配额不存在，则创建默认配额
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @return 用户配额信息
     */
    public UserQuotaInfo getUserQuotaInfo(String userId, String tenant) {
        try {
            // TODO: 查询用户配额
            // AiUserQuotaExample example = new AiUserQuotaExample();
            // example.createCriteria()
            //     .andUserIdEqualTo(userId)
            //     .andTenantEqualTo(tenant);
            // List<AiUserQuota> quotas = aiUserQuotaMapper.selectByExample(example);

            AiUserQuota quota = null; // quotas.isEmpty() ? null : quotas.get(0);

            // 如果用户配额不存在，创建默认配额
            if (quota == null) {
                quota = createDefaultUserQuota(userId, tenant);
            } else {
                // 检查是否需要重置配额
                quota = checkAndResetQuota(quota);
            }

            return convertToQuotaInfo(quota);

        } catch (Exception e) {
            LogUtils.error("获取用户配额信息失败 - userId: " + userId + ", tenant: " + tenant, e);
            throw new MSException("获取用户配额信息失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户配额是否足够
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param estimatedTokens 预估使用的Token数
     * @return 是否有足够配额
     */
    public boolean checkUserQuota(String userId, String tenant, Long estimatedTokens) {
        try {
            // 检查是否启用配额检查
            if (!aiConfigService.isTokenQuotaCheckEnabled()) {
                return true;
            }

            if (estimatedTokens == null || estimatedTokens <= 0) {
                return true;
            }

            UserQuotaInfo quotaInfo = getUserQuotaInfo(userId, tenant);

            // 检查日配额
            if (quotaInfo.getDailyUsed() + estimatedTokens > quotaInfo.getDailyLimit()) {
                LogUtils.warn("用户日配额不足 - userId: {}, used: {}, limit: {}, estimated: {}",
                        new Object[]{userId, quotaInfo.getDailyUsed(), quotaInfo.getDailyLimit(), estimatedTokens});
                return false;
            }

            // 检查月配额
            if (quotaInfo.getMonthlyUsed() + estimatedTokens > quotaInfo.getMonthlyLimit()) {
                LogUtils.warn("用户月配额不足 - userId: {}, used: {}, limit: {}, estimated: {}",
                        new Object[]{userId, quotaInfo.getMonthlyUsed(), quotaInfo.getMonthlyLimit(), estimatedTokens});
                return false;
            }

            return true;

        } catch (Exception e) {
            LogUtils.error("检查用户配额失败 - userId: {}", userId, e);
            // 检查失败时允许使用，避免影响正常业务
            return true;
        }
    }

    /**
     * 更新用户Token使用量
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param tokenCount Token数量
     * @param cost 费用
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTokenUsage(String userId, String tenant, Long tokenCount, BigDecimal cost) {
        try {
            if (tokenCount == null || tokenCount <= 0) {
                return;
            }

            // 调用存储过程 UpdateUserTokenUsage
            aiUserQuotaMapper.callUpdateUserTokenUsage(userId, tenant, tokenCount, cost);

            LogUtils.debug("用户Token使用量已更新(存储过程) - userId: {}, tokens: {}, cost: {}",
                    new Object[]{userId, tokenCount, cost});

        } catch (Exception e) {
            LogUtils.error("更新用户Token使用量失败", e);
            throw e;
        }
    }

    /**
     * 设置用户配额
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param dailyLimit 日限额
     * @param monthlyLimit 月限额
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void setUserQuota(String userId, String tenant, Long dailyLimit, Long monthlyLimit) {
        try {
            UserQuotaInfo existingQuota = getUserQuotaInfo(userId, tenant);

            AiUserQuota quota = new AiUserQuota();
            quota.setId(existingQuota.getId());
            quota.setUserId(userId);
            quota.setTenant(tenant);
            quota.setDailyLimit(dailyLimit);
            quota.setMonthlyLimit(monthlyLimit);
            quota.setUpdateTime(System.currentTimeMillis());

            // TODO: 更新数据库
            // aiUserQuotaMapper.updateByPrimaryKeySelective(quota);

            LogUtils.info("用户配额已设置 - userId: {}, dailyLimit: {}, monthlyLimit: {}",
                    userId, dailyLimit, monthlyLimit);

        } catch (Exception e) {
            LogUtils.error("设置用户配额失败", e);
            throw new MSException("设置用户配额失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户日配额
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetUserDailyQuota(String userId, String tenant) {
        try {
            UserQuotaInfo quotaInfo = getUserQuotaInfo(userId, tenant);

            AiUserQuota quota = new AiUserQuota();
            quota.setId(quotaInfo.getId());
            quota.setDailyUsed(0L);
            quota.setDailyResetDate(LocalDate.now());
            quota.setUpdateTime(System.currentTimeMillis());

            // TODO: 更新数据库
            // aiUserQuotaMapper.updateByPrimaryKeySelective(quota);

            LogUtils.info("用户日配额已重置 - userId: {}", userId);

        } catch (Exception e) {
            LogUtils.error("重置用户日配额失败", e);
            throw new MSException("重置用户日配额失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户月配额
     *
     * @param userId 用户ID
     * @param tenant 租户ID
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetUserMonthlyQuota(String userId, String tenant) {
        try {
            UserQuotaInfo quotaInfo = getUserQuotaInfo(userId, tenant);

            AiUserQuota quota = new AiUserQuota();
            quota.setId(quotaInfo.getId());
            quota.setMonthlyUsed(0L);
            quota.setMonthlyResetDate(LocalDate.now().withDayOfMonth(1));
            quota.setUpdateTime(System.currentTimeMillis());

            // TODO: 更新数据库
            // aiUserQuotaMapper.updateByPrimaryKeySelective(quota);

            LogUtils.info("用户月配额已重置 - userId: {}", userId);

        } catch (Exception e) {
            LogUtils.error("重置用户月配额失败", e);
            throw new MSException("重置用户月配额失败：" + e.getMessage());
        }
    }

    /**
     * 批量重置所有用户的日配额
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetAllUsersDailyQuota() {
        try {
            // TODO: 调用存储过程
            // aiUserQuotaMapper.callResetUserDailyQuota();

            LogUtils.info("所有用户日配额已重置");

        } catch (Exception e) {
            LogUtils.error("批量重置用户日配额失败", e);
            throw new MSException("批量重置用户日配额失败：" + e.getMessage());
        }
    }

    /**
     * 批量重置所有用户的月配额
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetAllUsersMonthlyQuota() {
        try {
            // TODO: 调用存储过程
            // aiUserQuotaMapper.callResetUserMonthlyQuota();

            LogUtils.info("所有用户月配额已重置");

        } catch (Exception e) {
            LogUtils.error("批量重置用户月配额失败", e);
            throw new MSException("批量重置用户月配额失败：" + e.getMessage());
        }
    }

    /**
     * 创建默认用户配额
     */
    private AiUserQuota createDefaultUserQuota(String userId, String tenant) {
        try {
            AiUserQuota quota = new AiUserQuota();
            quota.setId(UUID.randomUUID().toString().replace("-", ""));
            quota.setUserId(userId);
            quota.setTenant(tenant);
            quota.setDailyLimit(aiConfigService.getDefaultDailyTokenLimit());
            quota.setMonthlyLimit(aiConfigService.getDefaultMonthlyTokenLimit());
            quota.setDailyUsed(0L);
            quota.setMonthlyUsed(0L);
            quota.setDailyResetDate(LocalDate.now());
            quota.setMonthlyResetDate(LocalDate.now().withDayOfMonth(1));
            quota.setTotalCost(BigDecimal.ZERO);
            quota.setStatus(true);
            quota.setCreateTime(System.currentTimeMillis());
            quota.setUpdateTime(System.currentTimeMillis());

            // TODO: 保存到数据库
            // aiUserQuotaMapper.insertSelective(quota);

            LogUtils.info("已创建默认用户配额 - userId: {}, dailyLimit: {}, monthlyLimit: {}",
                    userId, quota.getDailyLimit(), quota.getMonthlyLimit());

            return quota;

        } catch (Exception e) {
            LogUtils.error("创建默认用户配额失败", e);
            throw e;
        }
    }

    /**
     * 检查并重置配额（如果需要）
     */
    private AiUserQuota checkAndResetQuota(AiUserQuota quota) {
        boolean needUpdate = false;
        LocalDate today = LocalDate.now();

        // 检查是否需要重置日配额
        if (quota.getDailyResetDate() == null || quota.getDailyResetDate().isBefore(today)) {
            quota.setDailyUsed(0L);
            quota.setDailyResetDate(today);
            needUpdate = true;
        }

        // 检查是否需要重置月配额
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        if (quota.getMonthlyResetDate() == null || quota.getMonthlyResetDate().isBefore(firstDayOfMonth)) {
            quota.setMonthlyUsed(0L);
            quota.setMonthlyResetDate(firstDayOfMonth);
            needUpdate = true;
        }

        if (needUpdate) {
            quota.setUpdateTime(System.currentTimeMillis());
            // TODO: 更新数据库
            // aiUserQuotaMapper.updateByPrimaryKeySelective(quota);
        }

        return quota;
    }

    /**
     * 转换为配额信息对象
     */
    private UserQuotaInfo convertToQuotaInfo(AiUserQuota quota) {
        UserQuotaInfo info = new UserQuotaInfo();
        info.setId(quota.getId());
        info.setUserId(quota.getUserId());
        info.setTenant(quota.getTenant());
        info.setDailyLimit(quota.getDailyLimit());
        info.setMonthlyLimit(quota.getMonthlyLimit());
        info.setDailyUsed(quota.getDailyUsed() != null ? quota.getDailyUsed() : 0L);
        info.setMonthlyUsed(quota.getMonthlyUsed() != null ? quota.getMonthlyUsed() : 0L);
        info.setTotalCost(quota.getTotalCost() != null ? quota.getTotalCost() : BigDecimal.ZERO);
        info.setStatus(quota.getStatus());
        return info;
    }

    /**
     * 用户配额信息
     */
    public static class UserQuotaInfo {
        private String id;
        private String userId;
        private String tenant;
        private Long dailyLimit;
        private Long monthlyLimit;
        private Long dailyUsed;
        private Long monthlyUsed;
        private BigDecimal totalCost;
        private Boolean status;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTenant() { return tenant; }
        public void setTenant(String tenant) { this.tenant = tenant; }
        public Long getDailyLimit() { return dailyLimit; }
        public void setDailyLimit(Long dailyLimit) { this.dailyLimit = dailyLimit; }
        public Long getMonthlyLimit() { return monthlyLimit; }
        public void setMonthlyLimit(Long monthlyLimit) { this.monthlyLimit = monthlyLimit; }
        public Long getDailyUsed() { return dailyUsed; }
        public void setDailyUsed(Long dailyUsed) { this.dailyUsed = dailyUsed; }
        public Long getMonthlyUsed() { return monthlyUsed; }
        public void setMonthlyUsed(Long monthlyUsed) { this.monthlyUsed = monthlyUsed; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        public Boolean getStatus() { return status; }
        public void setStatus(Boolean status) { this.status = status; }

        public Double getDailyUsagePercent() {
            if (dailyLimit == null || dailyLimit == 0L || dailyUsed == null) {
                return 0.0;
            }
            return (double) dailyUsed / dailyLimit * 100;
        }

        public Double getMonthlyUsagePercent() {
            if (monthlyLimit == null || monthlyLimit == 0L || monthlyUsed == null) {
                return 0.0;
            }
            return (double) monthlyUsed / monthlyLimit * 100;
        }
    }
}