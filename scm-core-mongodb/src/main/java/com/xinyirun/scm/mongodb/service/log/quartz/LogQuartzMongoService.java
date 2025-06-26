package com.xinyirun.scm.mongodb.service.log.quartz;

import com.xinyirun.scm.bean.entity.mongo.log.quartz.SJobLogMongoEntity;

/**
 * @author Wang Qianfeng
 * @Description app日志, mongo 交互
 * @date 2023/3/1 14:21
 */
public interface LogQuartzMongoService {

    /**
     * 保存数据到 mongodb
     * @param entity 实体类
     */
    void save(SJobLogMongoEntity entity);
}
