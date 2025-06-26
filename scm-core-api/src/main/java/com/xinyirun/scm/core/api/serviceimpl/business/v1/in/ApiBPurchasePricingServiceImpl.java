package com.xinyirun.scm.core.api.serviceimpl.business.v1.in;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBPurchasePricingVo;
import com.xinyirun.scm.bean.entity.busniess.in.BPurchasePricingEntity;
import com.xinyirun.scm.bean.entity.busniess.in.BWkPurchasePricingEntity;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.mapper.business.in.ApiPurchasePricingMapper;
import com.xinyirun.scm.core.api.mapper.business.in.ApiWkPurchasePricingMapper;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIBPurchasePricingService;
import com.xinyirun.scm.core.api.service.business.v1.in.ApiIBWkPurchasePricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-11
 */
@Service
public class ApiBPurchasePricingServiceImpl extends ServiceImpl<ApiPurchasePricingMapper, BPurchasePricingEntity> implements ApiIBPurchasePricingService {

    @Autowired
    private ApiWkPurchasePricingMapper bWkPurchasePricingMapper;

    @Autowired
    private ApiIBWkPurchasePricingService apiIBWkPurchasePricingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(List<ApiBPurchasePricingVo> list) {

        // 锁表
        bWkPurchasePricingMapper.lockB_wk_purchase_pricing10();

        // 清空数据
        bWkPurchasePricingMapper.deleteB_wk_purchase_pricing00();

        List<BWkPurchasePricingEntity> pWkList = new ArrayList<>();
        for (ApiBPurchasePricingVo vo: list) {
            BWkPurchasePricingEntity wkEntity = new BWkPurchasePricingEntity();
            BeanUtilsSupport.copyProperties(vo, wkEntity);
            pWkList.add(wkEntity);
//            bWkPurchasePricingMapper.
        }
        apiIBWkPurchasePricingService.saveBatch(pWkList);

        // 插入b_purchase_pricing
        bWkPurchasePricingMapper.updateB_wk_purchase_pricing30();
        bWkPurchasePricingMapper.insertB_wk_purchase_pricing20();

    }
}
