package com.xinyirun.scm.framework.config.event.define;

import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainClickHouseVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 数据变更事件
 */
public class DataChangeEvent extends ApplicationEvent {


    @Serial
    private static final long serialVersionUID = -7723530647667522143L;

    // 其他的变化
    private SDataChangeLogVo dataChangeVo;
    // s_code的变化
    private SLogDataChangeMainClickHouseVo dataChangeMain;

    /**
     * 其他的变化
     * @param source
     * @param dataChangeVo
     */
    public DataChangeEvent(Object source, SDataChangeLogVo dataChangeVo) {
        super(source);
        this.dataChangeVo = dataChangeVo;
    }

    /**
     * s_code的变化
     * @param source
     * @param dataChangeMain
     */
    public DataChangeEvent(Object source, SLogDataChangeMainClickHouseVo dataChangeMain) {
        super(source);
        this.dataChangeMain = dataChangeMain;
    }

    public SDataChangeLogVo getDataChangeVo() {
        return dataChangeVo;
    }

    public SLogDataChangeMainClickHouseVo getDataChangeMain() {
        return dataChangeMain;
    }
}