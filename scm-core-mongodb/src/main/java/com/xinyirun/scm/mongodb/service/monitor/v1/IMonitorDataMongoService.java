package com.xinyirun.scm.mongodb.service.monitor.v1;

import com.xinyirun.scm.bean.entity.mongo.monitor.v1.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v1.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMonitorDataMongoService  {

    /**
     * 保存(单条)
     *
     * @param userSaveParam
     * @return
     */
    BMonitorDataMongoEntity saveAndFlush(BMonitorDataMongoEntity userSaveParam);

    /**
     * 保存(单条)
     *
     * @param userSaveParam
     * @return
     */
    List<BMonitorDataMongoEntity> saveAll(List<BMonitorDataMongoVo> userSaveParam);

    /**
     * 分页查询
     * @param searchCondition
     */
    Page<BMonitorBackupVo> selectPageList(BMonitorBackupVo searchCondition);

    /**
     * 数据求和
     * @param searchCondition
     * @return
     */
    BMonitorBackupSumVo selectSumData(BMonitorBackupVo searchCondition);

    /**
     * 返回详情
     * @param searchCondition
     * @return
     */
    BMonitorBackupDetailVo getDetail(BMonitorBackupVo searchCondition);

    /**
     * 附件导出
     * @param searchCondition
     * @return
     */
    List<BMonitorFileDownloadMongoVo> exportFile(List<BMonitorBackupVo> searchCondition);

    /**
     * 数据导出
     * @param searchCondition
     * @return
     */
    List<BMonitorMongoExportVo> selectExportList(List<BMonitorBackupVo> searchCondition);

    /**
     * 数据导出,全部
     * @param searchCondition
     * @return
     */
    List<BMonitorMongoExportVo> selectExportAllList(BMonitorBackupVo searchCondition);

    /**
     * 根据 监管任务id 查询详情
     * @param monitor_id
     * @return
     */
    BMonitorDataMongoEntity getEntityByMonitorId(Integer monitor_id);

    /**
     * 根据 id 查询
     * @param ids
     * @return
     */
    List<BBkMonitorLogDetailVo> selectLogDetailListByIds(List<String> ids);

    /**
     * 更新是否恢复状态
     * @param monitorId 监管任务 ID
     */
    void updateRestoreStatus(Integer monitorId);

    /**
     * 根据 id更新是否可见状态
     * @param ids
     * @param isShow
     */
    void updateVisibilityStatusByIds(List<String> ids, String isShow);

    /**
     * 根据 monitor_id 更新是否可见状态
     * @param monitorId
     * @param isShow
     */
    void updateVisibilityStatusByMonitorId(Integer monitorId, String isShow);

    /**
     * 查询 monitor Id
     * @param scheduleId
     * @return
     */
    List<BMonitorDataMongoEntity> selectByScheduleId(Integer scheduleId);

    /**
     * 查询文件
     * @param param
     * @return
     */
    BBkMonitorVo getFiles(BBkMonitorVo param);
}
