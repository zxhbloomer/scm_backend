package com.xinyirun.scm.core.system.serviceimpl.business.bkmonitor.v2;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorLogEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.core.system.mapper.business.bkmonitor.v1.BBkMonitorLogMapper;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v2.IBBkMonitorLogV2Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * monitor备份日志 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-29
 */
@Service
public class BBkMonitorLogV2ServiceImpl extends ServiceImpl<BBkMonitorLogMapper, BBkMonitorLogEntity> implements IBBkMonitorLogV2Service {

    /**
     * 新增 监管任务备份日志
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveBackupData(BBkMonitorVo param) {
        BBkMonitorLogEntity entity = new BBkMonitorLogEntity();
        // 默认状态为 ing
        entity.setFlag("ing");
        entity.setType(param.getType());
        entity.setParam(JSONObject.toJSONString(param));
        entity.setCount(param.getCount());
        baseMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 更新监管任务日志
     *
     * @param param
     */
    @Override
    public void updateLog(BBkMonitorVo param) {
        BBkMonitorLogEntity entity = baseMapper.selectById(param.getLog_id());
        entity.setException(param.getException());
        entity.setComplete_time(LocalDateTime.now());
        entity.setFlag(param.getFlag());
        baseMapper.updateById(entity);
    }
}
