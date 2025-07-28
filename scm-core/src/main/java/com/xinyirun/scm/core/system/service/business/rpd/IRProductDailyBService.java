package com.xinyirun.scm.core.system.service.business.rpd;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyBEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;

/**
 * <p>
 * 糙米 加工日报表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
public interface IRProductDailyBService extends IService<RProductDailyBEntity> {

    /**
     * 新增糙米
     * @param vo
     */
    void insertR_product_daily_b_30(BProductDailyVo vo);

    void insertR_product_daily_b_300(BProductDailyVo vo);
}
