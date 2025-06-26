package com.xinyirun.scm.core.system.serviceimpl.sys.config.config;

import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.core.system.mapper.sys.app.SAppConfigDetailMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * app配置表 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class SAppConfigDetailServiceImpl extends BaseServiceImpl<SAppConfigDetailMapper, SAppConfigDetailEntity> implements ISAppConfigDetailService {

    @Autowired
    private SAppConfigDetailMapper mapper;

    @Override
    public SAppConfigDetailVo getDataByCode(String code, String app_config_type) {
        return mapper.getDataByCode(code, app_config_type);
    }
}
