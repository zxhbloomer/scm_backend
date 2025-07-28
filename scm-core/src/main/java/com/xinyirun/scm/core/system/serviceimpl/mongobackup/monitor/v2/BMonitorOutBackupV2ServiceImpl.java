package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorOutEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v2.BMonitorOutDeliveryDataMongoV2Vo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2.BMonitorBackupOutV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorOutBackupV2Service;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorOutBackupV2ServiceImpl extends ServiceImpl<BMonitorBackupOutV2Mapper, BMonitorOutEntity> implements IBMonitorOutBackupV2Service {

    /**
     * 查询监管入库详情
     *
     * @param id 监管任务 id
     * @return
     */
    @Override
    public BMonitorOutDeliveryDataMongoV2Vo selectOutDeliveryByMonitorId(Integer id) {
        return baseMapper.selectOutDeliveryByMonitorId(id);
    }

    /**
     * 行级锁
     *
     * @param vo
     */
    @Override
    public void selectForUpdate(BBkMonitorLogDetailVo vo) {
        baseMapper.selectForUpdate(vo);
    }
}
