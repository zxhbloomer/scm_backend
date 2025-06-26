package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.xinyirun.scm.bean.entity.master.org.MStaffOrgEntity;
import com.xinyirun.scm.core.system.mapper.master.org.MStaffOrgMapper;
import com.xinyirun.scm.core.system.service.master.org.IMStaffOrgService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  用户组织机构关系表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class MStaffOrgServiceImpl extends BaseServiceImpl<MStaffOrgMapper, MStaffOrgEntity> implements IMStaffOrgService {

    @Autowired
    private MStaffOrgMapper mapper;

}
