package com.xinyirun.scm.mongodb.service.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.sys.SLogSysMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogSysMongoVo;

/**
 * @author Wang Qianfeng
 * @Description 系统日志, mongo 交互
 * @date 2023/3/1 14:21
 */
public interface LogPcSystemMongoService {

    /**
     * 保存数据到 mongodb
     * @param entity 实体类
     */
    void save(SLogSysMongoEntity entity);

    /**
     * 根据查询信息分页查询
     * @param searchCondition
     * @return
     */
    IPage<SLogSysMongoVo> selectPage(SLogSysMongoVo searchCondition);

    /**
     * 查询详情
     * @param searchCondition
     * @return
     */
    SLogSysMongoVo getById(SLogSysMongoVo searchCondition);
}
