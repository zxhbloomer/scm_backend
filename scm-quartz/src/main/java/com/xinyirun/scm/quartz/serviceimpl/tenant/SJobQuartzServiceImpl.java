package com.xinyirun.scm.quartz.serviceimpl.tenant;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.quartz.mapper.tenant.SJobQuartzMapper;
import com.xinyirun.scm.quartz.service.tenant.ISJobQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
//@DS("#header.X-Tenant-ID")
@Service
public class SJobQuartzServiceImpl extends ServiceImpl<SJobQuartzMapper, SJobEntity> implements ISJobQuartzService {

    @Autowired
    SJobQuartzMapper mapper;

    /**
     *  任务vo
     * @param job
     */
//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateJob(SJobEntity job) {
        // 先获取最新的数据后，再更新至数据库中
        SJobEntity entity = mapper.selectById(job.getId());
        entity.setFire_time(job.getFire_time());
        entity.setScheduled_fire_time(job.getScheduled_fire_time());
        entity.setPrev_fire_time(job.getPrev_fire_time());
        entity.setNext_fire_time(job.getNext_fire_time());
        entity.setRun_times(job.getRun_times());

        int runtimes = 0;
        if(entity.getRun_times() == null) {
            runtimes = 1;
        } else {
            runtimes = entity.getRun_times();
            runtimes = runtimes + 1;
        }
        entity.setRun_times(runtimes);

        mapper.updateJob(entity);
    }
}
