package com.xinyirun.scm.core.app.service.sys.config;

import com.xinyirun.scm.bean.app.vo.sys.config.AppNoticeVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppNoticeEntity;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
public interface AppISAppNoticeService extends AppIBaseService<SAppNoticeEntity> {


    /**
     * 获取logo
     */
    public AppNoticeVo get(AppNoticeVo searchCondition);

}
