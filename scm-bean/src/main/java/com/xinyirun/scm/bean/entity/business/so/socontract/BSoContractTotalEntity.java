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
     * 表头 预收款未收总金额
     */
    @TableField("advance_unreceive_total")
    @DataChangeLabelAnnotation(value="表头 预收款未收总金额")
    private BigDecimal advance_unreceive_total;

    /**
     * 表头 预收款已收总金额
     */
    @TableField("advance_received_total")
    @DataChangeLabelAnnotation(value="表头 预收款已收总金额")
    private BigDecimal advance_received_total;

    /**
     * 表头 预收款已中止总金额
     */
    @TableField("advance_stopreceive_total")
    @DataChangeLabelAnnotation(value="表头 预收款已中止总金额")
    private BigDecimal advance_stopreceive_total;

    /**
     * 表头 预收款计划收款总金额
     */
    @TableField("advance_receive_total")
    @DataChangeLabelAnnotation(value="表头 预收款计划收款总金额")
    private BigDecimal advance_receive_total;

    /**
     * 表头 结算总金额
     */
    @TableField("settle_amount_total")
    @DataChangeLabelAnnotation(value="表头 结算总金额")
    private BigDecimal settle_amount_total;

    /**
     * 表头 应收款未收总金额
     */
    @TableField("receivable_unreceive_total")
    @DataChangeLabelAnnotation(value="表头 应收款未收总金额")
    private BigDecimal receivable_unreceive_total;

    /**
     * 表头 应收款已收总金额
     */
    @TableField("receivable_received_total")
    @DataChangeLabelAnnotation(value="表头 应收款已收总金额")
    private BigDecimal receivable_received_total;

    /**
     * 表头 应收款计划收款总金额
     */
    @TableField("receivable_receive_total")
    @DataChangeLabelAnnotation(value="表头 应收款计划收款总金额")
    private BigDecimal receivable_receive_total;

    /**
     * 表头 已收款总金额
     */
    @TableField("received_total")
    @DataChangeLabelAnnotation(value="表头 已收款总金额")
    private BigDecimal received_total;

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
     * 表头 应收退款进行中总金额
     */
    @TableField("refund_receivable_doing_total")
    @DataChangeLabelAnnotation(value="表头 应收退款进行中总金额")
    private BigDecimal refund_receivable_doing_total;

    /**
     * 表头 应收退款已完成总金额
     */
    @TableField("refund_receivable_done_total")
    @DataChangeLabelAnnotation(value="表头 应收退款已完成总金额")
    private BigDecimal refund_receivable_done_total;

    /**
     * 表头 应收退款总金额
     */
    @TableField("refund_receivable_total")
    @DataChangeLabelAnnotation(value="表头 应收退款总金额")
    private BigDecimal refund_receivable_total;

    /**
     * 表头 出库总数量
     */
    @TableField("inventory_out_total")
    @DataChangeLabelAnnotation(value="表头 出库总数量")
    private BigDecimal inventory_out_total;

    /**
     * 表头 出库计划总数量
     */
    @TableField("inventory_out_plan_total")
    @DataChangeLabelAnnotation(value="表头 出库计划总数量")
    private BigDecimal inventory_out_plan_total;

    /**
     * 表头 结算出库总数量
     */
    @TableField("settle_inventory_out_total")
    @DataChangeLabelAnnotation(value="表头 结算出库总数量")
    private BigDecimal settle_inventory_out_total;

    /**
     * 订单笔数
     */
    @TableField("order_count")
    @DataChangeLabelAnnotation(value="订单笔数")
    private Integer order_count;

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