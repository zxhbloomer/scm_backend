package com.xinyirun.scm.core.system.service.master.rbac.permission;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionStaffExcludeEntity;

import java.util.List;

/**
 * <p>
 * 员工权限排除表 服务类
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
public interface IMPermissionStaffExcludeService extends IService<MPermissionStaffExcludeEntity> {
    
    /**
     * 获取员工已排除的权限ID列表
     * @param staffId 员工ID
     * @return 权限ID列表
     */
    List<Long> getStaffExcludedPermissionIds(Long staffId);
    
    /**
     * 保存员工排除权限关系（全删全插模式）
     * @param staffId 员工ID
     * @param permissionIds 权限ID列表
     * @return 保存是否成功
     */
    boolean saveStaffExcludePermissions(Long staffId, List<Long> permissionIds);
}