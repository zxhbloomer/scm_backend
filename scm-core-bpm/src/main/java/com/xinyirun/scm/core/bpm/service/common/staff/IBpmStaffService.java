package com.xinyirun.scm.core.bpm.service.common.staff;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;

/**
 * <p>
 * 员工 服务类
 * </p>
 */
public interface IBpmStaffService extends IService<MStaffEntity> {

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MStaffVo selectByid(Long id);

    /**
     * 获取数据byid
     * @param code
     * @return
     */
    MStaffVo selectByCode(String code);

}
