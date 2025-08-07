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
     * 表头 预收款未收总金额
     */
    private BigDecimal advance_unreceive_total;

    /**
     * 表头 预收款已收总金额
     */
    private BigDecimal advance_received_total;

    /**
     * 表头 预收款已中止总金额
     */
    private BigDecimal advance_stopreceive_total;

    /**
     * 表头 预收款计划收款总金额
     */
    private BigDecimal advance_receive_total;

    /**
     * 表头 结算总金额
     */
    private BigDecimal settle_amount_total;

    /**
     * 表头 应收款未收总金额
     */
    private BigDecimal receivable_unreceive_total;

    /**
     * 表头 应收款已收总金额
     */
    private BigDecimal receivable_received_total;

    /**
     * 表头 应收款计划收款总金额
     */
    private BigDecimal receivable_receive_total;

    /**
     * 表头 已收款总金额
     */
    private BigDecimal received_total;

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
     * 表头 应收退款进行中总金额
     */
    private BigDecimal refund_receivable_doing_total;

    /**
     * 表头 应收退款已完成总金额
     */
    private BigDecimal refund_receivable_done_total;

    /**
     * 表头 应收退款总金额
     */
    private BigDecimal refund_receivable_total;

    /**
     * 表头 出库总数量
     */
    private BigDecimal inventory_out_total;

    /**
     * 表头 出库计划总数量
     */
    private BigDecimal inventory_out_plan_total;

    /**
     * 表头 结算出库总数量
     */
    private BigDecimal settle_inventory_out_total;

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
     * 计算公式：实际出库汇总/总销售数量
     */
    private BigDecimal virtual_progress;

    /**
     * 累计实收金额（虚拟列）
     * 计算公式：预收款已收总金额+应收款已收总金额
     */
    private BigDecimal virtual_total_received_amount;

    /**
     * 未收金额（虚拟列）
     * 计算公式：预收款未收总金额+应收款未收总金额
     */
    private BigDecimal virtual_unreceived_amount;

}