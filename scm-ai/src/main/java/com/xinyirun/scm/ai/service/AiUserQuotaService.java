package com.xinyirun.scm.ai.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.statistics.AiUserQuotaEntity;
import com.xinyirun.scm.ai.bean.vo.statistics.AiUserQuotaVo;
import com.xinyirun.scm.ai.mapper.statistics.AiUserQuotaMapper;
import com.xinyirun.scm.ai.common.exception.MSException;
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
 * @author zxh
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
            // 查询用户配额
            AiUserQuotaEntity quota = aiUserQuotaMapper.selectByUserIdAndTenant(userId);

            // 如果用户配额不存在，创建默认配额
            if (quota == null) {
                quota = createDefaultUserQuota(userId);
            } else {
                // 检查是否需要重置配额
                quota = checkAndResetQuota(quota);
            }

            return convertToQuotaInfo(quota);

        } catch (Exception e) {
            log.error("获取用户配额信息失败 - userId: " + userId + ", tenant: " + tenant, e);
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
                log.warn("用户日配额不足 - userId: {}, used: {}, limit: {}, estimated: {}",
                        new Object[]{userId, quotaInfo.getDailyUsed(), quotaInfo.getDailyLimit(), estimatedTokens});
                return false;
            }

            // 检查月配额
            if (quotaInfo.getMonthlyUsed() + estimatedTokens > quotaInfo.getMonthlyLimit()) {
                log.warn("用户月配额不足 - userId: {}, used: {}, limit: {}, estimated: {}",
                        new Object[]{userId, quotaInfo.getMonthlyUsed(), quotaInfo.getMonthlyLimit(), estimatedTokens});
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("检查用户配额失败 - userId: {}", userId, e);
            // 检查失败时允许使用，避免影响正常业务
            return true;
        }
    }

    /**
     * 更新用户Token使用量
     *
     * @param userId 用户ID
     * @param tokenCount Token数量
     * @param cost 费用
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTokenUsage(String userId, Long tokenCount, BigDecimal cost) {
        try {
            if (tokenCount == null || tokenCount <= 0) {
                return;
            }

            // 调用存储过程 UpdateUserTokenUsage
            aiUserQuotaMapper.callUpdateUserTokenUsage(userId,  tokenCount, cost);

            log.debug("用户Token使用量已更新(存储过程) - userId: {}, tokens: {}, cost: {}",
                    new Object[]{userId, tokenCount, cost});

        } catch (Exception e) {
            log.error("更新用户Token使用量失败", e);
            throw e;
        }
    }

    /**
     * 设置用户配额
     *
     * @param userId 用户ID
     * @param dailyLimit 日限额
     * @param monthlyLimit 月限额
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void setUserQuota(String userId, Long dailyLimit, Long monthlyLimit) {
        try {
            UserQuotaInfo existingQuota = getUserQuotaInfo(userId, null);

            AiUserQuotaEntity quota = new AiUserQuotaEntity();
            quota.setId(existingQuota.getId());
            quota.setUser_id(userId);
            quota.setDaily_limit(dailyLimit);
            quota.setMonthly_limit(monthlyLimit);
            // 注意：u_time 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT_UPDATE) 会自动处理修改时间

            // 更新数据库
            aiUserQuotaMapper.updateById(quota);

            log.info("用户配额已设置 - userId: {}, dailyLimit: {}, monthlyLimit: {}",
                    userId, dailyLimit, monthlyLimit);

        } catch (Exception e) {
            log.error("设置用户配额失败", e);
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

            AiUserQuotaEntity quota = new AiUserQuotaEntity();
            quota.setId(quotaInfo.getId());
            quota.setDaily_used(0L);
            quota.setDaily_reset_date(LocalDate.now());
            // 注意：u_time 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT_UPDATE) 会自动处理修改时间

            // 更新数据库
            aiUserQuotaMapper.updateById(quota);

            log.info("用户日配额已重置 - userId: {}", userId);

        } catch (Exception e) {
            log.error("重置用户日配额失败", e);
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

            AiUserQuotaEntity quota = new AiUserQuotaEntity();
            quota.setId(quotaInfo.getId());
            quota.setMonthly_used(0L);
            quota.setMonthly_reset_date(LocalDate.now().withDayOfMonth(1));
            // 注意：u_time 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT_UPDATE) 会自动处理修改时间

            // 更新数据库
            aiUserQuotaMapper.updateById(quota);

            log.info("用户月配额已重置 - userId: {}", userId);

        } catch (Exception e) {
            log.error("重置用户月配额失败", e);
            throw new MSException("重置用户月配额失败：" + e.getMessage());
        }
    }

    /**
     * 批量重置所有用户的日配额
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetAllUsersDailyQuota() {
        try {
            // 调用存储过程
            aiUserQuotaMapper.callResetUserDailyQuota();

            log.info("所有用户日配额已重置");

        } catch (Exception e) {
            log.error("批量重置用户日配额失败", e);
            throw new MSException("批量重置用户日配额失败：" + e.getMessage());
        }
    }

    /**
     * 批量重置所有用户的月配额
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetAllUsersMonthlyQuota() {
        try {
            // 调用存储过程
            aiUserQuotaMapper.callResetUserMonthlyQuota();

            log.info("所有用户月配额已重置");

        } catch (Exception e) {
            log.error("批量重置用户月配额失败", e);
            throw new MSException("批量重置用户月配额失败：" + e.getMessage());
        }
    }

    /**
     * 创建默认用户配额
     */
    private AiUserQuotaEntity createDefaultUserQuota(String userId) {
        try {
            AiUserQuotaEntity quota = new AiUserQuotaEntity();
            quota.setId(UUID.randomUUID().toString().replace("-", ""));
            quota.setUser_id(userId);
            quota.setDaily_limit(aiConfigService.getDefaultDailyTokenLimit());
            quota.setMonthly_limit(aiConfigService.getDefaultMonthlyTokenLimit());
            quota.setDaily_used(0L);
            quota.setMonthly_used(0L);
            quota.setDaily_reset_date(LocalDate.now());
            quota.setMonthly_reset_date(LocalDate.now().withDayOfMonth(1));
            quota.setTotal_cost(BigDecimal.ZERO);
            quota.setStatus(true);
            // 注意：c_time、u_time、c_id、u_id 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT/INSERT_UPDATE) 会自动处理审计字段

            // 保存到数据库
            aiUserQuotaMapper.insert(quota);

            log.info("已创建默认用户配额 - userId: {}, dailyLimit: {}, monthlyLimit: {}",
                    userId, quota.getDaily_limit(), quota.getMonthly_limit());

            return quota;

        } catch (Exception e) {
            log.error("创建默认用户配额失败", e);
            throw e;
        }
    }

    /**
     * 检查并重置配额（如果需要）
     */
    private AiUserQuotaEntity checkAndResetQuota(AiUserQuotaEntity quota) {
        boolean needUpdate = false;
        LocalDate today = LocalDate.now();

        // 检查是否需要重置日配额
        if (quota.getDaily_reset_date() == null || quota.getDaily_reset_date().isBefore(today)) {
            quota.setDaily_used(0L);
            quota.setDaily_reset_date(today);
            needUpdate = true;
        }

        // 检查是否需要重置月配额
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        if (quota.getMonthly_reset_date() == null || quota.getMonthly_reset_date().isBefore(firstDayOfMonth)) {
            quota.setMonthly_used(0L);
            quota.setMonthly_reset_date(firstDayOfMonth);
            needUpdate = true;
        }

        if (needUpdate) {
            // 注意：u_time 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT_UPDATE) 会自动处理修改时间
            // 更新数据库
            aiUserQuotaMapper.updateById(quota);
        }

        return quota;
    }

    /**
     * 转换为配额信息对象
     */
    private UserQuotaInfo convertToQuotaInfo(AiUserQuotaEntity quota) {
        UserQuotaInfo info = new UserQuotaInfo();
        info.setId(quota.getId());
        info.setUserId(quota.getUser_id());
        info.setDailyLimit(quota.getDaily_limit() != null ? quota.getDaily_limit() : 0L);
        info.setMonthlyLimit(quota.getMonthly_limit() != null ? quota.getMonthly_limit() : 0L);
        info.setDailyUsed(quota.getDaily_used() != null ? quota.getDaily_used() : 0L);
        info.setMonthlyUsed(quota.getMonthly_used() != null ? quota.getMonthly_used() : 0L);
        info.setTotalCost(quota.getTotal_cost() != null ? quota.getTotal_cost() : BigDecimal.ZERO);
        info.setStatus(quota.getStatus());
        return info;
    }

    /**
     * 根据用户ID获取配额信息
     *
     * @param userId 用户ID
     * @return 用户配额VO
     */
    public AiUserQuotaVo getByUserId(String userId) {
        try {
            UserQuotaInfo quotaInfo = getUserQuotaInfo(userId, null);

            AiUserQuotaVo vo = new AiUserQuotaVo();
            vo.setUserId(userId);
            vo.setDailyLimit(quotaInfo.getDailyLimit());
            vo.setMonthlyLimit(quotaInfo.getMonthlyLimit());
            vo.setDailyUsed(quotaInfo.getDailyUsed());
            vo.setMonthlyUsed(quotaInfo.getMonthlyUsed());
            vo.setDailyResetDate(quotaInfo.getDailyResetDate());
            vo.setMonthlyResetDate(quotaInfo.getMonthlyResetDate());
            vo.setTotalCost(quotaInfo.getTotalCost());
            vo.setStatus(true);

            return vo;
        } catch (Exception e) {
            log.error("根据用户ID获取配额信息失败, userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 创建用户配额
     *
     * @param quotaVo 配额VO
     * @param operatorId 操作员ID
     * @return 创建的配额VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiUserQuotaVo createQuota(AiUserQuotaVo quotaVo, Long operatorId) {
        try {
            Long dailyLimit = quotaVo.getDailyLimit() != null ? quotaVo.getDailyLimit() : quotaVo.getTotal_quota();
            Long monthlyLimit = quotaVo.getMonthlyLimit() != null ? quotaVo.getMonthlyLimit() : quotaVo.getTotal_quota();

            setUserQuota(quotaVo.getUserId(), dailyLimit, monthlyLimit);

            return getByUserId(quotaVo.getUserId());
        } catch (Exception e) {
            log.error("创建用户配额失败", e);
            throw new RuntimeException("创建用户配额失败", e);
        }
    }

    /**
     * 重置用户配额
     *
     * @param userId 用户ID
     * @param newQuota 新配额
     * @param tenant 租户ID
     * @param operatorId 操作员ID
     * @return 重置结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean resetQuota(String userId, Long newQuota, String tenant, Long operatorId) {
        try {
            setUserQuota(userId, newQuota, newQuota);
            return true;
        } catch (Exception e) {
            log.error("重置用户配额失败, userId: {}, newQuota: {}", userId, newQuota, e);
            return false;
        }
    }

    /**
     * 检查配额是否充足
     *
     * @param userId 用户ID
     * @param estimatedTokens 预估Token数
     * @param tenant 租户ID
     * @return 是否充足
     */
    public boolean checkQuotaSufficient(String userId, Long estimatedTokens, String tenant) {
        try {
            return checkUserQuota(userId, tenant, estimatedTokens);
        } catch (Exception e) {
            log.error("检查配额充足性失败, userId: {}, estimatedTokens: {}", userId, estimatedTokens, e);
            return true; // 检查失败时允许使用
        }
    }

    /**
     * 获取接近配额限制的用户列表
     *
     * @param threshold 阈值
     * @param tenant 租户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 用户列表
     */
    public IPage<AiUserQuotaVo> getUsersNearQuotaLimit(Double threshold, String tenant, int pageNum, int pageSize) {
        try {
            // 创建分页对象
            Page<AiUserQuotaVo> page = new Page<>(pageNum, pageSize);

            // 简单实现，返回空分页结果
            // TODO: 实际业务逻辑应该查询接近配额限制的用户
            page.setTotal(0);
            page.setRecords(new java.util.ArrayList<>());

            return page;
        } catch (Exception e) {
            log.error("获取接近配额限制的用户失败", e);
            // 返回空分页结果
            Page<AiUserQuotaVo> emptyPage = new Page<>(pageNum, pageSize);
            emptyPage.setTotal(0);
            emptyPage.setRecords(new java.util.ArrayList<>());
            return emptyPage;
        }
    }

    /**
     * 消费用户配额
     *
     * @param userId 用户ID
     * @param tokenCount Token数量
     * @param tenant 租户ID
     * @param operatorId 操作员ID
     * @return 消费结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean consumeQuota(String userId, Long tokenCount, String tenant, Long operatorId) {
        try {
            updateTokenUsage(userId, tokenCount, BigDecimal.ZERO);
            return true;
        } catch (Exception e) {
            log.error("消费用户配额失败, userId: {}, tokenCount: {}", userId, tokenCount, e);
            return false;
        }
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
        private LocalDate dailyResetDate;
        private LocalDate monthlyResetDate;
        private BigDecimal totalCost;
        private Boolean status;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTenant() { return tenant; }
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
        public LocalDate getDailyResetDate() { return dailyResetDate; }
        public void setDailyResetDate(LocalDate dailyResetDate) { this.dailyResetDate = dailyResetDate; }
        public LocalDate getMonthlyResetDate() { return monthlyResetDate; }
        public void setMonthlyResetDate(LocalDate monthlyResetDate) { this.monthlyResetDate = monthlyResetDate; }

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