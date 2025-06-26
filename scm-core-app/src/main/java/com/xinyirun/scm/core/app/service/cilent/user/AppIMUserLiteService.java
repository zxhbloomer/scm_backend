package com.xinyirun.scm.core.app.service.cilent.user;

import com.xinyirun.scm.bean.app.vo.master.user.AppMUserLiteVo;
import com.xinyirun.scm.bean.entity.master.user.MUserLiteEntity;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

public interface AppIMUserLiteService extends AppIBaseService<MUserLiteEntity> {
    /**
     * 重建用户简单
     *
     * @param user_id
     * @return
     */
    AppMUserLiteVo reBuildUserLiteData(Long user_id);
}
