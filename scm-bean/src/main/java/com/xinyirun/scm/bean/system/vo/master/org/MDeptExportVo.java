package com.xinyirun.scm.bean.system.vo.master.org;

import cn.idev.excel.annotation.ExcelIgnore;
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
 * 部门主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MDeptExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1189343038090433062L;

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "集团信息", index = 1)
    private String group_simple_name;

    @ExcelProperty(value = "企业信息", index = 2)
    private String company_simple_name;

    @ExcelProperty(value = "上级部门", index = 3)
    private String parent_dept_simple_name;

    @ExcelProperty(value = "部门编号", index = 4)
    private String code;

    /**
     * 全称
     */
    @ExcelProperty(value = "部门名称", index = 5)
    private String name;

    /**
     * 简称
     */
    @ExcelProperty(value = "部门简称", index = 6)
    private String simple_name;

    /**
     * 部门主管
     */
    @ExcelIgnore
    private Long handler_id;
    @ExcelProperty(value = "部门主管", index = 7)
    private String handler_id_name;

    /**
     * 部门副主管
     */
    @ExcelIgnore
    private Long sub_handler_id;
    @ExcelProperty(value = "部门副主管", index = 8)
    private String sub_handler_id_name;

    /**
     * 上级主管领导
     */
    @ExcelIgnore
    private Long leader_id;
    @ExcelProperty(value = "上级主管领导", index = 9)
    private String leader_id_name;

    /**
     * 上级分管领导
     */
    @ExcelIgnore
    private Long response_leader_id;
    @ExcelProperty(value = "上级分管领导", index = 10)
    private String response_leader_id_name;

    /**
     * 说明
     */
    @ExcelProperty(value = "备注", index = 11)
    private String descr;

    /**
     * 是否删除
     */
    @ExcelIgnore
    private Boolean is_del;
    @ExcelIgnore
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    @ExcelIgnore
    private Long c_id;
    @ExcelIgnore
    private String c_name;
    @ExcelIgnore
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;
    @ExcelIgnore
    private Long u_id;
    @ExcelProperty(value = "更新人", index = 12)
    private String u_name;
    @ExcelProperty(value = "更新时间", index = 13)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @ExcelIgnore
    private Integer dbversion;

    /**
     * 关联单号
     */
    @ExcelIgnore
    private Long parent_serial_id;

    /**
     * 关联单号类型
     */
    @ExcelIgnore
    private String parent_serial_type;
    @ExcelIgnore
    private String parent_name;
    @ExcelIgnore
    private String parent_simple_name;
    @ExcelIgnore
    private String parent_type_text;

    /**
     * 弹出框模式：空：普通模式；10：组织使用，需要排除已经选择的数据；
     */
    @ExcelIgnore
    private String dataModel;
}
