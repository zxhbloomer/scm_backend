package com.xinyirun.scm.bean.system.vo.business.so.socontract;

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
 * 销售合同导出VO
 * @Description: 基于BPoContractExportVo创建，采购对标销售的字段映射
 * @CreateTime : 2025/1/20 16:05
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BSoContractExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1144329875661636282L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "编号", index = 1)
    private String code;

    @ExcelProperty(value = "项目编号", index = 2)
    private String project_code;

    @ExcelProperty(value = "合同编号", index = 3)
    private String contract_code;

    @ExcelProperty(value = "类型", index = 4)
    private String type_name;

    @ExcelProperty(value = "订单量", index = 5)
    private String order_count;

    @ExcelProperty(value = "状态", index = 6)
    private String status_name;

    @ExcelProperty(value = "客户", index = 7)
    private String customer_name;

    @ExcelProperty(value = "主体企业", index = 8)
    private String seller_name;


    @ExcelProperty(value = "签约日期", index = 10)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime sign_date;

    @ExcelProperty(value = "到期日期", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime expiry_date;

    @ExcelProperty(value = "交货日期", index = 12)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime delivery_date;

    @ExcelProperty(value = "运输方式", index = 13)
    private String delivery_type_name;

    @ExcelProperty(value = "结算方式", index = 14)
    private String settle_type_name;

    @ExcelProperty(value = "结算单据类型", index = 15)
    private String bill_type_name;

    @ExcelProperty(value = "收款方式", index = 16)
    private String payment_type_name;

    @ExcelProperty(value = "合同总金额", index = 17)
    private BigDecimal contract_amount_sum;

    @ExcelProperty(value = "总销售数量", index = 18)
    private BigDecimal contract_total;

    @ExcelProperty(value = "总税额", index = 19)
    private BigDecimal tax_amount_sum;

    @ExcelProperty(value = "已结算数量", index = 20)
    private BigDecimal settled_qty;

    @ExcelProperty(value = "结算金额", index = 21)
    private BigDecimal settled_price;

    @ExcelProperty(value = "预收款金额", index = 22)
    private BigDecimal advance_receive_price;

    @ExcelProperty(value = "累计实收", index = 23)
    private BigDecimal accumulated_act_price;

    @ExcelProperty(value = "未收", index = 24)
    private BigDecimal unreceived_amount;

    @ExcelProperty(value = "预收款可退金额", index = 25)
    private BigDecimal advance_receive_rt_price;

    @ExcelProperty(value = "可开票金额", index = 26)
    private BigDecimal already_invoice_price;

    @ExcelProperty(value = {"商品信息", "商品编码"}, index = 27)
    private String sku_code;

    @ExcelProperty(value = {"商品信息", "商品名称"}, index = 28)
    private String sku_name;

    @ExcelProperty(value = {"商品信息", "商品产地"}, index = 29)
    private String origin;

    @ExcelProperty(value = {"商品信息", "商品数量"}, index = 30)
    private BigDecimal qty;

    @ExcelProperty(value = {"商品信息", "商品单价"}, index = 31)
    private BigDecimal price;

    @ExcelProperty(value = {"商品信息", "商品税率"}, index = 32)
    private BigDecimal tax_rate;

    @ExcelProperty(value = "创建人", index = 33)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 34)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 35)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 36)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelIgnore
    private Integer sku_id;
}