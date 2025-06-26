package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1.BMonitorRestoreEntity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v1.BMonitorRestoreMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v1.IBMonitorRestoreService;
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
public class BMonitorRestoreServiceImpl extends ServiceImpl<BMonitorRestoreMapper, BMonitorRestoreEntity> implements IBMonitorRestoreService {

}
