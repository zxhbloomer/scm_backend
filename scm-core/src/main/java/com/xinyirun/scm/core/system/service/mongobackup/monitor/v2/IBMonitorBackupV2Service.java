package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorBackupEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;

/**
 * <p>
 * 备份后保存入库, 出库数量, 用于计算监管任务 已出库, 已入库数量 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-07-05
 */
public interface IBMonitorBackupV2Service extends IService<BMonitorBackupEntity> {

    /**
     * 新增或更新
     * @param bMonitorDataMongoEntity
     */
    void saveAndFlush(BMonitorDataMongoEntity bMonitorDataMongoEntity);

}
