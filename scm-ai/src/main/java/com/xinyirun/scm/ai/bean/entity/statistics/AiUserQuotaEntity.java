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
import java.time.LocalDateTime;

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
    private static final long serialVersionUID = -3389918882426974241L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @DataChangeLabelAnnotation("配额记录ID")
    private String id;

    @TableField("user_id")
    @DataChangeLabelAnnotation("用户ID")
    private String user_id;


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

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建时间", extension = "getCTimeExtension")
    private LocalDateTime c_time;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建人", extension = "getUserNameExtension")
    private Long c_id;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人", extension = "getUserNameExtension")
    private Long u_id;

    @TableField("dbversion")
    private Integer dbversion;
}
