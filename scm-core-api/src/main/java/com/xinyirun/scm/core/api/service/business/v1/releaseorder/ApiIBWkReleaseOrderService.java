package com.xinyirun.scm.core.api.service.business.v1.releaseorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderEntity;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:26
 */
public interface ApiIBWkReleaseOrderService extends IService<BWkReleaseOrderEntity> {

    /**
     * 锁表
     */
    void lockB_wk_release_order00();


}
