package com.xinyirun.scm.core.system.mapper.sys.rbac.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRoleStaffEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 角色员工关系表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-01-23
 */
@Mapper
@Repository
public interface MRoleStaffMapper extends BaseMapper<MRoleStaffEntity> {

}