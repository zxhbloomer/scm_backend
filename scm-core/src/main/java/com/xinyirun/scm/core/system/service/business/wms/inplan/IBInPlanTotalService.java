package com.xinyirun.scm.core.system.service.business.wms.inplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.inplan.BInPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inplan.BInPlanTotalVo;

/**
 * <p>
 * 入库计划汇总 服务类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
public interface IBInPlanTotalService extends IService<BInPlanTotalEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BInPlanTotalVo> selectPage(BInPlanTotalVo searchCondition);

}
