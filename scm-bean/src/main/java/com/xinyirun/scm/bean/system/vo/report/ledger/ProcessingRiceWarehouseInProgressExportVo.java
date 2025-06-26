package com.xinyirun.scm.bean.system.vo.report.ledger;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 加工稻谷入库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
public class ProcessingRiceWarehouseInProgressExportVo implements Serializable {

    private static final long serialVersionUID = 7542980315958032248L;


    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = {"承储库出库情况", "实际存储库点"}, index = 1)
    private String out_warehouse_name;

    @ExcelProperty(value = {"承储库出库情况", "品种"}, index = 2)
    private String goods_name;

    @ExcelProperty(value = {"承储库出库情况", "所在货位混合扦样等级"}, index = 3)
    private String spec;

    @ExcelProperty(value = {"承储库出库情况", "合同号"}, index = 4)
    private String contract_no;

    @ExcelProperty(value = {"承储库出库情况", "出库时间"}, index = 5)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime out_time;

    @ExcelProperty(value = {"承储库出库情况", "车牌号"}, index = 6)
    private String out_vehicle_no;

    @ExcelProperty(value = {"承储库出库情况", "出库净重（吨）"}, index = 7)
    private BigDecimal out_qty;

    @ExcelProperty(value = {"加工掺混入库情况", "加工掺混点"}, index = 8)
    private String in_warehouse_name;

    @ExcelProperty(value = {"加工掺混入库情况", "入库时间"}, index = 9)
    private LocalDateTime in_time;

    @ExcelProperty(value = {"加工掺混入库情况", "车牌号"}, index = 10)
    private String in_vehicle_no;

    @ExcelProperty(value = {"加工掺混入库情况", "入库净重（吨）"}, index = 11)
    private BigDecimal in_qty;

}
