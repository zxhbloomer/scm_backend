package com.xinyirun.scm.core.tenant.serviceimpl.business.login;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.tenant.manager.user.SLoginUserEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.SLoginUserBo;
import com.xinyirun.scm.core.tenant.mapper.business.login.SLoginUserMapper;
import com.xinyirun.scm.core.tenant.service.business.login.ISLoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录系统表（用户名密码），与各个租户系统联动，实时更新 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
@Service
public class SLoginUserServiceImpl extends ServiceImpl<SLoginUserMapper, SLoginUserEntity> implements ISLoginUserService {

    @Autowired
    private SLoginUserMapper loginUserMapper;
    
    /**
     * 获取租户信息
     *
     * @param loginUserAo 登录用户参数
     * @return 租户用户列表
     */
    @Override
    public List<SLoginUserBo> getTenant(SLoginUserBo loginUserAo) {
        return loginUserMapper.getDataByName(loginUserAo.getLogin_name());
    }
}
