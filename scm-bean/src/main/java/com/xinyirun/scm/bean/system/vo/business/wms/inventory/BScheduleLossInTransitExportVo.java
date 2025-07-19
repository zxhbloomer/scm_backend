package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Wang Qianfeng
 * @date 2022/9/1 9:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BScheduleLossInTransitExportVo implements Serializable {

    private static final long serialVersionUID = -9025823763154138600L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(20)
    private Integer no;

    @ExcelProperty(value = "物流订单号", index = 1)
    private String schedule_code;

    @ExcelProperty(value = "状态", index = 2)
    private String status_name;

    @ExcelProperty(value = "发货仓库", index = 3)
    private String out_warehouse_name;

    @ExcelProperty(value = "收货仓库", index = 4)
    private String in_warehouse_name;

    @ExcelProperty(value = "承运商", index = 5)
    private String customer_name;

    @ExcelProperty(value = "物料名称", index = 6)
    private String goods_name;

    @ExcelProperty(value = "品名", index = 7)
    private String pm;

    @ExcelProperty(value = "规格", index = 8)
    private String spec;

    @ExcelProperty(value = "在途数量", index = 9)
    private BigDecimal qty_loss;

    @ExcelProperty(value = "派车数", index = 10)
    private int counts;

    @ExcelProperty(value = "创建时间", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新时间", index = 12)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

}
