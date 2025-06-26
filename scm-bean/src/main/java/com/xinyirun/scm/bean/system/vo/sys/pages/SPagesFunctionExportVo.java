package com.xinyirun.scm.bean.system.vo.sys.pages;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 页面按钮表
 * </p>
 *
 * @author zxh
 * @since 2020-06-04
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SPagesFunctionExportVo implements Serializable {

    private static final long serialVersionUID = 6408649934054725143L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "页面编号", index = 1)
    private String page_code;

    @ExcelProperty(value = "页面名称", index = 2)
    private String page_name;

    @ExcelProperty(value = "页面权限标识", index = 3)
    private String page_perms;

    @ExcelProperty(value = "按钮编号", index = 4)
    private String function_code;

    @ExcelProperty(value = "按钮名称", index = 5)
    private String function_name;

    @ExcelProperty(value = "权限标识", index = 6)
    private String perms;

    @ExcelProperty(value = "排序", index = 7)
    private Integer sort;

    @ExcelProperty(value = "更新人", index = 8)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 9)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime u_time;

}
