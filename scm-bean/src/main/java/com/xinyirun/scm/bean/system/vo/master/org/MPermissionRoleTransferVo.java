package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: MPermissionRoleTransferVo
 * @Description: 权限角色vo
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限bean，为穿梭框服务", description = "权限bean，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class MPermissionRoleTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1235882312168402941L;

    /**
     * 穿梭框：全部权限
     */
    List<MPermissionTransferVo> permission_all;

    /**
     * 穿梭框：该角色下，全部权限
     */
    Integer [] role_permission;

    /**
     * 该岗位下，员工数量
     */
    int role_permission_count;
}
