package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorOutEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2.BMonitorOutDeliveryDataMongoV2Vo;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:15
 */
public interface IBMonitorOutBackupV2Service extends IService<BMonitorOutEntity> {

    /**
     * 查询监管入库详情
     * @param id 监管任务 id
     * @return
     */
    BMonitorOutDeliveryDataMongoV2Vo selectOutDeliveryByMonitorId(Integer id);

    /**
     * 行级锁
     * @param vo
     */
    void selectForUpdate(BBkMonitorLogDetailVo vo);
}
