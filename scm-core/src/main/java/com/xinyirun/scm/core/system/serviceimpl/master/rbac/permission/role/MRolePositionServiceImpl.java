package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.role;

import com.xinyirun.scm.bean.entity.sys.rbac.role.MRolePositionEntity;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.role.MRolePositionMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.role.IMRolePositionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  用户组织机构关系表 服务实现类
 * </p>
 */
@Service
public class MRolePositionServiceImpl extends BaseServiceImpl<MRolePositionMapper, MRolePositionEntity> implements IMRolePositionService {

    @Autowired
    private MRolePositionMapper mapper;

}
