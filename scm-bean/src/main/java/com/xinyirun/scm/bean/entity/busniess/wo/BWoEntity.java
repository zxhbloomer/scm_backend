package com.xinyirun.scm.bean.entity.busniess.wo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *  生产管理 表
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wo")
public class BWoEntity implements Serializable {

    private static final long serialVersionUID = -8770554992845696307L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    @TableField("type")
    private String type;

    @TableField("router_id")
    private Integer router_id;

    @TableField("owner_id")
    private Integer owner_id;

    @TableField("wc_warehouse_id")
    private Integer wc_warehouse_id;

    @TableField("wc_warehouse_code")
    private String wc_warehouse_code;

    @TableField("wc_location_id")
    private Integer wc_location_id;

    @TableField("wc_location_code")
    private String wc_location_code;

    @TableField("wc_bin_id")
    private Integer wc_bin_id;

    @TableField("wc_bin_code")
    private String wc_bin_code;

    @TableField("status")
    private String status;

    @TableField("wo_dt")
    private LocalDateTime wo_dt;

    @TableField("delivery_order_id")
    private Integer delivery_order_id;

    @TableField("delivery_order_code")
    private String delivery_order_code;

    @TableField("delivery_order_detail_id")
    private Integer delivery_order_detail_id;

    @TableField("delivery_order_detail_no")
    private Integer delivery_order_detail_no;

    @TableField("delivery_order_detail_sku_code")
    private String delivery_order_detail_sku_code;

    @TableField("delivery_order_detail_qty")
    private BigDecimal delivery_order_detail_qty;

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

    @TableField("e_time")
    private LocalDateTime e_time;

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

    @TableField("e_id")
    private Integer e_id;

    @TableField("dbversion")
    @Version
    private Integer dbversion;

    /**
     * 原材料json
     */
    @TableField("json_material_list")
    private String json_material_list;

    /**
     * 产成品、副产品json
     */
    @TableField("json_product_list")
    private String json_product_list;

    /**
     * 生产计划id
     */
    @TableField("pp_id")
    private Integer pp_id;

}
