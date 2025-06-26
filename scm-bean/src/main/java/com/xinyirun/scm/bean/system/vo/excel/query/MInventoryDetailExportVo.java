package com.xinyirun.scm.bean.system.vo.excel.query;

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
public class MInventoryDetailExportVo implements Serializable {

    private static final long serialVersionUID = 174288118666077089L;

    @ExcelProperty(value="NO", index = 0)
    @ColumnWidth(10)
    private Integer no;

    @ExcelProperty(value="业务板块", index = 1)
    private String business_name;

    @ExcelProperty(value="行业", index = 2)
    private String industry_name;

    @ExcelProperty(value="分类", index = 3)
    private String category_name;

    @ExcelProperty(value="货主", index = 4)
    private String owner_short_name;

    @ExcelProperty(value="仓库", index = 5)
    private String warehouse_short_name;

    @ExcelProperty(value="仓库类型", index = 6)
    private String warehouse_type_name;

    @ExcelProperty(value="库区", index = 7)
    private String location_short_name;

    @ExcelProperty(value="库位", index = 8)
    private String bin_name;

    @ExcelProperty(value="物料编号", index = 9)
    private String sku_code;

    @ExcelProperty(value="物料名称", index = 10)
    private String sku_name;

    @ExcelProperty(value = "商品属性", index = 11)
    private String goods_prop;

    @ExcelProperty(value="规格", index = 12)
    private String spec;

    @ExcelProperty(value="品名", index = 13)
    private String pm;

    @ExcelProperty(value="可用库存", index = 14)
    private BigDecimal qty_avaible;

    @ExcelProperty(value="锁定库存", index = 15)
    private BigDecimal qty_lock;

    @ExcelProperty(value="库存单位", index = 16)
    private String unit_name;

    @ExcelProperty(value="实时单价", index = 17)
    private BigDecimal price;

    @ExcelProperty(value="货值", index = 18)
    private BigDecimal amount;

}
