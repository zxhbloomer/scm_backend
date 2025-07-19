package com.xinyirun.scm.core.api.service.business.v1.out;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutOrderGoodsVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutOrderGoodsEntity;
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
public interface ApiIBOutOrderGoodsService extends IService<BOutOrderGoodsEntity> {

    /**
     * 插入逻辑
     */
    public InsertResultAo<Integer> insert(ApiBOutOrderGoodsVo vo);

    /**
     * 插入逻辑
     */
    public UpdateResultAo<Integer> update(ApiBOutOrderGoodsVo vo);

    /**
     * 删除逻辑
     */
    public void delete(Integer order_id);


}
