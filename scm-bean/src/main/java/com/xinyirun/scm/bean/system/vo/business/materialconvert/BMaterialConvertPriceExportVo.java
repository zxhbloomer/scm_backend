package com.xinyirun.scm.bean.system.vo.business.materialconvert;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialConvertPriceExportVo implements Serializable {


    private static final long serialVersionUID = 2384804924380454938L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "业务板块", index = 1)
    private String business_name;

    @ExcelProperty(value = "货主", index = 2)
    private String owner_simple_name;

    @ExcelProperty(value = "仓库", index = 3)
    private String warehouse_simple_name;

    @ExcelProperty(value = "物料名称", index = 4)
    private String goods_name;

    @ExcelProperty(value = "规格", index = 5)
    private String spec;

    @ExcelProperty(value = "规格编号", index = 6)
    private String sku_code;

    @ExcelProperty(value = "日期", index = 7)
    @ColumnWidth(20)
    private String dt;

    @ExcelProperty(value = "单价", index = 8)
    private BigDecimal price;

    @ExcelProperty(value = "15天累计到货量", index = 9)
    private BigDecimal qty;

    @ExcelProperty(value = "15天累计货值", index = 10)
    private BigDecimal amount;

}
