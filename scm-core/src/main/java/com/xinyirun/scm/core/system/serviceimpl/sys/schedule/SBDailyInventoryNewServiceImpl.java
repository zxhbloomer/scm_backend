package com.xinyirun.scm.core.system.serviceimpl.sys.schedule;

import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v1.SBDailyInventoryMappper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.ISBDailyInventoryNewService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyInventoryV2Service;
import com.xinyirun.scm.core.system.service.sys.schedule.v3.ISBDailyInventoryV3Service;
import com.xinyirun.scm.core.system.service.sys.schedule.v4.ISBDailyInventoryV4Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  每日库存变化表的service 定时任务专用
 * </p>
 * 废弃
 * @author zxh
 * @since 2019-07-04
 */
@Service
@Slf4j
public class SBDailyInventoryNewServiceImpl extends BaseServiceImpl<SBDailyInventoryMappper, BDailyInventoryEntity> implements ISBDailyInventoryNewService {

    @Autowired
    private ISBDailyInventoryV2Service ibDailyInventoryV2Service;

    @Autowired
    private ISBDailyInventoryV3Service ibDailyInventoryV3Service;

    @Autowired
    private ISBDailyInventoryV4Service ibDailyInventoryV4Service;

    @Autowired
    private ISConfigService isConfigService;

    /**
     * 重新生成每日库存表，所有仓库
     *
     */
    @Override
    public void reCreateDailyInventoryAll() {
//        SConfigEntity config = isConfigService.selectByKey(SystemConstants.DAILY_INVENTORY_GENERATION_METHOD);
//        if ("0".equals(config.getValue())) {
//            ibDailyInventoryV2Service.reCreateDailyInventoryAll();
//        } else if ("1".equals(config.getValue())) {
//            ibDailyInventoryV3Service.reCreateDailyInventoryAll();
//        }
        ibDailyInventoryV4Service.reCreateDailyInventoryAll();
    }


}
