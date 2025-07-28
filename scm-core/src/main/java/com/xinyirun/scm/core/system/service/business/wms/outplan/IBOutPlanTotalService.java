package com.xinyirun.scm.core.system.service.business.wms.outplan;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanTotalVo;

/**
 * <p>
 * 出库计划汇总 服务类
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
public interface IBOutPlanTotalService extends IService<BOutPlanTotalEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BOutPlanTotalVo> selectPage(BOutPlanTotalVo searchCondition);

}