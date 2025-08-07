package com.xinyirun.scm.bean.system.vo.business.po.pocontract;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 采购合同表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BPoContractTotalVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -3713281653151400899L;

    private Integer id;

    /**
     * 采购合同主表ID
     */
    private Integer po_contract_id;

    /**
     * 合同总金额
     */
    private BigDecimal order_amount_total;

    /**
     * 总税额
     */
    private BigDecimal tax_amount_total;

    /**
     * 总采购数量
     */
    private BigDecimal order_total;

    /**
     * 预付款未付总金额
     */
    private BigDecimal advance_unpay_total;    /**
     * 预付款已付款总金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 预付款中止付款总金额
     */
    private BigDecimal advance_stoppay_total;

    /**
     * 预付款计划付款金额
     */
    private BigDecimal advance_pay_total;

    /**
     * 结算总金额
     */
    private BigDecimal settle_amount_total;

    /**
     * 应付款未付总金额
     */
    private BigDecimal payable_unpay_total;

    /**
     * 应付款已付总金额
     */
    private BigDecimal payable_paid_total;

    /**
     * 应付款计划付款金额
     */
    private BigDecimal payable_pay_total;

    /**
     * 累计实付
     */
    private BigDecimal paid_total;

    /**
     * 未开票金额
     */
    private BigDecimal uninvoiced_amount_total;

    /**
     * 已开票金额
     */
    private BigDecimal invoiced_amount_total;

    /**
     * 退款：预付退款总金额-未完成
     */
    private BigDecimal refund_advance_doing_total;

    /**
     * 退款：预付退款总金额-已完成
     */
    private BigDecimal refund_advance_done_total;

    /**
     * 退款：预付退款总金额-计划金额
     */
    private BigDecimal refund_advance_total;

    /**
     * 退款：应付退款总金额-未完成
     */
    private BigDecimal refund_payable_doing_total;

    /**
     * 退款：应付退款总金额-已完成
     */
    private BigDecimal refund_payable_done_total;

    /**
     * 退款：应付退款总金额-计划金额
     */
    private BigDecimal refund_payable_total;

    /**
     * 实际入库汇总

     */
    private BigDecimal inventory_in_total;

    /**
     * 计划入库汇总
     */
    private BigDecimal inventory_in_plan_total;

    /**
     * 入库结算汇总
     */
    private BigDecimal settle_inventory_in_total;

    /**
     * 订单笔数
     */
    private Integer order_count;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 执行进度（百分比）
     * 计算公式：实际入库汇总/总采购数量*100
     */
    private BigDecimal virtual_progress;

    /**
     * 累计实付金额（虚拟列）
     * 计算公式：预付款已付款总金额+应付款已付总金额
     */
    private BigDecimal virtual_total_paid_amount;

    /**
     * 未付金额（虚拟列）
     * 计算公式：预付款未付总金额+应付款未付总金额
     */
    private BigDecimal virtual_unpaid_amount;

}
