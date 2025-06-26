package com.xinyirun.scm.core.whapp.service.master.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-24
 */
public interface WhAppIMUserService extends IService<MUserEntity> {

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MUserVo selectUserById(Long id);

}
