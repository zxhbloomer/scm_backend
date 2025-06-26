package com.xinyirun.scm.bean.system.vo.business.returnrelation;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BReturnRelationExportVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -4790085127430217855L;

    @ExcelProperty(value="No", index = 0)
    @ColumnWidth(5)
    private Integer excel_no;

    @ExcelProperty(value="退货单号", index = 1)
    private String code;

    @ExcelProperty(value="关联单号", index = 2)
    private String serial_code;

    @ExcelProperty(value="单据类型", index = 3)
    private String serial_type_name;

    @ExcelProperty(value="状态", index = 4)
    private String status_name;

    @ExcelProperty(value="数量", index = 5)
    private BigDecimal qty;

    @ExcelProperty(value="单位", index = 6)
    private String unit_name;

    @ExcelProperty(value="理由", index = 7)
    private String quantity_reason;

    @ExcelProperty(value="创建人", index = 8)
    private String c_name;

    @ExcelProperty(value="创建时间", index = 9)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value="更新人", index = 10)
    private String u_name;

    @ExcelProperty(value="更新时间", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}
