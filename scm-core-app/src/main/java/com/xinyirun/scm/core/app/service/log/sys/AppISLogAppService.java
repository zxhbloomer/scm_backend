package com.xinyirun.scm.core.app.service.log.sys;

import com.xinyirun.scm.bean.entity.log.sys.SLogAppEntity;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

public interface AppISLogAppService extends AppIBaseService<SLogAppEntity> {
    /**
     * 异步保存
     * @param entity
     */
    void asyncSave(SLogAppEntity entity);
}
