package com.xinyirun.scm.quartz.service.master;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.tenant.manager.quartz.SJobManagerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.bo.tenant.manager.quartz.SJobManagerBo;

/**
 * <p>
 * 定时任务 服务接口
 * </p>
 */
public interface ISJobManagerQuartzService extends IService<SJobManagerEntity> {

    /**
     * 保存定时任务
     *
     * @param bo 定时任务实体
     * @return 插入结果
     */
    InsertResultAo<SJobManagerBo> insert(SJobManagerBo bo);
}
