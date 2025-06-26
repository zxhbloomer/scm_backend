package com.xinyirun.scm.core.system.serviceimpl.business.sync;

import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusEntity;
import com.xinyirun.scm.core.system.mapper.business.sync.BSyncStatusMapper;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务数据同步状态服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Service
public class BSyncStatusServiceImpl extends BaseServiceImpl<BSyncStatusMapper, BSyncStatusEntity> implements IBSyncStatusService {

}
