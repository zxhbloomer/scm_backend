package com.xinyirun.scm.bean.system.vo.business.po.pocontract;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description:
 * @CreateTime : 2025/1/14 16:05
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BPoContractExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1144329875661636281L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "项目编号", index = 1)
    private String project_code;

    @ExcelProperty(value = "合同编号", index = 2)
    private String contract_code;

    @ExcelProperty(value = "类型", index = 3)
    private String type_name;

    @ExcelProperty(value = "订单笔数", index = 4)
    private Integer order_count;

    @ExcelProperty(value = "状态", index = 5)
    private String status_name;

    @ExcelProperty(value = "审批情况", index = 6)
    private String next_approve_name;

    @ExcelProperty(value = "供应商", index = 7)
    private String supplier_name;

    @ExcelProperty(value = "采购方（主体企业）", index = 8)
    private String purchaser_name;

    @ExcelProperty(value = "执行进度", index = 9)
    private String virtual_progress;

    @ExcelProperty(value = "签约日期", index = 10)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime sign_date;

    @ExcelProperty(value = "到期日期", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime expiry_date;

    @ExcelProperty(value = "交货日期", index = 12)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime delivery_date;

    @ExcelProperty(value = "运输方式", index = 13)
    private String delivery_type_name;

    @ExcelProperty(value = "结算方式", index = 14)
    private String settle_type_name;

    @ExcelProperty(value = "结算单据类型", index = 15)
    private String bill_type_name;

    @ExcelProperty(value = "付款方式", index = 16)
    private String payment_type_name;

    @ExcelProperty(value = "交货地点", index = 17)
    private String delivery_location;

    @ExcelProperty(value = "自动生成订单", index = 18)
    private String auto_create_name;

    @ExcelProperty(value = "备注", index = 19)
    private String remark;

    @ExcelProperty(value = "合同总金额", index = 20)
    private BigDecimal contract_amount_sum;

    @ExcelProperty(value = "总采购数量（吨）", index = 21)
    private BigDecimal contract_total;

    @ExcelProperty(value = "税额", index = 22)
    private BigDecimal tax_amount_sum;

    @ExcelProperty(value = "已结算数量（吨）", index = 23)
    private BigDecimal settled_qty_total;

    @ExcelProperty(value = "结算金额", index = 24)
    private BigDecimal settled_amount_total;

    @ExcelProperty(value = "预付款金额", index = 25)
    private BigDecimal advance_paid_total;

    @ExcelProperty(value = "累计实付", index = 26)
    private BigDecimal virtual_total_paid_amount;

    @ExcelProperty(value = "未付", index = 27)
    private BigDecimal virtual_unpaid_amount;

    @ExcelProperty(value = "预付款可退金额", index = 28)
    private BigDecimal advance_refund_amount_total;

    @ExcelProperty(value = "已开票金额", index = 29)
    private BigDecimal already_invoice_price;

    @ExcelProperty(value = {"商品", "商品编码"}, index = 30)
    private String sku_code;

    @ExcelProperty(value = {"商品", "商品名称"}, index = 31)
    private String goods_name;

    @ExcelProperty(value = {"商品", "规格"}, index = 32)
    private String sku_name;

    @ExcelProperty(value = {"商品", "产地"}, index = 33)
    private String origin;

    @ExcelProperty(value = {"商品", "数量"}, index = 34)
    private BigDecimal qty;

    @ExcelProperty(value = {"商品", "单价"}, index = 35)
    private BigDecimal price;

    @ExcelProperty(value = {"商品", "税率"}, index = 36)
    private String tax_rate;

    @ExcelProperty(value = "创建人", index = 37)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 38)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 39)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 40)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelIgnore
    private Integer sku_id;
}
