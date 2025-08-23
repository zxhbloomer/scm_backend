package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRolePositionEntity;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.role.MRolePositionMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.role.IMRolePositionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  角色岗位关系表 服务实现类
 * </p>
 */
@Service
public class MRolePositionServiceImpl extends BaseServiceImpl<MRolePositionMapper, MRolePositionEntity> implements IMRolePositionService {

    @Autowired
    private MRolePositionMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savePositionRoles(Long positionId, List<Integer> roleIds) {
        if (positionId == null) {
            throw new IllegalArgumentException("岗位ID不能为空");
        }

        try {
            // 第一步：删除该岗位的所有角色关联
            QueryWrapper<MRolePositionEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("position_id", positionId);
            mapper.delete(queryWrapper);

            // 第二步：如果有新角色，批量插入
            if (roleIds != null && !roleIds.isEmpty()) {
                List<MRolePositionEntity> entities = new ArrayList<>();
                for (Integer roleId : roleIds) {
                    if (roleId != null) {
                        MRolePositionEntity entity = new MRolePositionEntity();
                        entity.setPosition_id(positionId.intValue());
                        entity.setRole_id(roleId);
                        entities.add(entity);
                    }
                }
                
                if (!entities.isEmpty()) {
                    return saveBatch(entities);  // 使用MyBatis Plus的saveBatch
                }
            }
            
            return true; // 只删除没有新增也是成功的
        } catch (Exception e) {
            throw new RuntimeException("保存岗位角色失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Integer> getPositionAssignedRoleIds(Long positionId) {
        if (positionId == null) {
            return new ArrayList<>();
        }
        
        QueryWrapper<MRolePositionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("position_id", positionId)
                   .select("role_id")
                   .orderByAsc("role_id");
        
        List<MRolePositionEntity> entities = mapper.selectList(queryWrapper);
        return entities.stream()
                      .map(MRolePositionEntity::getRole_id)
                      .collect(Collectors.toList());
    }

}
