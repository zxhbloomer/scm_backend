package com.xinyirun.scm.bean.system.vo.excel.in;

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
public class BInExportVo implements Serializable {

    private static final long serialVersionUID = 5391233167549062990L;

    @ExcelProperty(value="No", index = 0)
    @ColumnWidth(5)
    private Integer excel_no;

    @ExcelProperty(value="入库单号", index = 1)
    private String code;

    @ExcelProperty(value={"计划单号", "单号"}, index = 2)
    private String plan_code;

    @ExcelProperty(value={"计划单号", "序号"}, index = 3)
    private Integer no;

    @ExcelProperty(value="状态", index = 4)
    private String status_name;

    @ExcelProperty(value="入库类型", index = 5)
    private String type_name;

    @ExcelProperty(value="入库仓库（简称）", index = 6)
    private String warehouse_name;

    @ExcelProperty(value="入库仓库（全称）", index = 7)
    private String warehouse_full_name;

    @ExcelProperty(value="委托方", index = 8)
    private String consignor_name;

    @ExcelProperty(value="货主", index = 9)
    private String owner_name;

    @ExcelProperty(value="供应商", index = 10)
    private String customer_name;

    @ExcelProperty(value="船名", index = 11)
    private String ship_name;

    @ExcelProperty(value="合同编号", index = 12)
    private String contract_no;

    @ExcelProperty(value="单据类型", index = 13)
    private String bill_type_name;

    @ExcelProperty(value="合同日期", index = 14)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(20)
    private LocalDateTime contract_dt;

    @ExcelProperty(value="合同量", index = 15)
    private BigDecimal contract_num;

    @ExcelProperty(value="物流合同", index = 16)
    private String waybill_code;

    @ExcelProperty(value="物料名称", index = 17)
    private String goods_name;

    @ExcelProperty(value="品名", index = 18)
    private String pm;

    @ExcelProperty(value="规格", index = 19)
    private String spec;

    @ExcelProperty(value="规格编号", index = 20)
    private String sku_code;

    @ExcelProperty(value="原发数量", index = 21)
    private BigDecimal primary_quantity;

    @ExcelProperty(value="毛重", index = 22)
    private BigDecimal gross_weight;

    @ExcelProperty(value="皮重", index = 23)
    private BigDecimal tare_weight;

    @ExcelProperty(value="入库数量", index = 24)
    private BigDecimal actual_count;

    @ExcelProperty(value="单位", index = 25)
    private String unit_name;

    @ExcelProperty(value="换算后数量", index = 26)
    private BigDecimal actual_weight;

    @ExcelProperty(value="单位", index = 27)
    private String unit_name1 = "吨";

    @ExcelProperty(value="单价", index = 28)
    private BigDecimal price;

    @ExcelProperty(value="入库金额", index = 29)
    private BigDecimal amount;

    @ExcelProperty(value="实收车数", index = 30)
    private Integer car_count;

    @ExcelProperty(value="车牌号", index = 31)
    private String vehicle_no;

    @ExcelProperty(value="入库时间", index = 32)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime inbound_time;

    @ExcelProperty(value="作废理由", index = 33)
    private String cancel_remark;

    @ExcelProperty(value="创建人", index = 34)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 35)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="审核人", index = 36)
    private String e_name;

    @ExcelProperty(value="审核时间", index = 37)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime e_dt;

    @ExcelProperty(value="更新人", index = 38)
    private String u_name;

    @ExcelProperty(value="更新时间", index = 39)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelProperty(value="作废审核人", index = 40)
    private String cancel_audit_name;

    @ExcelProperty(value="作废审核时间", index = 41)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime cancel_audit_dt;

}
