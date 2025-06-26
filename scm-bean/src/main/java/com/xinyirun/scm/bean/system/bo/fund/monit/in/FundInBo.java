package com.xinyirun.scm.bean.system.bo.fund.monit.in;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金使用情况，增加资金的bo
 */
@Data
@Builder
public class FundInBo implements Serializable {


    @Serial
    private static final long serialVersionUID = -4373606691102183371L;


    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 类型：0-冻结；1-生效
     */
    private String type;

    /**
     * YUFU_PAY_INC	预付款金额增加
     * YUFU_PAY_DEC	预付款金额减少
     * YUFU_RTN_INC	预付款退回金额增加
     * YUFU_RTN_DEC	预付款退回金额减少
     */
    private String business_type;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 关联单号id
     */
    private Integer serial_id;

    /**
     * 关联单号code
     */
    private String serial_code;

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
     * 款想类型code
     */
    private String bank_accounts_type_code;

    /**
     * 资金类型（0：资金池；1：专款专用）
     */
    private String fund_type;

    /**
     * 交易id
     */
    private Integer trade_id;

    /**
     * 交易编号
     */
    private String trade_code;

    /**
     * 业务类型（表名）
     */
    private String trade_type;

    /**
     * 资金事件类型
     */
    private String fund_event;

    /**
     * 交易订单id（采购订单、销售订单）
     */
    private Integer trade_order_id;

    /**
     * 交易订单编号（采购/销售订单）
     */
    private String trade_order_code;

    /**
     * 交易订单类型：表名
     */
    private String trade_order_type;

    /**
     * 交易合同id（采购/销售合同）
     */
    private Integer trade_contract_id;

    /**
     * 交易合同编号（采购/销售合同）
     */
    private String trade_contract_code;

    /**
     * 交易合同类型：表名
     */
    private String trade_contract_type;

    /**
     * 金额
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


}
