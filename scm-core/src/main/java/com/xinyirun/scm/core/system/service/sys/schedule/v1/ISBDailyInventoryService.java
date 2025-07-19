package com.xinyirun.scm.core.system.service.sys.schedule.v1;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyInventoryEntity;

/**
 * <p>
 *  每日库存变化表的service
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Deprecated
public interface ISBDailyInventoryService extends IService<BDailyInventoryEntity> {

    /**
     * 重新生成每日库存表
     */
    void reCreateDailyInventoryAll();
}
