package com.xinyirun.scm.core.system.mapper.business.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorBackupEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 备份后保存入库, 出库数量, 用于计算监管任务 已出库, 已入库数量 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-07-05
 */
@Repository
public interface BMonitorBackupDataMapper extends BaseMapper<BMonitorBackupEntity> {

}
