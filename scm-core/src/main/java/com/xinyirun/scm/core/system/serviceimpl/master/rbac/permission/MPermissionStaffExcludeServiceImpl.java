package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionStaffExcludeEntity;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionStaffExcludeMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionStaffExcludeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工权限排除表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MPermissionStaffExcludeServiceImpl extends ServiceImpl<MPermissionStaffExcludeMapper, MPermissionStaffExcludeEntity> 
    implements IMPermissionStaffExcludeService {
    
    @Override
    public List<Long> getStaffExcludedPermissionIds(Long staffId) {
        QueryWrapper<MPermissionStaffExcludeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("staff_id", staffId);
        wrapper.select("permission_id");
        
        return list(wrapper).stream()
            .map(MPermissionStaffExcludeEntity::getPermission_id)
            .collect(Collectors.toList());
    }
    
    @Override 
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStaffExcludePermissions(Long staffId, List<Long> permissionIds) {
        // 1. 删除现有关系
        QueryWrapper<MPermissionStaffExcludeEntity> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("staff_id", staffId);
        remove(deleteWrapper);
        
        // 2. 批量插入新关系
        if (CollectionUtils.isNotEmpty(permissionIds)) {
            List<MPermissionStaffExcludeEntity> entities = permissionIds.stream()
                .map(permissionId -> {
                    MPermissionStaffExcludeEntity entity = new MPermissionStaffExcludeEntity();
                    entity.setStaff_id(staffId);
                    entity.setPermission_id(permissionId);
                    return entity;
                })
                .collect(Collectors.toList());
            
            return saveBatch(entities);
        }
        
        return true;
    }
}