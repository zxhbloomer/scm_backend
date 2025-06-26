package com.xinyirun.scm.bean.system.vo.excel.customer;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MEnterpiseExcelVo implements Serializable {


    private static final long serialVersionUID = 8999265309732803282L;

    @ExcelProperty(value = "No", index = 0)
    @ColumnWidth(15)
    private Integer no;

    @ExcelProperty(value = "企业信用代码", index = 1)
    private String credit_no;

    @ExcelProperty(value = "企业名称", index = 2)
    private String name;

    @ExcelProperty(value = "曾用名", index = 3)
    private String former_name;

    @ExcelProperty(value = "法定代表人", index = 4)
    private String legal_person;

    @ExcelProperty(value = "注册资金", index = 5)
    private BigDecimal registration_capital;

    @ExcelProperty(value = "联系人", index = 6)
    private String contact_person;

    @ExcelProperty(value = "联系电话", index = 7)
    private String contact_number;

    @ExcelProperty(value = "企业类型", index = 8)
    private String type_ids_str;

    @ExcelProperty(value = "状态", index = 9)
    private String audit_status_name;

    @ExcelProperty(value = "创建人", index = 10)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 12)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 13)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelProperty(value = "是否黑名单", index = 14)
    private String blacklist;

}
