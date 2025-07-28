package com.xinyirun.scm.bean.system.vo.business.so.arrefund;

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
 * <p>
 * 应收退款管理表导出VO（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BArReFundExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3847592018471956283L;

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

    @ExcelProperty(value = "主体企业（收款方）", index = 5)
    private String seller_enterprise_name;

    @ExcelProperty(value = "客户（付款方）", index = 6)
    private String customer_enterprise_name;

    @ExcelProperty(value = {"关联合同", "合同号"}, index = 7)
    private String so_contract_code;

    @ExcelProperty(value = {"收款信息", "收款账户"}, index = 8)
    private String account_number;

    @ExcelProperty(value = {"收款信息", "收款类型"}, index = 9)
    private String accounts_purpose_type_name;

    @ExcelProperty(value = {"收款信息", "可退款金额"}, index = 11)
    private BigDecimal refundable_amount;

    @ExcelProperty(value = {"收款信息", "备注"}, index = 12)
    private String remark;

    @ExcelProperty(value = "退款状态", index = 13)
    private String refund_status_name;

    @ExcelProperty(value = "申请退款总金额", index = 14)
    private BigDecimal total_refund_amount;

    @ExcelProperty(value = "已退款总金额", index = 15)
    private BigDecimal total_refunded_amount;

    @ExcelProperty(value = "退款中总金额", index = 16)
    private BigDecimal total_refunding_amount;

    @ExcelProperty(value = "未退款总金额", index = 17)
    private BigDecimal total_unrefund_amount;

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
    private String ar_refund_id;
}