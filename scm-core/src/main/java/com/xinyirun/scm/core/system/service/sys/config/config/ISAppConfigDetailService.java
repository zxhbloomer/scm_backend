package com.xinyirun.scm.core.system.service.sys.config.config;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;

/**
 * <p>
 * app配置表 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ISAppConfigDetailService extends IService<SAppConfigDetailEntity> {
    /**
     * 根据code查询
     */
    SAppConfigDetailVo getDataByCode(String app_code, String app_config_type);

}
