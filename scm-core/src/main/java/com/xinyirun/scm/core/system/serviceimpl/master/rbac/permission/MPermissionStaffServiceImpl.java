package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionStaffEntity;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionStaffMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionStaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限员工关系表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MPermissionStaffServiceImpl extends ServiceImpl<MPermissionStaffMapper, MPermissionStaffEntity> implements IMPermissionStaffService {

    @Override
    public List<Long> getAssignedPermissionIds(Long staffId) {
        if (staffId == null) {
            return new ArrayList<>();
        }
        
        // 使用MyBatis Plus的QueryWrapper查询
        QueryWrapper<MPermissionStaffEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("staff_id", staffId)
                   .select("permission_id")
                   .orderByAsc("permission_id");
        
        List<MPermissionStaffEntity> entities = list(queryWrapper);
        
        // 提取permission_id列表
        List<Long> permissionIds = entities.stream()
                                          .map(MPermissionStaffEntity::getPermission_id)
                                          .collect(Collectors.toList());
        
        log.info("获取员工{}已分配权限ID列表，共{}个权限", staffId, permissionIds.size());
        return permissionIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStaffPermissions(Long staffId, List<Long> permissionIds) {
        if (staffId == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
        
        try {
            // 🔥 第一步：使用MyBatis Plus删除员工所有现有权限关系
            QueryWrapper<MPermissionStaffEntity> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("staff_id", staffId);
            remove(deleteWrapper);
            
            log.info("已删除员工{}的所有权限关联", staffId);
            
            // 🚀 第二步：如果有新权限，使用MyBatis Plus批量插入
            if (permissionIds != null && !permissionIds.isEmpty()) {
                List<MPermissionStaffEntity> entities = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                Long currentUserId = getCurrentUserId();
                
                for (Long permissionId : permissionIds) {
                    MPermissionStaffEntity entity = new MPermissionStaffEntity();
                    entity.setStaff_id(staffId);
                    entity.setPermission_id(permissionId);
                    entity.setC_time(now);
                    entity.setU_time(now);
                    entity.setC_id(currentUserId);
                    entity.setU_id(currentUserId);
                    entity.setDbversion(1);
                    entities.add(entity);
                }
                
                // 使用MyBatis Plus的saveBatch方法批量保存
                saveBatch(entities);
                log.info("员工{}权限关系保存成功，保存{}个权限", staffId, entities.size());
            }
            
            log.info("员工权限关系保存成功: staffId={}, permissionCount={}", staffId, 
                    permissionIds != null ? permissionIds.size() : 0);
            return true;
        } catch (Exception e) {
            log.error("保存员工权限关系失败: staffId={}, permissionIds={}", staffId, permissionIds, e);
            throw new BusinessException("保存员工权限关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户ID
     * TODO: 需要根据实际的Spring Security配置实现
     */
    private Long getCurrentUserId() {
        // 临时返回固定值，实际项目中需要从SecurityContext获取当前用户
        // 可以通过以下方式获取：
        // SecurityContext context = SecurityContextHolder.getContext();
        // Authentication authentication = context.getAuthentication();
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // return userDetails.getId();
        return 1L;
    }
}