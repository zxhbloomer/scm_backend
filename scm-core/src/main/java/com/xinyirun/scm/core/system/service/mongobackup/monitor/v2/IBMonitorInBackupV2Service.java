package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorInEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2.BMonitorInUnloadDataMongoV2Vo;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:15
 */
public interface IBMonitorInBackupV2Service extends IService<BMonitorInEntity> {

    /**
     * 查询监管入库详情
     * @param id 监管任务 id
     * @return
     */
    BMonitorInUnloadDataMongoV2Vo selectMonitorInUnloadByMonitorId(Integer id);

    /**
     * 死锁, 行级锁
     * @param vo
     */
    void selectForUpdate(BBkMonitorLogDetailVo vo);
}
