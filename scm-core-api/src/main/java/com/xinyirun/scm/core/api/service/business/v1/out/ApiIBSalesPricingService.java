package com.xinyirun.scm.core.api.service.business.v1.out;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBSalesPricingVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BSalesPricingEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-11
 */
public interface ApiIBSalesPricingService extends IService<BSalesPricingEntity> {
    /**
     * 同步数据
     */
    void sync(List<ApiBSalesPricingVo> list);
}
