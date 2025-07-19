package com.xinyirun.scm.bean.entity.busniess.wms.inplan;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库计划附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_in_plan_attach")
public class BInPlanAttachEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 4461891518579466718L;

    /**
     * 主键id
     */    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 入库计划id
     */
    @TableField(value = "in_plan_id")
    private Integer in_plan_id;

    /**
     * 入库计划附件
     */
    @TableField(value = "one_file")
    private Integer one_file;    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value = "dbversion")
    private Integer dbversion;

}
