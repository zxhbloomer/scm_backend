package com.xinyirun.scm.core.system.service.business.fund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.fund.BFundMonitorEntity;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundMonitorVo;

/**
 * <p>
 * 资金流水监控表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
public interface IBFundMonitorService extends IService<BFundMonitorEntity> {

    /**
     * 查询资金流水监控
     */
    IPage<BFundMonitorVo> selectPage(BFundMonitorVo searchCondition);
}
