package com.xinyirun.scm.core.system.service.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryOwnerGoodsQueryVo;

/**
 * <p>
 * 货主库存查询
 * </p>
 *
 * @author xyr
 * @since 2021-09-23
 */
public interface IMInventoryOwnerGoodsQueryService extends IService<MInventoryEntity> {

    /**
     * 货主库存查询
     */
    IPage<MInventoryOwnerGoodsQueryVo> queryInventoryOwnerGoods(MInventoryOwnerGoodsQueryVo searchCondition);

}
