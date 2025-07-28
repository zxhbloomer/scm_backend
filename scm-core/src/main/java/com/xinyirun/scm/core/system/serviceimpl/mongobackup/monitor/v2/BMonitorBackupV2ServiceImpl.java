package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorBackupEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorBackupDataMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v2.IBMonitorBackupV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 备份后保存入库, 出库数量, 用于计算监管任务 已出库, 已入库数量 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-07-05
 */
@Service
public class BMonitorBackupV2ServiceImpl extends ServiceImpl<BMonitorBackupDataMapper, BMonitorBackupEntity> implements IBMonitorBackupV2Service {

    @Autowired
    private BMonitorBackupDataMapper mapper;

    /**
     * 新增或更新
     *
     * @param bMonitorDataMongoEntity
     */
    @Override
    public void saveAndFlush(BMonitorDataMongoEntity bMonitorDataMongoEntity) {
        BMonitorBackupEntity entity = new BMonitorBackupEntity();

        BMonitorBackupEntity bMonitorBackupEntity = mapper.selectOne(Wrappers.<BMonitorBackupEntity>lambdaQuery().eq(BMonitorBackupEntity::getCode, bMonitorDataMongoEntity.getCode()));
        if (null != bMonitorBackupEntity) {
            entity.setId(bMonitorBackupEntity.getId());
        }
        entity.setCode(bMonitorDataMongoEntity.getCode());
        entity.setSchedule_code(bMonitorDataMongoEntity.getSchedule_code());
        entity.setSchedule_id(bMonitorDataMongoEntity.getSchedule_id());
        entity.setOut_qty(bMonitorDataMongoEntity.getOut_qty());
        entity.setIn_qty(bMonitorDataMongoEntity.getIn_qty());
        saveOrUpdate(entity);
    }
}
