package com.xinyirun.scm.core.api.serviceimpl.business.v1.out;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBSalesPricingVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BSalesPricingEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BWkSalesPricingEntity;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.api.mapper.business.out.ApiSalesPricingMapper;
import com.xinyirun.scm.core.api.mapper.business.out.ApiWkSalesPricingMapper;
import com.xinyirun.scm.core.api.service.business.v1.out.ApiIBSalesPricingService;
import com.xinyirun.scm.core.api.service.business.v1.out.ApiIBWkSalesPricingService;
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
public class ApiBSalesPricingServiceImpl extends ServiceImpl<ApiSalesPricingMapper, BSalesPricingEntity> implements ApiIBSalesPricingService {

    @Autowired
    private ApiWkSalesPricingMapper bWkSalesPricingMapper;

    @Autowired
    private ApiIBWkSalesPricingService apiIBWkSalesPricingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sync(List<ApiBSalesPricingVo> list) {

        // 锁表
        bWkSalesPricingMapper.lockB_wk_sales_pricing10();

        // 清空数据
        bWkSalesPricingMapper.deleteB_wk_sales_pricing00();

        List<BWkSalesPricingEntity> pWkList = new ArrayList<>();
        for (ApiBSalesPricingVo vo: list) {
            BWkSalesPricingEntity wkEntity = new BWkSalesPricingEntity();
            BeanUtilsSupport.copyProperties(vo, wkEntity);
            pWkList.add(wkEntity);
//            bWkPurchasePricingMapper.
        }
        apiIBWkSalesPricingService.saveBatch(pWkList);

        // 插入b_purchase_pricing
        bWkSalesPricingMapper.updateB_wk_sales_pricing30();
        bWkSalesPricingMapper.insertB_wk_sales_pricing20();

    }

}
