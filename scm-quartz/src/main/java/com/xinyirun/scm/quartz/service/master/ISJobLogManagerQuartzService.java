package com.xinyirun.scm.quartz.service.master;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.tenant.manager.quartz.SJobLogManagerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.bo.tenant.manager.quartz.SJobLogManagerBo;

/**
 * <p>
 * 定时任务日志 服务接口
 * </p>
 */
public interface ISJobLogManagerQuartzService extends IService<SJobLogManagerEntity> {

    /**
     * 保存定时任务日志
     *
     * @param bo 定时任务日志实体
     * @return 插入结果
     */
    InsertResultAo<SJobLogManagerBo> insert(SJobLogManagerBo bo);
}
