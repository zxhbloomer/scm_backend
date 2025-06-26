package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1.BMonitorOutRestoreEntity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v1.BMonitorOutRestoreMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v1.IBMonitorOutRestoreService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorOutRestoreServiceImpl extends ServiceImpl<BMonitorOutRestoreMapper, BMonitorOutRestoreEntity> implements IBMonitorOutRestoreService {
}
