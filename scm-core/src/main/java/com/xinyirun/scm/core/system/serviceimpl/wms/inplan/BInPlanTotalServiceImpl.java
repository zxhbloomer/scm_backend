package com.xinyirun.scm.core.system.serviceimpl.wms.inplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanTotalVo;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanTotalMapper;
import com.xinyirun.scm.core.system.service.wms.inplan.IBInPlanTotalService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 入库计划汇总 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Slf4j
@Service
public class BInPlanTotalServiceImpl extends BaseServiceImpl<BInPlanTotalMapper, BInPlanTotalEntity> implements IBInPlanTotalService {

    @Override
    public IPage<BInPlanTotalVo> selectPage(BInPlanTotalVo searchCondition) {
        // TODO: 实现分页查询逻辑
        return null;
    }
}
