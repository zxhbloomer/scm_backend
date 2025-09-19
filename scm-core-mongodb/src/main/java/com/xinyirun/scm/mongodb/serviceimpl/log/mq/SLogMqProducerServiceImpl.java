package com.xinyirun.scm.mongodb.serviceimpl.log.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.mongodb.bean.entity.mq.SLogMqProducerMongoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqProducerMongoVo;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.service.log.mq.ISLogMqProducerService;
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

import java.util.List;

import static com.xinyirun.scm.common.utils.pattern.PatternUtils.regexPattern;

/**
 * @Author: Wqf
 * @Description: 生产者日志
 * @CreateTime : 2023/3/17 10:04
 */
@Service
@Slf4j
public class SLogMqProducerServiceImpl implements ISLogMqProducerService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 分页查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SLogMqProducerMongoVo> selectPageList(SLogMqProducerMongoVo searchCondition) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        paramBuilder(criteria, searchCondition);
        // 分页查询
        Query query = Query.query(criteria);
        query.fields().exclude("mq_data");
//        long count = mongoTemplate.count(query, SLogMqProducerMongoEntity.class);
        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) searchCondition.getPageCondition().getCurrent() - 1,
                (int) searchCondition.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "producter_c_time"));
        List<SLogMqProducerMongoEntity> list = mongoTemplate.find(query.with(pageParam), SLogMqProducerMongoEntity.class);
        List<SLogMqProducerMongoVo> resultList = BeanUtilsSupport.copyProperties(list, SLogMqProducerMongoVo.class);

        // 动态计算最大的limit
        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
        // 根据动态计算的最大limit，计算count
        long count = mongoTemplate.count(query.skip((searchCondition.getPageCondition().getCurrent() - 1) * searchCondition.getPageCondition().getSize())
                .limit(searchCondition.getPageCondition().getLimit_count()), SLogMqProducerMongoEntity.class);

        Page<SLogMqProducerMongoVo> result = MongoPageUtil.covertPages(searchCondition.getPageCondition(), count, resultList);
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
    public SLogMqProducerMongoVo getById(SLogMqProducerMongoVo searchCondition) {
        Criteria cr = Criteria.where("id").is(searchCondition.getId());
        Query query = Query.query(cr);
        SLogMqProducerMongoEntity entity = mongoTemplate.findOne(query, SLogMqProducerMongoEntity.class);
        return (SLogMqProducerMongoVo) BeanUtilsSupport.copyProperties(entity, SLogMqProducerMongoVo.class);
    }

    /**
     * 构建查询参数
     * @param criteria
     * @param searchCondition
     */
    private void paramBuilder(Criteria criteria, SLogMqProducerMongoVo searchCondition) {
        // 类型
        if (StringUtils.isNotEmpty(searchCondition.getType())) {
            criteria.and("type").regex(regexPattern(searchCondition.getType()));
        }
        // 创建 时间起止
        if (null != searchCondition.getProducer_c_time_start() && null != searchCondition.getProducer_c_time_end()) {
            criteria.andOperator(
                    Criteria.where("producter_c_time").gte(LocalDateTimeUtils.getDayStart(searchCondition.getProducer_c_time_start())),
                    Criteria.where("producter_c_time").lte(LocalDateTimeUtils.getDayEnd(searchCondition.getProducer_c_time_end()))
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
