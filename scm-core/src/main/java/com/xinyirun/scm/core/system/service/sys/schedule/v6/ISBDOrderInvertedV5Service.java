package com.xinyirun.scm.core.system.service.sys.schedule.v6;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderInvertedEntity;

/**
 * <p>
 *  每日监管任务损耗预警service
 * </p>
 */
public interface ISBDOrderInvertedV5Service extends IService<BOrderInvertedEntity> {

     /**
      * 定时备份快照
      */
     void queryOrderInverted(String parameterClass , String parameter);
}
