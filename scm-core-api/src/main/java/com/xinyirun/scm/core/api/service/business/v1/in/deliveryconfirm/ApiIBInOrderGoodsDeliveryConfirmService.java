package com.xinyirun.scm.core.api.service.business.v1.in.deliveryconfirm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.bo.steel.ApiDeliveryConfirmBo;
import com.xinyirun.scm.bean.api.vo.business.orderdoc.ApiDeliveryConfirmVo;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderGoodsEntity;

import java.util.List;

public interface ApiIBInOrderGoodsDeliveryConfirmService extends IService<BInOrderGoodsEntity> {
    /**
     * 获取附件信息
     *
     * @return
     */
    List<ApiDeliveryConfirmVo> getDeliveryConfirmLists(ApiDeliveryConfirmBo bo);
}
