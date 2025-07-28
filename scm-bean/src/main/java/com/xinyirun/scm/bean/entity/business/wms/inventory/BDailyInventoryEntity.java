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
 * 每日库存表
 * </p>
 *
 * @author wwl
 * @since 2022-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_daily_inventory")
public class BDailyInventoryEntity implements Serializable {

    private static final long serialVersionUID = -4043484553052909385L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 时间
     */
    @TableField(value="dt")
    private LocalDateTime dt;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主编码
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 委托方id
     */
    @TableField("consignor_id")
    private Integer consignor_id;

    /**
     * 委托方编码
     */
    @TableField("consignor_code")
    private String consignor_code;

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
     * 物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编码
     */
    @TableField("sku_code")
    private String sku_code;

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
     * 实际数量
     */
    @TableField("qty_adjust")
    private BigDecimal qty_adjust;

    /**
     * 调整量
     */
    @TableField("actual_weight")
    private BigDecimal actual_weight;

    /**
     * 移动加权货值单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 库存货值
     */
    @TableField("inventory_amount")
    private BigDecimal inventory_amount;

    /**
     * 实时单价
     */
    @TableField("realtime_price")
    private BigDecimal realtime_price;

    /**
     * 实时货值
     */
    @TableField("realtime_amount")
    private BigDecimal realtime_amount;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

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
}
