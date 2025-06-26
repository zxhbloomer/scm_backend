package com.xinyirun.scm.core.system.serviceimpl.business.sync;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusErrorEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.mapper.business.sync.BSyncStatusErrorMapper;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 业务数据同步状态服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Service
public class BSyncStatusErrorServiceImpl extends BaseServiceImpl<BSyncStatusErrorMapper, BSyncStatusErrorEntity> implements IBSyncStatusErrorService {

    @Override
    public Long selectCount() {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<BSyncStatusErrorEntity>()
                .eq(BSyncStatusErrorEntity::getStatus, DictConstant.DICT_LOG_SYNC_STATUS_E)
                .eq(BSyncStatusErrorEntity::getSync_status, "OK"));
        return count == null ? 0L : count;
    }

    /**
     * 根据 serial_id 和 serial_type 更新
     * @param collect
     * @param type
     */
    @Override
    public void updateSyncErrorStatus(Set<Integer> collect, String type, String sync_status) {
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        List<BSyncStatusErrorEntity> entityList = baseMapper.selectList(new LambdaQueryWrapper<BSyncStatusErrorEntity>()
                        .eq(BSyncStatusErrorEntity::getSerial_type, type)
                        .in(BSyncStatusErrorEntity::getSerial_detail_id, collect));
        if (!CollectionUtils.isEmpty(entityList)) {
            entityList.forEach(item -> item.setSync_status(sync_status));
            this.updateBatchById(entityList);
        }
    }

}
