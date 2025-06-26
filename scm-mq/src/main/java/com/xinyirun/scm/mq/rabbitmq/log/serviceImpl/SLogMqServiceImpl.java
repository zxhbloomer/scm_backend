package com.xinyirun.scm.mq.rabbitmq.log.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.log.mq.SLogMqEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.mq.rabbitmq.log.mapper.SLogMqMapper1;
import com.xinyirun.scm.mq.rabbitmq.log.service.ISLogMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
@Service("logMqService")
public class SLogMqServiceImpl extends ServiceImpl<SLogMqMapper1, SLogMqEntity> implements ISLogMqService {

    @Autowired
    private SLogMqMapper1 mapper;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public SLogMqEntity selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public InsertResultAo<Integer> insertProducter(SLogMqEntity entity) {
        // 插入逻辑保存
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(SLogMqEntity entity) {
        // 更新逻辑保存
        return UpdateResultUtil.OK(mapper.updateById(entity));
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param message_id
     * @return
     */
    @Override
    public SLogMqEntity selectByMessageId(String message_id) {
        // 查询 数据
        SLogMqEntity entity = mapper.selectByMessageId(message_id);
        return entity;
    }
}
