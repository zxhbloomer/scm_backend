package com.xinyirun.scm.bean.system.vo.business.pp;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 导出生产计划表
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelIgnoreUnannotated
public class BPpExportVo implements Serializable {


    
    private static final long serialVersionUID = -502309007378073592L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "工单号", index = 1)
    private String code;

    @ExcelProperty(value = "状态", index = 2)
    private String status_name;

    @ExcelProperty(value = "生产订单数量", index = 3)
    private Integer bwo_sum;

    @ExcelProperty(value = "订单编号", index = 4)
    private String release_order_code;

    /**
     *仓库名字
     */
    @ExcelProperty(value = "仓库", index = 5)
    private String warehouse_name;

    @ExcelProperty(value = "配方编号", index = 6)
    private String router_code;

    @ExcelProperty(value = "配方名称", index = 7)
    private String router_name;

    @ExcelProperty(value = {"产成品, 副产品配比", "物料名称"}, index = 8)
    private String p_sku_name;

    @ExcelProperty(value = {"产成品, 副产品配比", "规格"}, index = 9)
    private String p_spec;

    @ExcelProperty(value = {"产成品, 副产品配比", "计划入库数量"}, index = 10)
    private BigDecimal p_qty;

    @ExcelProperty(value = {"产成品, 副产品配比", "已生产入库数量"}, index = 11)
    private BigDecimal p_actual_qty;

    @ExcelProperty(value = {"产成品, 副产品配比", "待生产入库数量"}, index = 12)
    private BigDecimal p_actual_wait;

    @ExcelProperty(value = {"原材料消耗配比", "物料名称"}, index = 13)
    private String m_sku_name;

    @ExcelProperty(value = {"原材料消耗配比", "规格"}, index = 14)
    private String m_spec;

    @ExcelProperty(value = {"原材料消耗配比", "配比（%）"}, index = 15)
    private BigDecimal m_router;

    @ExcelProperty(value = {"原材料消耗配比", "计划领料数量"}, index = 16)
    private BigDecimal m_qty;

    @ExcelProperty(value = {"原材料消耗配比", "已领料出库数量"}, index = 17)
    private BigDecimal m_actual_qty;

    @ExcelProperty(value = {"原材料消耗配比", "待领料出库数量"}, index = 18)
    private BigDecimal m_actual_wait;

    @ExcelProperty(value = "作废理由", index = 19)
    private String remark;

    @ExcelProperty(value = "创建人", index = 20)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 21)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 22)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 23)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    @ExcelProperty(value = "审核人", index = 24)
    private String audit_name;

    @ExcelProperty(value = "审核时间", index = 25)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime audit_time;

    @ExcelIgnore
    private String product_id;

    @ExcelIgnore
    private String material_id;

}
