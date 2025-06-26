package com.xinyirun.scm.bean.system.vo.excel.materialconvert;

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
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialConvertExportVo implements Serializable {

    private static final long serialVersionUID = -5914351479963839175L;

    @ExcelProperty(value="No", index = 0)
    @ColumnWidth(10)
    private Integer excel_no;

    @ExcelProperty(value="转换编号", index = 1)
    private String code;

    @ExcelProperty(value="货主", index = 2)
    private String owner_name;

    @ExcelProperty(value="转换类型", index = 3)
    private String type_name;

    @ExcelProperty(value="启用状态", index = 4)
    private String effective_name;

    @ExcelProperty(value={"转换前", "物料名称"}, index = 5)
    private String source_goods_name;

    @ExcelProperty(value={"转换前", "规格编号"}, index = 6)
    private String source_sku_code;

    @ExcelProperty(value={"转换前", "规格"}, index = 7)
    private String source_spec;

    @ExcelProperty(value={"转换后", "物料名称"}, index = 8)
    private String target_goods_name;

    @ExcelProperty(value={"转换后", "物料编码"}, index = 9)
    private String target_sku_code;

    @ExcelProperty(value={"转换后", "规格"}, index = 10)
    private String target_spec;

    @ExcelProperty(value="上次转换时间", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private String convert_time;

    @ExcelProperty(value="创建人", index = 12)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 13)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="创建人", index = 14)
    private String u_name;

    @ExcelProperty(value="创建时间", index = 15)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;
}
