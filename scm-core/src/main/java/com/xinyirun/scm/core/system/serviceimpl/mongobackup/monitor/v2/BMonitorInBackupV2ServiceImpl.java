package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorInEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2.BMonitorInUnloadDataMongoV2Vo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2.BMonitorBackupInV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorInBackupV2Service;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorInBackupV2ServiceImpl extends ServiceImpl<BMonitorBackupInV2Mapper, BMonitorInEntity> implements IBMonitorInBackupV2Service {

    /**
     * 查询监管入库详情
     *
     * @param id 监管任务 id
     * @return
     */
    @Override
    public BMonitorInUnloadDataMongoV2Vo selectMonitorInUnloadByMonitorId(Integer id) {
        return baseMapper.selectMonitorInUnloadByMonitorId(id);
    }

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
