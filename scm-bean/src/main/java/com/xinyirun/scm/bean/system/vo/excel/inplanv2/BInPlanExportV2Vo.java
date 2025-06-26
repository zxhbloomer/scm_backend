package com.xinyirun.scm.bean.system.vo.excel.inplanv2;

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
public class BInPlanExportV2Vo implements Serializable {

    private static final long serialVersionUID = 5391233167549062990L;

    @ExcelProperty(value="No", index = 0)
    @ColumnWidth(5)
    private Integer excel_no;

    @ExcelProperty(value={"计划单号", "单号"}, index = 1)
    private String plan_code;

    @ExcelProperty(value={"计划单号", "序号"}, index = 2)
    private Integer no;

    @ExcelProperty(value="计划日期", index = 3)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(25)
    private LocalDateTime plan_time;

    @ExcelProperty(value="状态", index = 4)
    private String status_name;

    @ExcelProperty(value="入库单数量", index = 5)
    private Integer in_counts;

    @ExcelProperty(value="合同编号", index = 6)
    private String contract_no;

    @ExcelProperty(value="订单编号", index = 7)
    private String order_no;

    @ExcelProperty(value="外部关联单号", index = 8)
    private String extra_code;

    @ExcelProperty(value="入库类型", index = 9)
    private String type_name;

    @ExcelProperty(value="物流合同", index = 10)
    private String waybill_code;

    @ExcelProperty(value="入库仓库", index = 11)
    private String warehouse_name;

    @ExcelProperty(value="委托方", index = 12)
    private String consignor_name;

    @ExcelProperty(value="货主", index = 13)
    private String owner_name;

    @ExcelProperty(value="供应商", index = 14)
    private String customer_name;

    @ExcelProperty(value="船名", index = 15)
    private String ship_name;

    @ExcelProperty(value="单据类型", index = 16)
    private String bill_type_name;

    @ExcelProperty(value="合同日期", index = 17)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime contract_dt;

    @ExcelProperty(value="合同量", index = 18)
    private BigDecimal contract_num;

    @ExcelProperty(value="物料名称", index = 19)
    private String goods_name;

    @ExcelProperty(value="品名", index = 20)
    private String pm;

    @ExcelProperty(value="规格", index = 21)
    private String spec;

    @ExcelProperty(value="型规", index =22)
    private String type_gauge;

    @ExcelProperty(value="规格编号", index = 23)
    private String sku_code;

    @ExcelProperty(value="计划入库数量", index = 24)
    private BigDecimal count;

    @ExcelProperty(value="单位", index = 25)
    private String unit_name;

    @ExcelProperty(value="换算后数量", index = 26)
    private BigDecimal weight;

    @ExcelProperty(value="单位", index = 27)
    private String unit_name1 = "吨";

    @ExcelProperty(value="已入库数量", index = 28)
    private BigDecimal has_handle_count;

    @ExcelProperty(value="待入库数量", index = 29)
    private BigDecimal pending_count;

    @ExcelProperty(value="作废理由", index = 30)
    private String cancel_remark;

    @ExcelProperty(value="创建人", index = 31)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 32)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="更新人", index = 33)
    private String u_name;

    @ExcelProperty(value="更新时间", index = 34)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelProperty(value="审核人", index = 35)
    private String audit_name;

    @ExcelProperty(value="审核时间", index = 36)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime audit_dt;

    @ExcelProperty(value="作废审核人", index = 37)
    private String cancel_audit_name;

    @ExcelProperty(value="作废审核时间", index = 38)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime cancel_audit_dt;

}
