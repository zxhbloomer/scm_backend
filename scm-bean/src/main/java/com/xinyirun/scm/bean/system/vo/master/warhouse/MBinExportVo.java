package com.xinyirun.scm.bean.system.vo.master.warhouse;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库位
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MBinExportVo implements Serializable {

    private static final long serialVersionUID = 7093744223944949243L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(15)
    private Integer no;

    @ExcelProperty(value = "所属仓库", index = 1)
    private String warehouse_name;

    @ExcelProperty(value = "是否启用", index = 2)
    private String enable;

    @ExcelProperty(value = "创建人", index = 3)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 4)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 5)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 6)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(20)
    private LocalDateTime u_time;

}
