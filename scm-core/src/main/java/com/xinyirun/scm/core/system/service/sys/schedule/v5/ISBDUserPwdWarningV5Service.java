package com.xinyirun.scm.core.system.service.sys.schedule.v5;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;

/**
 * <p>
 *  用户密码预警
 * </p>
 */
public interface ISBDUserPwdWarningV5Service extends IService<MUserEntity> {

    /**
     * 用户密码预警
     */
    public void userPwdWarning(String parameterClass , String parameter);
}
