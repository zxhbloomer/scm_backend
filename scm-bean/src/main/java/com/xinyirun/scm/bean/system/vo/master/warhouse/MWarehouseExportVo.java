package com.xinyirun.scm.bean.system.vo.master.warhouse;

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
 * 仓库
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MWarehouseExportVo implements Serializable {

    private static final long serialVersionUID = 4613393722188326101L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(15)
    private Integer no;

    @ExcelProperty(value = "仓库编码", index = 1)
    private String code;

    @ExcelProperty(value = "仓库名称", index = 2)
    private String name;

    @ExcelProperty(value = "仓库简称", index = 3)
    private String short_name;

    @ExcelProperty(value = "仓库类型", index = 4)
    private String warehouse_type_name;

    @ExcelProperty(value = "监管企业", index = 5)
    private Integer charge_company_id;

    @ExcelProperty(value = "运营企业", index = 6)
    private Integer operate_company_id;

    @ExcelProperty(value = "联系人", index = 7)
    private String contact_person;

    @ExcelProperty(value = "联系人手机", index = 8)
    private String mobile_phone;

    @ExcelProperty(value = "省市区", index = 9)
    private String cascader_areas_name;

    @ExcelProperty(value = "仓库地址", index = 10)
    private String address;

    @ExcelProperty(value = "片区", index = 11)
    private String zone_name;

    @ExcelProperty(value = "仓库容积", index = 13)
    private BigDecimal warehouse_capacity;

    @ExcelProperty(value = "仓库面积", index = 12)
    private BigDecimal area;

    @ExcelProperty(value = "是否启用", index = 14)
    private String enable;

    @ExcelProperty(value = "创建人", index = 15)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 16)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 17)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 18)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime u_time;
}
