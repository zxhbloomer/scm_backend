package com.xinyirun.scm.bean.entity.busniess.rpd;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 糙米 加工日报表
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("r_product_daily_b")
public class RProductDailyBEntity implements Serializable {

    private static final long serialVersionUID = -8659934917652299996L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * 日期
     */
    @TableField("date")
    private LocalDate date;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 商品ID
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 商品 code
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 加工入库 ( 糙米 生产入库 )
     */
    @TableField("in_qty")
    private BigDecimal in_qty;

    /**
     * 掺混数量 ( 糙米, 领料出库 )
     */
    @TableField("router_qty")
    private BigDecimal router_qty;

    /**
     * 出库数量 ( 糙米, 监管出库, 调拨出库)
     */
    @TableField("out_qty")
    private BigDecimal out_qty;

    @TableField("inventory_qty")
    private BigDecimal inventory_qty;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;


}
