package com.xinyirun.scm.ai.bean.entity.statistics;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * AI用户配额实体类
 * 对应数据表：ai_user_quota
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_user_quota")
public class AiUserQuotaEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @DataChangeLabelAnnotation("配额记录ID")
    private String id;

    @TableField("user_id")
    @DataChangeLabelAnnotation("用户ID")
    private String user_id;

    @TableField("tenant")
    @DataChangeLabelAnnotation("租户ID")
    private String tenant;

    @TableField("daily_limit")
    @DataChangeLabelAnnotation("日Token限额")
    private Long daily_limit;

    @TableField("monthly_limit")
    @DataChangeLabelAnnotation("月Token限额")
    private Long monthly_limit;

    @TableField("daily_used")
    @DataChangeLabelAnnotation("当日已使用Token数")
    private Long daily_used;

    @TableField("monthly_used")
    @DataChangeLabelAnnotation("当月已使用Token数")
    private Long monthly_used;

    @TableField("daily_reset_date")
    @DataChangeLabelAnnotation("日配额重置日期")
    private LocalDate daily_reset_date;

    @TableField("monthly_reset_date")
    @DataChangeLabelAnnotation("月配额重置日期")
    private LocalDate monthly_reset_date;

    @TableField("total_cost")
    @DataChangeLabelAnnotation("累计费用（美元）")
    private BigDecimal total_cost;

    @TableField("status")
    @DataChangeLabelAnnotation("状态（1启用，0禁用）")
    private Boolean status;

    @TableField("create_time")
    @DataChangeLabelAnnotation("创建时间")
    private Long create_time;

    @TableField("update_time")
    @DataChangeLabelAnnotation("更新时间")
    private Long update_time;
}
