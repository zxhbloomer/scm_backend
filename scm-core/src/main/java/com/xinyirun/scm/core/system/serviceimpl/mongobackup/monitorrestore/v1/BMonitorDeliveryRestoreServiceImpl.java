package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v1;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1.BMonitorDeliveryRestoreEntity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v1.BMonitorDeliveryRestoreMapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v1.IBMonitorDeliveryRestoreService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorDeliveryRestoreServiceImpl extends ServiceImpl<BMonitorDeliveryRestoreMapper, BMonitorDeliveryRestoreEntity> implements IBMonitorDeliveryRestoreService {

}
