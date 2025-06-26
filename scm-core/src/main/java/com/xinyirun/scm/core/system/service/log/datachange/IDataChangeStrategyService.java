package com.xinyirun.scm.core.system.service.log.datachange;

import com.xinyirun.scm.bean.system.vo.mongo.log.SLogDataChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogDetailVo;
import org.apache.ibatis.mapping.BoundSql;

import java.time.LocalDateTime;

/**
 * 策略模式：数据更新记录
 */
public interface IDataChangeStrategyService {
    /**
     * 获取数据插入的对象，还没有进行比对
     * @return
     */
    SDataChangeLogDetailVo getDataChangeVoByInsert(BoundSql boundSql);

    /**
     * 获取数据更新的对象，还没有进行比对
     * @return
     */
    SDataChangeLogDetailVo getDataChangeVoByUpdateBefore(BoundSql boundSql);

    /**
     * 获取数据更新的对象，还没有进行比对
     * @return
     */
    SDataChangeLogDetailVo getDataChangeVoByUpdateAfter(BoundSql boundSql);

    /**
     * 获取数据删除的对象，还没有进行比对
     * @return
     */
    SDataChangeLogDetailVo getDataChangeVoByDelete(BoundSql boundSql);


    /**
     * 根据id获取单号order_code
     * @return
     */
    String getOrderCode(Integer id);

    /**
     * 根据id获取到c_time
     * 注意：因为通过了mybatisplus的拦截器，所以事务在这里除了问题，所以查询数据需要有时间的代价，也就是定时任务来发起
     * DataChangeLabelAnnotation( extension = "getCTimeExtension")的扩展
     * @return
     */
    SLogDataChangeDetailVo getCTimeExtension(String param, String _json_data, String clm_name, String clm_label);

    /**
     * 根据id获取到u_time
     * 注意：因为通过了mybatisplus的拦截器，所以事务在这里除了问题，所以查询数据需要有时间的代价，也就是定时任务来发起
     * DataChangeLabelAnnotation( extension = "getUTimeExtension")的扩展
     * @return
     */
    SLogDataChangeDetailVo getUTimeExtension(String param, String _json_data, String clm_name, String clm_label);
}