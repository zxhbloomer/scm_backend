package com.xinyirun.scm.core.system.service.business.alarm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmGroupStaffEntity;

import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
public interface IBAlarmGroupStaffService extends IService<BAlarmGroupStaffEntity> {

    /**
     * 根据预警组查询预警人员
     * @param groupList
     * @return
     */
    Set<Integer> selectStaffIdsByGroupIds(Set<Integer> groupList);

}
