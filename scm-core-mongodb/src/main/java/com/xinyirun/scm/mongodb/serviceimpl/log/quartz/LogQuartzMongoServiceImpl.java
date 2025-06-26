package com.xinyirun.scm.mongodb.serviceimpl.log.quartz;

import com.xinyirun.scm.bean.entity.mongo.log.quartz.SJobLogMongoEntity;
import com.xinyirun.scm.mongodb.service.log.quartz.LogQuartzMongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description 调度 日志保存 mongodb
 * @date 2023/3/1 14:23
 */
@Service
public class LogQuartzMongoServiceImpl implements LogQuartzMongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存数据到 mongodb
     *
     * @param entity 实体类
     */
    @Override
    public void save(SJobLogMongoEntity entity) {
        mongoTemplate.save(entity);
    }


}
