package com.xinyirun.scm.core.system.service.business.rpd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyCEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;

/**
 * <p>
 * 玉米 加工日报表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
public interface IRProductDailyCService extends IService<RProductDailyCEntity> {

    void insertR_product_daily_c_40(BProductDailyVo vo);

    void insertR_product_daily_c_400(BProductDailyVo vo);
}
