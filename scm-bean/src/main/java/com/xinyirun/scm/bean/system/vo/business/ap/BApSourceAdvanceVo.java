package com.xinyirun.scm.bean.system.vo.business.ap;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付账款管理表-预付款业务表（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApSourceAdvanceVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 2592341317464135882L;
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 应付账款主表id
     */
    private Integer ap_id;

    /**
     * 应付账款主表code
     */
    private String ap_code;

    /**
     *
     */
    private String type;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单id
     */
    private Integer po_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String po_goods;

    /**
     * 总数量
     */
    private BigDecimal qty_total;

    /**
     * 总金额
     */
    private BigDecimal amount_total;

    /**
     * 累计预付款金额
     */
    private BigDecimal po_advance_payment_amount;

    /**
     * 可申请预付款金额
     */
    private BigDecimal po_can_advance_payment_amount;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

    /**
     * 申请付款总金额
     */
    private BigDecimal payable_amount_total;

    /**
     * 已付款总金额
     */
    private BigDecimal paid_amount_total;

    /**
     * 付款中总金额
     */
    private BigDecimal paying_amount_total;

    /**
     * 未付款总金额
     */
    private BigDecimal unpay_amount_total;

    /**
     * 中止付款总金额
     */
    private BigDecimal stoppay_amount_total;

    /**
     * 取消付款总金额
     */
    private BigDecimal cancelpay_amount_total;

    /**
     * 采购合同id
     */
    private Integer po_contract_id;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 付款单主表id
     */
    private Integer ap_pay_id;

    /**
     * 付款主表code
     */
    private String ap_pay_code;

    /**
     * 采购订单编号
     */
    private String po_order_code;

}
