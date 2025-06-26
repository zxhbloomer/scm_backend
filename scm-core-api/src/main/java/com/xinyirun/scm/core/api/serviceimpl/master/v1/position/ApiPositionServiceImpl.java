package com.xinyirun.scm.core.api.serviceimpl.master.v1.position;

import com.xinyirun.scm.bean.api.vo.business.position.ApiPositionVo;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.core.api.mapper.master.position.ApiPositionMapper;
import com.xinyirun.scm.core.api.service.master.v1.position.ApiPositionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  岗位主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class ApiPositionServiceImpl extends BaseServiceImpl<ApiPositionMapper, MPositionEntity> implements ApiPositionService {

    @Autowired
    private ApiPositionMapper mapper;


    @Override
    public List<ApiPositionVo> list(ApiPositionVo searchCondition) {
        return mapper.list(searchCondition);
    }
}
