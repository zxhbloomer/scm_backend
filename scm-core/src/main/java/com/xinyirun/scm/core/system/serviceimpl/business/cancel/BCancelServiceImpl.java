package com.xinyirun.scm.core.system.serviceimpl.business.cancel;


import com.xinyirun.scm.bean.entity.busniess.cancel.BCancelEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.cancel.BCancelVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.cancel.BCancelMapper;
import com.xinyirun.scm.core.system.service.business.cancel.IBCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 作废单 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-23
 */
@Service
public class BCancelServiceImpl extends BaseServiceImpl<BCancelMapper, BCancelEntity> implements IBCancelService {

    @Autowired
    private BCancelMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BCancelVo vo) {
        BCancelEntity entity = (BCancelEntity) BeanUtilsSupport.copyProperties(vo, BCancelEntity.class);
        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }
}
