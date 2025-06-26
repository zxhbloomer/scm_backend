package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MPermissionTransferVo
 * @Description: 权限bean，为穿梭框服务
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限bean，为穿梭框服务", description = "权限bean，为穿梭框服务")
@EqualsAndHashCode(callSuper=false)
public class MPermissionTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -2140675627571778112L;

    /** 穿梭框 key */
    private Integer key;
    /** 穿梭框 label */
    private String label;
    /**
     * 租户id
     */
//    private Long tenant_id;

    /** 角色ID */
    private Integer role_id;

    /** 穿梭框已经选择的权限id */
    Integer [] role_permissions;

}
