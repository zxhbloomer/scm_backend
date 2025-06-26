package com.xinyirun.scm.core.system.config.event.define;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import org.springframework.context.ApplicationEvent;

/**
 * 数据变更事件
 */
public class DataChangeEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 8929893689628625794L;

    // 其他的变化
    private SDataChangeLogVo dataChangeVo;
    // s_code的变化
    private SLogDataChangeMainMongoEntity dataChangeMain;

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
    public DataChangeEvent(Object source, SLogDataChangeMainMongoEntity dataChangeMain) {
        super(source);
        this.dataChangeMain = dataChangeMain;
    }

    public SDataChangeLogVo getDataChangeVo() {
        return dataChangeVo;
    }

    public SLogDataChangeMainMongoEntity getDataChangeMain() {
        return dataChangeMain;
    }
}