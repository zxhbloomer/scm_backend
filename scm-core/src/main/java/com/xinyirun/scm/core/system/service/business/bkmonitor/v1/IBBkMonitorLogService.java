package com.xinyirun.scm.core.system.service.business.bkmonitor.v1;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorLogEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorVo;

/**
 * <p>
 * monitor备份日志 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-29
 */
public interface IBBkMonitorLogService extends IService<BBkMonitorLogEntity> {
    /**
     * 新增 监管任务备份日志
     * @param param
     * @return
     */
    Integer saveBackupData(BBkMonitorVo param);

    /**
     * 更新监管任务日志
     * @param param
     */
    void updateLog(BBkMonitorVo param);
}
