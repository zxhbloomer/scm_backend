package com.xinyirun.scm.common.enums.app;

/**
 * 库存操作核心代码错误
 */
public enum AppResultEnum {
    OK(0, "成功"),
    UNKNOWN_ERROR(-1, "未知错误！"),

    PASSWORD_NEW_SAME_OLD(301,"新旧密码不能相同！"),

    PASSWORD_NOT_CORRECT(301,"原密码错误！"),
    // 所有APP check都需要在该枚举类中定义

    ;

    private Integer code;

    private String msg;

    AppResultEnum(Integer code, String msg) {
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
