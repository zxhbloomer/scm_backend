package com.xinyirun.scm.bean.entity.busniess.monitor;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2023-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_carriage_order_goods")
public class BCarriageOrderGoodsEntity implements Serializable {

    private static final long serialVersionUID = -6122732459201463588L;

    /**
     * 主键ID
     */
    @TableId("id")
    private Integer id;

    @TableField("order_id")
    private Integer orderId;

    /**
     * 序号
     */
    @TableField("no")
    private String no;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

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
    private Integer skuId;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    private Integer unitId;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contractNo;

    /**
     * 规格CODE
     */
    @TableField("sku_code")
    private String skuCode;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    private String skuName;

    /**
     * 单位code
     */
    @TableField("unit_code")
    private String unitCode;

    /**
     * 单位名称
     */
    @TableField("unit_name")
    private String unitName;

    /**
     * 货值不含税金额
     */
    @TableField("amount_not")
    private BigDecimal amountNot;

    /**
     * 货值税款金额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

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

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;


}
