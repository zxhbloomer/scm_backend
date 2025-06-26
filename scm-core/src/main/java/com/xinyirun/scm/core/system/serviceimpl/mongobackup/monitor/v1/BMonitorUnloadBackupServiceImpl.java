package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorUnloadEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v1.BMonitorBackupUnloadMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v1.IBMonitorUnloadBackupService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorUnloadBackupServiceImpl extends ServiceImpl<BMonitorBackupUnloadMapper, BMonitorUnloadEntity> implements IBMonitorUnloadBackupService {

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
