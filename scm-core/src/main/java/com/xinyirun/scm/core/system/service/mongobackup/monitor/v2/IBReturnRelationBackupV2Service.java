package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.returnrelation.BReturnRelationEntity;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BReturnRelationRestoreV2Entity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;

/**
 * <p>
 * 备份后保存入库, 出库数量, 用于计算监管任务 已出库, 已入库数量 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-07-05
 */
public interface IBReturnRelationBackupV2Service extends IService<BReturnRelationEntity> {

    /**
     * 行级锁
     * @param vo
     */
    public void selectForUpdate(BBkMonitorLogDetailVo vo);

    public BReturnRelationRestoreV2Entity selectByMonitorId(Integer monitorId);

    public void deleteByMonitorId(Integer monitorId);

}
