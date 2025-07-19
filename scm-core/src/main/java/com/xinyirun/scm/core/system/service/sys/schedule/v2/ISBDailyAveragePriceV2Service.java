package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyAveragePriceEntity;

/**
 * <p>
 *  每日平均单价service
 * </p>
 *
 * @author wwl
 * @since 2022-03-21
 */
public interface ISBDailyAveragePriceV2Service extends IService<BDailyAveragePriceEntity> {

    /**
     * 重新生成每日移动平均单价
     *
     */
    public void reCreateDailyAveragePriceAll();

    /**
     * 生成每日移动平均单价
     * 定时任务，根据条件来生成
     * 1、仓库、库区、库位id
     * 2、货主
     * 3、sku_id
     * 4、日期
     *
     */
    public void createDailyAveragePrice(String parameterClass , String parameter);
}
