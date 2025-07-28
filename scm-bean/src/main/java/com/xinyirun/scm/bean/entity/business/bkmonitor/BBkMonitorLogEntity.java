package com.xinyirun.scm.bean.entity.business.bkmonitor;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * monitor备份日志
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_bk_monitor_log")
public class BBkMonitorLogEntity implements Serializable {

    private static final long serialVersionUID = -2420168168067422487L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 查询参数
     */
    @TableField("param")
    private String param;

    /**
     * 备份开始时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * ing, ok, ng
     */
    @TableField("flag")
    private String flag;

    /**
     * 创建人
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 类型, 备份, 恢复
     */
    @TableField("type")
    private String type;

    /**
     * ng日志
     */
    @TableField("exception")
    private String exception;

    /**
     * 备份完成时间
     */
    @TableField("complete_time")
    private LocalDateTime complete_time;

    /**
     * 总条数
     */
    @TableField("count")
    private Long count;

}
