package com.xinyirun.scm.bean.system.vo.excel.schedule;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BScheduleExcelVo implements Serializable {

    private static final long serialVersionUID = 7144845163213288624L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(15)
    private Integer excel_no;

    @ExcelProperty(value = "物流订单单号", index = 1)
    @ColumnWidth(5)
    private String code;

    @ExcelProperty(value = "派车数", index = 2)
    private Integer monitor_count;

    @ExcelProperty(value = "单据状态", index = 3)
    private String status_name;

    @ExcelProperty(value = "单据类型", index = 4)
    private String type_name;

    @ExcelProperty(value = "物流合同", index = 5)
    private String waybill_contract_no;

    @ExcelProperty(value = "关联承运订单号", index = 6)
    private String carriage_order_no;

    @ExcelProperty(value = "承运商", index = 7)
    private String customer_name;

    @ExcelProperty(value = "采购/销售合同号", index = 8)
    private String contract_no;

    @ExcelProperty(value = "出库计划单号", index = 9)
    private String out_plan_code;

    @ExcelProperty(value = "发货委托方", index = 10)
    private String out_consignor_name;

    @ExcelProperty(value = "发货货主", index = 11)
    private String out_owner_name;

    @ExcelProperty(value = "发货仓库（简称）", index = 12)
    private String out_warehouse_name;

    @ExcelProperty(value = "发货仓库（全称）", index = 13)
    private String out_warehouse_full_name;

    @ExcelProperty(value = "发货仓库类型", index = 14)
    private String out_warehouse_type_name;

    @ExcelProperty(value = "发货执行情况", index = 15)
    private String out_rate;

    @ExcelProperty(value = "入库计划单号", index = 16)
    private String in_plan_code;

    @ExcelProperty(value = "收货委托方", index = 17)
    private String in_consignor_name;

    @ExcelProperty(value = "收货货主", index = 18)
    private String in_owner_name;

    @ExcelProperty(value = "收货仓库（简称）", index = 19)
    private String in_warehouse_name;

    @ExcelProperty(value = "收货仓库（全称）", index = 20)
    private String in_warehouse_full_name;

    @ExcelProperty(value = "收货仓库类型", index = 21)
    private String in_warehouse_type_name;

    @ExcelProperty(value = "收货执行情况", index = 22)
    private String in_rate;

    @ExcelProperty(value = "在途数量",index = 23)
    private int in_transit;

    @ExcelProperty(value = "物料名称", index = 24)
    private String goods_name;

    @ExcelProperty(value = "品名", index = 25)
    private String pm;

    @ExcelProperty(value = "规格", index = 26)
    private String spec;

    @ExcelProperty(value = "规格编号", index = 27)
    private String sku_code;

    @ExcelProperty(value = "应发货数量", index = 28)
    private BigDecimal out_schedule_qty;

    @ExcelProperty(value = "已发货数量", index = 29)
    private BigDecimal out_operated_qty;

    @ExcelProperty(value = "待发货数量", index = 30)
    private BigDecimal out_balance_qty;

    @ExcelProperty(value = "超发数量", index = 31)
    private BigDecimal out_over_qty;

    @ExcelProperty(value = "出库单位", index = 32)
    private String out_unit = "吨";

    @ExcelProperty(value = "应收货数量", index = 33)
    private BigDecimal in_schedule_qty;

    @ExcelProperty(value = "已收货数量", index = 34)
    private BigDecimal in_operated_qty;

    @ExcelProperty(value = "待收货数量", index = 35)
    private BigDecimal in_balance_qty;

    @ExcelProperty(value = "超收数量", index = 36)
    private BigDecimal in_over_qty;

    @ExcelProperty(value = "入库单位", index = 37)
    private String in_unit = "吨";

    @ExcelProperty(value = "退货数量", index = 38)
    private BigDecimal return_qty;

    @ExcelProperty(value = "创建人", index = 39)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 40)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(30)
    private LocalDateTime c_time;

    @ExcelProperty(value = "修改人", index = 41)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 42)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(30)
    private LocalDateTime u_time;
}
