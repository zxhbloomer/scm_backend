package com.xinyirun.scm.bean.system.vo.master.driver;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 司机
 * </p>
 *
 * @author wwl
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MDriverExportVo implements Serializable {

    private static final long serialVersionUID = 174069106449050770L;

    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(10)
    private Integer no;

    @ExcelProperty(value = "司机编号", index = 1)
    private String code;

    /**
     * 司机名称
     */
    @ExcelProperty(value = "司机名称", index = 2)
    private String name;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 4)
    private String mobile_phone;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "身份证号", index = 3)
    private String id_card;

    /*@ExcelProperty(value = "身份证人像面路径", index = 5)
    private String id_card_front_url;

    @ExcelProperty(value = "身份证国徽面路径", index = 6)
    private String id_card_back_url;

    @ExcelProperty(value = "驾驶证路径", index = 7)
    private String driver_license_url;*/

    @ExcelProperty(value = "是否删除", index = 5)
    private String is_delete;

    @ExcelProperty(value = "创建人", index = 6)
    private String c_name;

    @ExcelProperty(value = "创建时间", index = 7)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime c_time;

    @ExcelProperty(value = "更新人", index = 8)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 9)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(25)
    private LocalDateTime u_time;
}
