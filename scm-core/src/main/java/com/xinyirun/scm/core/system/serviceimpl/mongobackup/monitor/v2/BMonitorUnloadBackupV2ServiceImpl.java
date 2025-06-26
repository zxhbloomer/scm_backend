package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorUnloadEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2.BMonitorBackupUnloadV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorUnloadBackupV2Service;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorUnloadBackupV2ServiceImpl extends ServiceImpl<BMonitorBackupUnloadV2Mapper, BMonitorUnloadEntity> implements IBMonitorUnloadBackupV2Service {

    /**
     * 死锁, 行级锁
     *
     * @param vo
     */
    @Override
    public void selectForUpdate(BBkMonitorLogDetailVo vo) {
        baseMapper.selectForUpdate(vo);
    }
}
