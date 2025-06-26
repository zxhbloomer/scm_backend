package com.xinyirun.scm.core.api.serviceimpl.business.v1.borroworder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.core.api.mapper.business.borroworder.ApiBBorrowOrderDetailMapper;
import com.xinyirun.scm.core.api.mapper.business.releaseorder.ApiBReleaseOrderDetailMapper;
import com.xinyirun.scm.core.api.service.business.v1.borroworder.ApiIBBorrowOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:29
 */
@Service
public class ApiIBBorrowOrderDetailServiceImpl extends ServiceImpl<ApiBBorrowOrderDetailMapper, BReleaseOrderDetailEntity> implements ApiIBBorrowOrderDetailService {


    /**
     * 新增表
     */
    @Override
    public void insertB_release_order_detail30() {
        baseMapper.insertB_release_order_detail30();
    }

    /**
     *
     */
    @Override
    public void updateB_release_order_detail30() {
        baseMapper.updateB_release_order_detail30();
    }
}
