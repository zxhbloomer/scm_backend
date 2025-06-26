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
 * 玉米 加工日报表
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("r_product_daily_c")
public class RProductDailyCEntity implements Serializable {

    private static final long serialVersionUID = -4640478929625721066L;

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
     * 入库数量 ( 玉米, 监管入库, 采购入库 )
     */
    @TableField("in_qty")
    private BigDecimal in_qty;

    /**
     * 掺混数量 ( 玉米, 领料出库)
     */
    @TableField("cost_qty")
    private BigDecimal cost_qty;

    /**
     * 在生成报表时，玉米在该仓库的实时库存
     */
    @TableField("inventory_qty")
    private BigDecimal inventory_qty;

    /**
     * 掺混比例 ( 把第一个玉米生产入库.审批通过.入库单，商品玉米使用的配比显示出来  )
     */
    @TableField("router")
    private BigDecimal router;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;


}
