package com.xinyirun.scm.mongodb.service.log.datachange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainVo;

/**
 * 数据变动记录
 */
public interface LogChangeMainMongoService {

    /**
     * 查询数据变动记录
     * @param searchCondition
     * @return
     */
    public IPage<SLogDataChangeMainVo> selectPage(SLogDataChangeMainVo searchCondition);

    /**
     * 保存数据到 mongodb
     */
    void save(SLogDataChangeMainMongoEntity bean);

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    SLogDataChangeMainMongoEntity findById(String id) ;

    /**
     * 根据orderCode查询数据
     * @param orderCode
     * @return
     */
    SLogDataChangeMainMongoEntity findByOrderCode(String orderCode);

    /**
     * 添加按request_id删除数据的方法
     * @param requestId
     * @return
     */
    void deleteByRequestId(String requestId) ;
}
