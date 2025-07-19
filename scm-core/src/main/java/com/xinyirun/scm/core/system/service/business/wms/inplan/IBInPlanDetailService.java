package com.xinyirun.scm.core.system.service.business.wms.inplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanDetailVo;

/**
 * <p>
 * 入库计划明细 服务类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
public interface IBInPlanDetailService extends IService<BInPlanDetailEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BInPlanDetailVo> selectPage(BInPlanDetailVo searchCondition);

}
