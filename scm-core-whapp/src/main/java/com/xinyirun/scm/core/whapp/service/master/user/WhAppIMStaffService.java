package com.xinyirun.scm.core.whapp.service.master.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.whapp.vo.master.user.WhAppStaffUserBpmInfoVo;

/**
 * <p>
 * 员工 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
public interface WhAppIMStaffService extends IService<MStaffEntity> {

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MStaffVo selectByid(Long id);

    /**
     * 获取审批节点使用的数据
     * @param vo
     * @return
     */
    WhAppStaffUserBpmInfoVo getBpmDataByStaffid(WhAppStaffUserBpmInfoVo vo);
}
