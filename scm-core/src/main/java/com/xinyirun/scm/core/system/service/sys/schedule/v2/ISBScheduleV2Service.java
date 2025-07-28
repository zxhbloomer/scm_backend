package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.inventory.BDailyAveragePriceEntity;

/**
 * <p>
 *  物流订单service
 * </p>
 *
 * @author wwl
 * @since 2022-03-21
 */
public interface ISBScheduleV2Service extends IService<BDailyAveragePriceEntity> {

    /**
     * 查询所有待调度、已完成的且手动选择出库计划的物流订单
     *
     */
    void createSchedule();

    /**
     * 查询所有待调度、已完成的且手动选择出库计划的物流订单
     *
     */
    void createScheduleAll(String parameterClass , String parameter);

}
