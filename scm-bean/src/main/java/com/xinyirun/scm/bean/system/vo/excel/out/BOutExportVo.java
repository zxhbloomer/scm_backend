package com.xinyirun.scm.bean.system.vo.excel.out;

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
public class BOutExportVo implements Serializable {

    private static final long serialVersionUID = 2353546042038297923L;

    @ExcelProperty(value="No", index = 0)
    @ColumnWidth(5)
    private Integer excel_no;

    @ExcelProperty(value="出库单号", index = 1)
    private String code;

    @ExcelProperty(value={"计划单号", "单号"}, index = 2)
    private String plan_code;

    @ExcelProperty(value={"计划单号", "序号"}, index = 3)
    private Integer no;

    @ExcelProperty(value="状态", index = 4)
    private String status_name;

    @ExcelProperty(value="出库类型", index = 5)
    private String type_name;

    @ExcelProperty(value="出库仓库（简称）", index = 6)
    private String warehouse_name;

    @ExcelProperty(value="出库仓库（全称）", index = 7)
    private String warehouse_full_name;


    @ExcelProperty(value="委托方", index = 8)
    private String consignor_name;

    @ExcelProperty(value="货主", index = 9)
    private String owner_name;

    @ExcelProperty(value="客户", index = 10)
    private String client_name;

    @ExcelProperty(value="合同编号", index = 11)
    private String contract_no;

    @ExcelProperty(value="外部关联单号", index = 12)
    private String extra_code;

    @ExcelProperty(value="放货指令编号", index = 13)
    private String release_order_code;

    @ExcelProperty(value="单据类型", index = 14)
    private String bill_type_name;

    @ExcelProperty(value="合同日期", index = 15)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(20)
    private LocalDateTime contract_dt;

    @ExcelProperty(value="合同量", index = 16)
    private BigDecimal contract_num;

    @ExcelProperty(value="物料名称", index = 17)
    private String goods_name;

    @ExcelProperty(value="品名", index = 18)
    private String pm;

    @ExcelProperty(value="规格", index = 19)
    private String spec;

    @ExcelProperty(value="规格编号", index = 20)
    private String sku_code;

    @ExcelProperty(value = "型规", index = 21)
    private String type_gauge;

    @ExcelProperty(value = "别称", index = 22)
    private String alias;

    @ExcelProperty(value="毛重", index = 23)
    private BigDecimal gross_weight;

    @ExcelProperty(value="皮重", index = 24)
    private BigDecimal tare_weight;

    @ExcelProperty(value="出库数量", index = 25)
    private BigDecimal actual_count;

    @ExcelProperty(value="退货数量", index = 26)
    private BigDecimal return_qty;

    @ExcelProperty(value="换算比例", index = 27)
    private String cale_name;

    @ExcelProperty(value="换算出库数量", index = 28)
    private BigDecimal actual_count_return;

    @ExcelProperty(value="实际换算后数量", index = 29)
    private BigDecimal actual_weight_return;

    @ExcelProperty(value="单位", index = 30)
    private String unit_name;

    @ExcelProperty(value="换算后数量", index = 31)
    private BigDecimal actual_weight;

    @ExcelProperty(value="单位", index = 32)
    private String unit_name1 = "吨";

    @ExcelProperty(value="单价", index = 33)
    private BigDecimal price;

    @ExcelProperty(value="出库金额", index = 34)
    private BigDecimal amount;

    @ExcelProperty(value = "车牌号", index = 35)
    private String vehicle_no;

    @ExcelProperty(value="出库时间", index = 36)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime outbound_time;

    @ExcelProperty(value="作废理由", index = 37)
    private String cancel_remark;

    @ExcelProperty(value="创建人", index = 38)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 39)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="审核人", index = 40)
    private String e_name;

    @ExcelProperty(value="审核时间", index = 41)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime e_dt;

    @ExcelProperty(value="更新人", index = 42)
    private String u_name;

    @ExcelProperty(value="更新时间", index = 43)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelProperty(value="作废审核人", index = 44)
    private String cancel_audit_name;

    @ExcelProperty(value="作废审核时间", index = 45)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime cancel_audit_dt;

    /**
     * 换算后比例
     */
    private BigDecimal calc;

    /**
     * 换算前
     */
    private String src_unit;

    /**
     * 换算后
     */
    private String tgt_unit;

    public String getCale_name() {
        return src_unit != null && tgt_unit != null && calc != null ? src_unit + ":" + tgt_unit + "[" + calc + "]" : null;
    }
}
