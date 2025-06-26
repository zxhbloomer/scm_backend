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
 * 物料规格
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MGoodsSpecExportVo implements Serializable {


    private static final long serialVersionUID = -4446521395619369683L;

    @ExcelProperty(value = "No", index = 0)
    @ColumnWidth(15)
    private Integer no;

    @ExcelProperty(value = "所属板块", index = 1)
    private String business_name;

    @ExcelProperty(value = "所属行业", index = 2)
    private String industry_name;

    @ExcelProperty(value = "所属类别", index = 3)
    private String category_name;

    @ExcelProperty(value = "物料名称", index = 4)
    private String name;

    @ExcelProperty(value = "属性", index = 5)
    private String prop_name;

    @ExcelProperty(value = "规格", index = 6)
    private String spec;

    @ExcelProperty(value = "规格编号", index = 7)
    private String sku_code;

    @ExcelProperty(value = "是否启用", index = 8)
    private String enable;

    @ExcelProperty(value="创建人", index = 9)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 10)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="修改人", index = 11)
    private String u_name;

    @ExcelProperty(value="修改时间", index = 12)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;
}
