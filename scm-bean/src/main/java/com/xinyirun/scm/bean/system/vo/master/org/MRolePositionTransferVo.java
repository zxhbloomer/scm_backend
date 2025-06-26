package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRoleTransferVo;

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
// @ApiModel(value = "角色bean，为穿梭框服务", description = "角色bean，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class MRolePositionTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7244430738591392867L;

    /**
     * 穿梭框：全部角色
     */
    List<MRoleTransferVo> role_all;

    /**
     * 穿梭框：该岗位下，全部角色
     */
    Integer [] position_role;

    /**
     * 该岗位下，员工数量
     */
    int position_role_count;
}
