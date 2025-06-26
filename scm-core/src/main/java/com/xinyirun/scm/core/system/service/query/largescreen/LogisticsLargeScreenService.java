package com.xinyirun.scm.core.system.service.query.largescreen;

import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsLargeScreenVo;
import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsMonitorLargeScreenVo;

import java.util.List;

public interface LogisticsLargeScreenService {

    /**
     * 供应链相关
     * @return
     */
    LogisticsLargeScreenVo querySupply();

    /**
     * 车次相关
     * @return
     */
    LogisticsLargeScreenVo queryTrainCount();

    /**
     * 物流合同
     * @return
     */
    LogisticsLargeScreenVo querySchedule();

    /**
     * 当月 & 当天数据
     * @return
     */
    LogisticsLargeScreenVo queryMonthData();

    /**
     * 实时运输状态
     * @return
     */
    List<LogisticsMonitorLargeScreenVo> queryMonitor();

}
