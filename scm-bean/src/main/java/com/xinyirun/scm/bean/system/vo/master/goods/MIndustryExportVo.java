package com.xinyirun.scm.bean.system.vo.master.goods;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 行业
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MIndustryExportVo implements Serializable {

    private static final long serialVersionUID = 7311240187706065527L;

    @ExcelProperty(value = "No", index = 0)
    @ColumnWidth(12)
    private Integer no;

    @ExcelProperty(value = "板块名称", index = 1)
    private String business_name;

    @ExcelProperty(value = "行业名称", index = 2)
    private String name;

    @ExcelProperty(value = "行业编号", index = 3)
    private String code;

    @ExcelProperty(value = "启用状态", index = 4)
    private String enable;

    @ExcelProperty(value = "创建人", index = 5)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 6)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 7)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 8)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}
