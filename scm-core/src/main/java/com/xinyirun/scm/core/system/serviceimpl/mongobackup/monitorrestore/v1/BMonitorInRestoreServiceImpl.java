package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1.BMonitorInRestoreEntity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v1.BMonitorInRestoreMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v1.IBMonitorInRestoreService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorInRestoreServiceImpl extends ServiceImpl<BMonitorInRestoreMapper, BMonitorInRestoreEntity> implements IBMonitorInRestoreService {

}
