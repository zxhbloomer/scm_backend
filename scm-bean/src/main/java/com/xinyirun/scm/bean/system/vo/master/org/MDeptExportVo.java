package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.common.annotations.ExcelAnnotion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    private static final long serialVersionUID = 1189343038090433062L;

    private Long id;

    @ExcelAnnotion(name = "集团信息")
    private String group_full_simple_name;

    @ExcelAnnotion(name = "企业信息")
    private String company_simple_name;

    @ExcelAnnotion(name = "上级部门")
    private String parent_dept_simple_name;

    @ExcelAnnotion(name = "编码")
    private String code;

    /**
     * 全称
     */
    @ExcelAnnotion(name = "名称")
    private String name;

    /**
     * 简称
     */
    @ExcelAnnotion(name = "简称")
    private String simple_name;

    /**
     * 部门主管
     */
    private Long handler_id;
    @ExcelAnnotion(name = "部门主管")
    private String handler_id_name;

    /**
     * 部门副主管
     */
    private Long sub_handler_id;
    @ExcelAnnotion(name = "部门副主管")
    private String sub_handler_id_name;

    /**
     * 上级主管领导
     */
    private Long leader_id;
    @ExcelAnnotion(name = "上级主管领导")
    private String leader_id_name;

    /**
     * 上级分管领导
     */
    private Long response_leader_id;
    @ExcelAnnotion(name = "上级分管领导")
    private String response_leader_id_name;

    /**
     * 说明
     */
    @ExcelAnnotion(name = "说明")
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;
    @ExcelAnnotion(name = "是否删除")
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;
    @ExcelAnnotion(name = "新增人")
    private String c_name;
    @ExcelAnnotion(name = "新增时间")
    private LocalDateTime c_time;
    private Long u_id;
    @ExcelAnnotion(name = "更新人")
    private String u_name;
    @ExcelAnnotion(name = "更新时间")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 关联单号
     */
    private Long parent_serial_id;

    /**
     * 关联单号类型
     */
    private String parent_serial_type;
    private String parent_name;
    private String parent_simple_name;
    private String parent_type_text;

    /**
     * 弹出框模式：空：普通模式；10：组织使用，需要排除已经选择的数据；
     */
    private String dataModel;
}
