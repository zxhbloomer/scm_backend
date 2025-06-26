package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v2.BMonitorDataDetailMongoV2Vo;

import java.util.List;

/**
 * 监管任务service
 */
public interface IBMonitorBackupBusinessV2Service extends IService<BMonitorEntity> {

    /**
     * 同步数据
     * @param size 条数
     * @return
     */
//    void backupDataLimitSize(Integer size);

    /**
     * 记录同步日志
     * @param param 同步数据条件
     * @result Integer 日志id
     */
    Integer insertBackupLog(BBkMonitorVo param);

//    void insertLogDetail(BBkMonitorVo param);

    /**
     * 查询 数据传入 mq
     * @param param
     * @return
     */
    List<BBkMonitorLogDetailVo> selectData2Mq(BBkMonitorVo param);

    /**
     * 查询备份条数
     * @param param
     * @return
     */
    BBkMonitorVo selectPageMyCount(BBkMonitorVo param);

    /**
     * 查询 分页显示数据, 根据id
     * @param id
     * @return
     */
    BMonitorDataMongoEntity selectPageById(Integer id);

    /**
     * 查询详情 by 监管任务id
     * @param monitorId
     * @return
     */
    BMonitorDataDetailMongoV2Vo getDetail(Integer monitorId);

    void selectForUpdate(BBkMonitorLogDetailVo vo);

    /**
     * 查询 文件
     * @param monitorId 监管任务 id
     */
    void getMonitorFiles(Integer monitorId);
}
