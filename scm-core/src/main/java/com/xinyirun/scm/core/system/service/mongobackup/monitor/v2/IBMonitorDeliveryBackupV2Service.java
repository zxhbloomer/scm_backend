package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorDeliveryEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:15
 */
public interface IBMonitorDeliveryBackupV2Service extends IService<BMonitorDeliveryEntity> {


    void selectForUpdate(BBkMonitorLogDetailVo vo);
}
