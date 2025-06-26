package com.xinyirun.scm.core.system.serviceimpl.master.direction;

import com.xinyirun.scm.bean.entity.master.direction.MDirectionEntity;
import com.xinyirun.scm.core.system.mapper.master.direction.MDirectionMapper;
import com.xinyirun.scm.core.system.service.master.direction.IMDirectionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wwl
 * @since 2022-03-10
 */
@Service
public class IMDirectionServiceImpl extends BaseServiceImpl<MDirectionMapper, MDirectionEntity> implements IMDirectionService {

    @Autowired
    private MDirectionMapper mapper;

}
