package com.xinyirun.scm.mongodb.serviceimpl.log.excelimport;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.excelimport.SLogImportMongoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogImportMongoVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogSysClickHouseVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.service.log.excelimport.LogImportMongoService;
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
 * @Description excel 日志 mongodb
 * @date 2023/3/1 14:23
 */
@Service
public class LogImportMongoServiceImpl implements LogImportMongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存数据到 mongodb
     *
     * @param entity 实体类
     */
    @Override
    public void save(SLogImportMongoEntity entity) {
        mongoTemplate.save(entity);
    }

    /**
     * 根据查询信息分页查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SLogImportMongoVo> selectPage(SLogImportMongoVo searchCondition) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        paramBuilder(criteria, searchCondition);
        // 分页查询
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, SLogSysClickHouseVo.class);
        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) searchCondition.getPageCondition().getCurrent() - 1,
                (int) searchCondition.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "c_time"));
        List<SLogImportMongoEntity> list = mongoTemplate.find(query.with(pageParam), SLogImportMongoEntity.class);
        List<SLogImportMongoVo> resultList = BeanUtilsSupport.copyProperties(list, SLogImportMongoVo.class);
        return MongoPageUtil.covertPages(searchCondition.getPageCondition(), count, resultList);
    }

    /**
     * 拼接参数
     *
     * @param criteria
     * @param searchCondition
     */
    private void paramBuilder(Criteria criteria, SLogImportMongoVo searchCondition) {
        // 类型
        if (StringUtils.isNotEmpty(searchCondition.getType())) {
            criteria.and("type").regex(regexPattern(searchCondition.getType()));
        }
    }
}
