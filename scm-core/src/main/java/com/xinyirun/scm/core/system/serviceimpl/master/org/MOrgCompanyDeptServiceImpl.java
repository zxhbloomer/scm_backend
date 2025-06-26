package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.master.org.MOrgCompanyDeptEntity;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgCompanyDeptMapper;
import com.xinyirun.scm.core.system.service.master.org.IMOrgCompanyDeptService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 部门与部门关系表，多部门嵌套关系表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-05-15
 */
@Service
public class MOrgCompanyDeptServiceImpl extends ServiceImpl<MOrgCompanyDeptMapper, MOrgCompanyDeptEntity> implements
    IMOrgCompanyDeptService {

}
