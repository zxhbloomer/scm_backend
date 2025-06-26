package com.xinyirun.scm.bean.system.vo.report.ledger;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 稻壳 出库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
public class ProcessingRiceHullWarehouseOutDetailExportVo implements Serializable {

    private static final long serialVersionUID = -3371013529350724828L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "品种", index = 1)
    private String goods_name;

    @ExcelProperty(value = "收货单位（流向）", index = 2)
    private String client_name;

    @ExcelProperty(value = "销售合同号", index = 3)
    private String contract_no;

    @ExcelProperty(value = "出库时间", index = 4)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime e_dt;

    @ExcelProperty(value = "车牌号", index = 5)
    private String vehicle_no;

    @ExcelProperty(value = "毛重（吨）", index = 6)
    private BigDecimal gross_weight;

    @ExcelProperty(value = "皮重（吨）", index = 7)
    private BigDecimal tare_weight;

    @ExcelProperty(value = "净重（吨）", index = 8)
    private BigDecimal actual_count;
}
