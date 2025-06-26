package com.xinyirun.scm.mongodb.serviceimpl.log.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.mongo.log.sys.SLogSysMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogSysMongoVo;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.service.log.sys.LogPcSystemMongoService;
import com.xinyirun.scm.mongodb.util.MongoPageUtil;
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
 * @author Wang Qianfeng
 * @Description 系统日志
 * @date 2023/3/1 14:23
 */
@Service
public class LogPcSystemMongoServiceImpl implements LogPcSystemMongoService {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存数据到 mongodb
     *
     * @param entity 实体类
     */
    @Override
    public void save(SLogSysMongoEntity entity) {
        mongoTemplate.save(entity);
    }

    /**
     * 根据查询信息分页查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SLogSysMongoVo> selectPage(SLogSysMongoVo searchCondition) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        paramBuilder(criteria, searchCondition);
        // 分页查询
        Query query = Query.query(criteria);
        query.fields().exclude("params", "session", "exception");
//        long count = mongoTemplate.count(query, SLogSysMongoEntity.class);
        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) searchCondition.getPageCondition().getCurrent() - 1,
                (int) searchCondition.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "c_time"));
        List<SLogSysMongoEntity> list = mongoTemplate.find(query.with(pageParam), SLogSysMongoEntity.class);
        List<SLogSysMongoVo> resultList = BeanUtilsSupport.copyProperties(list, SLogSysMongoVo.class);

        // 动态计算最大的limit
        searchCondition.getPageCondition().setLimit_count((int) (searchCondition.getPageCondition().getSize() * 10));

        // 根据动态计算的最大limit，计算count
        long count = mongoTemplate.count(query.skip((searchCondition.getPageCondition().getCurrent() - 1) * searchCondition.getPageCondition().getSize())
                .limit(searchCondition.getPageCondition().getLimit_count()), SLogSysMongoEntity.class);

        Page<SLogSysMongoVo> result = MongoPageUtil.covertPages(searchCondition.getPageCondition(), count, resultList);
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
    public SLogSysMongoVo getById(SLogSysMongoVo searchCondition) {
        Criteria cr = Criteria.where("id").is(searchCondition.getId());
        Query query = Query.query(cr);
        SLogSysMongoEntity entity = mongoTemplate.findOne(query, SLogSysMongoEntity.class);
        return (SLogSysMongoVo) BeanUtilsSupport.copyProperties(entity, SLogSysMongoVo.class);
    }

    /**
     * 拼接参数
     *
     * @param criteria
     * @param searchCondition
     */
    private void paramBuilder(Criteria criteria, SLogSysMongoVo searchCondition) {
        // 类型
        if (StringUtils.isNotEmpty(searchCondition.getType())) {
            criteria.and("type").regex(regexPattern(searchCondition.getType()));
        }
        // 创建时间起
        if (null != searchCondition.getStart_time() && null == searchCondition.getOver_time()) {
            criteria.andOperator(Criteria.where("c_time").gte(LocalDateTimeUtils.getDayStart(searchCondition.getStart_time())));
        }
        // 创建时间结束
        if (null != searchCondition.getOver_time() && null == searchCondition.getStart_time()) {
            criteria.andOperator(Criteria.where("c_time").lte(LocalDateTimeUtils.getDayEnd(searchCondition.getOver_time())));
        }
        // 创建 时间起止
        if (null != searchCondition.getOver_time() && null != searchCondition.getStart_time()) {
            criteria.andOperator(
                    Criteria.where("c_time").gte(LocalDateTimeUtils.getDayStart(searchCondition.getStart_time())),
                    Criteria.where("c_time").lte(LocalDateTimeUtils.getDayEnd(searchCondition.getOver_time()))
            );
        }
        // 类名
        if (StringUtils.isNotEmpty(searchCondition.getClass_name())) {
            criteria.and("class_name").regex(regexPattern(searchCondition.getClass_name()));
        }
        // 方法名
        if (StringUtils.isNotEmpty(searchCondition.getClass_method())) {
            criteria.and("class_method").regex(regexPattern(searchCondition.getClass_method()));
        }
        // url
        if (StringUtils.isNotEmpty(searchCondition.getUrl())) {
            criteria.and("url").regex(regexPattern(searchCondition.getUrl()));
        }
        // 参数
//        if (StringUtils.isNotEmpty(searchCondition.getParams())) {
//            criteria.and("params").regex(regexPattern(searchCondition.getParams()));
//        }
        //用户名
        if (StringUtils.isNotEmpty(searchCondition.getUser_name())) {
            criteria.and("user_name").regex(regexPattern(searchCondition.getUser_name()));
        }
        //员工姓名
        if (StringUtils.isNotEmpty(searchCondition.getStaff_name())) {
            criteria.and("staff_name").regex(regexPattern(searchCondition.getStaff_name()));
        }
        //操作说明
        if (StringUtils.isNotEmpty(searchCondition.getOperation())) {
            criteria.and("operation").regex(regexPattern(searchCondition.getOperation()));
        }
    }
}
