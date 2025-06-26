package com.xinyirun.scm.bean.entity.busniess.rtwo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 生产管理_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_rt_wo_product")
public class BRtWoProductEntity implements Serializable {

    private static final long serialVersionUID = 253624021844230221L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    /**
     * 产品类型 1.产成品, 2副产品
     */
    @TableField("type")
    private String type;

    /**
     * wo_id
     */
    @TableField("wo_id")
    private Integer wo_id;


    @TableField("sku_id")
    private Integer sku_id;

    @TableField("sku_code")
    private String sku_code;

    @TableField("wo_router")
    private BigDecimal wo_router;

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

    @TableField("b_in_plan_id")
    private Integer b_in_plan_id;

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
