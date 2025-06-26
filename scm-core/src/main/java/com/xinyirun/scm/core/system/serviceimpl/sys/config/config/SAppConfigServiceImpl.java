package com.xinyirun.scm.core.system.serviceimpl.sys.config.config;

import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;
import com.xinyirun.scm.core.system.mapper.sys.app.SAppConfigMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * app配置表 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class SAppConfigServiceImpl extends BaseServiceImpl<SAppConfigMapper, SAppConfigEntity> implements ISAppConfigService {

    @Autowired
    SAppConfigMapper mapper;

    @Override
    public SAppConfigEntity getDataByAppCode(String app_code) {
        return mapper.getDataByCode(app_code);
    }

    @Override
    public SAppConfigEntity getDataByAppKey(String app_key) {
        return mapper.getDataByAppKey(app_key);
    }

    /**
     * 获取所有数据
     */
    @Override
    public List<SAppConfigVo> getListData(SAppConfigVo searchCondition) {
        return mapper.getListData(searchCondition);
    }

}
