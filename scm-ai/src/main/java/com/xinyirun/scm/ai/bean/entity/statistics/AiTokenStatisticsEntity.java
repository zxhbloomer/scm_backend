package com.xinyirun.scm.ai.bean.entity.statistics;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI Token统计实体类
 * 对应数据表：ai_token_statistics
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_token_statistics")
public class AiTokenStatisticsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    @DataChangeLabelAnnotation("用户ID")
    private String user_id;

    @TableField("model_source_id")
    @DataChangeLabelAnnotation("模型源ID")
    private Integer model_source_id;

    @TableField("total_prompt_tokens")
    @DataChangeLabelAnnotation("输入Token数")
    private Integer total_prompt_tokens;

    @TableField("total_completion_tokens")
    @DataChangeLabelAnnotation("输出Token数")
    private Integer total_completion_tokens;

    @TableField("total_tokens")
    @DataChangeLabelAnnotation("总Token数")
    private Integer total_tokens;

    @TableField("statistics_date")
    @DataChangeLabelAnnotation("统计日期")
    private LocalDateTime statistics_date;

    @TableField("tenant")
    @DataChangeLabelAnnotation("租户")
    private String tenant;

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
