package com.xinyirun.scm.mongodb.service.log.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.api.SLogApiMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogApiMongoVo;

/**
 * @author Wang Qianfeng
 * @Description 系统日志, mongo 交互
 * @date 2023/3/1 14:21
 */
public interface LogApiMongoService {

    /**
     * 保存数据到 mongodb
     * @param entity 实体类
     */
    void save(SLogApiMongoEntity entity);

    /**
     * 分页查询
     * @param searchCondition
     * @return
     */
    IPage<SLogApiMongoVo> selectPage(SLogApiMongoVo searchCondition);

    /**
     * 查询详情
     * @param searchCondition
     * @return
     */
    SLogApiMongoVo getById(SLogApiMongoVo searchCondition);

}
