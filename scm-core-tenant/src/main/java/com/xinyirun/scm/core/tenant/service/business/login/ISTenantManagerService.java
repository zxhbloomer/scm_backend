package com.xinyirun.scm.core.tenant.service.business.login;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.tenant.manager.user.STenantManagerEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.STenantManagerBo;

/**
 * <p>
 *  租户管理 服务接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-14
 */
public interface ISTenantManagerService extends IService<STenantManagerEntity> {

    /**
     * 根据租户名称搜索租户信息
     *
     * @param tenant 租户名称
     * @return 租户信息列表
     */
    STenantManagerBo searchByTenant(String tenant);

}
