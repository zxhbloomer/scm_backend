package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitor.v1;

import com.xinyirun.scm.bean.entity.mongo.monitor.v1.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1.*;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v1.BMonitorDataDetailMongoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorLogDetailService;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorSyncLogService;
import com.xinyirun.scm.core.system.service.mongobackup.monitor.v1.*;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v1.*;
import com.xinyirun.scm.mongodb.service.monitor.v1.IMonitorDataMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BMonitorConsumerServiceImpl implements IBMonitorConsumerService {

    @Autowired
    private IBMonitorBackupBusinessService monitorBackupBusinessService;

    @Autowired
    private IBMonitorInBackupService monitorInBackupService;

    @Autowired
    private IBMonitorOutBackupService monitorOutBackupService;

    @Autowired
    private IBMonitorDeliveryBackupService monitorDeliveryBackupService;

    @Autowired
    private IBMonitorUnloadBackupService monitorUnloadBackupService;

    @Autowired
    private IBBkMonitorLogDetailService logDetailService;

    @Autowired
    private IBMonitorRestoreService monitorRestoreService;

    @Autowired
    private IBMonitorInRestoreService monitorInRestoreService;

    @Autowired
    private IBMonitorOutRestoreService monitorOutRestoreService;

    @Autowired
    private IBMonitorDeliveryRestoreService monitorDeliveryRestoreService;

    @Autowired
    private IBMonitorUnloadRestoreService monitorUnloadRestoreService;

    @Autowired
    private IMonitorDataMongoService monitorDataMongoService;

    @Autowired
    private IBBkMonitorSyncLogService syncLogService;

    @Autowired
    private IBMonitorBackupService backupService;


    /**
     * 执行
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exec(BBkMonitorLogDetailVo vo) {
        // 添加事务锁
        monitorBackupBusinessService.selectForUpdate(vo);
        monitorInBackupService.selectForUpdate(vo);
        monitorUnloadBackupService.selectForUpdate(vo);
        monitorOutBackupService.selectForUpdate(vo);
        monitorDeliveryBackupService.selectForUpdate(vo);

        try {
            save2Mongo(vo);
        } catch(Exception e) {
            // 如果此处失败, 不再执行删除操作, 扔处异常
            logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
            log.error("数据保存mongodb失败!! {}", e.toString());
            syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
            throw new BusinessException(e.getMessage());
        }

        try {
            delete2Mysql(vo);
        } catch (Exception e) {
            log.error("删除数据失败!! {}", e.toString());
            logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
            syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_4, e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    private void save2Mongo(BBkMonitorLogDetailVo vo) {

        // 保存前 需要把所有的图片同步到 oss库中
        saveFile2OSS(vo);

        // 保存到 mongodb 中的 monitor 表
        BMonitorDataMongoEntity bMonitorDataMongoEntity = builderMonitorEntity(vo);

        if (null == bMonitorDataMongoEntity) {
            log.debug("当前监管任务已经备份过了");
            return;
        }

        backupService.saveAndFlush(bMonitorDataMongoEntity);

        // 保存到mongo
        monitorDataMongoService.saveAndFlush(bMonitorDataMongoEntity);

        // 更新 日志详情 状态
        logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_2);
        syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_2);
    }

    /**
     * 需保存所有图片到 oss
     * @param vo
     */
    private void saveFile2OSS(BBkMonitorLogDetailVo vo) {
        Integer monitorId = vo.getMonitor_id();
        monitorBackupBusinessService.getMonitorFiles(monitorId);

    }

    @Transactional(rollbackFor = Exception.class)
    public void delete2Mysql(BBkMonitorLogDetailVo vo) {


        // 先判断是否备份成功
        BMonitorDataMongoEntity entity = monitorDataMongoService.getEntityByMonitorId(vo.getMonitor_id());

        if (null == entity || DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_T.equals(entity.getIs_restore())) {
//            log.error("结果 --> {}", DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_T.equals(entity.getIs_restore()));
            throw new BusinessException("当前监管任务, 未备份到 mongo 或 已恢复到 mysql");
        }

        // b_monitor 表
        if (null != entity.getMonitor_json()) {
            monitorBackupBusinessService.removeById(vo.getMonitor_id());
        } else {
            throw new BusinessException("当前监管任务, 未备份到 mongo");
        }

        // b_monitor_in 表
        if (null != vo.getMonitor_in_id()) {
            if (null != entity.getMonitor_in_json()) {
                monitorInBackupService.removeById(vo.getMonitor_in_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_in 失败");
            }
        }

        // b_monitor_out 表
        if (null != vo.getMonitor_out_id()) {
            if (null != entity.getMonitor_out_json()) {
                monitorOutBackupService.removeById(vo.getMonitor_out_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_out 失败");
            }
        }

        // b_monitor_delivery 表
        if (null != vo.getMonitor_delivery_id()) {
            if (null != entity.getMonitor_delivery_json()) {
                monitorDeliveryBackupService.removeById(vo.getMonitor_delivery_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_delivery 失败");
            }
        }

        // b_monitor_unload 表
        if (null != vo.getMonitor_unload_id()) {
            if (null != entity.getMonitor_unload_json()) {
                monitorUnloadBackupService.removeById(vo.getMonitor_unload_id());
            } else {
                throw new BusinessException("当前监管任务, 删除 b_monitor_unload 失败");
            }
        }

        syncLogService.updateByMontorId(vo.getMonitor_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3);
        logDetailService.updateStatus(vo.getLog_detail_id(), DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_3);
    }

    private BMonitorDataMongoEntity builderMonitorEntity(BBkMonitorLogDetailVo vo) {

        // 查询 分页 页面 的数据
        BMonitorDataMongoEntity mongoEntity = monitorBackupBusinessService.selectPageById(vo.getMonitor_id());
        if (mongoEntity == null) {
//            throw new RuntimeException("备份失败, 查询不到数据");
            return null;
        }
        // 查询详情
        BMonitorDataDetailMongoVo detail = monitorBackupBusinessService.getDetail(vo.getMonitor_id());
        mongoEntity.setDetailVo(detail);

        // 查询 关联表数据, 以 json 格式存储数据库
        BMonitorRestoreEntity entity = monitorRestoreService.getById(vo.getMonitor_id());
        if (entity == null) {
            throw new RuntimeException("备份失败, 查询不到数据");
        }
        mongoEntity.setMonitor_json(entity);

        // b_monitor_in 表
        if (null != vo.getMonitor_in_id()) {
            BMonitorInRestoreEntity inEntity = monitorInRestoreService.getById(vo.getMonitor_in_id());
            mongoEntity.setMonitor_in_json(inEntity);
        }

        // b_monitor_out 表
        if (null != vo.getMonitor_out_id()) {
            BMonitorOutRestoreEntity outEntity = monitorOutRestoreService.getById(vo.getMonitor_out_id());
            mongoEntity.setMonitor_out_json(outEntity);
        }

        // b_monitor_delivery 表
        if (null != vo.getMonitor_delivery_id()) {
            BMonitorDeliveryRestoreEntity byId = monitorDeliveryRestoreService.getById(vo.getMonitor_delivery_id());
            mongoEntity.setMonitor_delivery_json(byId);
        }

        // b_monitor_unload 表
        if (null != vo.getMonitor_unload_id()) {
            BMonitorUnloadRestoreEntity byId = monitorUnloadRestoreService.getById(vo.getMonitor_unload_id());
            mongoEntity.setMonitor_unload_json(byId);
        }
        return mongoEntity;
    }
}
