package com.xinyirun.scm.bean.entity.business.check;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 盘盈盘亏明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_check_result_detail")
public class BCheckResultDetailEntity implements Serializable {

    private static final long serialVersionUID = 2659728174133841509L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 盘盈盘亏单id
     */
    @TableField("check_result_id")
    private Integer check_result_id;

    /**
     * 库存id
     */
    @TableField("inventory_id")
    private Integer inventory_id;

    /**
     * 规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格code
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 库存重量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 盘点库存重量
     */
    @TableField("qty_check")
    private BigDecimal qty_check;

    /**
     * 盘盈盘亏
     */
    @TableField("qty_diff")
    private BigDecimal qty_diff;

}
