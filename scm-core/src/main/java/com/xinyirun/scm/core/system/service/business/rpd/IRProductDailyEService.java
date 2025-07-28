package com.xinyirun.scm.core.system.service.business.rpd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyEEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;

/**
 * <p>
 * 稻壳 加工日报表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
public interface IRProductDailyEService extends IService<RProductDailyEEntity> {

    void insertR_product_daily_e_60(BProductDailyVo vo);

    void insertR_product_daily_e_600(BProductDailyVo vo);
}
