package com.xinyirun.scm.core.system.service.business.rpd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.rpd.RProductDailyDEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;

/**
 * <p>
 * 混合物 加工日报表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
public interface IRProductDailyDService extends IService<RProductDailyDEntity> {

    void insertR_product_daily_d_50(BProductDailyVo vo);

    void insertR_product_daily_d_500(BProductDailyVo vo);
}
