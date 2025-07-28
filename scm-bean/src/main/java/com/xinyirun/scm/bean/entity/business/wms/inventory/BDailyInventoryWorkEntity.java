package com.xinyirun.scm.bean.entity.business.wms.inventory;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_daily_inventory_work")
public class BDailyInventoryWorkEntity implements Serializable {

    private static final long serialVersionUID = -8592100112125995360L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 日期
     */
    @TableField("dt")
    private LocalDateTime dt;

    /**
     * 类型：1、入库；2、出库；3、调整
     */
    @TableField("type")
    private String type;

    /**
     * 单据类型：按实际单据来判断；实际单据（入库单、出库单、调整单）
     */
    @TableField("bill_type")
    private String bill_type;

    /**
     * 计划id
     */
    @TableField("plan_id")
    private Integer plan_id;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主code
     */
    @TableField("owner_code")
    private String owner_code;

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
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 库位id
     */
    @TableField("bin_id")
    private Integer bin_id;

    /**
     * 库存数量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 移动平均单价
     */
    @TableField("average_price")
    private BigDecimal average_price;

    /**
     * 当时间节点实际库存
     */
    @TableField("inventory_qty")
    private BigDecimal inventory_qty;

    /**
     * 库存货值
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 更新时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;


}
