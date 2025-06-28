package com.xinyirun.scm.common.serialtype;

/**
 * @author zxh
 */
public class SerialType {

    private SerialType() {
    }

    // 入库单
    public static final String BILL_BUSINESS_IN = "b_in";
    // 出库单
    public static final String BILL_BUSINESS_OUT = "b_out";
    // 调整单
    public static final String BILL_BUSINESS_ADJUST = "b_adjust";
    // 物料转换
    public static final String BILL_MATERIAL_CONVERT = "b_material_convert";


    // 创建定时任务：数据变更日志中，没找到order_code，发起定时任务开始找
    public static final String DATA_CHANGE_FIND_ORDER_CODE = "mongodb.s_log_data_change_detail";

    // 入库单
    public static final String BILL_BUSINESS_IN_NAME = "入库单";
    // 出库单
    public static final String BILL_BUSINESS_OUT_NAME = "出库单";
    // 调整单
    public static final String BILL_BUSINESS_ADJUST_NAME = "库存调整单";
    // 物料转换
    public static final String BILL_MATERIAL_CONVERT_NAME = "物料转换";
}
