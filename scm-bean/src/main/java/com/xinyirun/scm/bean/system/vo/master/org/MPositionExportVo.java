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
import java.util.List;

/**
 * <p>
 * 岗位主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MPositionExportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8990646953974899179L;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    @ExcelProperty(value = "集团信息", index = 1)
    private String group_simple_name;

    @ExcelProperty(value = "企业信息", index = 2)
    private String company_simple_name;

    @ExcelProperty(value = "部门信息", index = 3)
    private String parent_dept_simple_name;

    @ExcelProperty(value = "岗位编号", index = 4)
    private String code;

    @ExcelProperty(value = "岗位名称", index = 5)
    private String name;

    @ExcelProperty(value = "岗位简称", index = 6)
    private String simple_name;

    @ExcelProperty(value = "角色信息", index = 7)
    private String role_concat_name;
    
    @ExcelProperty(value = "权限信息", index = 8)
    private String permission_concat_name;
    
    /**
     * 权限数量（保留用于统计，不导出到Excel）
     */
    private Integer permission_count;
    
    /**
     * 角色列表（用于前端角色点击功能，不导出到Excel）
     */
    private List<MPositionVo.RoleItem> roleList;

    /**
     * 权限列表（用于前端权限点击功能，不导出到Excel）
     */
    private List<MPositionVo.PermissionItem> permissionList;

    @ExcelProperty(value = "是否删除", index = 9)
    private String delete_status;

    @ExcelProperty(value = "更新人", index = 10)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 11)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;
}
