package com.xinyirun.scm.core.api.serviceimpl.business.v1.in.deliveryconfirm;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.bo.steel.ApiDeliveryConfirmBo;
import com.xinyirun.scm.bean.api.vo.business.orderdoc.ApiDeliveryConfirmVo;
import com.xinyirun.scm.bean.entity.busniess.wms.in.order.BInOrderGoodsEntity;
import com.xinyirun.scm.core.api.mapper.business.in.order.deliveryconfirm.ApiInOrderGoodsDeliveryConfirmMapper;
import com.xinyirun.scm.core.api.service.business.v1.in.deliveryconfirm.ApiIBInOrderGoodsDeliveryConfirmService;
import com.xinyirun.scm.core.system.mapper.business.wms.in.order.BInOrderGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Service
public class ApiBInOrderGoodsDeliveryConfirmServiceImpl extends ServiceImpl<BInOrderGoodsMapper, BInOrderGoodsEntity> implements ApiIBInOrderGoodsDeliveryConfirmService {

    @Autowired
    ApiInOrderGoodsDeliveryConfirmMapper mapper;

    @Override
    public List<ApiDeliveryConfirmVo> getDeliveryConfirmLists(ApiDeliveryConfirmBo bo) {
        return mapper.getDeliveryConfirmLists(bo);
    }
}
