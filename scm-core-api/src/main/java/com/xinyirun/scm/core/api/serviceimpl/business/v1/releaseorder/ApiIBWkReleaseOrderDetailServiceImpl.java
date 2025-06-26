package com.xinyirun.scm.core.api.serviceimpl.business.v1.releaseorder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderDetailEntity;
import com.xinyirun.scm.core.api.mapper.business.releaseorder.ApiBWkReleaseOrderDetailMapper;
import com.xinyirun.scm.core.api.service.business.v1.releaseorder.ApiIBWkReleaseOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:29
 */
@Service
public class ApiIBWkReleaseOrderDetailServiceImpl extends ServiceImpl<ApiBWkReleaseOrderDetailMapper, BWkReleaseOrderDetailEntity> implements ApiIBWkReleaseOrderDetailService {


    /**
     * 锁表
     */
    @Override
    public void lockB_wk_release_order_detail00() {
        baseMapper.lockB_wk_release_order_detail00();
    }
}
