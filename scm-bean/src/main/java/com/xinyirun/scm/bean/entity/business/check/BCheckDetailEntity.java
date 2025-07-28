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
 * 盘点任务明细
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_check_detail")
public class BCheckDetailEntity implements Serializable {

    private static final long serialVersionUID = -9025134536725831879L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 盘点单id
     */
    @TableField("check_id")
    private Integer check_id;

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
     * 规格名称
     */
    @TableField("sku_name")
    private String sku_name;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goods_name;

    /**
     * 品名
     */
    @TableField("pm")
    private String pm;

    /**
     * 计量单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 库存重量
     */
    @TableField("qty")
    private BigDecimal qty;


}
