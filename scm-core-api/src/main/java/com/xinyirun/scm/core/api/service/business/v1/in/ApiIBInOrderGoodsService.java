package com.xinyirun.scm.core.api.service.business.v1.in;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBInOrderGoodsVo;
import com.xinyirun.scm.bean.entity.busniess.wms.in.order.BInOrderGoodsEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
public interface ApiIBInOrderGoodsService extends IService<BInOrderGoodsEntity> {

    /**
     * 插入逻辑
     */
    public InsertResultAo<Integer> insert(ApiBInOrderGoodsVo vo);

    /**
     * 插入逻辑
     */
    public UpdateResultAo<Integer> update(ApiBInOrderGoodsVo vo);

    /**
     * 删除逻辑
     */
    public void delete(Integer order_id);

}
