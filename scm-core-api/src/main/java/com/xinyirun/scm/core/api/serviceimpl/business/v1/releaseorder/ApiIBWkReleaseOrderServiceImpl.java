package com.xinyirun.scm.core.api.serviceimpl.business.v1.releaseorder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BWkReleaseOrderEntity;
import com.xinyirun.scm.core.api.mapper.business.releaseorder.ApiBWkReleaseOrderMapper;
import com.xinyirun.scm.core.api.service.business.v1.releaseorder.ApiIBWkReleaseOrderService;
import org.springframework.stereotype.Service;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:29
 */
@Service
public class ApiIBWkReleaseOrderServiceImpl extends ServiceImpl<ApiBWkReleaseOrderMapper, BWkReleaseOrderEntity> implements ApiIBWkReleaseOrderService {


    /**
     * 锁表
     */
    @Override
    public void lockB_wk_release_order00() {
        baseMapper.lockB_wk_release_order00();
    }
}
