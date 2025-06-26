package com.xinyirun.scm.common.enums;

/**
 * 库存业务类型
 * @author zxh
 * @date 2019/8/22
 */
public enum InventoryBusinessTypeEnum {
    IN_CREATE("10", "入库单生成"),                    // 数量进入锁定库存
    IN_AGREE("11", "入库单审核同意"),                 // 锁定库存转入可用库存，锁定库存释放
    IN_NOT_AGREE("12", "入库单审核驳回"),            // 锁定库存释放
    IN_CANCEL("13", "入库单作废"),                   // 制单时：锁定时库存释放，审核通过时可用库存释放
    IN_SUBMIT("14", "入库单提交"),                   // 提交
    OUT_CREATE("20", "出库单生成"),
    OUT_AGREE("21", "出库单生成审核同意"),
    OUT_NOT_AGREE("22", "出库单审核驳回"),
    OUT_CANCEL("23", "出库单作废"),
    OUT_SUBMIT("24", "出库单提交"),
    OUT_EXPIRES("26", "出库单过期"),
    ADJUST_CREATE("30", "调整单生成"),
    ADJUST_AGREE("31", "调整单审核同意"),
    ADJUST_NOT_AGREE("32", "调整单审核驳回"),
    ADJUST_CANCELLED("33", "调整单作废"),
    ;

    private String code;

    private String msg;

    InventoryBusinessTypeEnum(String code, String msg) {
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
