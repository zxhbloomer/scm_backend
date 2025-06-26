package com.xinyirun.scm.bean.system.vo.business.carriage;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BCarriageOrderExportVo implements Serializable {

    private static final long serialVersionUID = 1567638444906114053L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "订单编号", index = 1)
    private String order_no;

    @ExcelProperty(value = "合同编号", index = 2)
    private String carriage_contract_code;

    @ExcelProperty(value = "销售合同号", index = 3)
    private String sales_contract_code;

    @ExcelProperty(value = "托运人", index = 4)
    private String org_name;

    @ExcelProperty(value = "承运人", index = 5)
    private String company_name;

    @ExcelProperty(value = "签订日期", index = 6)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDateTime sign_dt;

    @ExcelProperty(value = "合同截止日期", index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDateTime deadline_dt;

    @ExcelProperty(value = "运输方式", index = 8)
    private String transport_type_name;

    @ExcelProperty(value = "状态", index = 9)
    private String status;

    @ExcelProperty(value = "起运地", index = 10)
    private String origin_place;

    @ExcelProperty(value = "目的地", index = 11)
    private String destination_place;

    @ExcelProperty(value = "运费单价", index = 12)
    private BigDecimal price;

    @ExcelProperty(value = "运输总数量", index = 13)
    private BigDecimal num;

    @ExcelProperty(value = "运输总金额", index = 14)
    private BigDecimal transport_amount;

    @ExcelProperty(value = "总货值", index = 15)
    private BigDecimal transport_amount_1;

    @ExcelProperty(value = "更新时间", index = 16)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime u_time;;

}
