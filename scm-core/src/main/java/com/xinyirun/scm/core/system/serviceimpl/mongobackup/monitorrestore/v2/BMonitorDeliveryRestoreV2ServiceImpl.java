package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BMonitorDeliveryRestoreV2Entity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v2.BMonitorDeliveryRestoreV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v2.IBMonitorDeliveryRestoreV2Service;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorDeliveryRestoreV2ServiceImpl extends ServiceImpl<BMonitorDeliveryRestoreV2Mapper, BMonitorDeliveryRestoreV2Entity> implements IBMonitorDeliveryRestoreV2Service {

}
