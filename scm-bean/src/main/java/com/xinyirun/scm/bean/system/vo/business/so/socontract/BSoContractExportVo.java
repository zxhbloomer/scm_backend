package com.xinyirun.scm.bean.system.vo.business.so.socontract;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.NumberFormat;
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
 * 销售合同导出VO - 重构版本
 * 
 * 基于前端列表页面完整字段分析重新设计，确保与前端显示100%匹配
 * 参照PO项目管理ExportVo的成功设计模式，支持AbstractBusinessMergeStrategy
 * 
 * 合并逻辑：
 * - 合并列：0-26（基础合同信息+财务汇总）+ 34-37（审计信息）
 * - 不合并：27-33（商品信息，每个商品明细独立显示）
 * - 分组字段：合同编号(contract_code)
 * 
 * @author SCM系统
 * @version 2.0 - 完整字段版本
 * @since 2025-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BSoContractExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1144329875661636282L;

    // ========== 基础合同信息（合并字段：0-8） ==========
    
    @ExcelProperty(value = "No", index = 0)
    private Integer no;

    @ExcelProperty(value = "项目编号", index = 1)
    private String project_code;

    @ExcelProperty(value = "合同编号", index = 2)
    private String contract_code;

    @ExcelProperty(value = "类型", index = 3)
    private String type_name;

    @ExcelProperty(value = "订单笔数", index = 4)
    private String order_count;

    @ExcelProperty(value = "状态", index = 5)
    private String status_name;

    @ExcelProperty(value = "审批情况", index = 6)
    private String approval_status;

    @ExcelProperty(value = "客户", index = 7)
    private String customer_name;

    @ExcelProperty(value = "销售方（主体企业）", index = 8)
    private String seller_name;

    // ========== 业务执行信息（合并字段：9-16） ==========
    
    @ExcelProperty(value = "执行进度", index = 9)
    private String virtual_progress;

    @ExcelProperty(value = "签约日期", index = 10)
    @ColumnWidth(20)
    private String sign_date_formatted;

    @ExcelProperty(value = "到期日期", index = 11)
    @ColumnWidth(20)
    private String expiry_date_formatted;

    @ExcelProperty(value = "交货日期", index = 12)
    @ColumnWidth(20)
    private String delivery_date_formatted;

    @ExcelProperty(value = "运输方式", index = 13)
    private String delivery_type_name;

    @ExcelProperty(value = "结算方式", index = 14)
    private String settle_type_name;

    @ExcelProperty(value = "结算单据类型", index = 15)
    private String bill_type_name;

    @ExcelProperty(value = "付款方式", index = 16)
    private String payment_type_name;

    // ========== 财务汇总信息（合并字段：17-26） ==========
    
    @ExcelProperty(value = "合同总金额", index = 17)
    @NumberFormat("¥#,##0.00")
    private BigDecimal contract_amount_sum;

    @ExcelProperty(value = "总销售数量（吨）", index = 18)
    @NumberFormat("#,##0.0000")
    private BigDecimal contract_total;

    @ExcelProperty(value = "税额", index = 19)
    @NumberFormat("¥#,##0.00")
    private BigDecimal tax_amount_sum;

    @ExcelProperty(value = "已结算数量（吨）", index = 20)
    @NumberFormat("#,##0.0000")
    private BigDecimal settled_qty;

    @ExcelProperty(value = "结算金额", index = 21)
    @NumberFormat("¥#,##0.00")
    private BigDecimal settled_price;

    @ExcelProperty(value = "预收款金额", index = 22)
    @NumberFormat("¥#,##0.00")
    private BigDecimal advance_receive_price;

    @ExcelProperty(value = "累计实收", index = 23)
    @NumberFormat("¥#,##0.00")
    private BigDecimal accumulated_act_price;

    @ExcelProperty(value = "未收", index = 24)
    @NumberFormat("¥#,##0.00")
    private BigDecimal unreceived_amount;

    @ExcelProperty(value = "预收款可退金额", index = 25)
    @NumberFormat("¥#,##0.00")
    private BigDecimal advance_receive_rt_price;

    @ExcelProperty(value = "可开票金额", index = 26)
    @NumberFormat("¥#,##0.00")
    private BigDecimal already_invoice_price;

    // ========== 商品信息（不合并字段：27-33） ==========
    
    @ExcelProperty(value = {"商品信息", "商品编码"}, index = 27)
    private String sku_code;

    @ExcelProperty(value = {"商品信息", "商品名称"}, index = 28)
    private String goods_name;

    @ExcelProperty(value = {"商品信息", "规格"}, index = 29)
    private String sku_name;

    @ExcelProperty(value = {"商品信息", "商品产地"}, index = 30)
    private String origin;

    @ExcelProperty(value = {"商品信息", "商品数量"}, index = 31)
    @NumberFormat("#,##0.0000")
    private BigDecimal qty;

    @ExcelProperty(value = {"商品信息", "商品单价"}, index = 32)
    @NumberFormat("¥#,##0.00")
    private BigDecimal price;

    @ExcelProperty(value = {"商品信息", "商品税率"}, index = 33)
    private String tax_rate;

    // ========== 审计信息（合并字段：34-37） ==========
    
    @ExcelProperty(value = "创建人", index = 34)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 35)
    @ColumnWidth(20)
    private String c_time_formatted;

    @ExcelProperty(value = "更新人", index = 36)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 37)
    @ColumnWidth(20)
    private String u_time_formatted;

    // ========== 内部字段（不导出） ==========
    
    @ExcelIgnore
    private Integer sku_id;
}