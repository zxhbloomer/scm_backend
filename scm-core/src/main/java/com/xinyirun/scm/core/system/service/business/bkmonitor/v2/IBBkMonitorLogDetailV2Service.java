package com.xinyirun.scm.core.system.service.business.bkmonitor.v2;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorLogDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;

import java.util.List;

/**
 * monitor 备份状态表 服务类
 *
 * @author xinyirun
 * @since 2023-03-29
 */
public interface IBBkMonitorLogDetailV2Service extends IService<BBkMonitorLogDetailEntity> {

    /**
     * 分页新增
     * @param curSize 当前条数
     * @param pageSize 分页大小
     * @param param 查询参数
     * @param logId 日志id
     * @return 新增总条数
     */
    int selectForInsert(int curSize, int pageSize, BBkMonitorVo param, Integer logId);

    /**
     * 根据 状态查询
     * @param param
     * @return
     */
    List<BBkMonitorLogDetailEntity> selectListByStatus(int page, int pageSize, BBkMonitorLogDetailEntity param);

    /**
     * 更新 状态
     * @param id 主键id
     * @param status 状态
     */
    void updateStatus(Integer id, String status, String exception);

    /**
     * 更新 状态
     * @param id 主键id
     * @param status 状态
     */
    void updateStatus(Integer id, String status);

    /**
     * 新增日志
     * @param vo 日志
     */
    Integer insertLog(BBkMonitorLogDetailVo vo);

}
