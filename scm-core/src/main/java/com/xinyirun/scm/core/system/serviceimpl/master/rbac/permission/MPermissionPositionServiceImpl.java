package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionPositionEntity;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.master.permission.MPermissionPositionMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionPositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限岗位关系表 服务实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MPermissionPositionServiceImpl extends ServiceImpl<MPermissionPositionMapper, MPermissionPositionEntity> implements IMPermissionPositionService {

    @Override
    public List<Long> getAssignedPermissionIds(Long positionId) {
        if (positionId == null) {
            return new ArrayList<>();
        }
        
        // 使用MyBatis Plus的QueryWrapper查询
        QueryWrapper<MPermissionPositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("position_id", positionId)
                   .select("permission_id")
                   .orderByAsc("permission_id");
        
        List<MPermissionPositionEntity> entities = list(queryWrapper);
        
        // 提取permission_id列表
        return entities.stream()
                      .map(MPermissionPositionEntity::getPermission_id)
                      .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savePositionPermissions(Long positionId, List<Long> permissionIds) {
        if (positionId == null) {
            throw new IllegalArgumentException("岗位ID不能为空");
        }
        
        try {
            // 第一步：使用MyBatis Plus删除岗位所有现有权限关系
            QueryWrapper<MPermissionPositionEntity> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("position_id", positionId);
            remove(deleteWrapper);
            
            // 第二步：如果有新权限，使用MyBatis Plus批量插入
            if (permissionIds != null && !permissionIds.isEmpty()) {
                List<MPermissionPositionEntity> entities = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                Long currentUserId = getCurrentUserId();
                
                for (Long permissionId : permissionIds) {
                    MPermissionPositionEntity entity = new MPermissionPositionEntity();
                    entity.setPosition_id(positionId);
                    entity.setPermission_id(permissionId);
                    entity.setC_time(now);
                    entity.setU_time(now);
                    entity.setC_id(currentUserId);
                    entity.setU_id(currentUserId);
                    entities.add(entity);
                }
                
                // 使用MyBatis Plus的saveBatch方法批量保存
                saveBatch(entities);
            }
            
            log.info("岗位权限关系保存成功: positionId={}, permissionCount={}", positionId, 
                    permissionIds != null ? permissionIds.size() : 0);
            return true;
        } catch (Exception e) {
            log.error("保存岗位权限关系失败: positionId={}, permissionIds={}", positionId, permissionIds, e);
            throw new BusinessException("保存岗位权限关系失败: " + e.getMessage());
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