package com.xinyirun.scm.core.system.service.business.bkmonitor.v2;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorSyncLogEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;

/**
 * <p>
 * monitor 备份 同步信息表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-04-06
 */

public interface IBBkMonitorSyncLogV2Service extends IService<BBkMonitorSyncLogEntity> {

    /**
     * 根据 monitor_id 新增 货 刷新
     * @param vo
     */
    void saveAndFlushByMonitorId(BBkMonitorLogDetailVo vo, String type, Long staffId);

    /**
     * 根据 monitor id 更新
     * @param monitorId
     * @param status
     * @param message
     */
    void updateByMontorId(Integer monitorId, String status, String message);

    /**
     * 根据 monitor id 更新
     * @param monitorId
     * @param status
     */
    void updateByMontorId(Integer monitorId, String status);

    /**
     * 日志查询
     * @param param
     * @return
     */
    IPage<BBkMonitorLogDetailVo> selectPage(BBkMonitorLogDetailVo param);
}
