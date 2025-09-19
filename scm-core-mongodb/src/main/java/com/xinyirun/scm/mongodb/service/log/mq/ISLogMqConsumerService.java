package com.xinyirun.scm.mongodb.service.log.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.mongodb.bean.entity.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerMongoVo;

import java.util.Map;

/**
 * @Author:      Wqf
 * @Description:
 * @CreateTime : 2023/3/17 10:04
 */

public interface ISLogMqConsumerService {

    /**
     * 新增 消费者日志
     * @param vo
     * @return
     */
    void insert(SLogMqConsumerClickHouseVo vo, Map<String, Object> headers, MqSenderAo mqSenderAo);

    /**
     * 分页查询
     * @param searchCondition
     * @return
     */
    IPage<SLogMqConsumerMongoVo> selectPageList(SLogMqConsumerMongoVo searchCondition);

    /**
     * 查询详情
     * @param searchCondition
     * @return
     */
    SLogMqConsumerMongoVo getById(SLogMqConsumerMongoVo searchCondition);
}
