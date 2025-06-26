package com.xinyirun.scm.quartz.serviceimpl.tenant;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo.log.quartz.SJobLogMongoEntity;
import com.xinyirun.scm.bean.entity.quartz.SJobLogEntity;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.quartz.LogQuartzProducer;
import com.xinyirun.scm.quartz.mapper.tenant.SJobLogQuartzMapper;
import com.xinyirun.scm.quartz.service.tenant.ISJobLogQuartzService;
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
public class SJobLogQuartzServiceImpl extends ServiceImpl<SJobLogQuartzMapper, SJobLogEntity> implements ISJobLogQuartzService {

    @Autowired
    private SJobLogQuartzMapper mapper;

    @Autowired
    private LogQuartzProducer producer;

    /**
     * 新增任务日志
     *
     * @param jobLog 调度日志信息
     */
    @Override
    public void addJobLog(SJobLogEntity jobLog) {
//        mapper.insert(jobLog);
        SJobLogMongoEntity entity =(SJobLogMongoEntity) BeanUtilsSupport.copyProperties(jobLog, SJobLogMongoEntity.class);
        // 设置租户code
        entity.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
        producer.mqSendMq(entity);
    }
}
