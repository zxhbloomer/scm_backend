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
 * 稻谷 加工日报表
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("r_product_daily_a")
public class RProductDailyAEntity implements Serializable {

    private static final long serialVersionUID = -7979802113711040002L;

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
     * 定向入库
     */
    @TableField("in_qty")
    private BigDecimal in_qty;

    /**
     * 惨混/加工使用 ( 领料出库的 )
     */
    @TableField("product_qty")
    private BigDecimal product_qty;

    /**
     * 出库数量 ( 稻壳, 监管出库, 调拨出库的 )
     */
    @TableField("out_qty")
    private BigDecimal out_qty;

    /**
     * 生成报表时的稻壳库存
     */
    @TableField("inventory_qty")
    private BigDecimal inventory_qty;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;


}
