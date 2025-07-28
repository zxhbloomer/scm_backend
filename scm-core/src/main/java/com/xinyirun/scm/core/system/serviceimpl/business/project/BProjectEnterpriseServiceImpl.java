package com.xinyirun.scm.core.system.serviceimpl.business.project;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.project.BProjectEnterpriseEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectEnterpriseMapper;
import com.xinyirun.scm.core.system.service.business.project.IBProjectEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 项目管理-企业 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Service
public class BProjectEnterpriseServiceImpl extends ServiceImpl<BProjectEnterpriseMapper, BProjectEnterpriseEntity> implements IBProjectEnterpriseService {

    @Autowired
    private BProjectEnterpriseMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BProjectEnterpriseEntity entity) {
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BProjectEnterpriseEntity entity) {
        int rtn = mapper.updateById(entity);
        return UpdateResultUtil.OK(rtn);
    }
}
