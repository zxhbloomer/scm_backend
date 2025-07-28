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
@TableName("b_daily_inventory_temp")
public class BDailyInventoryTempEntity implements Serializable {

    private static final long serialVersionUID = 2397120925895927427L;

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
     * 入库量
     */
    @TableField("qty_in")
    private BigDecimal qty_in;

    /**
     * 出库量
     */
    @TableField("qty_out")
    private BigDecimal qty_out;

    /**
     * 调整量
     */
    @TableField("qty_adjust")
    private BigDecimal qty_adjust;

    /**
     * 移动加权货值单价
     */
    @TableField("price")
    private BigDecimal price;

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
     * 实时货值总价
     */
    @TableField("realtime_amount")
    private BigDecimal realtime_amount;

    /**
     * 实时单价
     */
    @TableField("realtime_price")
    private BigDecimal realtime_price;

    /**
     * 更新时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

}
