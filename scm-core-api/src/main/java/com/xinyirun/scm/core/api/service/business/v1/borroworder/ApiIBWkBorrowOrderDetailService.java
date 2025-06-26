package com.xinyirun.scm.core.api.service.business.v1.borroworder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderDetailEntity;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:26
 */
public interface ApiIBWkBorrowOrderDetailService extends IService<BWkReleaseOrderDetailEntity> {

    /**
     * 锁表
     */
    void lockB_wk_release_order_detail00();


}
