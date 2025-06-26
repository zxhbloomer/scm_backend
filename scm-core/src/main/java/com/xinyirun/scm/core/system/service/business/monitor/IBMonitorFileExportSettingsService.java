package com.xinyirun.scm.core.system.service.business.monitor;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorFileExportSettingsEntity;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000068Vo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000158Vo;

/**
 * <p>
 * 监管任务文件导出 配置 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-08-18
 */
public interface IBMonitorFileExportSettingsService extends IService<BMonitorFileExportSettingsEntity> {

    /**
     * 根据 staff ID 查询
     * @return
     */
    P00000068Vo getByStaffId();

    /**
     * 保存 / 更新
     * @param jsonObject
     */
    void saveAndFlush(JSONObject jsonObject);

    P00000158Vo getByDirectStaffId();

    void saveDirect(JSONObject jsonObject);
}
