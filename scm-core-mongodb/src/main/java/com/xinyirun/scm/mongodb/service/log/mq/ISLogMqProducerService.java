package com.xinyirun.scm.mongodb.service.log.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogMqProducerMongoVo;

/**
 * @Author:      Wqf
 * @Description:
 * @CreateTime : 2023/3/17 10:04
 */

public interface ISLogMqProducerService {


    /**
     * 分页查询
     * @param searchCondition
     * @return
     */
    IPage<SLogMqProducerMongoVo> selectPageList(SLogMqProducerMongoVo searchCondition);

    /**
     * 查询详情
     * @param searchCondition
     * @return
     */
    SLogMqProducerMongoVo getById(SLogMqProducerMongoVo searchCondition);
}
