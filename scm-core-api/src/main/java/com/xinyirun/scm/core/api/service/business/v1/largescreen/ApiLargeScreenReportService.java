package com.xinyirun.scm.core.api.service.business.v1.largescreen;

import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiContractQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiTodayQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiWarehouseInventoryStatisticsVo;

public interface ApiLargeScreenReportService {

    /**
     * 按仓库类型 获取库存量
     * @return
     */
    ApiWarehouseInventoryStatisticsVo getWarehouseTypeInventory();

    /**
     * 查询采购数量
     * @return
     */
    ApiContractQtyStatisticsVo getContractQty();


    /**
     * 当日累计数量
     * @return
     */
    ApiTodayQtyStatisticsVo getTodayQty();


    /**
     * 查询监管任务预警数量
     * @return
     */
    Integer selectMonitorAlarmCount();

}
