package com.xinyirun.scm.bean.system.vo.business.monitor;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorExportVo implements Serializable {

    private static final long serialVersionUID = 5644274572207822207L;

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
    @ColumnWidth(20)
    private String out_time;

    @ExcelProperty(value="收货类型", index = 16)
    private String in_type_name;

    @ExcelProperty(value="收货地", index = 17)
    private String in_warehouse_name;

    @ExcelProperty(value="收货地类型", index = 18)
    private String in_warehouse_type_name;

    @ExcelProperty(value="收货详细地址", index = 19)
    private String in_warehouse_address;

    @ExcelProperty(value="入库/卸货时间", index = 20)
    @ColumnWidth(20)
    private String in_time;

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

    @ExcelProperty(value="验车状态", index = 28)
    private String validate_vehicle;

    @ExcelProperty(value="箱号", index = 29)
    private String container_code;

    @ExcelProperty(value="发货数量", index = 30)
    private BigDecimal out_qty;

    @ExcelProperty(value="发货单位", index = 31)
    private String unit_name1 = "吨";

    @ExcelProperty(value="收货数量", index = 32)
    private BigDecimal in_qty;

    @ExcelProperty(value="收货单位", index = 33)
    private String unit_name2 = "吨";

    @ExcelProperty(value="损耗", index = 34)
    private BigDecimal qty_loss;

    @ExcelProperty(value="退货数量", index = 35)
    private BigDecimal return_qty;

    @ExcelProperty(value="是否有退货", index = 36)
    private String if_return_qty_name;

    @ExcelProperty(value="作废理由", index = 37)
    private String cancel_remark;

    @ExcelProperty(value="出库审核人", index = 38)
    private String out_audit_name;

    @ExcelProperty(value="出库审核时间", index = 39)
    @ColumnWidth(20)
    private String out_audit_time;

    @ExcelProperty(value="入库审核人", index = 40)
    private String in_audit_name;

    @ExcelProperty(value="入库审核时间", index = 41)
    @ColumnWidth(20)
    private String in_audit_time;

    @ExcelProperty(value="备份状态", index = 42)
    private String backup_status_name;

    @ExcelProperty(value="备份结果", index = 43)
    private String backup_flag;

    @ExcelProperty(value="创建人", index = 44)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 45)
    @ColumnWidth(20)
    private String c_time;

    @ExcelProperty(value="修改人", index = 46)
    private String u_name;

    @ExcelProperty(value="修改时间", index = 47)
    @ColumnWidth(20)
    private String u_time;

}
