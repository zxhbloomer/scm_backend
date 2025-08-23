package com.xinyirun.scm.bean.system.vo.master.permission;

import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionPositionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 权限岗位关系表VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MPermissionPositionVo extends MPermissionPositionEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 权限名称
     */
    private String permission_name;
    
    /**
     * 岗位名称
     */
    private String position_name;
    
    /**
     * 创建人姓名
     */
    private String c_name;
    
    /**
     * 修改人姓名
     */
    private String u_name;
}