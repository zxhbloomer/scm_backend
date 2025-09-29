package com.xinyirun.scm.ai.bean.vo.statistics;

import com.xinyirun.scm.ai.validation.groups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AiUserQuotaVo implements Serializable {

    @Schema(description = "配额记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_user_quota.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_user_quota.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_user_quota.user_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{ai_user_quota.user_id.length_range}", groups = {Created.class, Updated.class})
    private String userId;


    @Schema(description = "日Token限额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_user_quota.daily_limit.not_null}", groups = {Created.class})
    @Min(value = 0, message = "{ai_user_quota.daily_limit.min}", groups = {Created.class, Updated.class})
    private Long dailyLimit;

    @Schema(description = "月Token限额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_user_quota.monthly_limit.not_null}", groups = {Created.class})
    @Min(value = 0, message = "{ai_user_quota.monthly_limit.min}", groups = {Created.class, Updated.class})
    private Long monthlyLimit;

    @Schema(description = "当日已使用Token数")
    @Min(value = 0, message = "{ai_user_quota.daily_used.min}", groups = {Created.class, Updated.class})
    private Long dailyUsed;

    @Schema(description = "当月已使用Token数")
    @Min(value = 0, message = "{ai_user_quota.monthly_used.min}", groups = {Created.class, Updated.class})
    private Long monthlyUsed;

    @Schema(description = "日配额重置日期")
    private LocalDate dailyResetDate;

    @Schema(description = "月配额重置日期")
    private LocalDate monthlyResetDate;

    @Schema(description = "累计费用（美元）")
    @DecimalMin(value = "0.0", message = "{ai_user_quota.total_cost.min}", groups = {Created.class, Updated.class})
    private BigDecimal totalCost;

    @Schema(description = "状态（1启用，0禁用）")
    private Boolean status;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "更新时间")
    private Long updateTime;

    private static final long serialVersionUID = 1L;

    // 兼容方法，用于保持与原有调用的一致性

    /**
     * 设置用户ID（兼容方法）
     */
    public void setUser_id(String userId) {
        this.userId = userId;
    }

    /**
     * 获取用户ID（兼容方法）
     */
    public String getUser_id() {
        return this.userId;
    }

    /**
     * 设置总配额（兼容方法，映射到月限额）
     */
    public void setTotal_quota(Long totalQuota) {
        this.monthlyLimit = totalQuota;
    }

    /**
     * 获取总配额（兼容方法，返回月限额）
     */
    public Long getTotal_quota() {
        return this.monthlyLimit;
    }
}