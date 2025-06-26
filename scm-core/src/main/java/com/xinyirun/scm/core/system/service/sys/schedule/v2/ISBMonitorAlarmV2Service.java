package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;

public interface ISBMonitorAlarmV2Service extends IService<BMonitorEntity> {

    /**
     * 查询需要预警的监管任务
     */
    void getMonitorAlarmList(String parameterClass , String parameter);
//    void getMonitorAlarmList();
}
