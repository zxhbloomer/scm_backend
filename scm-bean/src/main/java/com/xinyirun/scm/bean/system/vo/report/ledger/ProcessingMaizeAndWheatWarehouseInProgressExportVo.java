package com.xinyirun.scm.bean.system.vo.report.ledger;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 加工稻谷, 小麦入库 导出
 * @CreateTime : 2023/8/1 15:11
 */

@Data
public class ProcessingMaizeAndWheatWarehouseInProgressExportVo implements Serializable {

    private static final long serialVersionUID = -2124279610247642906L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "发货单位", index = 1)
    private String supplier_name;

    @ExcelProperty(value = "采购合同号", index = 2)
    private String contract_no;

    @ExcelProperty(value = "发货地", index = 3)
    private String out_warehouse_name;

    @ExcelProperty(value = "收货地", index = 4)
    private String in_warehouse_name;

    @ExcelProperty(value = "品种", index = 5)
    private String goods_name;

    @ExcelProperty(value = "入库时间", index = 6)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(25)
    private LocalDateTime e_dt;

    @ExcelProperty(value = "车牌号", index = 7)
    private String vehicle_no;

    @ExcelProperty(value = "入库净重（吨）", index = 8)
    private BigDecimal actual_count;

}
