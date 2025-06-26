package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorDeliveryEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2.BMonitorBackupDeliveryV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorDeliveryBackupV2Service;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorDeliveryBackupV2ServiceImpl extends ServiceImpl<BMonitorBackupDeliveryV2Mapper, BMonitorDeliveryEntity> implements IBMonitorDeliveryBackupV2Service {

    /**
     * @param vo
     */
    @Override
    public void selectForUpdate(BBkMonitorLogDetailVo vo) {
        baseMapper.selectForUpdate(vo);
    }
}
