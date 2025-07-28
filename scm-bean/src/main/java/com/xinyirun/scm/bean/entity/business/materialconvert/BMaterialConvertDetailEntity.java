package com.xinyirun.scm.bean.entity.business.materialconvert;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调拨明细
 * </p>
 *
 * @author wwl
 * @since 2022-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_material_convert_detail")
public class BMaterialConvertDetailEntity implements Serializable {

    private static final long serialVersionUID = -4261100181601899322L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 状态 0制单，状态 1已提交，2审核通过，3审核驳回，4作废
     */
    @TableField("status")
    private String status;

    /**
     * 物料转换id
     */
    @TableField("material_convert_id")
    private Integer material_convert_id;


    /**
     * 是否启用
     */
    @TableField("is_effective")
    private Boolean is_effective;

    /**
     * 原物料id
     */
    @TableField("source_sku_id")
    private Integer source_sku_id;

    /**
     * 原物料code
     */
    @TableField("source_sku_code")
    private String source_sku_code;

    /**
     * 新物料id
     */
    @TableField("target_sku_id")
    private Integer target_sku_id;

    /**
     * 新物料code
     */
    @TableField("target_sku_code")
    private String target_sku_code;

    /**
     * 转换后比例
     */
    @TableField("calc")
    private BigDecimal calc;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


}
