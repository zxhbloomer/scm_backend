package com.xinyirun.scm.core.system.serviceimpl.sys.rbac.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRoleStaffEntity;
import com.xinyirun.scm.core.system.mapper.sys.rbac.role.MRoleStaffMapper;
import com.xinyirun.scm.core.system.service.sys.rbac.role.IMRoleStaffService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色员工关系表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class MRoleStaffServiceImpl extends BaseServiceImpl<MRoleStaffMapper, MRoleStaffEntity> implements IMRoleStaffService {

    @Autowired
    private MRoleStaffMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveStaffRoles(Long staffId, List<Integer> roleIds) {
        if (staffId == null) {
            throw new IllegalArgumentException("员工ID不能为空");
        }

        try {
            // 🔥 第一步：删除该员工的所有角色关联
            QueryWrapper<MRoleStaffEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("staff_id", staffId);
            mapper.delete(queryWrapper);
            
            log.info("已删除员工{}的所有角色关联", staffId);

            // 🚀 第二步：如果有新角色，批量插入
            if (roleIds != null && !roleIds.isEmpty()) {
                List<MRoleStaffEntity> entities = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
                Long currentUserId = getCurrentUserId();
                
                for (Integer roleId : roleIds) {
                    if (roleId != null) {
                        MRoleStaffEntity entity = new MRoleStaffEntity();
                        entity.setStaff_id(staffId);
                        entity.setRole_id(roleId.longValue());
                        entity.setC_time(now);
                        entity.setU_time(now);
                        entity.setC_id(currentUserId);
                        entity.setU_id(currentUserId);
                        entity.setDbversion(1);
                        entities.add(entity);
                    }
                }
                
                if (!entities.isEmpty()) {
                    boolean result = saveBatch(entities);  // MyBatis Plus批量保存
                    log.info("员工{}角色关系保存成功，保存{}个角色", staffId, entities.size());
                    return result;
                }
            }
            
            log.info("员工{}角色关系处理完成，无新角色需要保存", staffId);
            return true; // 只删除没有新增也是成功的
        } catch (Exception e) {
            log.error("保存员工角色失败: staffId={}, roleIds={}", staffId, roleIds, e);
            throw new RuntimeException("保存员工角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Integer> getStaffAssignedRoleIds(Long staffId) {
        if (staffId == null) {
            return new ArrayList<>();
        }
        
        QueryWrapper<MRoleStaffEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("staff_id", staffId)
                   .select("role_id")
                   .orderByAsc("role_id");
        
        List<MRoleStaffEntity> entities = mapper.selectList(queryWrapper);
        List<Integer> roleIds = entities.stream()
                                       .map(entity -> entity.getRole_id().intValue())
                                       .collect(Collectors.toList());
        
        log.info("获取员工{}已分配角色ID列表，共{}个角色", staffId, roleIds.size());
        return roleIds;
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