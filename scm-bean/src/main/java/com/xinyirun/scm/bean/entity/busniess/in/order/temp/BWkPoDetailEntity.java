package com.xinyirun.scm.bean.entity.busniess.in.order.temp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wk_po_detail")
public class BWkPoDetailEntity implements Serializable {

    private static final long serialVersionUID = -7313130679656260599L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    @TableField("order_id")
    private Integer order_id;

    /**
     * 明细编号
     */
    @TableField("no")
    private String no;

    /**
     * 订单id
     */
    @TableField("order_no")
    private String order_no;

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
     * 商品名称
     */
    @TableField("sku_name")
    private String sku_name;

    /**
     * 规格code
     */
    @TableField("pm")
    private String pm;

    /**
     * 规格code
     */
    @TableField("spec")
    private String spec;

    /**
     * 单位id
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 单位code
     */
    @TableField("unit_code")
    private String unit_code;

    /**
     * 单位名称
     */
    @TableField("unit_name")
    private String unit_name;

    /**
     * 单价(含税)
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 数量
     */
    @TableField("num")
    private BigDecimal num;

    /**
     * 金额(含税)
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 税率
     */
    @TableField("rate")
    private BigDecimal rate;

    /**
     * 交货日期
     */
    @TableField("delivery_date")
    private LocalDateTime delivery_date;

    /**
     * 交货方式(1-自提;2-物流)
     */
    @TableField("delivery_type")
    private String delivery_type;


}
