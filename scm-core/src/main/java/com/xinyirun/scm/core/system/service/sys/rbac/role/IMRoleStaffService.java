package com.xinyirun.scm.core.system.service.sys.rbac.role;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRoleStaffEntity;

import java.util.List;

/**
 * <p>
 * 角色员工关系表 服务类 接口
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
public interface IMRoleStaffService extends IService<MRoleStaffEntity> {

    /**
     * 保存员工角色关系（全删全插）
     * @param staffId 员工ID
     * @param roleIds 角色ID列表
     * @return 保存结果
     */
    boolean saveStaffRoles(Long staffId, List<Integer> roleIds);

    /**
     * 获取员工已分配的角色ID列表
     * @param staffId 员工ID
     * @return 角色ID列表
     */
    List<Integer> getStaffAssignedRoleIds(Long staffId);

}