package com.xinyirun.scm.bean.system.vo.excel.container;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/6/8 9:55
 */

@Data
public class MContainerExcelVo {

    @ExcelProperty(value = "no", index = 0)
    @ColumnWidth(10)
    private Integer no;

    @ExcelProperty(value = "箱号", index = 1)
    private String code;

    @ExcelProperty(value = "创建人", index = 2)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 3)
    @ColumnWidth(25)
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 4)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 5)
    @ColumnWidth(25)
    private LocalDateTime u_time;
}
