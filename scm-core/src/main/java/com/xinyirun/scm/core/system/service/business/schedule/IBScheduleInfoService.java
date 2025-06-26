package com.xinyirun.scm.core.system.service.business.schedule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleInfoVo;

/**
 * <p>
 *  调度服务类
 * </p>
 *
 * @author wwl
 * @since 2022-04-10
 */
public interface IBScheduleInfoService extends IService<BScheduleInfoEntity> {

    /**
     * 获取列表，页面查询
     */
    BScheduleInfoVo selectByScheduleId(BScheduleInfoVo searchCondition);

    /**
     * 插入一条记录
     */
    InsertResultAo<Integer> insert(BScheduleInfoVo vo);

    /**
     * 插入一条记录
     */
    UpdateResultAo<Integer> save(BScheduleInfoVo vo);


}
