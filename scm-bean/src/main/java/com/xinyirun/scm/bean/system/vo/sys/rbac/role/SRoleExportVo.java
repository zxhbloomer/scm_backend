package com.xinyirun.scm.bean.system.vo.sys.rbac.role;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.common.annotations.ExcelAnnotion;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 角色导出Bean
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SRoleExportVo implements Serializable {

    private static final long serialVersionUID = -7449124258332853610L;

    @ExcelAnnotion(name = "NO")
    private Integer no;

    @ExcelAnnotion(name = "角色编码")
    private String code;

    @ExcelAnnotion(name = "角色类型")
    private String type;

    @ExcelAnnotion(name = "角色名称")
    private String name;

    @ExcelAnnotion(name = "权限信息")
    private String permissionList;

    @ExcelAnnotion(name = "说明")
    private String descr;

    @ExcelAnnotion(name = "是否删除")
    private String is_delete;

    @ExcelAnnotion(name = "是否启用")
    private String enable_name;

    @ExcelAnnotion(name = "更新时间", dateFormat="yyyy年MM月dd HH:mm:ss", width=25)
    private Date u_time;


}
