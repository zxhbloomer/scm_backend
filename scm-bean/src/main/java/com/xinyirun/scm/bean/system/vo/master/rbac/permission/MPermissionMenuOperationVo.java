package com.xinyirun.scm.bean.system.vo.master.rbac.permission;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 权限菜单信息
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限菜单和功能操作提交信息", description = "权限菜单和功能操作提交信息")
@EqualsAndHashCode(callSuper=false)
public class MPermissionMenuOperationVo implements Serializable {

    private static final long serialVersionUID = 6400610339986889068L;

    /**
     * 菜单部分的信息
     */
    List<MPermissionMenuVo>  menu_data;

    /**
     * 功能操作部分的信息
     */
    List<MPermissionOperationVo>  operation_data;
}
