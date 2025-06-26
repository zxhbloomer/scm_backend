package com.xinyirun.scm.common.enums;

/**
 * 库存操作核心代码错误
 */
public enum InventoryResultEnum {

    CONSIGNOR_DATA_IS_NULL(201,"缺少参数consignor_id！"),
    OWNER_DATA_IS_NULL(202,"缺少参数owner_id！"),
    IN_BO_DATA_IS_NULL(203,"参数为空！"),
    OUT_BO_DATA_IS_NULL(204,"参数为空！"),
    ADJUST_BO_DATA_IS_NULL(205,"参数为空！"),
    SKU_DATA_IS_NULL(206,"缺少参数sku_id！"),
    TYPE_DATA_IS_NULL(207,"缺少流水参数wmsInventoryTypeEnum！"),
    BUSINESS_TYPE_DATA_IS_NULL(208,"缺少流水参数wmsInventoryBusinessTypeEnum！"),

    INVENTORY_IS_NOT_ENOUGH(301,"库存不足！"),
    INVENTORY_IS_NOT_ENOUGH_1(301,"【%s】货主的【%s】仓库，【%s】商品库存不足，当前商品可用库存为%s"),

//    INVENTORY_DATA_IS_NOT_EXISTS(301,"指定的库存数据不存在！"),
    INVENTORY_DATA_IS_NOT_EXISTS(301,"【%s】货主的【%s】仓库，【%s】商品库存不足"),
    WAREHOUSE_DATA_IS_NOT_EXISTS(301,"指定的仓库数据不存在！"),
    LOCATION_DATA_IS_NOT_EXISTS(302,"指定的库区数据不存在！"),
    BIN_DATA_IS_NOT_EXISTS(303,"指定的库位数据不存在！"),
    WAREHOUSE_DATA_IS_NOT_ENABLED(304,"指定的仓库数据不可用！"),
    LOCATION_DATA_IS_NOT_ENABLED(305,"指定的库区数据不可用！"),
    BIN_DATA_IS_NOT_ENABLED(306,"指定的库位数据不可用！"),
    SKU_DATA_IS_NOT_EXISTS(307,"指定的物料数据不存在！"),
    SKU_DATA_IS_NOT_ENABLED(308,"指定的物料数据不可用！"),
    CONSIGNOR_DATA_IS_NOT_EXISTS(309,"指定的委托方数据不存在！"),
    CONSIGNOR_DATA_IS_NOT_ENABLED(310,"指定的委托方数据不可用！"),
    OWNER_DATA_IS_NOT_EXISTS(311,"指定的货主数据不存在！"),
    OWNER_DATA_IS_NOT_ENABLED(312,"指定的货主数据不可用！"),
    IN_BO_COUNT_DATA_IS_NULL(313,"参数入库数量不能为空！"),
    OUT_BO_COUNT_DATA_IS_NULL(314,"参数出库数量不能为空！"),
    IN_BO_COUNT_DATA_IS_INVALID(315,"参数入库数量需要大于0！"),
    OUT_BO_COUNT_DATA_IS_INVALID(316,"参数出库数量需要大于0！"),
    OUT_COUNT_MORE_THAN_INVENTORY_LOCK_QTY(317,"参数出库数量大于锁定库存！"),
    OUT_COUNT_MORE_THAN_INVENTORY_QTY(318,"参数出库数量大于可用库存！"),
    ADJUST_BO_COUNT_DATA_IS_NULL(319,"参数调整数量不能为空！"),
    ADJUST_BO_COUNT_DATA_IS_INVALID(320,"参数调整数量需要大于等于0！"),
    LOT_NUM_DATA_IS_DUPLICATED(321,"参数批次发生重复了！"),
    BILL_IN_DATA_IS_NOT_EXISTS(322,"入库单不存在！"),
    BILL_IN_DATA_IS_READY_RUN(323,"该单据已经被执行过库存操作了！"),

    BILL_OUT_DATA_IS_NOT_EXISTS(324,"出库单不存在！"),
    BILL_OUT_DATA_IS_READY_RUN(325,"该单据已经被执行过库存操作了！"),

    BILL_ADJUST_DATA_IS_NOT_EXISTS(326,"调整单不存在！"),
    BILL_ADJUST_DATA_IS_READY_RUN(327,"该单据已经被执行过库存操作了！"),


    ;

    private Integer code;

    private String msg;

    InventoryResultEnum(Integer code, String msg) {
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
