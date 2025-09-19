package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorOutEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v1.BMonitorOutDeliveryDataMongoVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v1.BMonitorBackupOutMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v1.IBMonitorOutBackupService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorOutBackupServiceImpl extends ServiceImpl<BMonitorBackupOutMapper, BMonitorOutEntity> implements IBMonitorOutBackupService {

    /**
     * 查询监管入库详情
     *
     * @param id 监管任务 id
     * @return
     */
    @Override
    public BMonitorOutDeliveryDataMongoVo selectOutDeliveryByMonitorId(Integer id) {
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
