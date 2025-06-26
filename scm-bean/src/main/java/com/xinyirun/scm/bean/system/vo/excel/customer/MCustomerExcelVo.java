package com.xinyirun.scm.bean.system.vo.excel.customer;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public class MCustomerExcelVo implements Serializable {


    private static final long serialVersionUID = 8999265309732803282L;

    @ExcelProperty(value = "No", index = 0)
    @ColumnWidth(15)
    private Integer no;

    @ExcelProperty(value = "客户编码", index = 1)
    private String code;

    @ExcelProperty(value = "客户名称", index = 2)
    private String name;

    @ExcelProperty(value = "客户简称", index = 3)
    private String short_name;

    @ExcelProperty(value = "所属集团", index = 4)
    private Integer superior_id;

    @ExcelProperty(value = "联系电话", index = 5)
    private String contact_number;

    @ExcelProperty(value = "启用状态", index = 6)
    private String enable;

    @ExcelProperty(value="创建人", index = 7)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 8)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="更新人", index = 9)
    private String u_name;

    @ExcelProperty(value="更新时间", index = 10)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}
