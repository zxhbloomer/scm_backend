package com.xinyirun.scm.mongodb.service.log.datachange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeMainVo;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeOperateMongoVo;

/**
 * 数据变动记录
 */
public interface LogChangeOperateMongoService {

    /**
     * 保存数据到 mongodb
     */
    void save(SLogDataChangeOperateMongoEntity bean);

    SLogDataChangeOperateMongoEntity findByRequestId(String request_id);

    /**
     * 查询数据变动记录
     * @param searchCondition
     * @return
     */
    public IPage<SLogDataChangeOperateMongoVo> selectPage(SLogDataChangeOperateMongoVo searchCondition);

}
