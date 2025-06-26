package com.xinyirun.scm.common.enums;

/**
 * @author zxh
 * @date 2019/8/22
 */
public enum DeleteTypeEnum {
    NOT_DELETED(0, "未删除"),
    DELETED(1, "已经删除"),
    ALL(null, "所有数据");

    private Integer code;

    private String msg;

    DeleteTypeEnum(Integer code, String msg) {
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
