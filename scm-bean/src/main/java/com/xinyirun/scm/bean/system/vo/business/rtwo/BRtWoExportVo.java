package com.xinyirun.scm.bean.system.vo.business.rtwo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *  生产管理 表
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BRtWoExportVo implements Serializable {

    private static final long serialVersionUID = -3972258660990374713L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "工单编号", index = 1)
    private String code;

    @ExcelProperty(value = "订单编号", index = 2)
    private String delivery_order_code;

    @ExcelProperty(value = "仓库", index = 3)
    private String wc_warehouse_name;

    @ExcelProperty(value = "状态", index = 4)
    private String status_name;

    @ExcelProperty(value = {"产成品", "物料名称"}, index = 5)
    private String product_goods_name;

    @ExcelProperty(value = {"产成品", "规格"}, index = 6)
    private String product_spec;

    @ExcelProperty(value = {"产成品", "生产入库数量"}, index = 7)
    private BigDecimal product_wo_qty;

    @ExcelProperty(value = {"副产品", "物料名称"}, index = 8)
    private String coproduct_goods_name;

    @ExcelProperty(value = {"副产品", "规格"}, index = 9)
    private String coproduct_spec;

    @ExcelProperty(value = {"副产品", "生产入库数量"}, index = 10)
    private BigDecimal coproduct_wo_qty;

    @ExcelProperty(value = {"原材料", "物料名称"}, index = 11)
    private String material_goods_name;

    @ExcelProperty(value = {"原材料", "规格"}, index = 12)
    private String material_spec;

    @ExcelProperty(value = {"原材料", "领料出库数量"}, index = 13)
    private BigDecimal material_wo_qty;

    @ExcelProperty(value = "作废理由", index = 14)
    private String remark;

    @ExcelProperty(value = "创建人", index = 15)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 16)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 17)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 18)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelProperty(value = "审核人", index = 19)
    private String e_name;

    @ExcelProperty(value = "审核时间", index = 20)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime e_time;

    @ExcelIgnore
    private String coproduct_id;

    @ExcelIgnore
    private String material_id;

}
