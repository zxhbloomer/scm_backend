package com.xinyirun.scm.core.api.service.business.v1.in;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBPurchasePricingVo;
import com.xinyirun.scm.bean.entity.busniess.wms.in.BPurchasePricingEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-11
 */
public interface ApiIBPurchasePricingService extends IService<BPurchasePricingEntity> {

    /**
     * 同步数据
     */
    void sync(List<ApiBPurchasePricingVo> list);

}
