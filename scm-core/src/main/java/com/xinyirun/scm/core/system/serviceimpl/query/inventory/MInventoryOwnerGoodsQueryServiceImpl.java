package com.xinyirun.scm.core.system.serviceimpl.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQueryVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryOwnerGoodsQueryVo;
import com.xinyirun.scm.core.system.mapper.query.inventory.MInventoryDetailQueryMapper;
import com.xinyirun.scm.core.system.mapper.query.inventory.MInventoryOwnerGoodsQueryMapper;
import com.xinyirun.scm.core.system.service.query.inventory.IMInventoryDetailQueryService;
import com.xinyirun.scm.core.system.service.query.inventory.IMInventoryOwnerGoodsQueryService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存明细查询
 * </p>
 *
 * @author xinyirun
 * @since 2021-09-23
 */
@Service
public class MInventoryOwnerGoodsQueryServiceImpl extends BaseServiceImpl<MInventoryOwnerGoodsQueryMapper, MInventoryEntity> implements IMInventoryOwnerGoodsQueryService {

    @Autowired
    private MInventoryOwnerGoodsQueryMapper mInventoryMapper;

    /**
     * 库存明细查询
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MInventoryOwnerGoodsQueryVo> queryInventoryOwnerGoods(MInventoryOwnerGoodsQueryVo searchCondition) {
        // 分页条件
        Page<MInventoryOwnerGoodsQueryVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mInventoryMapper.queryInventoryOwnerGoods(pageCondition, searchCondition);
    }
}
