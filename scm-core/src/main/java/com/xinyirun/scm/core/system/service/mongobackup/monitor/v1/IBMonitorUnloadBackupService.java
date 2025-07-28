package com.xinyirun.scm.core.system.service.mongobackup.monitor.v1;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorUnloadEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:15
 */
public interface IBMonitorUnloadBackupService extends IService<BMonitorUnloadEntity> {

    /**
     * 死锁, 行级锁
     * @param vo
     */
    void selectForUpdate(BBkMonitorLogDetailVo vo);
}
