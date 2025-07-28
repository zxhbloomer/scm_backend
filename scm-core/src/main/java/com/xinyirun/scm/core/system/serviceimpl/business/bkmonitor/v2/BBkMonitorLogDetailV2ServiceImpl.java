package com.xinyirun.scm.core.system.serviceimpl.business.bkmonitor.v2;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorLogDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.bkmonitor.v2.BBkMonitorLogDetailV2Mapper;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v2.IBBkMonitorLogDetailV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * monitor 备份状态表 服务实现类
 *
 * @author xinyirun
 * @since 2023-03-29
 */
@Service
@Slf4j
public class BBkMonitorLogDetailV2ServiceImpl extends ServiceImpl<BBkMonitorLogDetailV2Mapper, BBkMonitorLogDetailEntity> implements IBBkMonitorLogDetailV2Service {

    /**
     * 分页新增
     *
     * @param curSize  当前条数
     * @param pageSize 分页大小
     * @param param    查询参数
     * @param logId    日志id
     * @return 新增总条数
     */
    @Override
    public int selectForInsert(int curSize, int pageSize, BBkMonitorVo param, Integer logId) {
        return baseMapper.insertBatch(curSize, pageSize, param, logId);
    }

    /**
     * 根据 状态查询
     *
     * @param page
     * @param pageSize
     * @param param
     * @return
     */
    @Override
    public List<BBkMonitorLogDetailEntity> selectListByStatus(int page, int pageSize, BBkMonitorLogDetailEntity param) {
        return baseMapper.selectListByStatus(page*pageSize, pageSize, param);
    }

    /**
     * 更新 状态
     *
     * @param id     主键id
     * @param status 状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer id, String status) {
        LambdaUpdateWrapper<BBkMonitorLogDetailEntity> wrapper = new LambdaUpdateWrapper<BBkMonitorLogDetailEntity>()
                .eq(BBkMonitorLogDetailEntity::getId, id)
                .set(BBkMonitorLogDetailEntity::getFlag, "OK")
                .set(BBkMonitorLogDetailEntity::getStatus, status);
        int update = baseMapper.update(null, wrapper);
//        log.error("updateStatus ---> {}", update);
    }

    /**
     * 错误日志, 不需要回滚
     * @param id 主键id
     * @param status 状态
     * @param exception
     */
    @Override
//    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void updateStatus(Integer id, String status, String exception) {
        LambdaUpdateWrapper<BBkMonitorLogDetailEntity> wrapper = new LambdaUpdateWrapper<BBkMonitorLogDetailEntity>()
                .eq(BBkMonitorLogDetailEntity::getId, id)
                .set(BBkMonitorLogDetailEntity::getStatus, status)
                .set(BBkMonitorLogDetailEntity::getFlag, "NG")
                .set(BBkMonitorLogDetailEntity::getException, exception);
        baseMapper.update(null, wrapper);
    }

    /**
     * 新增日志
     *
     * @param vo 日志
     */
    @Override
    public Integer insertLog(BBkMonitorLogDetailVo vo) {
        BBkMonitorLogDetailEntity entity = (BBkMonitorLogDetailEntity) BeanUtilsSupport.copyProperties(vo, BBkMonitorLogDetailEntity.class);
        entity.setStatus(DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS_1);
        entity.setFlag("ING");
        entity.setMonitor_code(vo.getMonitor_code());
        baseMapper.insert(entity);
        return entity.getId();
    }

}
