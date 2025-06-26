package com.xinyirun.scm.bean.entity.busniess.monitor;

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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wk_co_detail")
public class BWkCoDetailEntity implements Serializable {

    private static final long serialVersionUID = -9051655293139671240L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("order_id")
    private Integer order_id;

    /**
     * 序号
     */
    @TableField("no")
    private String no;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String order_no;

    /**
     * 税率
     */
    @TableField("rate")
    private BigDecimal rate;

    /**
     * 货值单价(含税)
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 数量
     */
    @TableField("num")
    private BigDecimal num;

    /**
     * 货值总金额(含税)
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 规格ID
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 规格CODE
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    private String sku_name;

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
     * 货值不含税金额
     */
    @TableField("amount_not")
    private BigDecimal amount_not;

    /**
     * 货值税款金额
     */
    @TableField("tax_amount")
    private BigDecimal tax_amount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

}
