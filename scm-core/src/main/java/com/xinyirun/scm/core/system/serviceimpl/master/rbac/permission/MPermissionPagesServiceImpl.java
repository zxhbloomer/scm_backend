package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionPagesEntity;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionPagesMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionPagesService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限页面表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Service
public class MPermissionPagesServiceImpl extends ServiceImpl<MPermissionPagesMapper, MPermissionPagesEntity> implements
    IMPermissionPagesService {

}
