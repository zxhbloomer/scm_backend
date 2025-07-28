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
 * 销售合同总计表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_contract_total")
@DataChangeEntityAnnotation(value="销售合同总计表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.socontract.DataChangeStrategyBSoContractTotalEntityServiceImpl")
public class BSoContractTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 6236885781532882718L;

    @TableId("id")
    private Integer id;

    /**
     * 销售合同主表ID
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 表头 合同总金额
     */
    @TableField("order_amount_total")
    @DataChangeLabelAnnotation(value="表头 合同总金额")
    private BigDecimal order_amount_total;

    /**
     * 表头 总税额
     */
    @TableField("tax_amount_total")
    @DataChangeLabelAnnotation(value="表头 总税额")
    private BigDecimal tax_amount_total;

    /**
     * 表头 总销售数量
     */
    @TableField("order_total")
    @DataChangeLabelAnnotation(value="表头 总销售数量")
    private BigDecimal order_total;

    /**
     * 表头 预付未付总金额
     */
    @TableField("advance_unpay_total")
    @DataChangeLabelAnnotation(value="表头 预付未付总金额")
    private BigDecimal advance_unpay_total;

    /**
     * 表头 预付已付款总金额
     */
    @TableField("advance_paid_total")
    @DataChangeLabelAnnotation(value="表头 预付已付款总金额")
    private BigDecimal advance_paid_total;

    /**
     * 表头 预付停付款总金额
     */
    @TableField("advance_stoppay_total")
    @DataChangeLabelAnnotation(value="表头 预付停付款总金额")
    private BigDecimal advance_stoppay_total;

    /**
     * 表头 预付已付款总金额
     */
    @TableField("advance_pay_total")
    @DataChangeLabelAnnotation(value="表头 预付已付款总金额")
    private BigDecimal advance_pay_total;

    /**
     * 表头 结算总金额
     */
    @TableField("settle_amount_total")
    @DataChangeLabelAnnotation(value="表头 结算总金额")
    private BigDecimal settle_amount_total;

    /**
     * 表头 应付未付总金额
     */
    @TableField("payable_unpay_total")
    @DataChangeLabelAnnotation(value="表头 应付未付总金额")
    private BigDecimal payable_unpay_total;

    /**
     * 表头 应付已付款总金额
     */
    @TableField("payable_paid_total")
    @DataChangeLabelAnnotation(value="表头 应付已付款总金额")
    private BigDecimal payable_paid_total;

    /**
     * 表头 应付已付款总金额
     */
    @TableField("payable_pay_total")
    @DataChangeLabelAnnotation(value="表头 应付已付款总金额")
    private BigDecimal payable_pay_total;

    /**
     * 表头 实付总金额
     */
    @TableField("paid_total")
    @DataChangeLabelAnnotation(value="表头 实付总金额")
    private BigDecimal paid_total;

    /**
     * 表头 未开票总金额
     */
    @TableField("uninvoiced_amount_total")
    @DataChangeLabelAnnotation(value="表头 未开票总金额")
    private BigDecimal uninvoiced_amount_total;

    /**
     * 表头 已开票总金额
     */
    @TableField("invoiced_amount_total")
    @DataChangeLabelAnnotation(value="表头 已开票总金额")
    private BigDecimal invoiced_amount_total;

    /**
     * 表头 预付退款进行中总金额
     */
    @TableField("refund_advance_doing_total")
    @DataChangeLabelAnnotation(value="表头 预付退款进行中总金额")
    private BigDecimal refund_advance_doing_total;

    /**
     * 表头 预付退款已完成总金额
     */
    @TableField("refund_advance_done_total")
    @DataChangeLabelAnnotation(value="表头 预付退款已完成总金额")
    private BigDecimal refund_advance_done_total;

    /**
     * 表头 预付退款总金额
     */
    @TableField("refund_advance_total")
    @DataChangeLabelAnnotation(value="表头 预付退款总金额")
    private BigDecimal refund_advance_total;

    /**
     * 表头 应付退款进行中总金额
     */
    @TableField("refund_payable_doing_total")
    @DataChangeLabelAnnotation(value="表头 应付退款进行中总金额")
    private BigDecimal refund_payable_doing_total;

    /**
     * 表头 应付退款已完成总金额
     */
    @TableField("refund_payable_done_total")
    @DataChangeLabelAnnotation(value="表头 应付退款已完成总金额")
    private BigDecimal refund_payable_done_total;

    /**
     * 表头 应付退款总金额
     */
    @TableField("refund_payable_total")
    @DataChangeLabelAnnotation(value="表头 应付退款总金额")
    private BigDecimal refund_payable_total;

    /**
     * 表头 入库总数量
     */
    @TableField("inventory_in_total")
    @DataChangeLabelAnnotation(value="表头 入库总数量")
    private BigDecimal inventory_in_total;

    /**
     * 表头 入库计划总数量
     */
    @TableField("inventory_in_plan_total")
    @DataChangeLabelAnnotation(value="表头 入库计划总数量")
    private BigDecimal inventory_in_plan_total;

    /**
     * 表头 结算入库总数量
     */
    @TableField("settle_inventory_in_total")
    @DataChangeLabelAnnotation(value="表头 结算入库总数量")
    private BigDecimal settle_inventory_in_total;

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