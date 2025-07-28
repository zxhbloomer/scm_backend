package com.xinyirun.scm.bean.system.vo.business.so.arrefund;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收退款管理表-预收款业务表（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReFundSourceAdvanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 7284619503726384751L;
    
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 应收退款主表id
     */
    private Integer ar_refund_id;

    /**
     * 应收退款主表code
     */
    private String ar_refund_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    private String type;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String so_goods;

    /**
     * 申请退款总金额
     */
    private BigDecimal refundable_amount_total;

    /**
     * 已退款总金额
     */
    private BigDecimal refunded_amount_total;

    /**
     * 退款中总金额
     */
    private BigDecimal refunding_amount_total;

    /**
     * 未退款总金额
     */
    private BigDecimal unrefund_amount_total;

    /**
     * 取消退款总金额
     */
    private BigDecimal cancelrefund_amount_total;

    /**
     * 预收款已收金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 可退金额
     */
    private BigDecimal advance_refund_amount_total;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

}