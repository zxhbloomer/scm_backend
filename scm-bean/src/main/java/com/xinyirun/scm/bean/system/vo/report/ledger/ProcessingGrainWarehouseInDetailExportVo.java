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
public class ProcessingGrainWarehouseInDetailExportVo implements Serializable {

    private static final long serialVersionUID = -7172400782446557425L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "品名", index = 1)
    private String goods_name;

    @ExcelProperty(value = "发货单位（流向）", index = 2)
    private String warehouse_name;

    @ExcelProperty(value = "收货单位", index = 3)
    private String in_warehouse_name;

    @ExcelProperty(value = "物流运输合同号", index = 4)
    private String waybill_code;

    @ExcelProperty(value = "入库时间", index = 5)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime time;

    @ExcelProperty(value = "车牌号", index = 6)
    private String vehicle_no;

    @ExcelProperty(value = "入库净重（吨）", index = 7)
    private BigDecimal qty;
}
