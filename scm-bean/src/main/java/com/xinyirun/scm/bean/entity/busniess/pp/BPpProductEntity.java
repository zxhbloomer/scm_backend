package com.xinyirun.scm.bean.entity.busniess.pp;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 生产计划_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_pp_product")
public class BPpProductEntity implements Serializable {

    
    private static final long serialVersionUID = -7042301084024394774L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 类型, 1产成品, 2副产品
     */
    @TableField("type")
    private String type;

    /**
     * 生产计划id
     */
    @TableField("pp_id")
    private Integer pp_id;

    /**
     * 物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 物料code
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 配比
     */
    @TableField("pp_router")
    private BigDecimal pp_router;

    /**
     * 数量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库区code
     */
    @TableField("location_code")
    private String location_code;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 库位code
     */
    @TableField("bin_code")
    private String bin_code;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 单位名称
     */
    @TableField("unit_name")
    private String unit_name;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;


}
