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
public class BQtyInTransitExportVo implements Serializable {

    private static final long serialVersionUID = 6968960536115529818L;

    @ColumnWidth(20)
    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "商品属性", index = 1)
    private String goods_prop;

    @ExcelProperty(value = "在途数量", index = 2)
    private BigDecimal qty;


}
