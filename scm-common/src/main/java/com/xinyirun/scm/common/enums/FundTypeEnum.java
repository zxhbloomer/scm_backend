package com.xinyirun.scm.common.enums;

/**
 * 应付账款管理类型
 * @author zxh
 * @date 2019/8/22
 */
public enum FundTypeEnum {

    AP_ADVANCE_ORDER("UF01", "预付款付款指令"),
    AP_ADVANCE_VOUCHER("UF02", "预付款付款凭证上传"),
    AP_ADVANCE_PAY_REFUND_ORDER("UFT01", "预付退款下推退款单"),
    AP_ADVANCE_PAY_REFUND_VOUCHER("UFT02", "预付退款凭证上传"),
    AP_ADVANCE_PAY_CANCEL("UFZF01", "预付款付款凭证作废"),
    AP_ADVANCE_PAY_REFUND_CANCEL("UFZF02", "预付款退款凭证作废"),
    AP_ADVANCE_STOP_PAY("UFZZ01", "预付款中止付款"),
    AP_ADVANCE_STOP_REFUND_PAY("UFZZ02", "预付退款中止付款"),

    AP_PAY_ORDER("YF00", "应付款付款指令"),
    AP_PAY_VOUCHER("YF01", "应付款付款凭证上传"),
    AP_PAY_REFUND_ORDER("YFT01", "应付退款下推退款单"),
    AP_PAY_REFUND_VOUCHER("YFT02", "应付退款凭证上传"),
    AP_PAY_VOUCHER_CANCEL("YFZF01", "应付款付款凭证作废"),
    AP_PAY_REFUND_VOUCHER_CANCEL("YFZF02", "应付退款凭证作废"),
    AP_STOP_PAY("YFZZ01", "应付款中止付款"),
    AP_REFUND_STOP_PAY("YFZZ02", "应付款退款中止付款"),
    ;

    private String code;

    private String msg;

    FundTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
