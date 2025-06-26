package com.xinyirun.scm.quartz.service.tenant;

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
public interface ISJobLogQuartzService extends IService<SJobLogEntity> {

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    void addJobLog(SJobLogEntity jobLog);
}
