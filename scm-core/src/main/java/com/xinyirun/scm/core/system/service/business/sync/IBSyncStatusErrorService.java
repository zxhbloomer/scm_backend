package com.xinyirun.scm.core.system.service.business.sync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.sync.BSyncStatusErrorEntity;

import java.util.Set;

/**
 * <p>
 * 业务数据同步状态服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
public interface IBSyncStatusErrorService extends IService<BSyncStatusErrorEntity> {

    /**
     * 查询同步日志错入数量
     * @return
     */
    Long selectCount();

    /**
     * 更新
     * @param collect
     * @param dictSysCodeTypeBIn
     */
    void updateSyncErrorStatus(Set<Integer> collect, String dictSysCodeTypeBIn, String sync_status);
}
