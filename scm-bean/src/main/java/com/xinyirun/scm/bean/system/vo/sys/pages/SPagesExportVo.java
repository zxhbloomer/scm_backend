package com.xinyirun.scm.bean.system.vo.sys.pages;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 页面表 导出
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SPagesExportVo implements Serializable {

    
    private static final long serialVersionUID = 3542386670109918575L;

    @ExcelProperty(value = "NO", index = 0)
    private int no;

    @ExcelProperty(value = "页面编号", index = 1)
    private String code;

    @ExcelProperty(value = "页面名称", index = 2)
    private String name;

    @ExcelProperty(value = "页面地址", index = 3)
    private String component;

    @ExcelProperty(value = "权限标识", index = 4)
    private String perms;

    @ExcelProperty(value = "更新人", index = 5)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 6)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;
}
