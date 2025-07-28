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
 * 盘点操作明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_check_operate_detail")
public class BCheckOperateDetailEntity implements Serializable {

    private static final long serialVersionUID = 1440469178715046833L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 盘点单id
     */
    @TableField("check_operate_id")
    private Integer check_operate_id;

    /**
     * 规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 库存id
     */
    @TableField("inventory_id")
    private Integer inventory_id;


    /**
     * 库存重量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 盘点重量
     */
    @TableField("qty_check")
    private BigDecimal qty_check;

    /**
     * 盘盈盘亏
     */
    @TableField("qty_diff")
    private BigDecimal qty_diff;


}
