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
public class ProcessingComboWarehouseOutProgressExportVo implements Serializable {

    private static final long serialVersionUID = -8061226776873569154L;
    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "品名", index = 1)
    private String goods_name;

    @ExcelProperty(value = "发货单位", index = 2)
    private String out_owner_name;

    @ExcelProperty(value = "发货地", index = 3)
    private String out_warehouse_name;

    @ExcelProperty(value = "收货单位（流向）", index = 4)
    private String in_warehouse_name;

    @ExcelProperty(value = "承运商", index = 5)
    private String customer_name;

    @ExcelProperty(value = "销售合同号", index = 6)
    private String contract_no;

    @ExcelProperty(value = "出库时间", index = 7)
    @DateTimeFormat("yyyy年MM月dd日")
    private LocalDateTime out_time;

    @ExcelProperty(value = "车牌号", index = 8)
    private String vehicle_no;

    @ExcelProperty(value = "出库净重（吨）", index = 9)
    private BigDecimal qty;

    @ExcelProperty(value = "退货数量", index = 10)
    private BigDecimal return_qty;

    @ExcelProperty(value = "实际出库数量（吨）", index = 11)
    private BigDecimal actual_count_return;

    @ExcelProperty(value = "糙米使用量", index = 12)
    private BigDecimal ricehull_qty;

    @ExcelProperty(value = "玉米使用量", index = 13)
    private BigDecimal maize_qty;

    @ExcelProperty(value = "稻谷使用量", index = 14)
    private BigDecimal rice_qty;

    @ExcelProperty(value = "小麦使用量", index = 15)
    private BigDecimal wheat_qty;
}
