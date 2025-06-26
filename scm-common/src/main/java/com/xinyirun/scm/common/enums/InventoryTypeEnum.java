package com.xinyirun.scm.common.enums;

/**
 * 流水类型
 * @author zxh
 * @date 2019/8/22
 */
public enum InventoryTypeEnum {
    IN("0", "入库"),
    OUT("1", "出库"),
    ADJUST("2", "调整"),
    ;

    private String code;

    private String msg;

    InventoryTypeEnum(String code, String msg) {
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
