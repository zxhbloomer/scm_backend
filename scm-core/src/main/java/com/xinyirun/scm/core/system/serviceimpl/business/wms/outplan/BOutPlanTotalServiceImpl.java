package com.xinyirun.scm.core.system.serviceimpl.business.wms.outplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanTotalVo;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanTotalMapper;
import com.xinyirun.scm.core.system.service.business.wms.outplan.IBOutPlanTotalService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 出库计划汇总 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Slf4j
@Service
public class BOutPlanTotalServiceImpl extends BaseServiceImpl<BOutPlanTotalMapper, BOutPlanTotalEntity> implements IBOutPlanTotalService {

    @Override
    public IPage<BOutPlanTotalVo> selectPage(BOutPlanTotalVo searchCondition) {
        // TODO: 实现分页查询逻辑
        return null;
    }
}