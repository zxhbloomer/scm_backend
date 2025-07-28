package com.xinyirun.scm.bean.entity.business.monitor;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 备份后保存入库, 出库数量, 用于计算监管任务 已出库, 已入库数量
 *
 * @author xinyirun
 * @since 2023-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_monitor_backup")
public class BMonitorBackupEntity implements Serializable {


    private static final long serialVersionUID = 5027206686873333670L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 监管任务code
     */
    @TableField("code")
    private String code;

    @TableField("schedule_id")
    private Integer schedule_id;

    @TableField("schedule_code")
    private String schedule_code;

    @TableField("out_qty")
    private BigDecimal out_qty;

    @TableField("in_qty")
    private BigDecimal in_qty;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;
}
