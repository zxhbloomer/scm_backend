package com.xinyirun.scm.core.system.service.business.wms.out.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutOrderGoodsEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderGoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
public interface IBOutOrderGoodsService extends IService<BOutOrderGoodsEntity> {
    /**
     * 插入逻辑
     */
    public InsertResultAo<Integer> insert(BOutOrderGoodsVo vo);

    /**
     * 插入逻辑
     */
    public UpdateResultAo<Integer> update(BOutOrderGoodsVo vo);

    /**
     * 查询逻辑
     */
    public List<BOutOrderGoodsVo> list(BOutOrderGoodsVo searchCondition);
}
