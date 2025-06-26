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
public class BWarehouseGoodsTotalExportVo implements Serializable {


    private static final long serialVersionUID = -4608501819090568947L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(25)
    private Integer no;

    @ExcelProperty(value = "仓库类型", index = 1)
    private String warehouse_type_name;

    @ExcelProperty(value = "仓库", index = 2)
    private String warehouse_name;

    @ExcelProperty(value = "物料名称", index = 3)
    private String goods_name;

    @ExcelProperty(value = "物料编码", index = 4)
    private String goods_code;

    @ExcelProperty(value = "存货合计", index = 5)
    private BigDecimal qty;

}
