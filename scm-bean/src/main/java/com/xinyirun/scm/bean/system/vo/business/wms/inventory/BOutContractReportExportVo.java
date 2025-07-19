package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

/**
 * @author Wang Qianfeng
 * @date 2022/8/31 15:35
 */
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
public class BOutContractReportExportVo implements Serializable {


    private static final long serialVersionUID = 1236746424598634143L;

    @ColumnWidth(20)
    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "货主", index = 1)
    private String owner;

    @ExcelProperty(value = "客户", index = 2)
    private String supplier_name;

    @ExcelProperty(value = "物料名称", index = 3)
    private String goods_name;

    @ExcelProperty(value = "销售数量", index = 4)
    private BigDecimal qty;

    @ExcelProperty(value = "实际出库数量", index = 5)
    private BigDecimal actual_count;


}
