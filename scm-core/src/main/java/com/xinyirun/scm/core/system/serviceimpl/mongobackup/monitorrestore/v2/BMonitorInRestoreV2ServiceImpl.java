package com.xinyirun.scm.core.system.serviceimpl.mongobackup.monitorrestore.v2;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BMonitorInRestoreV2Entity;
import com.xinyirun.scm.core.system.mapper.mongobackup.monitorresotre.v2.BMonitorInRestoreV2Mapper;
import com.xinyirun.scm.core.system.service.mongobackup.monitorrestore.v2.IBMonitorInRestoreV2Service;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @Description
 * @date 2023/2/16 17:16
 */
@Service
public class BMonitorInRestoreV2ServiceImpl extends ServiceImpl<BMonitorInRestoreV2Mapper, BMonitorInRestoreV2Entity> implements IBMonitorInRestoreV2Service {

}
