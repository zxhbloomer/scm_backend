package com.xinyirun.scm.bean.system.vo.business.so.socontract;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 销售合同总计信息
 * @CreateTime : 2025/1/22 15:48
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoContractTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4993954200081695010L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 销售合同主表ID
     */
    private Integer so_contract_id;

    /**
     * 表头 合同总金额
     */
    private BigDecimal order_amount_total;

    /**
     * 表头 总税额
     */
    private BigDecimal tax_amount_total;

    /**
     * 表头 总销售数量
     */
    private BigDecimal order_total;

    /**
     * 表头 预付未付总金额
     */
    private BigDecimal advance_unpay_total;

    /**
     * 表头 预付已付款总金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 表头 预付停付款总金额
     */
    private BigDecimal advance_stoppay_total;

    /**
     * 表头 预付已付款总金额
     */
    private BigDecimal advance_pay_total;

    /**
     * 表头 结算总金额
     */
    private BigDecimal settle_amount_total;

    /**
     * 表头 应付未付总金额
     */
    private BigDecimal payable_unpay_total;

    /**
     * 表头 应付已付款总金额
     */
    private BigDecimal payable_paid_total;

    /**
     * 表头 应付已付款总金额
     */
    private BigDecimal payable_pay_total;

    /**
     * 表头 实付总金额
     */
    private BigDecimal paid_total;

    /**
     * 表头 未开票总金额
     */
    private BigDecimal uninvoiced_amount_total;

    /**
     * 表头 已开票总金额
     */
    private BigDecimal invoiced_amount_total;

    /**
     * 表头 预付退款进行中总金额
     */
    private BigDecimal refund_advance_doing_total;

    /**
     * 表头 预付退款已完成总金额
     */
    private BigDecimal refund_advance_done_total;

    /**
     * 表头 预付退款总金额
     */
    private BigDecimal refund_advance_total;

    /**
     * 表头 应付退款进行中总金额
     */
    private BigDecimal refund_payable_doing_total;

    /**
     * 表头 应付退款已完成总金额
     */
    private BigDecimal refund_payable_done_total;

    /**
     * 表头 应付退款总金额
     */
    private BigDecimal refund_payable_total;

    /**
     * 表头 入库总数量
     */
    private BigDecimal inventory_in_total;

    /**
     * 表头 入库计划总数量
     */
    private BigDecimal inventory_in_plan_total;

    /**
     * 表头 结算入库总数量
     */
    private BigDecimal settle_inventory_in_total;

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

}