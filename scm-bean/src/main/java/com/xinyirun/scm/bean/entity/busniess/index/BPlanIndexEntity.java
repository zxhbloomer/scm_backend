package com.xinyirun.scm.bean.entity.busniess.index;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 计划序号
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_plan_index")
public class BPlanIndexEntity implements Serializable {

    private static final long serialVersionUID = 935375351446821015L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 计划单主表id
     */
    @TableField("plan_id")
    private int plan_id;

    /**
     * 类型 1：入库计划 2：出库计划
     */
    @TableField("type")
    private String type;

    /**
     * 序号
     */
    @TableField("idx")
    private int idx;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;
}
