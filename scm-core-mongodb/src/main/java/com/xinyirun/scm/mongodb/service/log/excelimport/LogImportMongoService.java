package com.xinyirun.scm.mongodb.service.log.excelimport;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.excelimport.SLogImportMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogImportMongoVo;

/**
 * @author Wang Qianfeng
 * @Description excel导入日志, mongo 交互
 * @date 2023/3/1 14:21
 */
public interface LogImportMongoService {

    /**
     * 保存数据到 mongodb
     * @param entity 实体类
     */
    void save(SLogImportMongoEntity entity);

    /**
     * 根据查询信息分页查询
     * @param searchCondition
     * @return
     */
    IPage<SLogImportMongoVo> selectPage(SLogImportMongoVo searchCondition);
}
