package com.xinyirun.scm.core.system.service.master.rbac.permission;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionRoleEntity;

import java.util.List;

/**
 * <p>
 * 用户组织机构关系表 服务类 接口
 * </p>
 */
public interface IMPermissionRoleService extends IService<MPermissionRoleEntity> {

    /**
     * 保存角色权限关系（全删全插）
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean saveRolePermissions(Long roleId, List<Integer> permissionIds);

}
