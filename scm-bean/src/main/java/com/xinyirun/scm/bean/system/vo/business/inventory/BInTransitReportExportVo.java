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
public class BInTransitReportExportVo implements Serializable {


    private static final long serialVersionUID = -5284673445486064414L;
    @ColumnWidth(15)
    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "物料名称", index = 1)
    private String goods_name;

    @ExcelProperty(value = "在途数量", index = 2)
    private BigDecimal qty;

    @ExcelProperty(value = "存货数量", index = 3)
    private BigDecimal qty_inventory;

    @ExcelProperty(value = "合计", index = 4)
    private BigDecimal count_qty;


}
