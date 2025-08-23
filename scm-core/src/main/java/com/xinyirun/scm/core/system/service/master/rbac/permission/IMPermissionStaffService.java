package com.xinyirun.scm.core.system.service.master.rbac.permission;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionStaffEntity;

import java.util.List;

/**
 * <p>
 * 权限员工关系表 服务接口
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
public interface IMPermissionStaffService extends IService<MPermissionStaffEntity> {
    
    /**
     * 获取员工已分配的权限ID列表
     * @param staffId 员工ID
     * @return 权限ID列表
     */
    List<Long> getAssignedPermissionIds(Long staffId);
    
    /**
     * 保存员工权限关系（全删全插）
     * @param staffId 员工ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    boolean saveStaffPermissions(Long staffId, List<Long> permissionIds);
}