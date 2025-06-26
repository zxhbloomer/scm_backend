package com.xinyirun.scm.core.system.service.sys.schedule.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.price.BMaterialPriceEntity;

/**
 * <p>
 *  每日平均单价service
 * </p>
 *
 * @author wwl
 * @since 2022-03-21
 */
public interface ISBMaterialPriceV2Service extends IService<BMaterialPriceEntity> {

    /**
     * 生成每日商品单价
     */
    public void createMaterialPrice(String parameterClass , String parameter);
}
