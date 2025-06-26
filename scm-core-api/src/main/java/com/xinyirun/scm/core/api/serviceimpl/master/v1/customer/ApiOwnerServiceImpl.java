package com.xinyirun.scm.core.api.serviceimpl.master.v1.customer;

import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiOwnerVo;
import com.xinyirun.scm.core.api.mapper.master.customer.ApiOwnerMapper;
import com.xinyirun.scm.core.api.service.master.v1.customer.ApiOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Service
public class ApiOwnerServiceImpl extends BaseServiceImpl<ApiOwnerMapper, MOwnerEntity> implements ApiOwnerService {

    @Autowired
    private ApiOwnerMapper mapper;

    /**
     * 货主下拉
     */
    public List<ApiOwnerVo> getOwner(ApiOwnerVo vo){
        return mapper.getOwner(vo);
    }
}
