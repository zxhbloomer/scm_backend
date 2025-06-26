package com.xinyirun.scm.quartz.service.tenant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
public interface ISJobQuartzService extends IService<SJobEntity> {

    /**
     *  任务vo
     * @param job
     */
    void updateJob(SJobEntity job);

}
