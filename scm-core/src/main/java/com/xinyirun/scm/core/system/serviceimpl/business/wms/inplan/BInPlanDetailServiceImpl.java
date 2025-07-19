package com.xinyirun.scm.core.system.serviceimpl.business.wms.inplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanDetailVo;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.service.business.wms.inplan.IBInPlanDetailService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 入库计划明细 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Slf4j
@Service
public class BInPlanDetailServiceImpl extends BaseServiceImpl<BInPlanDetailMapper, BInPlanDetailEntity> implements IBInPlanDetailService {

    @Override
    public IPage<BInPlanDetailVo> selectPage(BInPlanDetailVo searchCondition) {
        // TODO: 实现分页查询逻辑
        return null;
    }
}
