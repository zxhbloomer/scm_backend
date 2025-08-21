package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionRoleEntity;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionRoleMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionRoleService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  用户组织机构关系表 服务实现类
 * </p>
 */
@Service
public class MPermissionRoleServiceImpl extends BaseServiceImpl<MPermissionRoleMapper, MPermissionRoleEntity> implements IMPermissionRoleService {

    @Autowired
    private MPermissionRoleMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRolePermissions(Long roleId, List<Integer> permissionIds) {
        if (roleId == null) {
            throw new IllegalArgumentException("角色ID不能为空");
        }

        try {
            // 第一步：删除该角色的所有权限关联
            QueryWrapper<MPermissionRoleEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId);
            mapper.delete(queryWrapper);

            // 第二步：如果有新权限，批量插入
            if (permissionIds != null && !permissionIds.isEmpty()) {
                List<MPermissionRoleEntity> entities = new ArrayList<>();
                for (Integer permissionId : permissionIds) {
                    if (permissionId != null) {
                        MPermissionRoleEntity entity = new MPermissionRoleEntity();
                        entity.setRole_id(roleId.intValue());
                        entity.setPermission_id(permissionId);
                        entities.add(entity);
                    }
                }
                
                if (!entities.isEmpty()) {
                    return saveBatch(entities);
                }
            }
            
            return true; // 只删除没有新增也是成功的
        } catch (Exception e) {
            throw new RuntimeException("保存角色权限失败: " + e.getMessage(), e);
        }
    }

}
