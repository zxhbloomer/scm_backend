package com.xinyirun.scm.bean.system.vo.master.category;

import cn.idev.excel.annotation.ExcelIgnore;
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
 * <p>
 * 类别主表导出VO - 按照仓库导出模式设计
 * </p>
 *
 * @author zxh
 * @since 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MCategoryExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8990646953974899181L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "类别名称", index = 1)
    private String name;

    @ExcelProperty(value = "类别编号", index = 2)
    private String code;

    @ExcelProperty(value = "状态", index = 3)
    private String enable;

    @ExcelProperty(value = "创建人", index = 4)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 6)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 7)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;
}