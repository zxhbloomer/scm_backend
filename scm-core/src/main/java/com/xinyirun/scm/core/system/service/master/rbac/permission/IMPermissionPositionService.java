package com.xinyirun.scm.core.system.service.master.rbac.permission;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionPositionEntity;

import java.util.List;

/**
 * 权限岗位关系表 服务接口
 */
public interface IMPermissionPositionService extends IService<MPermissionPositionEntity> {
    
    /**
     * 获取岗位已分配的权限ID列表
     * @param positionId 岗位ID
     * @return 权限ID列表
     */
    List<Long> getAssignedPermissionIds(Long positionId);
    
    /**
     * 保存岗位权限关系（全删全插）
     * @param positionId 岗位ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    boolean savePositionPermissions(Long positionId, List<Long> permissionIds);
}