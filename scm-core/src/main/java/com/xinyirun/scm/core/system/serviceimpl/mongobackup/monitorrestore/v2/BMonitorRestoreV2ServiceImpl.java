package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BMonitorRestoreV2Entity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v2.BMonitorRestoreV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v2.IBMonitorRestoreV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 监管任务_出库 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Service
@Slf4j
public class BMonitorRestoreV2ServiceImpl extends ServiceImpl<BMonitorRestoreV2Mapper, BMonitorRestoreV2Entity> implements IBMonitorRestoreV2Service {

}
