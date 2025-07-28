package com.xinyirun.scm.bean.entity.business.so.socontract;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 销售合同明细表-商品
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_contract_detail")
@DataChangeEntityAnnotation(value="销售合同明细表-商品", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.socontract.DataChangeStrategyBSoContractDetailEntityServiceImpl")
public class BSoContractDetailEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -4779972545193403188L;

    @TableId("id")
    private Integer id;

    /**
     * 销售合同主表ID
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * 商品编号
     */
    @TableField("goods_code")
    @DataChangeLabelAnnotation("商品编号")
    private String goods_code;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    @DataChangeLabelAnnotation("商品名称")
    private String goods_name;

    /**
     * 物料Id,商品id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编号
     */
    @TableField("sku_code")
    @DataChangeLabelAnnotation("规格编号")
    private String sku_code;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    @DataChangeLabelAnnotation("规格名称")
    private String sku_name;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    @DataChangeLabelAnnotation(value="单位",  extension = "getUnitNameExtension")
    private Integer unit_id;

    /**
     * 规格
     */
    @TableField("spec")
    @DataChangeLabelAnnotation(value="规格")
    private String spec;

    /**
     * 产地
     */
    @TableField("origin")
    @DataChangeLabelAnnotation(value="产地")
    private String origin;

    /**
     * 数量
     */
    @TableField("qty")
    @DataChangeLabelAnnotation(value="数量")
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    @TableField("price")
    @DataChangeLabelAnnotation(value="单价（含税）")
    private BigDecimal price;

    /**
     * 金额
     */
    @TableField("amount")
    @DataChangeLabelAnnotation(value="金额")
    private BigDecimal amount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    @DataChangeLabelAnnotation(value="税额")
    private BigDecimal tax_amount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    @DataChangeLabelAnnotation(value="税率")
    private BigDecimal tax_rate;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}