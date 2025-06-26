package com.xinyirun.scm.mq.rabbitmq.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.log.mq.SLogMqEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

/**
 * <p>
 * 消息队列 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface ISLogMqService extends IService<SLogMqEntity> {

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    SLogMqEntity selectByid(Long id);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insertProducter(SLogMqEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SLogMqEntity entity);

    /**
     * 通过key查询
     *
     */
    SLogMqEntity selectByMessageId(String message_id);
}
