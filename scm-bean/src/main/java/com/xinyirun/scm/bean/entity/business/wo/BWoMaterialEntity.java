package com.xinyirun.scm.bean.entity.business.wo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *   生产配方_原材料
 * </p>
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wo_material")
public class BWoMaterialEntity implements Serializable {

    private static final long serialVersionUID = 5329541188905071659L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    @TableField("wo_id")
    private Integer wo_id;

    @TableField("sku_id")
    private Integer sku_id;

    @TableField("sku_code")
    private String sku_code;

//    @TableField("goods_id")
//    private Integer goods_id;
//
//    @TableField("goods_code")
//    private String goods_code;

    @TableField("wo_router")
    private BigDecimal wo_router;

    @TableField("src_warehouse_id")
    private Integer src_warehouse_id;

    @TableField("src_warehouse_code")
    private String src_warehouse_code;

    @TableField("src_location_id")
    private Integer src_location_id;

    @TableField("src_location_code")
    private String src_location_code;

    @TableField("src_bin_id")
    private Integer src_bin_id;

    @TableField("src_bin_code")
    private String src_bin_code;

    @TableField("allocate_id")
    private Integer allocate_id;

    @TableField("wo_qty")
    private BigDecimal wo_qty;

    @TableField("warehouse_id")
    private Integer warehouse_id;

    @TableField("warehouse_code")
    private String warehouse_code;

    @TableField("location_id")
    private Integer location_id;

    @TableField("location_code")
    private String location_code;

    @TableField("bin_id")
    private Integer bin_id;

    @TableField("bin_code")
    private String bin_code;

    @TableField("unit_id")
    private Integer unit_id;

    @TableField("unit_name")
    private String unit_name;

    @TableField("b_out_plan_id")
    private Integer b_out_plan_id;

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

}
