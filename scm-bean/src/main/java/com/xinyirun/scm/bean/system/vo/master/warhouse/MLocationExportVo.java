package com.xinyirun.scm.bean.system.vo.master.warhouse;

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
 * 库区导出VO - 完全基于列表页面字段设计
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MLocationExportVo implements Serializable {

    private static final long serialVersionUID = 3920669632719750242L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "库区名称", index = 1)
    private String name;

    @ExcelProperty(value = "库区简称", index = 2)
    private String short_name;

    @ExcelProperty(value = "所属仓库", index = 3)
    private String warehouse_name;

    @ExcelProperty(value = "启用状态", index = 4)
    private String enable_status;

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
