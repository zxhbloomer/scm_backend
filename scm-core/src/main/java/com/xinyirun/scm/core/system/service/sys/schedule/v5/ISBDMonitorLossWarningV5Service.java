package com.xinyirun.scm.core.system.service.sys.schedule.v5;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;

/**
 * <p>
 *  每日监管任务损耗预警service
 * </p>
 */
public interface ISBDMonitorLossWarningV5Service extends IService<BMonitorEntity> {

     /**
      * 损耗预警
      */
     void monitorLossWarning(String parameterClass , String parameter);
}
