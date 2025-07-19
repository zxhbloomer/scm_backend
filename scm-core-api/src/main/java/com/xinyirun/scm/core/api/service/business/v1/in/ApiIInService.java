package com.xinyirun.scm.core.api.service.business.v1.in;

import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanDisContinuedVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.core.api.service.base.v1.ApiIBaseService;

/**
 * <p>
 * 入库计划 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiIInService extends ApiIBaseService<BInPlanEntity> {

    /**
     * 同步新增入库计划
     */
    InsertResultAo<ApiInPlanIdCodeVo> save(ApiInPlanVo vo);

    /**
     * 入库通知中止
     * @param param
     */
    void discontinue(ApiInPlanDisContinuedVo param);
}
