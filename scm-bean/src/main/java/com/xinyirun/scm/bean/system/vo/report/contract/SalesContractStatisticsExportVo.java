package com.xinyirun.scm.bean.system.vo.report.contract;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Wqf
 * @Description: 采购合同统计表
 * @CreateTime : 2023/9/19 15:55
 */

@Data
@Builder
public class SalesContractStatisticsExportVo implements Serializable {

    private static final long serialVersionUID = -3924454522568599510L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;


    @ExcelProperty(value = "合同编号", index = 1)
    private String contract_no;

    @ExcelProperty(value = "订单编号", index = 2)
    private String order_no;

    @ExcelProperty(value = "物流订单数", index = 3)
    private Integer schedule_count;

    @ExcelProperty(value = "状态", index = 4)
    private String status_name;

    @ExcelProperty(value = "合同日期", index = 5)
    private String contract_dt;

    @ExcelProperty(value = "到期日期", index = 6)
    private String contract_expire_dt;

    @ExcelProperty(value = "客户", index = 7)
    private String client_name;

    @ExcelProperty(value = "货主名称", index = 8)
    private String owner_name;

    @ExcelProperty(value = "商品名称", index = 9)
    private String sku_name;

    @ExcelProperty(value = "品名", index = 10)
    private String pm;

    @ExcelProperty(value = "规格", index = 11)
    private String spec;

    @ExcelProperty(value = "合同量", index = 12)
    private BigDecimal contract_num;

    @ExcelProperty(value = "执行情况", index = 13)
    private String execute_processing;

    @ExcelProperty(value = "出库地", index = 14)
    private String out_address;

    @ExcelProperty(value = "已出库数量", index = 15)
    private BigDecimal has_handle_count;

    @ExcelProperty(value = "到货数量", index = 16)
    private BigDecimal arrived_count;

    @ExcelProperty(value = "在途数量", index = 17)
    private BigDecimal in_transit_count;

    @ExcelProperty(value = "损耗", index = 18)
    private BigDecimal qty_loss;
}
