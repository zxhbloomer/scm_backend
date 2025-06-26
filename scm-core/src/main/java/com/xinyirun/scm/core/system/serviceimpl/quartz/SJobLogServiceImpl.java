package com.xinyirun.scm.core.system.serviceimpl.quartz;

import com.xinyirun.scm.bean.entity.quartz.SJobLogEntity;
import com.xinyirun.scm.core.system.mapper.quartz.SJobLogMapper;
import com.xinyirun.scm.core.system.service.quartz.ISJobLogService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
@Service
public class SJobLogServiceImpl extends BaseServiceImpl<SJobLogMapper, SJobLogEntity> implements ISJobLogService {

    @Autowired
    private SJobLogMapper mapper;

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    @Override
    public void addJobLog(SJobLogEntity jobLog)
    {
        mapper.insert(jobLog);
    }
}
