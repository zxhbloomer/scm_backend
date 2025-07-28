package com.xinyirun.scm.bean.system.vo.business.so.ar;

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
 * @Description: 应收账款导出VO
 * @CreateTime : 2025/1/25 16:05
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BArExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1144329875661636282L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "应收账款编号", index = 1)
    private String code;

    @ExcelProperty(value = "状态", index = 2)
    private String status_name;

    @ExcelProperty(value = "业务类型", index = 3)
    private String type_name;

    @ExcelProperty(value = "流程状态", index = 4)
    private String next_approve_name;

    @ExcelProperty(value = "主体企业（销售方）", index = 5)
    private String seller_enterprise_name;

    @ExcelProperty(value = "客户（付款方）", index = 6)
    private String customer_enterprise_name;

    @ExcelProperty(value = {"关联合同", "合同号"}, index = 7)
    private String so_contract_code;

    @ExcelProperty(value = {"关联合同", "订单号"}, index = 8)
    private String so_order_code;

    @ExcelProperty(value = {"收款信息", "收款账户"}, index = 9)
    private String account_number;

    @ExcelProperty(value = {"收款信息", "收款类型"}, index = 10)
    private String accounts_purpose_type_name;

    @ExcelProperty(value = {"收款信息", "收款金额"}, index = 11)
    private BigDecimal receivable_amount;

    @ExcelProperty(value = {"收款信息", "备注"}, index = 12)
    private String remark;

    @ExcelProperty(value = "收款状态", index = 13)
    private String receive_status_name;

    @ExcelProperty(value = "应收金额总计", index = 14)
    private BigDecimal total_receivable_amount;

    @ExcelProperty(value = "已收款总金额", index = 15)
    private BigDecimal total_received_amount;

    @ExcelProperty(value = "收款中总金额", index = 16)
    private BigDecimal total_receiving_amount;

    @ExcelProperty(value = "未收款总金额", index = 17)
    private BigDecimal total_unreceive_amount;

    @ExcelProperty(value = "备注", index = 18)
    private String remarks;

    @ExcelProperty(value = "创建人", index = 19)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 20)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 21)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 22)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelIgnore
    private String ar_id;
}