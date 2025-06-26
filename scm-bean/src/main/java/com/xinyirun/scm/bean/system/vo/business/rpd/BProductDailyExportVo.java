package com.xinyirun.scm.bean.system.vo.business.rpd;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/5/16 15:46
 */
@Data
public class BProductDailyExportVo implements Serializable {

    private static final long serialVersionUID = 2484789444844124929L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(10)
    private Integer no;

    @ExcelProperty(value = "日期", index = 1)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(20)
    private LocalDateTime date;

    @ExcelProperty(value = "掺混/加工库点", index = 2)
    private String warehouse_name;

    @ExcelProperty(value = "加工方式", index = 3)
    private String product_type;

    @ExcelProperty(value = {"稻谷", "定向入库"}, index = 4)
    private BigDecimal a_in_qty;

    @ExcelProperty(value = {"稻谷", "掺混/加工使用"}, index = 5)
    private BigDecimal a_product_qty;

    @ExcelProperty(value = {"稻谷", "出库数量"}, index = 6)
    private BigDecimal a_out_qty;

    @ExcelProperty(value = {"稻谷", "库存数量"}, index = 7)
    private BigDecimal a_inventory_qty;

    @ExcelProperty(value = {"糙米", "加工入库"}, index = 8)
    private BigDecimal b_in_qty;

    @ExcelProperty(value = {"糙米", "掺混数量"}, index = 9)
    private BigDecimal b_cost_qty;

    @ExcelProperty(value = {"糙米", "出库数量"}, index = 10)
    private BigDecimal b_out_qty;

    @ExcelProperty(value = {"糙米", "库存数量"}, index = 11)
    private BigDecimal b_inventory_qty;

    @ExcelProperty(value = {"玉米", "入库数量"}, index = 12)
    private BigDecimal c_in_qty;

    @ExcelProperty(value = {"玉米", "掺混使用"}, index = 13)
    private BigDecimal c_cost_qty;

    @ExcelProperty(value = {"玉米", "库存数量"}, index = 14)
    private BigDecimal c_inventory_qty;

    @ExcelProperty(value = "掺混比例(玉米)", index = 15)
    private BigDecimal router;

    @ExcelProperty(value = {"混合物", "入库数量(掺混)"}, index = 16)
    private BigDecimal d_in_qty;

    @ExcelProperty(value = {"混合物", "出库数量"}, index = 17)
    private BigDecimal d_out_qty;

    @ExcelProperty(value = {"混合物", "损耗"}, index = 18)
    private BigDecimal loss_qty;

    @ExcelProperty(value = {"混合物", "剩余库存"}, index = 19)
    private BigDecimal d_residue_qty;

    @ExcelProperty(value = {"稻壳", "入库数量"}, index = 20)
    private BigDecimal e_in_qty;

    @ExcelProperty(value = {"稻壳", "出库数量"}, index = 21)
    private BigDecimal e_out_qty;

    @ExcelProperty(value = {"稻壳", "剩余库存"}, index = 22)
    private BigDecimal e_residue_qty;

}
