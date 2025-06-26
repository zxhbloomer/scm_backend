package com.xinyirun.scm.core.api.service.business.v1.in;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanEntity;

/**
 * <p>
 * 入库计划 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiInPlanService extends IService<BInPlanEntity> {

    ApiInPlanVo selectPlanById(Integer id);

}
