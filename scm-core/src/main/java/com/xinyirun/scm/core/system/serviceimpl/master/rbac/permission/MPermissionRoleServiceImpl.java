package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionRoleEntity;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionRoleMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionRoleService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  用户组织机构关系表 服务实现类
 * </p>
 */
@Service
public class MPermissionRoleServiceImpl extends BaseServiceImpl<MPermissionRoleMapper, MPermissionRoleEntity> implements IMPermissionRoleService {

    @Autowired
    private MPermissionRoleMapper mapper;

}
