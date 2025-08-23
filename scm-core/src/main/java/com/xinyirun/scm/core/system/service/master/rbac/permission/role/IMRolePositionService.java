package com.xinyirun.scm.core.system.service.master.rbac.permission.role;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRolePositionEntity;

import java.util.List;

/**
 * <p>
 * 角色岗位关系表 服务类 接口
 * </p>
 */
public interface IMRolePositionService extends IService<MRolePositionEntity> {

    /**
     * 保存岗位角色关系（全删全插）
     * @param positionId 岗位ID
     * @param roleIds 角色ID列表
     * @return 保存结果
     */
    boolean savePositionRoles(Long positionId, List<Integer> roleIds);

    /**
     * 获取岗位已分配的角色ID列表
     * @param positionId 岗位ID
     * @return 角色ID列表
     */
    List<Integer> getPositionAssignedRoleIds(Long positionId);

}
