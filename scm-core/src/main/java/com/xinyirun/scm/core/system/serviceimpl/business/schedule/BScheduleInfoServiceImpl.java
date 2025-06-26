package com.xinyirun.scm.core.system.serviceimpl.business.schedule;

import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleInfoVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.schedule.BScheduleInfoMapper;
import com.xinyirun.scm.core.system.service.business.schedule.IBScheduleInfoService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  调度服务实现类
 * </p>
 *
 * @author wwl
 * @since 2022-01-10
 */
@Service
public class BScheduleInfoServiceImpl extends BaseServiceImpl<BScheduleInfoMapper, BScheduleInfoEntity> implements IBScheduleInfoService {

    @Autowired
    private BScheduleInfoMapper mapper;

    @Override
    public BScheduleInfoVo selectByScheduleId(BScheduleInfoVo searchCondition) {
        return mapper.selectByScheduleId(searchCondition.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BScheduleInfoVo vo) {
        BScheduleInfoEntity entity = new BScheduleInfoEntity();
        BeanUtilsSupport.copyProperties(vo, entity);

        int rtn = mapper.insert(entity);
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> save(BScheduleInfoVo vo) {
        BScheduleInfoEntity entity = new BScheduleInfoEntity();
        BeanUtilsSupport.copyProperties(vo, entity);

        int rtn = mapper.updateById(entity);
        return UpdateResultUtil.OK(rtn);
    }
}
