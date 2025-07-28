package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.inventory.BDailyInventoryEntity;

/**
 * <p>
 *  每日库存变化表的service
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISBDailyInventoryNewV2Service extends IService<BDailyInventoryEntity> {

    /**
     * 重新生成每日库存表，所有仓库
     *
     */
    void reCreateDailyInventoryAll();

    /**
     * 重新生成每日库存表
     * 定时任务，根据条件来生成
     * 1、仓库、库区、库位id
     * 2、货主
     * 3、sku_id
     * 4、日期
     *
     */
    void reCreateDailyInventoryAll(String parameterClass , String parameter);

    /**
     * 重新生成每日库存表，所有仓库
     *
     */
    void syncDailyInventoryAll(String parameterClass , String parameter);
}
