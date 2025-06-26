package com.xinyirun.scm.bean.system.vo.mongo.monitor.v1;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.common.convert.excel.ValidateVehicleConvertor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorMongoExportVo implements Serializable {

    private static final long serialVersionUID = 2469957521358583998L;

    @ExcelProperty(value="NO", index = 0)
    @ColumnWidth(5)
    private Integer no;

    @ExcelProperty(value="任务单号", index = 1)
    @ColumnWidth(30)
    private String code;

    @ExcelProperty(value="监管状态", index = 2)
    private String status_name;

    @ExcelProperty(value="审核状态", index = 3)
    private String audit_status_name;

    @ExcelProperty(value="结算状态", index = 4)
    private String settlement_status_name;

    @ExcelProperty(value="物流订单号", index = 5)
    private String schedule_code;

    @ExcelProperty(value="采购/销售合同号", index = 6)
    private String contract_no;

    @ExcelProperty(value="物流合同号", index = 7)
    private String waybill_contract_no;

    @ExcelProperty(value="出库计划单号", index = 8)
    private String out_plan_code;

    @ExcelProperty(value="入库计划单号", index = 9)
    private String in_plan_code;

    @ExcelProperty(value="运单号", index = 10)
    private String waybill_code;

    @ExcelProperty(value="发货类型", index = 11)
    private String out_type_name;

    @ExcelProperty(value="发货地", index = 12)
    private String out_warehouse_name;

    @ExcelProperty(value="发货地类型", index = 13)
    private String out_warehouse_type_name;

    @ExcelProperty(value="发货详细地址", index = 14)
    private String out_warehouse_address;

    @ExcelProperty(value="出库/提货时间", index = 15)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime out_time;

    @ExcelProperty(value="收货类型", index = 16)
    private String in_type_name;

    @ExcelProperty(value="收货地", index = 17)
    private String in_warehouse_name;

    @ExcelProperty(value="收货地类型", index = 18)
    private String in_warehouse_type_name;

    @ExcelProperty(value="收货详细地址", index = 19)
    private String in_warehouse_address;

    @ExcelProperty(value="入库/卸货时间", index = 20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime in_time;

    @ExcelProperty(value="物料名称", index = 21)
    private String goods_name;

    @ExcelProperty(value="品名", index = 22)
    private String pm;

    @ExcelProperty(value="规格", index = 23)
    private String spec;

    @ExcelProperty(value="规格编号", index = 24)
    private String sku_code;

    @ExcelProperty(value="承运商", index = 25)
    private String customer_name;

    @ExcelProperty(value="司机", index = 26)
    private String driver_name;

    @ExcelProperty(value="车牌号", index = 27)
    private String vehicle_no;

    @ExcelProperty(value="验车状态", converter = ValidateVehicleConvertor.class, index = 28)
    private String validate_vehicle;

    @ExcelProperty(value="发货数量", index = 29)
    private BigDecimal out_qty;

    @ExcelProperty(value="发货单位", index = 30)
    private String unit_name1 = "吨";

    @ExcelProperty(value="收货数量", index = 31)
    private BigDecimal in_qty;

    @ExcelProperty(value="收货单位", index = 32)
    private String unit_name2 = "吨";

    @ExcelProperty(value="损耗", index = 33)
    private BigDecimal qty_loss;

    @ExcelProperty(value="作废理由", index = 34)
    private String cancel_remark;

    @ExcelProperty(value="出库审核人", index = 35)
    private String out_audit_name;

    @ExcelProperty(value="出库审核时间", index = 36)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private String out_audit_time;

    @ExcelProperty(value="入库审核人", index = 37)
    private String in_audit_name;

    @ExcelProperty(value="入库审核时间", index = 38)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private String in_audit_time;

    @ExcelProperty(value="创建人", index = 39)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 40)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="修改人", index = 41)
    private String u_name;

    @ExcelProperty(value="修改时间", index = 42)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}
