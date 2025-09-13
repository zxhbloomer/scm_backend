package com.xinyirun.scm.framework.config.event.listener;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.framework.config.event.define.DataChangeEvent;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.datachange.LogDataChangeProducer;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 数据变更事件监听器
 *
 */
@Component
public class DataChangeEventListener {

    @Autowired
    LogDataChangeProducer producer;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onDataChanged(DataChangeEvent event) {
        // 获取数据变更的main bean
        SLogDataChangeMainMongoEntity dataChangeMain = event.getDataChangeMain();
        // 获取数据变更的bean
        SDataChangeLogVo dataChangeVo = event.getDataChangeVo();

        // s_code的变化，提交至mq
        if (!Objects.isNull(dataChangeMain)) {
            // 设置租户code
            dataChangeMain.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
            producer.mqSendMq(dataChangeMain);
        }

        // 其他的变化，提交至mq
        if (!Objects.isNull(dataChangeVo)) {
            dataChangeVo.setTenant_code(DataSourceHelper.getCurrentDataSourceName());
            producer.mqSendMq(dataChangeVo);
        }
    }
}
