package com.xinyirun.scm.bean.system.vo.master.goods;

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
 * 类别
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MCategoryExportVo implements Serializable {

    private static final long serialVersionUID = -5807294006025057527L;

    @ColumnWidth(15)
    @ExcelProperty(value = "No", index = 0)
    private Integer no;

    @ExcelProperty(value = "所属板块", index = 1)
    private String business_name;

    @ExcelProperty(value = "所属行业", index = 2)
    private String industry_name;

    @ExcelProperty(value = "类别名称", index = 3)
    private String name;

    @ExcelProperty(value = "类别编号", index = 4)
    private String code;

    @ExcelProperty(value = "启用状态", index = 5)
    private String enable;

    @ExcelProperty(value="创建人", index = 6)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 7)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="更新人", index = 8)
    private String u_name;

    @ExcelProperty(value="更新时间", index = 9)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}
