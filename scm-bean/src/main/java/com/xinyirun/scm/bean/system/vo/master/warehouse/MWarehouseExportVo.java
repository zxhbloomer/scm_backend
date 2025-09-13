package com.xinyirun.scm.bean.system.vo.master.warehouse;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 仓库主表导出VO - 按照岗位导出模式设计
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MWarehouseExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8990646953974899180L;

    @ExcelProperty(value = "NO", index = 0)
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
    private String charge_company_name;

    @ExcelProperty(value = "运营企业", index = 6)
    private String operate_company_name;

    @ExcelProperty(value = "联系人", index = 7)
    private String contact_person;

    @ExcelProperty(value = "联系电话", index = 8)
    private String mobile_phone;

    @ExcelProperty(value = "省份", index = 9)
    private String province;

    @ExcelProperty(value = "城市", index = 10)
    private String city;

    @ExcelProperty(value = "区域", index = 11)
    private String district;

    @ExcelProperty(value = "详细地址", index = 12)
    private String address;

    @ExcelProperty(value = "级联区域名称", index = 13)
    private String cascader_areas_name;

    @ExcelProperty(value = "区域名称", index = 14)
    private String zone_name;

    @ExcelProperty(value = "面积", index = 15)
    private String area;

    @ExcelProperty(value = "仓储容量", index = 16)
    private String warehouse_capacity;

    @ExcelProperty(value = "状态", index = 17)
    private String enable_status;

    @ExcelProperty(value = "创建人", index = 18)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 19)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelProperty(value = "修改人", index = 20)
    private String u_name;

    @ExcelProperty(value = "修改时间", index = 21)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;
}
