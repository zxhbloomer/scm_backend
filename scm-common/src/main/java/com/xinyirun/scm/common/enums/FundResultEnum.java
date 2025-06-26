package com.xinyirun.scm.common.enums;

/**
 * 库存操作核心代码错误
 */
public enum FundResultEnum {

    FUND_IN_BO_IS_NULL(201,"参数为空！"),
    FUND_IN_BO_FUND_TYPE_ERROR(202,"业务类型不存在！"),
    FUND_IN_BO_ENTERPRISE_ID_IS_NULL(301,"企业ID不能为空！"),
    FUND_IN_BO_ENTERPRISE_CODE_IS_NULL(302,"企业CODE不能为空！"),
    FUND_IN_BO_ENTERPRISE_IS_NULL(303,"企业不存在！"),
    FUND_IN_BO_BANK_ACCOUNT_ID_IS_NULL(304,"银行账户ID不能为空！"),
    FUND_IN_BO_BANK_ACCOUNT_CODE_IS_NULL(305,"银行账户CODE不能为空！"),
    FUND_IN_BO_BANK_ACCOUNTS_TYPE_ID_IS_NULL(306,"款项类型ID不能为空！"),
    FUND_IN_BO_BANK_ACCOUNTS_TYPE_CODE_IS_NULL(307,"款项类型CODE不能为空！"),
    FUND_IN_BO_BANK_TRADE_NO_IS_NULL(308,"交易编号不能为空！"),
    FUND_IN_BO_SERIAL_ID_IS_NULL(309,"关联单号ID不能为空！"),
    FUND_IN_BO_SERIAL_CODE_IS_NULL(310,"关联单号CODE不能为空！"),
    FUND_IN_BO_SERIAL_TYPE_IS_NULL(311,"关联单号TYPE不能为空！"),
    FUND_IN_BO_AP_PAY_IS_NULL(312,"付款单不能为空！"),
    FUND_IN_BO_PAY_AMOUNT_IS_NULL(313,"金额为空不能为空！"),
    FUND_IN_BO_PAY_AMOUNT_IS_INVALID(314,"金额需要大于0！"),
    FUND_IN_BO_AP_PAY_TYPE_IS_NULL(315,"付款单状态不能为空！"),
    FUND_IN_BO_AP_REFUND_PAY_IS_NULL(316,"退款单不能为空！"),
    FUND_IN_BO_AP_REFUND_PAY_TYPE_IS_NULL(317,"退款单状态不能为空！"),

    ;

    private Integer code;

    private String msg;

    FundResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
