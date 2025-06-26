package com.xinyirun.scm.core.system.service.quartz;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.quartz.SJobLogEntity;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
public interface ISJobLogService extends IService<SJobLogEntity> {

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    void addJobLog(SJobLogEntity jobLog);
}
