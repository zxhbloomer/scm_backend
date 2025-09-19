package com.xinyirun.scm.mongodb.service.log.app;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.app.SLogAppMongoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogAppMongoVo;

/**
 * @author Wang Qianfeng
 * @Description app日志, mongo 交互
 * @date 2023/3/1 14:21
 */
public interface LogAppMongoService {

    /**
     * 保存数据到 mongodb
     * @param entity 实体类
     */
    void save(SLogAppMongoEntity entity);

    /**
     * 分页查询
     * @param searchCondition
     * @return
     */
    IPage<SLogAppMongoVo> selectPage(SLogAppMongoVo searchCondition);

    /**
     * 查询详情
     * @param searchCondition
     * @return
     */
    SLogAppMongoVo getById(SLogAppMongoVo searchCondition);

}
