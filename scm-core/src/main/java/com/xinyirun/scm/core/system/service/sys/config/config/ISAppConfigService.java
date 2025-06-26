package com.xinyirun.scm.core.system.service.sys.config.config;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;

import java.util.List;

/**
 * <p>
 * app配置表 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ISAppConfigService extends IService<SAppConfigEntity> {

    SAppConfigEntity getDataByAppCode(String app_code);

    SAppConfigEntity getDataByAppKey(String app_key);

    /**
     * 获取所有数据
     */
    List<SAppConfigVo> getListData(SAppConfigVo searchCondition) ;

}
