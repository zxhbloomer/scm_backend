package com.xinyirun.scm.core.app.serviceimpl.sys.config;

import com.xinyirun.scm.bean.app.vo.sys.config.AppLogoVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppLogoEntity;
import com.xinyirun.scm.core.app.mapper.sys.config.AppSAppLogoMapper;
import com.xinyirun.scm.core.app.service.sys.config.AppISAppLogoService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
@Service
public class AppSAppLogoServiceImpl extends AppBaseServiceImpl<AppSAppLogoMapper, SAppLogoEntity> implements AppISAppLogoService {

    @Autowired
    private AppSAppLogoMapper mapper;

    @Override
    public AppLogoVo get() {
        return mapper.selectOne();
    }
}
