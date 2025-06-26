package com.xinyirun.scm.bean.system.vo.business.order;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: xtj
 * @Description:
 * @CreateTime : 2024/8/12 14:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOrderInvertedExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5044316500896406984L;


    @ColumnWidth(20)
    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    /**
     * 竞拍日期
     */
    @ExcelProperty(value = "竞拍日期", index = 1)
    private String auction_date;

    /**
     * 开库日期
     */
    @ExcelProperty(value = "开库日期", index = 2)
    private LocalDateTime opening_date;

    /**
     * 采购合同号
     */
    @ExcelProperty(value = "采购合同号", index = 3)
    private String contract_no;

    /**
     * 出库到期日
     */
    @ExcelProperty(value = "出库到期日", index = 4)
    private LocalDateTime delivery_due_date;

    /**
     * 实际储存库点
     */
    @ExcelProperty(value = "实际储存库点", index = 5)
    private String warehouse_name;

    /**
     * 合同量
     */
    @ExcelProperty(value = "合同量", index = 6)
    private BigDecimal contract_quantity;

    /**
     * 实际应出库数量（扣除升贴水数量）
     */
    @ExcelProperty(value = "实际应出库数量（扣除升贴水数量）", index = 7)
    private BigDecimal actual_quantity;

    /**
     * 剩余数量
     */
    @ExcelProperty(value = "当日剩余数量（已减除贴水）", index = 8)
    private BigDecimal remaining_quantity;

    /**
     * 实际日出库量
     */
    @ExcelProperty(value = "实际日出库量", index = 9)
    private BigDecimal actual_daily_quantity;

    /**
     * 累计出库量
     */
    @ExcelProperty(value = "累计出库量", index = 10)
    private BigDecimal accumulated_out_quantity;

    /**
     * 计划出库天数
     */
    @ExcelProperty(value = "计划出库天数", index = 11)
    private String plan_out_days;

    /**
     * 实际出库耗用天数
     */
    @ExcelProperty(value = "实际出库耗用天数", index = 12)
    private String actual_plan_out_days;

    /**
     * 日出库计划
     */
    @ExcelProperty(value = "日出库计划", index = 13)
    private BigDecimal plan_out_day;

    /**
     * 出库进度
     */
    @ExcelProperty(value = "出库进度", index = 14)
    private BigDecimal plan_out_speed;

}
