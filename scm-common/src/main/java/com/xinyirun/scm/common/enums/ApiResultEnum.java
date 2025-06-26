package com.xinyirun.scm.common.enums;

/**
 * 库存操作核心代码错误
 */
public enum ApiResultEnum {
    OK(0, "成功"),
    UNKNOWN_ERROR(-1, "未知错误！"),
    NEED_APP_KEY(101,"缺少参数app_key！"),
    NEED_SECRET_KEY(102,"缺少参数secret_key！"),
    NOT_NULL_APP_KEY(103,"缺少参数app_key！"),
    NOT_NULL_SECRET_KEY(104,"缺少参数secret_key！"),
    APP_KEY_DATA_IS_NULL(105,"没有找到对应app_key的数据，app_key在数据库中不存在！"),
    AUTH_DATA_IS_NULL(106,"secret_key不正确！"),


    ;

    private Integer code;

    private String msg;

    ApiResultEnum(Integer code, String msg) {
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
