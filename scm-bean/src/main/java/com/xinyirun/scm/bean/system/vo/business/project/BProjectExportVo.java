package com.xinyirun.scm.bean.system.vo.business.project;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.format.NumberFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目管理导出专用VO
 * 采用扁平化结构，支持FastExcel多级表头
 * 用于处理项目管理数据的Excel导出功能
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Data
@NoArgsConstructor
public class BProjectExportVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 7178169190694058248L;

    @ExcelProperty(value = "No", index = 0)
    private Integer no;

    @ExcelProperty(value = "项目编号", index = 1)
    private String code;

    @ExcelProperty(value = "项目名称", index = 2)
    private String name;

    @ExcelProperty(value = "状态", index = 3)
    private String status_name;

    @ExcelProperty(value = "审批情况", index = 4)
    private String approval_status;

    @ExcelProperty(value = "类型", index = 5)
    private String type_name;

    @ExcelProperty(value = "上游供应商", index = 6)
    private String supplier_name;

    @ExcelProperty(value = "下游客户（主体企业）", index = 7)
    private String purchaser_name;

    // 商品相关字段 - 使用多级表头
    @ExcelProperty(value = {"商品", "商品编码"}, index = 8)
    private String sku_code;

    @ExcelProperty(value = {"商品", "商品名称"}, index = 9)
    private String goods_name;

    @ExcelProperty(value = {"商品", "规格"}, index = 10)
    private String sku_name;

    @ExcelProperty(value = {"商品", "产地"}, index = 11)
    private String origin;

    @ExcelProperty(value = {"商品", "数量"}, index = 12)
    @NumberFormat("#,##0.0000")
    private BigDecimal qty;

    @ExcelProperty(value = {"商品", "单价"}, index = 13)
    @NumberFormat("¥#,##0.00")
    private BigDecimal price;

    @ExcelProperty(value = {"商品", "税率（%）"}, index = 14)
    private String tax_rate;

    @ExcelProperty(value = "付款方式", index = 15)
    private String payment_method_name;

    @ExcelProperty(value = "是否有账期/天数", index = 16)
    private String payment_days;

    @ExcelProperty(value = "融资额度", index = 17)
    @NumberFormat("¥#,##0.00")
    private BigDecimal amount;

    @ExcelProperty(value = "项目周期", index = 18)
    private String project_cycle;

    @ExcelProperty(value = "费率", index = 19)
    private String rate;

    @ExcelProperty(value = "交货地点", index = 20)
    private String delivery_location;

    @ExcelProperty(value = "运输方式", index = 21)
    private String delivery_type_name;

    @ExcelProperty(value = "备注", index = 22)
    private String remark;

    @ExcelProperty(value = "创建人", index = 23)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 24)
    private String c_time_formatted;

    @ExcelProperty(value = "更新人", index = 25)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 26)
    private String u_time_formatted;
}