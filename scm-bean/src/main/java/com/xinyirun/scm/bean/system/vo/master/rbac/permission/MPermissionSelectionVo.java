package com.xinyirun.scm.bean.system.vo.master.rbac.permission;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: MPermissionSelectionVo
 * @Description: 权限选择响应VO，为权限选择弹窗服务
 */
@Data
@NoArgsConstructor
public class MPermissionSelectionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 所有可选权限列表 */
    private List<MPermissionVo> permissions;
    
    /** 已选权限ID列表 */
    private List<Integer> selectedPermissions;
    
    /** 已选权限数量 */
    private Integer selectedCount;
    
    /** 总权限数量 */
    private Integer totalCount;

}