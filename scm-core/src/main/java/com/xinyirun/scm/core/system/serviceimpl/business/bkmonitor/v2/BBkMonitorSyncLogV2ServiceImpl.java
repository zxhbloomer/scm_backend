package com.xinyirun.scm.core.system.serviceimpl.business.bkmonitor.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorSyncLogEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.mapper.business.bkmonitor.v2.BBkMonitorSyncLogV2Mapper;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v2.IBBkMonitorSyncLogV2Service;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * monitor 备份 同步信息表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-04-06
 */
@Service
public class BBkMonitorSyncLogV2ServiceImpl extends ServiceImpl<BBkMonitorSyncLogV2Mapper, BBkMonitorSyncLogEntity> implements IBBkMonitorSyncLogV2Service {

    /**
     * 根据 monitor_id 新增 货 刷新
     *
     * @param vo
     */
    @Override
    public void saveAndFlushByMonitorId(BBkMonitorLogDetailVo vo, String type, Long staffId) {
        BBkMonitorSyncLogEntity logEntity = selectByMonitorId(vo.getMonitor_id());
        BBkMonitorSyncLogEntity entity = new BBkMonitorSyncLogEntity();
        if (null != logEntity) {
            // 更新
            entity.setId(logEntity.getId());
            // 继承查询到的数据
            entity.setLast_restore_id(logEntity.getLast_restore_id());
            entity.setLast_backup_id(logEntity.getLast_backup_id());
            entity.setLast_backup_time(logEntity.getLast_backup_time());
            entity.setLast_restore_time(logEntity.getLast_restore_time());
        }
        // 根据type 更新 继承的数据
        if (type.equals(DictConstant.DICT_B_MONITOR_BACKUP_TYPE_1)) {
            entity.setLast_backup_id(staffId);
            entity.setLast_backup_time(LocalDateTime.now());
        } else if (type.equals(DictConstant.DICT_B_MONITOR_BACKUP_TYPE_2)) {
            entity.setLast_restore_time(LocalDateTime.now());
            entity.setLast_restore_id(staffId);
        }
        entity.setU_time(LocalDateTime.now());
        entity.setMonitor_id(vo.getMonitor_id());
        entity.setMonitor_code(vo.getMonitor_code());
        entity.setType(type);
        // 待备份
        entity.setStatus(DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_1);
        entity.setFlag("ING");
        entity.setVersion("2");
        this.saveOrUpdate(entity);

    }

    /**
     * 根据 monitor id 失败
     *
     * @param monitorId
     * @param status
     * @param message
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateByMontorId(Integer monitorId, String status, String message) {
        LambdaUpdateWrapper<BBkMonitorSyncLogEntity> wrapper = new LambdaUpdateWrapper<BBkMonitorSyncLogEntity>()
                .eq(BBkMonitorSyncLogEntity::getMonitor_id, monitorId)
                .set(BBkMonitorSyncLogEntity::getStatus, status)
                .set(BBkMonitorSyncLogEntity::getFlag, "NG")
                .set(BBkMonitorSyncLogEntity::getU_time, LocalDateTime.now())
                .set(BBkMonitorSyncLogEntity::getException, message);
        baseMapper.update(null, wrapper);
    }

    /**
     * 根据 monitor id 成功
     *
     * @param monitorId
     * @param status
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateByMontorId(Integer monitorId, String status) {
        LambdaUpdateWrapper<BBkMonitorSyncLogEntity> wrapper = new LambdaUpdateWrapper<BBkMonitorSyncLogEntity>()
                .eq(BBkMonitorSyncLogEntity::getMonitor_id, monitorId)
                .set(BBkMonitorSyncLogEntity::getStatus, status)
                .set(BBkMonitorSyncLogEntity::getStatus, status)
                .set(BBkMonitorSyncLogEntity::getU_time, LocalDateTime.now())
                .set(BBkMonitorSyncLogEntity::getFlag, "OK");
        baseMapper.update(null, wrapper);
    }

    /**
     * 日志查询
     *
     * @param param
     * @return
     */
    @Override
    public IPage<BBkMonitorLogDetailVo> selectPage(BBkMonitorLogDetailVo param) {
        // 分页条件
        Page<BBkMonitorLogDetailVo> page = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(page, param.getPageCondition().getSort());
        return baseMapper.selectPageList(param, page);
    }

    private BBkMonitorSyncLogEntity selectByMonitorId(Integer monitorId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<BBkMonitorSyncLogEntity>().eq(BBkMonitorSyncLogEntity::getMonitor_id, monitorId));
    }
}
