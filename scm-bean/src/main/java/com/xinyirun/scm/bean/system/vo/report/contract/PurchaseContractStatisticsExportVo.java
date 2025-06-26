package com.xinyirun.scm.bean.system.vo.report.contract;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Wqf
 * @Description: 采购合同统计表 导出
 * @CreateTime : 2023/9/19 15:55
 */

@Data
public class PurchaseContractStatisticsExportVo implements Serializable {

    private static final long serialVersionUID = -3924454522568599568L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "采购合同号", index = 1)
    private String contract_no;

    @ExcelProperty(value = "直属库名称", index = 2)
    private String warehouse_name;

    @ExcelProperty(value = "物料名称", index = 3)
    private String goods_name;

    @ExcelProperty(value = "商品规格", index = 4)
    private String spec;

    @ExcelProperty(value = "合同数量", index = 5)
    private BigDecimal contract_num;

    @ExcelProperty(value = "入库数量", index = 6)
    private BigDecimal in_qty;

    @ExcelProperty(value = "当日出库数量", index = 7)
    private BigDecimal today_out_qty;

    @ExcelProperty(value = "累计出库数量", index = 8)
    private BigDecimal out_qty;

    @ExcelProperty(value = "开库日期", index = 9)
    private String start_date;

    @ExcelProperty(value = "开库时间", index = 10)
    private String start_time;

}
