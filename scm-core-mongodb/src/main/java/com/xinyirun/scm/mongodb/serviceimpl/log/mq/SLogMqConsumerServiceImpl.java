package com.xinyirun.scm.mongodb.serviceimpl.log.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.mongo.log.mq.SLogMqConsumerMongoEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogMqConsumerMongoVo;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqConsumerService;
import com.xinyirun.scm.mongodb.util.MongoPageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.xinyirun.scm.common.utils.pattern.PatternUtils.regexPattern;

/**
 * @Author: Wqf
 * @Description: 消费者日志
 * @CreateTime : 2023/3/17 10:04
 */
@Service
@Slf4j
public class SLogMqConsumerServiceImpl implements ISLogMqConsumerService {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 新增 消费者日志
     *
     * @param entity
     * @return
     */
    @Override
    public void insert(SLogMqConsumerMongoEntity entity, Map<String, Object> headers, MqSenderAo mqSenderAo) {
        try {
            entity.setConsumer_c_time(LocalDateTime.now());
            entity.setCode(mqSenderAo.getType());
            entity.setName(mqSenderAo.getName());
            entity.setExchange(headers.get("amqp_receivedExchange").toString());
            entity.setRouting_key(headers.get("amqp_receivedRoutingKey").toString());
            mongoTemplate.insert(entity);
        } catch (Exception e) {
            log.error("消费者日志保存错误!!!!!!!!!!!!!!!!!");
            log.error("insert error", e);
        }
    }

    /**
     * 分页查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SLogMqConsumerMongoVo> selectPageList(SLogMqConsumerMongoVo searchCondition) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        paramBuilder(criteria, searchCondition);
        // 分页查询
        Query query = Query.query(criteria);
        query.fields().exclude("mq_data");
//        long count = mongoTemplate.count(query, SLogMqConsumerMongoEntity.class);
        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) searchCondition.getPageCondition().getCurrent() - 1,
                (int) searchCondition.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "consumer_c_time"));
        List<SLogMqConsumerMongoEntity> list = mongoTemplate.find(query.with(pageParam), SLogMqConsumerMongoEntity.class);
        List<SLogMqConsumerMongoVo> resultList = BeanUtilsSupport.copyProperties(list, SLogMqConsumerMongoVo.class);

        // 动态计算最大的limit
        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
        // 根据动态计算的最大limit，计算count
        long count = mongoTemplate.count(query.skip((searchCondition.getPageCondition().getCurrent() - 1) * searchCondition.getPageCondition().getSize())
                .limit(searchCondition.getPageCondition().getLimit_count()), SLogMqConsumerMongoEntity.class);

        Page<SLogMqConsumerMongoVo> result = MongoPageUtil.covertPages(searchCondition.getPageCondition(), count, resultList);
        // 计算pages，加上之当前页前的pages
        if(count > searchCondition.getPageCondition().getSize()) {
            result.setTotal(count + searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        } else {
            result.setTotal( searchCondition.getPageCondition().getSize()*searchCondition.getPageCondition().getCurrent());
        }
        return result;
    }

    /**
     * 查询详情
     *
     * @param searchCondition
     * @return
     */
    @Override
    public SLogMqConsumerMongoVo getById(SLogMqConsumerMongoVo searchCondition) {
        Criteria cr = Criteria.where("id").is(searchCondition.getId());
        Query query = Query.query(cr);
        SLogMqConsumerMongoEntity entity = mongoTemplate.findOne(query, SLogMqConsumerMongoEntity.class);
        return (SLogMqConsumerMongoVo) BeanUtilsSupport.copyProperties(entity, SLogMqConsumerMongoVo.class);
    }

    /**
     * 构建查询参数
     * @param criteria
     * @param searchCondition
     */
    private void paramBuilder(Criteria criteria, SLogMqConsumerMongoVo searchCondition) {
        // 类型
        if (StringUtils.isNotEmpty(searchCondition.getType())) {
            criteria.and("type").regex(regexPattern(searchCondition.getType()));
        }
        // 创建 时间起止
        if (null != searchCondition.getConsumer_c_time_start() && null != searchCondition.getConsumer_c_time_end()) {
            criteria.andOperator(
                    Criteria.where("consumer_c_time").gte(LocalDateTimeUtils.getDayStart(searchCondition.getConsumer_c_time_start())),
                    Criteria.where("consumer_c_time").lte(LocalDateTimeUtils.getDayEnd(searchCondition.getConsumer_c_time_end()))
            );
        }
        // message_id
        if (StringUtils.isNotEmpty(searchCondition.getMessage_id())) {
            criteria.and("message_id").regex(regexPattern(searchCondition.getMessage_id()));
        }
        // 队列code
        if (StringUtils.isNotEmpty(searchCondition.getCode())) {
            criteria.and("code").regex(regexPattern(searchCondition.getCode()));
        }
        // 队列名称
        if (StringUtils.isNotEmpty(searchCondition.getName())) {
            criteria.and("name").regex(regexPattern(searchCondition.getName()));
        }
        // 消息体
        if (StringUtils.isNotEmpty(searchCondition.getMq_data())) {
            criteria.and("mq_data").regex(regexPattern(searchCondition.getMq_data()));
        }
    }
}
