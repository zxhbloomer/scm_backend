package com.xinyirun.scm.core.api.service.business.v1.borroworder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:26
 */
public interface ApiIBBorrowOrderDetailService extends IService<BReleaseOrderDetailEntity> {


    /**
     * 新增表
     */
    void insertB_release_order_detail30();

    void updateB_release_order_detail30();

}
