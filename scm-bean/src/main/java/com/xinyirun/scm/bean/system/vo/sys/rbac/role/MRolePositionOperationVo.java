package com.xinyirun.scm.bean.system.vo.sys.rbac.role;


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
@EqualsAndHashCode(callSuper=false)
public class MRolePositionOperationVo implements Serializable {

    private static final long serialVersionUID = -3153596980776629040L;

    private Integer id;

    /**
     * 权限名称
     */
    private String role_name;

    /**
     * 角色名称
     */
    private String position_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

}
