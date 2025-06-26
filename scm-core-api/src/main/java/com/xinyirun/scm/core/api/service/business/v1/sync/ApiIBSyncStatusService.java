package com.xinyirun.scm.core.api.service.business.v1.sync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;

/**
 * <p>
 * 业务数据同步状态服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
public interface ApiIBSyncStatusService extends IService<BSyncStatusEntity> {

    /**
     * 插入一条记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> save(BSyncStatusVo vo);

}
