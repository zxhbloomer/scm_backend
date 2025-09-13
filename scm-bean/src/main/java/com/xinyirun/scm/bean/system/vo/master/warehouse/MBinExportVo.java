package com.xinyirun.scm.bean.system.vo.master.warehouse;

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
 * 库位导出VO类
 * 与列表页面显示列完全对应，确保导出内容与显示内容一致
 * </p>
 *
 * @author htt
 * @since 2024-12-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MBinExportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序号（自动生成）
     * 对应列表页面的"No"列
     */
    @ExcelProperty(value = "NO", index = 0)
    @ColumnWidth(8)
    private Integer no;

    /**
     * 所属仓库名称
     * 对应列表页面的"所属仓库"列 - warehouse_name字段
     */
    @ExcelProperty(value = "所属仓库", index = 1)
    @ColumnWidth(20)
    private String warehouse_name;

    /**
     * 所属库区名称
     * 对应列表页面的"所属库区"列 - location_name字段
     */
    @ExcelProperty(value = "所属库区", index = 2)
    @ColumnWidth(20)
    private String location_name;

    /**
     * 库位名称
     * 对应列表页面的"库位名称"列 - name字段
     */
    @ExcelProperty(value = "库位名称", index = 3)
    @ColumnWidth(20)
    private String name;

    /**
     * 启用状态
     * 对应列表页面的"启用状态"列 - enable字段
     * 值：'是' 或 '否'（SQL中已转换）
     */
    @ExcelProperty(value = "启用状态", index = 4)
    @ColumnWidth(12)
    private String enable;

    /**
     * 默认库位状态
     * 对应列表页面的"默认库位"列 - is_default字段
     * 值：'是' 或 '否'（SQL中已转换）
     */
    @ExcelProperty(value = "默认库位", index = 5)
    @ColumnWidth(12)
    private String is_default_status;

    /**
     * 创建人姓名
     * 对应列表页面的"创建人"列 - c_name字段
     */
    @ExcelProperty(value = "创建人", index = 6)
    @ColumnWidth(15)
    private String c_name;

    /**
     * 创建时间
     * 对应列表页面的"创建时间"列 - c_time字段
     * 格式：yyyy年MM月dd日 HH:mm:ss（与仓库管理完全一致）
     */
    @ExcelProperty(value = "创建时间", index = 7)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(22)
    private LocalDateTime c_time;

    /**
     * 更新人姓名
     * 对应列表页面的"更新人"列 - u_name字段
     */
    @ExcelProperty(value = "更新人", index = 8)
    @ColumnWidth(15)
    private String u_name;

    /**
     * 更新时间
     * 对应列表页面的"更新时间"列 - u_time字段
     * 格式：yyyy年MM月dd日 HH:mm:ss（与仓库管理完全一致）
     */
    @ExcelProperty(value = "更新时间", index = 9)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    @ColumnWidth(22)
    private LocalDateTime u_time;

}
