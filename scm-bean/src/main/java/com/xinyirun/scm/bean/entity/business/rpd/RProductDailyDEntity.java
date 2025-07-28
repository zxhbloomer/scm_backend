package com.xinyirun.scm.bean.entity.business.rpd;

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
 * 混合物 加工日报表
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("r_product_daily_d")
public class RProductDailyDEntity implements Serializable {

    private static final long serialVersionUID = -324443312352726162L;

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
     * 入库数量(掺混) (稻谷小麦混合物，糙米玉米混合物，糙米小麦混合物，稻谷玉米混合物 )  生产入库
     */
    @TableField("in_qty")
    private BigDecimal in_qty;

    /**
     * 出库数量 (稻谷小麦混合物，糙米玉米混合物，糙米小麦混合物，稻谷玉米混合物,,,    销售出库、监管出库、调拨出库  )
     */
    @TableField("out_qty")
    private BigDecimal out_qty;

    /**
     * 损耗 ( 杂质,  生产入库)
     */
    @TableField("loss_qty")
    private BigDecimal loss_qty;

    /**
     * 剩余数量  稻谷小麦混合物，糙米玉米混合物，糙米小麦混合物，稻谷玉米混合物 库存
     */
    @TableField("residue_qty")
    private BigDecimal residue_qty;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;


}
