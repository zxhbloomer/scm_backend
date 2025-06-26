package com.xinyirun.scm.core.system.service.mongobackup.monitor.v1;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorOutEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v1.BMonitorOutDeliveryDataMongoVo;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:15
 */
public interface IBMonitorOutBackupService extends IService<BMonitorOutEntity> {

    /**
     * 查询监管入库详情
     * @param id 监管任务 id
     * @return
     */
    BMonitorOutDeliveryDataMongoVo selectOutDeliveryByMonitorId(Integer id);

    /**
     * 行级锁
     * @param vo
     */
    void selectForUpdate(BBkMonitorLogDetailVo vo);
}
