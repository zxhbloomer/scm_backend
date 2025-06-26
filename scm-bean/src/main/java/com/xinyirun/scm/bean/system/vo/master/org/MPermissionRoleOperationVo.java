package com.xinyirun.scm.bean.system.vo.master.org;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限角色关系，权限名称
 * </p>
 *
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限角色关系，权限名称", description = "权限角色关系，权限名称")
@EqualsAndHashCode(callSuper=false)
public class MPermissionRoleOperationVo implements Serializable {

    private static final long serialVersionUID = -2747194697713502805L;

    private Integer id;

    /**
     * 权限名称
     */
    private String permission_name;

    /**
     * 角色名称
     */
    private String role_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

}
