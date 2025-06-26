package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.org.MOrgDeptPositionEntity;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgDeptPositionMapper;
import com.xinyirun.scm.core.system.service.master.org.IMOrgDeptPositionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 岗位与部门关系表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-05-15
 */
@Service
public class MOrgDeptPositionServiceImpl extends ServiceImpl<MOrgDeptPositionMapper, MOrgDeptPositionEntity> implements
    IMOrgDeptPositionService {

}
