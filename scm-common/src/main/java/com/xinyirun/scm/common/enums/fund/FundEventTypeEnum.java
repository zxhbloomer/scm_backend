package com.xinyirun.scm.common.enums.fund;

/**
 * 资金事件类型枚举
 * @author system
 * @date 2025/06/17
 */
public enum FundEventTypeEnum {

    /** 预付款金额增加 */
    YUFU_PAY_INC("YUFU_PAY_INC", "预付款金额增加"),
    
    /** 预付款金额减少 */
    YUFU_PAY_DEC("YUFU_PAY_DEC", "预付款金额减少"),
    
    /** 预付款退回金额增加 */
    YUFU_RTN_INC("YUFU_RTN_INC", "预付款退回金额增加"),
    
    /** 预付款退回金额减少 */
    YUFU_RTN_DEC("YUFU_RTN_DEC", "预付款退回金额减少");

    private String code;

    private String msg;

    FundEventTypeEnum(String code, String msg) {
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
