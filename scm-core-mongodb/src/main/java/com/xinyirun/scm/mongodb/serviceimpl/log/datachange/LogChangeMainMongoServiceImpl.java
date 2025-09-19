package com.xinyirun.scm.mongodb.serviceimpl.log.datachange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.repository.LogDataChangeMongoMainRepository;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeMainMongoService;
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
public class LogChangeMainMongoServiceImpl implements LogChangeMainMongoService {

    @Autowired
    LogDataChangeMongoMainRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public IPage<SLogDataChangeMainVo> selectPage(SLogDataChangeMainVo searchCondition) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        paramBuilder(criteria, searchCondition);

        // 分页查询
        Query query = Query.query(criteria);
//    query.fields().exclude("params", "exception", "result", "session");
//    long count = mongoTemplate.count(query, SLogAppMongoEntity.class);

        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) searchCondition.getPageCondition().getCurrent() - 1,
                (int) searchCondition.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "c_time"));

        // 查询符合条件的数据总数
        long totalCount = mongoTemplate.count(query, SLogDataChangeMainMongoEntity.class);

        // 查询当前页的数据
        query.with(pageParam);
        List<SLogDataChangeMainMongoEntity> list = mongoTemplate.find(query, SLogDataChangeMainMongoEntity.class);


        List<SLogDataChangeMainVo> resultList = BeanUtilsSupport.copyProperties(list, SLogDataChangeMainVo.class);

        // 将查询结果封装为分页对象
        Page<SLogDataChangeMainVo> result = MongoPageUtil.covertPages(searchCondition.getPageCondition(), totalCount, resultList);

        return result;
    }


    /**
     * 保存数据到 mongodb
     *
     */
    @Override
    public void save(SLogDataChangeMainMongoEntity bean) {
        // s_code的操作
        repository.save(bean);
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public SLogDataChangeMainMongoEntity findById(String id) {
        Optional<SLogDataChangeMainMongoEntity> entity = repository.findById(id);
        return entity.orElse(null);
    }

    /**
     * 根据orderCode查询数据
     * @param orderCode
     * @return
     */
    @Override
    public SLogDataChangeMainMongoEntity findByOrderCode(String orderCode) {
        return repository.findByOrderCode(orderCode)
                .orElseThrow(() -> new BusinessException("没找到数据 order code: " + orderCode));
    }

    /**
     * 添加按request_id删除数据的方法
     * @param requestId
     * @return
     */
    @Override
    public void deleteByRequestId(String requestId) {
        repository.deleteByRequestId(requestId);
    }

    /**
     * 拼接参数
     *
     * @param criteria
     * @param searchCondition
     */
    private void paramBuilder(Criteria criteria, SLogDataChangeMainVo searchCondition) {
        // 业务类型
        if (StringUtils.isNotEmpty(searchCondition.getOrder_type())) {
            criteria.and("order_type").regex(regexPattern(searchCondition.getOrder_type()));
        }
        // 业务单号
        if (StringUtils.isNotEmpty(searchCondition.getOrder_code())) {
            criteria.and("order_code").regex(regexPattern(searchCondition.getOrder_code()));
        }
        // 编辑 时间起止
        if (null != searchCondition.getOver_time() && null != searchCondition.getStart_time()) {
            criteria.andOperator(
                    Criteria.where("u_time").gte(LocalDateTimeUtils.getDayStart(searchCondition.getStart_time())),
                    Criteria.where("u_time").lte(LocalDateTimeUtils.getDayEnd(searchCondition.getOver_time()))
            );
        }
        // request_id
        if (StringUtils.isNotEmpty(searchCondition.getRequest_id())) {
            criteria.and("request_id").regex(regexPattern(searchCondition.getRequest_id()));
        }
    }
}
