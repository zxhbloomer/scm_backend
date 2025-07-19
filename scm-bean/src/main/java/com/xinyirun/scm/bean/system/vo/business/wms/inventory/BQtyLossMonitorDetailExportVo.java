package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Wang Qianfeng
 * @date 2022/9/1 9:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BQtyLossMonitorDetailExportVo implements Serializable {


    private static final long serialVersionUID = -4302115748119548514L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(20)
    private Integer no;

    @ExcelProperty(value = "任务单号", index = 1)
    private String code;

    @ExcelProperty(value = "物流订单号", index = 2)
    private String schedule_code;

    @ExcelProperty(value = "状态", index = 3)
    private String status_name;

    @ExcelProperty(value = "承运商", index = 4)
    private String customer_name;

    @ExcelProperty(value = "车牌号", index = 5)
    private String vehicle_no;

    @ExcelProperty(value = "物料名称", index = 6)
    private String sku_name;

    @ExcelProperty(value = "品名", index = 7)
    private String pm;

    @ExcelProperty(value = "规格", index = 8)
    private String spec;

    @ExcelProperty(value = "已发货数量", index = 9)
    private BigDecimal out_qty;

    @ExcelProperty(value = "已收数量", index = 10)
    private BigDecimal in_qty;

    @ExcelProperty(value = "损耗数量", index = 11)
    private BigDecimal qty_loss;

}
