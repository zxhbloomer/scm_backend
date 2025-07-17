package com.xinyirun.scm.bean.system.vo.business.aprefundpay;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付退款单明细表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundPayDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3456789012345678901L;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 导出 id
     */
    private Integer[] ids;

    /**
     * id
     */
    private Integer id;

    /**
     * 退款单表id
     */
    private Integer ap_refund_pay_id;

    /**
     * 退款单表code
     */
    private String ap_refund_pay_code;

    /**
     * 退款管理id
     */
    private Integer ap_refund_id;

    /**
     * 退款管理code
     */
    private String ap_refund_code;

    /**
     * 企业银行账户表id
     */
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表编号
     */
    private String bank_accounts_code;

    /**
     * 企业银行账户名称
     */
    private String bank_accounts_name;

    /**
     * 计划退款金额
     */
    private BigDecimal refundable_amount;

    /**
     * 实退金额
     */
    private BigDecimal refunded_amount;

    /**
     * 退款中金额
     */
    private BigDecimal refunding_amount;

    /**
     * 未退款金额
     */
    private BigDecimal unrefund_amount;

    /**
     * 作废退款金额
     */
    private BigDecimal cancel_amount;

    /**
     * 本次退款金额
     */
    private BigDecimal order_amount;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 创建人名称
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 修改人名称
     */
    private String u_name;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    // 查询条件字段
    /**
     * 创建时间范围查询 - 开始时间
     */
    private LocalDateTime c_time_start;

    /**
     * 创建时间范围查询 - 结束时间
     */
    private LocalDateTime c_time_end;

    /**
     * 修改时间范围查询 - 开始时间
     */
    private LocalDateTime u_time_start;

    /**
     * 修改时间范围查询 - 结束时间
     */
    private LocalDateTime u_time_end;

    /**
     * 退款金额范围查询 - 最小值
     */
    private BigDecimal refundable_amount_min;

    /**
     * 退款金额范围查询 - 最大值
     */
    private BigDecimal refundable_amount_max;

    /**
     * 本次退款金额范围查询 - 最小值
     */
    private BigDecimal order_amount_min;

    /**
     * 本次退款金额范围查询 - 最大值
     */
    private BigDecimal order_amount_max;

}