package com.xinyirun.scm.core.api.service.business.v1.sync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.sync.ApiBSyncStatusErrorVo;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusErrorEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;

/**
 * <p>
 * 业务数据同步状态服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
public interface ApiIBSyncStatusErrorService extends IService<BSyncStatusErrorEntity> {

    /**
     * 插入一条记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> save(ApiBSyncStatusErrorVo vo);

}
