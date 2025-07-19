package com.xinyirun.scm.core.api.serviceimpl.business.v1.in;

import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.core.api.mapper.business.in.ApiInPlanMapper;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiInPlanService;
import com.xinyirun.scm.core.api.serviceimpl.base.v1.ApiBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 入库计划 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class ApiInPlanServiceImpl extends ApiBaseServiceImpl<ApiInPlanMapper, BInPlanEntity> implements ApiInPlanService {

    @Autowired
    private ApiInPlanMapper mapper;

    @Override
    public ApiInPlanVo selectPlanById(Integer id) {
        return mapper.selectPlanById(id);
    }
}
