package com.xinyirun.scm.bean.system.vo.wms.in.order;

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
 * <p>
 * 入库计划合同
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BInOrderExportVo implements Serializable {


    private static final long serialVersionUID = 7657521102601761036L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "合同编号", index = 1)
    private String contract_no;

    @ExcelProperty(value = "订单编号", index = 2)
    private String order_no;

    @ExcelProperty(value = "物流订单数量", index = 3)
    private Integer schedule_count = 0;

    @ExcelProperty(value = "状态", index = 4)
    private String status_name;

    @ExcelProperty(value = "序号", index = 5)
    private int idx;

    @ExcelProperty(value = "单据类型", index = 6)
    private String bill_type_name;

    @ExcelProperty(value = "合同日期", index = 7)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(25)
    private LocalDateTime contract_dt;

    @ExcelProperty(value = "到期日期", index = 8)
    @DateTimeFormat("yyyy年MM月dd日")
    @ColumnWidth(25)
    private LocalDateTime contract_expire_dt;

    @ExcelProperty(value = "供应商名称", index = 9)
    private String supplier_name;

    @ExcelProperty(value = "货主名称", index = 10)
    private String owner_name;

    @ExcelProperty(value = "商品名称", index = 11)
    private String goods_name;

    @ExcelProperty(value = "品名", index = 12)
    private String pm;

    @ExcelProperty(value = "规格", index = 13)
    private String spec;

    @ExcelProperty(value = "合同量", index = 14)
    private BigDecimal contract_num;

    @ExcelProperty(value = "合同金额", index = 15)
    private BigDecimal amount;

    @ExcelProperty(value = "已入库数量", index = 16)
    private BigDecimal actual_count;

    @ExcelProperty(value = "创建人", index = 17)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 18)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 19)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime u_time;
}
