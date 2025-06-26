package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorDeliveryEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v1.BMonitorBackupDeliveryMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v1.IBMonitorDeliveryBackupService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorDeliveryBackupServiceImpl extends ServiceImpl<BMonitorBackupDeliveryMapper, BMonitorDeliveryEntity> implements IBMonitorDeliveryBackupService {

    /**
     * @param vo
     */
    @Override
    public void selectForUpdate(BBkMonitorLogDetailVo vo) {
        baseMapper.selectForUpdate(vo);
    }
}
