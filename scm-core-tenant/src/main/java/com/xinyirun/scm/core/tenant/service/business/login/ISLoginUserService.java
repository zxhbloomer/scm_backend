package com.xinyirun.scm.core.tenant.service.business.login;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.tenant.manager.user.SLoginUserEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.SLoginUserBo;

import java.util.List;

/**
 * <p>
 * 用户登录系统表（用户名密码），与各个租户系统联动，实时更新 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
public interface ISLoginUserService extends IService<SLoginUserEntity> {
    
    /**
     * 获取租户信息
     *
     * @param loginUserAo 登录用户参数
     * @return 租户用户列表
     */
    List<SLoginUserBo> getTenant(SLoginUserBo loginUserAo);
}
