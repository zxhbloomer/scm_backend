package com.xinyirun.scm.bean.system.vo.excel.query;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MDailyInventoryExportVo implements Serializable {

    private static final long serialVersionUID = 1283632985077449835L;

    @ExcelProperty(value="No", index = 0)
    @ColumnWidth(5)
    private Integer no;

    @ExcelProperty(value="日期", index = 1)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime dt;

    @ExcelProperty(value="货主", index = 2)
    private String owner_name;

    @ExcelProperty(value="业务板块", index = 3)
    private String business_type_name;

    @ExcelProperty(value="行业", index = 4)
    private String industry_name;

    @ExcelProperty(value="类别", index = 5)
    private String category_name;

    @ExcelProperty(value="商品属性", index = 6)
    private String goods_prop;

    @ExcelProperty(value="物料编号", index = 7)
    private String sku_code;

    @ExcelProperty(value="物料名称", index = 8)
    private String goods_name;

    @ExcelProperty(value="规格", index = 9)
    private String sku_name;

    @ExcelProperty(value="品名", index = 10)
    private String pm;

    @ExcelProperty(value="仓库", index = 11)
    private String warehouse_name;

    @ExcelProperty(value="仓库类型", index = 12)
    private String warehouse_type_name;

    @ExcelProperty(value="库区", index = 13)
    private String location_name;

    @ExcelProperty(value="库位", index = 14)
    private String bin_name;

    @ExcelProperty(value="库存", index = 15)
    private BigDecimal qty;

    @ExcelProperty(value="入库量", index = 16)
    private BigDecimal qty_in;

    @ExcelProperty(value="出库量", index = 17)
    private BigDecimal qty_out;

    @ExcelProperty(value="调整量", index = 18)
    private BigDecimal qty_adjust;

    @ExcelProperty(value="变动量", index = 19)
    private BigDecimal qty_diff;

    @ExcelProperty(value="库存单位", index = 20)
    private String unit_name="吨";

    @ExcelProperty(value="实时单价", index = 21)
    private BigDecimal realtime_price;

    @ExcelProperty(value="实时货值", index = 22)
    private BigDecimal realtime_amount;

}
