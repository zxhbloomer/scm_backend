package com.xinyirun.scm.core.api.service.business.v1.logistics;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.logistics.LogisticsContractVo;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;

public interface ApiIBLogisticsOrderService extends IService<BScheduleEntity> {

    /**
     * 查询是否创建物流订单
     */
    LogisticsContractVo isContractRelevance(LogisticsContractVo vo);

}
