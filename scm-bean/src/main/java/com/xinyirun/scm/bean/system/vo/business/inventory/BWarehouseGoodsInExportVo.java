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
public class BWarehouseGoodsInExportVo implements Serializable {


    private static final long serialVersionUID = 5614119558017804725L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(20)
    private Integer no;

    @ExcelProperty(value = "仓库类型", index = 1)
    private String warehouse_type_name;

    @ExcelProperty(value = "仓库", index = 2)
    private String warehouse_name;

    @ExcelProperty(value = "物料名称", index = 3)
    private String goods_name;

    @ExcelProperty(value = "物料编码", index = 4)
    private String goods_code;

    @ExcelProperty(value = "合计入库库存数量", index = 5)
    private BigDecimal qty;

    @ExcelProperty(value = "合计退货数量", index = 6)
    private BigDecimal return_qty;

}
