package com.xinyirun.scm.bean.system.vo.business.fund;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 资金使用情况表
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BFundUsageVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -2177680290762223792L;

    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 企业id
     */
    private Integer enterprise_id;

    /**
     * 企业code
     */
    private String enterprise_code;

    /**
     * 企业银行账户id
     */
    private Integer bank_account_id;

    /**
     * 企业银行账户code
     */
    private String bank_account_code;

    /**
     * 款项类型id
     */
    private Integer bank_accounts_type_id;

    /**
     * 款项类型code
     */
    private String bank_accounts_type_code;

    /**
     * 资金类型（0：资金池；1：专款专用）
     */
    private String type;

    /**
     * 交易id（资金池：null、合同id：转款专用）
     */
    private Integer trade_id;

    /**
     * 交易编号（资金池：null、合同编号（手输入的编号）：转款专用））
     */
    private String trade_code;

    /**
     * 交易类型（资金池：null、转款专用：表名）
     */
    private String trade_type;

    /**
     * 金额加(审批中)
     */
    private BigDecimal increase_amount_lock;

    /**
     * 金额减(审批中)
     */
    private BigDecimal decrease_amount_lock;    /**
     * 收付金额
     */
    private BigDecimal pr_amount;

    /**
     * 作废收付金额
     */
    private BigDecimal cancel_pr_amount;

    /**
     * 退回金额
     */
    private BigDecimal refund_amount;

    /**
     * 作废退回金额
     */
    private BigDecimal cancel_refund_amount;

    /**
     * 核销金额
     */
    private BigDecimal settlement_amount;

    /**
     * 可用金额:'=收付金额-作废收付金额+退回金额-作废退回金额-累计核销金额
     */
    private BigDecimal amount;

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
     * 换页条件
     */
    private PageCondition pageCondition;


}
