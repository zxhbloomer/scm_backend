package com.xinyirun.scm.core.bpm.serviceimpl.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.bpm.mapper.business.BpmUsersMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Service
@Slf4j
public class BpmUsersServiceImpl extends ServiceImpl<BpmUsersMapper, BpmUsersEntity> implements IBpmUsersService {


    @Autowired
    private BpmUsersMapper mapper;

    /**
     * 根据用户编码查询用户信息
     */
    @Override
    public BpmUsersEntity selectByCode(String assignee) {
        return mapper.selectByCode(assignee);
    }

    /**
     * 新增BPM用户
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insertBpmUser(MStaffEntity staffEntity, MUserEntity userEntity) {
        try {
            BpmUsersEntity entity = new BpmUsersEntity();
            
            // 员工基础信息映射
            entity.setStaff_id(staffEntity.getId());
            entity.setUser_code(staffEntity.getCode());
            entity.setUser_name(staffEntity.getName());
            entity.setPingyin(staffEntity.getName_py());
            entity.setAlisa(staffEntity.getName());
            
            // 性别转换：String → Boolean
            if (staffEntity.getSex() != null) {
                String sex = staffEntity.getSex().trim();
                if ("1".equals(sex) || "男".equals(sex)) {
                    entity.setSex(true);
                } else if ("0".equals(sex) || "女".equals(sex)) {
                    entity.setSex(false);
                }
            }
            
            // 管理员标识：业务管理员(2) > 系统管理员(1) > 普通员工(0)
            Integer adminFlag = 0;
            if (Boolean.TRUE.equals(staffEntity.getIs_admin())) {
                adminFlag = 1;
            }
            if (userEntity != null && Boolean.TRUE.equals(userEntity.getIs_biz_admin())) {
                adminFlag = 2;
            }
            entity.setAdmin(adminFlag);
            
            // 时间和用户字段
            entity.setC_time(staffEntity.getC_time());
            entity.setU_time(staffEntity.getU_time());
            if (userEntity != null) {
                entity.setUser_id(userEntity.getId());
                entity.setAvatar(userEntity.getAvatar());
            }
            
            // 系统字段
            entity.setIs_del(false);
            
            // 执行插入
            mapper.insert(entity);
            
            log.info("BPM用户新增成功，staffId: {}, bpmUserId: {}", staffEntity.getId(), entity.getId());
            return InsertResultUtil.OK(1);
            
        } catch (Exception e) {
            throw new BusinessException("BPM用户新增失败: " + e.getMessage());
        }
    }

    /**
     * 更新BPM用户
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> updateBpmUser(MStaffEntity staffEntity, MUserEntity userEntity) {
        try {
            // 查找现有BPM用户记录
            BpmUsersEntity existingEntity = selectByStaffId(staffEntity.getId());
            
            if (existingEntity == null) {
                // 如果不存在，则新增
                log.warn("BPM用户不存在，转为新增操作，staffId: {}", staffEntity.getId());
                insertBpmUser(staffEntity, userEntity);
                return UpdateResultUtil.OK(1);
            }
            
            // 保持原有ID和创建时间，重新构建数据
            BpmUsersEntity updateEntity = new BpmUsersEntity();
            updateEntity.setId(existingEntity.getId());
            updateEntity.setC_time(existingEntity.getC_time());
            
            // 员工基础信息映射
            updateEntity.setStaff_id(staffEntity.getId());
            updateEntity.setUser_code(staffEntity.getCode());
            updateEntity.setUser_name(staffEntity.getName());
            updateEntity.setPingyin(staffEntity.getName_py());
            updateEntity.setAlisa(staffEntity.getName());
            
            // 性别转换：String → Boolean
            if (staffEntity.getSex() != null) {
                String sex = staffEntity.getSex().trim();
                if ("1".equals(sex) || "男".equals(sex)) {
                    updateEntity.setSex(true);
                } else if ("0".equals(sex) || "女".equals(sex)) {
                    updateEntity.setSex(false);
                }
            }
            
            // 管理员标识：业务管理员(2) > 系统管理员(1) > 普通员工(0)
            Integer adminFlag = 0;
            if (Boolean.TRUE.equals(staffEntity.getIs_admin())) {
                adminFlag = 1;
            }
            if (userEntity != null && Boolean.TRUE.equals(userEntity.getIs_biz_admin())) {
                adminFlag = 2;
            }
            updateEntity.setAdmin(adminFlag);
            
            // 时间和用户字段
            updateEntity.setU_time(staffEntity.getU_time());
            if (userEntity != null) {
                updateEntity.setUser_id(userEntity.getId());
                updateEntity.setAvatar(userEntity.getAvatar());
            }
            
            // 系统字段
            updateEntity.setIs_del(false);
            
            // 执行更新
            int count = mapper.updateById(updateEntity);
            
            log.info("BPM用户更新成功，staffId: {}, bpmUserId: {}", staffEntity.getId(), updateEntity.getId());
            return UpdateResultUtil.OK(count);
            
        } catch (Exception e) {
            throw new BusinessException("BPM用户更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除BPM用户 - 逻辑删除
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Integer> deleteBpmUser(Long staffId) {
        try {
            // 逻辑删除
            int count = mapper.update(null, 
                new UpdateWrapper<BpmUsersEntity>()
                    .eq("staff_id", staffId)
                    .set("is_del", true));
            
            log.info("BPM用户删除成功，staffId: {}, 影响行数: {}", staffId, count);
            return DeleteResultUtil.OK(count);
            
        } catch (Exception e) {
            log.error("BPM用户删除失败，staffId: {}", staffId, e);
            throw new BusinessException("BPM用户删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据员工ID查询BPM用户
     */
    @Override
    public BpmUsersEntity selectByStaffId(Long staffId) {
        return mapper.selectOne(new QueryWrapper<BpmUsersEntity>()
                .eq("staff_id", staffId)
                .eq("is_del", false));
    }

}
