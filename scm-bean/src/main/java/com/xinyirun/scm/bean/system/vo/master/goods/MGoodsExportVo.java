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
 * 物料
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MGoodsExportVo implements Serializable {


    private static final long serialVersionUID = -664209375727684307L;

    @ColumnWidth(15)
    @ExcelProperty(value = "No", index = 0)
    private Integer no;

    @ExcelProperty(value = "板块名称", index = 1)
    private String business_name;

    @ExcelProperty(value = "行业名称", index = 2)
    private String industry_name;

    @ExcelProperty(value = "类别名称", index = 3)
    private String category_name;

    @ExcelProperty(value = "物料名称", index = 4)
    private String name;

    @ExcelProperty(value = "物料编号", index = 5)
    private String code;

    @ExcelProperty(value = "启用状态", index = 6)
    private String enable;

    @ExcelProperty(value = "创建人", index = 7)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 8)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "修改人", index = 9)
    private String u_name;

    @ExcelProperty(value = "修改时间", index = 10)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}