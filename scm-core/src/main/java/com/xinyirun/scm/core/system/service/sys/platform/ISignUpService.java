package com.xinyirun.scm.core.system.service.sys.platform;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.sys.platform.SignUpVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISignUpService extends IService<MStaffEntity> {

    /**
     * 注册
     * @param bean
     * @return
     */
    Boolean signUp(SignUpVo bean);

    /**
     * 第一步：检查用户名，手机号码是否重复check
     * @param bean
     * @return
     */
    Boolean checkMobile(SignUpVo bean);

    /**
     * 第二步：检查租户名称、管理员名称，手机号码是否重复check
     * @param bean
     * @return
     */
    Boolean check(SignUpVo bean);
}
