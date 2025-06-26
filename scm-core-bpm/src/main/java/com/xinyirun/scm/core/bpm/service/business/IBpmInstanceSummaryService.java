package com.xinyirun.scm.core.bpm.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.bpm.vo.BpmInstanceSummaryVo;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;

/**
 * <p>
 * 审批流实例-摘要 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-20
 */
public interface IBpmInstanceSummaryService extends IService<BpmInstanceSummaryEntity> {

    /**
     * 根据编号获取数据
     * @param instanceCode
     * @return
     */
    BpmInstanceSummaryVo getDataByInstanceCode(String instanceCode);
}
