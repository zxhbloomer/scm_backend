package com.xinyirun.scm.bean.system.vo.master.customer;

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
 * 货主
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MOwnerExportVo implements Serializable {

    private static final long serialVersionUID = -2339776769867039393L;

    @ExcelProperty(value = "No", index = 0)
    @ColumnWidth(15)
    private Integer no;

    @ExcelProperty(value = "货主编码", index = 1)
    private String code;

    @ExcelProperty(value = "货主名称", index = 2)
    private String name;

    @ExcelProperty(value = "货主简称", index = 3)
    private String short_name;

    @ExcelProperty(value = "是否启用", index = 4)
    private String enable;

    @ExcelProperty(value = "创建人", index = 5)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 6)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 7)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 8)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime u_time;

}
