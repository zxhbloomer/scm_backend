package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.org.MOrgGroupCompanyEntity;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgGroupCompanyMapper;
import com.xinyirun.scm.core.system.service.master.org.IMOrgGroupCompanyService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门与企业关系表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-05-15
 */
@Service
public class MOrgGroupCompanyServiceImpl extends ServiceImpl<MOrgGroupCompanyMapper, MOrgGroupCompanyEntity> implements
    IMOrgGroupCompanyService {

}
