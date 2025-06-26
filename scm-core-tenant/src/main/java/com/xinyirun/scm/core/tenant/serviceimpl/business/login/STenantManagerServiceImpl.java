package com.xinyirun.scm.core.tenant.serviceimpl.business.login;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.tenant.manager.user.STenantManagerEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.STenantManagerBo;
import com.xinyirun.scm.core.tenant.mapper.business.login.STenantManagerMapper;
import com.xinyirun.scm.core.tenant.service.business.login.ISTenantManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 租户管理 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-14
 */
@Service
public class STenantManagerServiceImpl extends ServiceImpl<STenantManagerMapper, STenantManagerEntity> implements ISTenantManagerService {

    @Autowired
    private STenantManagerMapper mapper;

    /**
     * 根据租户名称搜索租户信息
     *
     * @param tenant 租户名称
     * @return 租户信息列表
     */
    @Override
    public STenantManagerBo searchByTenant(String tenant) {
        return mapper.selectByTenant(tenant);
    }

}
