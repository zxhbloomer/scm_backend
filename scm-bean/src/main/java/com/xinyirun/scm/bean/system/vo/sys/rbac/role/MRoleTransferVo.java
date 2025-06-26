package com.xinyirun.scm.bean.system.vo.sys.rbac.role;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MRoleTransferVo
 * @Description: 角色bean，为穿梭框服务
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "角色bean，为穿梭框服务", description = "角色bean，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class MRoleTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -2140675627571778112L;

    /** 穿梭框 key */
    private Integer key;
    /** 穿梭框 label */
    private String label;
    /**
     * 租户id
     */
//    private Long tenant_id;

    /** 岗位ID */
    private Integer position_id;

    /** 穿梭框已经选择的权限id */
    Integer [] position_roles;

}
