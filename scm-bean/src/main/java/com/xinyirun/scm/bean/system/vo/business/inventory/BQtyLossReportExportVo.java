package com.xinyirun.scm.bean.system.vo.business.inventory;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BQtyLossReportExportVo implements Serializable {


    private static final long serialVersionUID = 3120637622376078949L;

    @ColumnWidth(20)
    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "物料名称", index = 1)
    private String goods_name;

    @ExcelProperty(value = "损耗数量", index = 2)
    private BigDecimal qty;


}
