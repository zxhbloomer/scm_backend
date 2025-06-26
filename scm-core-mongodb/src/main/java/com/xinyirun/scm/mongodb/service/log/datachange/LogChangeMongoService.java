package com.xinyirun.scm.mongodb.service.log.datachange;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeMainVo;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeMongoVo;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeOperateMongoVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;

import java.lang.reflect.InvocationTargetException;

/**
 * 数据变动记录
 */
public interface LogChangeMongoService {

    /**
     * 保存数据到 mongodb
     */
    void save(SDataChangeLogVo bean) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException;

    /**
     * 根据业务单号搜索数据
     * @param order_code
     * @return
     */
    SLogDataChangeMainVo findMainByOrderCode(String order_code) ;

    SLogDataChangeOperateMongoVo findOperationByRequestId(String request_id) ;

    /**
     * 根据编号搜索数据
     * @param id
     * @return
     */
    SLogDataChangeMongoEntity findById(String id) ;

    /**
     * 添加按request_id删除数据的方法
     * @param requestId
     * @return
     */
    void deleteByRequestId(String requestId) ;
}
