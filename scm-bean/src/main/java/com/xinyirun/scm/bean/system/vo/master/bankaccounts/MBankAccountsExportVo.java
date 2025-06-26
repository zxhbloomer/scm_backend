package com.xinyirun.scm.bean.system.vo.master.bankaccounts;

import cn.idev.excel.annotation.ExcelProperty;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业银行账户表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MBankAccountsExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5840747088254885380L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "主体企业名称", index = 1)
    private String enterprise_name;

    @ExcelProperty(value = "账户编码", index = 2)
    private String code;

    @ExcelProperty(value = "账户名称", index = 3)
    private String name;

    @ExcelProperty(value = "银行账号", index = 4)
    private String account_number;

    @ExcelProperty(value = "开户名", index = 5)
    private String holder_name;

    @ExcelProperty(value = "开户行", index = 6)
    private String bank_name;

    @ExcelProperty(value = "币别", index = 7)
    private String currency;

    @ExcelProperty(value = "是否默认", index = 8)
    private String is_default_name;

    @ExcelProperty(value = "备注", index = 9)
    private String remarks;

    @ExcelProperty(value = "状态", index = 10)
    private String status_name;

    @ExcelProperty(value = "银企互联状态", index = 11)
    private String link_status_name;

    @ExcelProperty(value = "创建人", index = 12)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 13)
    private LocalDateTime c_time;

    @ExcelProperty(value = "修改人", index = 14)
    private String u_name;

    @ExcelProperty(value = "修改时间", index = 15)
    private LocalDateTime u_time;
}






