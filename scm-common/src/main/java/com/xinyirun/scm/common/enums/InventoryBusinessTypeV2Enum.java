package com.xinyirun.scm.common.enums;

/**
 * 库存业务V2类型
 * @author zxh
 * @date 2019/8/22
 */
public enum InventoryBusinessTypeV2Enum {
    IN_CREATE("10", "入库单生成"),                     // 待审批  生成入库单信息 ()
    IN_SUBMIT("11", "入库单审批中"),                   // 入库单审批中 -> 锁定库存数量 -> 更新流水类型(入库单生成，入库单审批中)
    IN_NOT_AGREE("12", "入库单审批驳回"),               // 入库单驳回 -> 释放锁定库存数量 -> 更新流水类型(入库单驳回)
    IN_REVOKE("13", "入库单审批撤销"),                  // 入库单撤回 -> 释放锁定库存数量 -> 更新流水类型(入库单撤回)
    IN_AGREE("14", "入库单审批同意"),                   // 入库单审批通过 -> 释放锁定库存数量 -> 更新可用库存数量  -> 更新流水类型(入库单审批通过)
    IN_CANCEL_CREATE("15", "入库单作废审批中"),          // 入库单作废审批中 -> 可用库存数量扣减 -> 锁定库存数量 -> 更新流水类型(入库单作废审批中)
    IN_CANCEL_AGREE("16", "入库单作废审批驳回"),         // 入库单作废驳回 -> 释放锁定库存数量 -> 更新可用库存数量 -> 更新流水类型(入库单作废驳回)
    IN_CANCEL_REVOKE("17", "入库单作废审批撤销"),        // 入库单作废撤回 -> 释放锁定库存数量 -> 更新可用库存数量 -> 更新流水类型(入库单作废撤回)

    IN_CANCEL("18", "入库单已作废"),                    // 入库单作废审批通过 -> 释放锁定库存数量 -> 更新流水类型(入库单作废审批通过)

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

    InventoryBusinessTypeV2Enum(String code, String msg) {
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
