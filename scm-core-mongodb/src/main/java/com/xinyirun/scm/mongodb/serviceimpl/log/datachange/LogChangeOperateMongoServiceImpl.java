package com.xinyirun.scm.mongodb.serviceimpl.log.datachange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeOperateMongoVo;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.repository.LogDataChangeOperateMongoMainRepository;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeOperateMongoService;
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
import java.util.Optional;

import static com.xinyirun.scm.common.utils.pattern.PatternUtils.regexPattern;

/**
 * @数据变动记录
 */
@Slf4j
@Service
public class LogChangeOperateMongoServiceImpl implements LogChangeOperateMongoService {

    @Autowired
    LogDataChangeOperateMongoMainRepository repository;

    @Autowired
    LogChangeMainMongoServiceImpl logChangeMainMongoService;

    @Autowired
    LogChangeMongoServiceImpl logChangeMongoService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存数据到 mongodb
     *
     */
    @Override
    public void save(SLogDataChangeOperateMongoEntity bean) {
        /**
         * 注意：
         * 1、发现后台端存在部分不规范，如先执行保存，后执行check，再回滚
         * 2、因为数据变更日志和事务无关，也就是即使事务回滚了，数据变更日志仍然会保存
         * 3、所以需要判断这个参数的type是否为NG，如果是NG，就需要到LogChangeMongoServiceImpl中搜索数据并删除，条件是request_id
         */
        if ("NG".equals(bean.getType())) {
            // 删除数据：变更主表的删除
            logChangeMainMongoService.deleteByRequestId(bean.getRequest_id());
            // 删除数据：变更明细表的删除
            logChangeMongoService.deleteByRequestId(bean.getRequest_id());
        }
        // s_code的操作
        repository.save(bean);
    }

    /**
     * 根据id查询数据
     *
     * @param request_id
     * @return
     */
    @Override
    public SLogDataChangeOperateMongoEntity findByRequestId(String request_id) {
        Optional<SLogDataChangeOperateMongoEntity> entity = repository.findByRequestId(request_id);
        return entity.orElse(null);
    }

    @Override
    public IPage<SLogDataChangeOperateMongoVo> selectPage(SLogDataChangeOperateMongoVo searchCondition) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        paramBuilder(criteria, searchCondition);
        // 分页查询
        Query query = Query.query(criteria);
//        query.fields().exclude("params", "exception", "result", "session");
//        long count = mongoTemplate.count(query, SLogAppMongoEntity.class);
        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) searchCondition.getPageCondition().getCurrent() - 1,
                (int) searchCondition.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "operate_time"));
        List<SLogDataChangeOperateMongoEntity> list = mongoTemplate.find(query.with(pageParam), SLogDataChangeOperateMongoEntity.class);
        List<SLogDataChangeOperateMongoVo> resultList = BeanUtilsSupport.copyProperties(list, SLogDataChangeOperateMongoVo.class);

        // 动态计算最大的limit
        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));
        // 根据动态计算的最大limit，计算count
        long count = mongoTemplate.count(query.skip((searchCondition.getPageCondition().getCurrent() - 1) * searchCondition.getPageCondition().getSize())
                .limit(searchCondition.getPageCondition().getLimit_count()), SLogDataChangeOperateMongoEntity.class);

        Page<SLogDataChangeOperateMongoVo> result = MongoPageUtil.covertPages(searchCondition.getPageCondition(), count, resultList);
        // 计算pages，加上之当前页前的pages
        if(count > searchCondition.getPageCondition().getSize()) {
            result.setTotal(count + searchCondition.getPageCondition().getSize()*(searchCondition.getPageCondition().getCurrent()-1));
        } else {
            result.setTotal(count + searchCondition.getPageCondition().getSize()*(searchCondition.getPageCondition().getCurrent()-1));
        }
        return result;
    }

    /**
     * 拼接参数
     *
     * @param criteria
     * @param searchCondition
     */
    private void paramBuilder(Criteria criteria, SLogDataChangeOperateMongoVo searchCondition) {
        // 用户名
        if (StringUtils.isNotEmpty(searchCondition.getUser_name())) {
            criteria.and("user_name").regex(regexPattern(searchCondition.getUser_name()));
        }
        // 姓名
        if (StringUtils.isNotEmpty(searchCondition.getStaff_id())) {
            criteria.and("staff_name").regex(regexPattern(searchCondition.getStaff_id()));
        }
        // 编辑 时间起止
        if (null != searchCondition.getOver_time() && null != searchCondition.getStart_time()) {
            criteria.andOperator(
                    Criteria.where("operate_time").gte(LocalDateTimeUtils.getDayStart(searchCondition.getStart_time())),
                    Criteria.where("operate_time").lte(LocalDateTimeUtils.getDayEnd(searchCondition.getOver_time()))
            );
        }
        // request_id
        if (StringUtils.isNotEmpty(searchCondition.getRequest_id())) {
            criteria.and("request_id").regex(regexPattern(searchCondition.getRequest_id()));
        }
        // 操作说明
        if (StringUtils.isNotEmpty(searchCondition.getOperation())) {
            criteria.and("operation").regex(regexPattern(searchCondition.getOperation()));
        }
    }
}
