package com.xinyirun.scm.core.system.mapper.master.rbac.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionStaffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 权限员工关系表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Mapper
@Repository
public interface MPermissionStaffMapper extends BaseMapper<MPermissionStaffEntity> {

}