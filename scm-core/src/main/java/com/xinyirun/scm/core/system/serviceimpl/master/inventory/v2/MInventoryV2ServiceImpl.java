package com.xinyirun.scm.core.system.serviceimpl.master.inventory.v2;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.inventory.v2.MInventoryV2Entity;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventorySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MMonitorInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MWarehouseVo;
import com.xinyirun.scm.common.annotations.DataScopeAnnotion;
import com.xinyirun.scm.core.system.mapper.master.inventory.v2.MInventoryV2Mapper;
import com.xinyirun.scm.core.system.service.master.inventory.V2.IMInventoryV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MInventoryV2ServiceImpl extends BaseServiceImpl<MInventoryV2Mapper, MInventoryV2Entity> implements IMInventoryV2Service {

    @Autowired
    private MInventoryV2Mapper mInventoryMapper;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MInventoryVo> selectPage(MInventoryVo searchCondition) {
        // 分页条件
        Page<MInventoryV2Entity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mInventoryMapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 查询分页列表-按货主
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<MInventoryVo> selectPageByOwner(MInventoryVo searchCondition) {
        // 分页条件
        Page<MInventoryV2Entity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mInventoryMapper.selectPageByOwner(pageCondition, searchCondition);
    }

    @Override
    public MInventorySumVo selectSumByOwner(MInventoryVo searchCondition) {
        return mInventoryMapper.selectSumByOwner(searchCondition);
    }

    /**
     * 查询分页列表-按货主规格
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public IPage<MInventoryVo> selectPageByOwnerSpec(MInventoryVo searchCondition) {
        // 分页条件
        Page<MInventoryV2Entity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mInventoryMapper.selectPageByOwnerSpec(pageCondition, searchCondition);
    }

    /**
     * 查询分页列表-按货主规格
     * @param searchCondition
     * @return
     */
    @Override
    @DataScopeAnnotion(type = "01", type01_condition = "t.warehouse_id")
    public MInventorySumVo selectPageByOwnerSpecSum(MInventoryVo searchCondition) {
        return mInventoryMapper.selectSumByOwnerSpec(searchCondition);
    }

    @Override
    public List<MInventoryVo> selectList(MInventoryVo searchCondition) {
        return mInventoryMapper.selectList(searchCondition);
    }

    @Override
    public List<MInventoryV2Entity> getInventory(Integer bin_id, Integer owner_id,String lot , Integer sku_id) {
        return mInventoryMapper.select(bin_id, owner_id, lot, sku_id);
    }

    @Override
    public List<MInventoryV2Entity> getInventoryForUpdate(Integer bin_id, Integer owner_id,String lot , Integer sku_id) {
        return mInventoryMapper.selectForUpdate(bin_id, owner_id, lot, sku_id);
    }

    @Override
    public List<MInventoryV2Entity> getInventoryForUpdate(Integer bin_id, Integer owner_id , Integer sku_id) {
        return mInventoryMapper.selectForUpdate(bin_id, owner_id, null, sku_id);
    }

    @Override
    public MInventoryV2Entity getInventoryByIdForUpdate(Integer id) {
        return mInventoryMapper.getInventoryEntityById(id);
    }

    @Override
    public MInventoryVo getInventoryInfo(MInventoryVo searchCondition) {
        return mInventoryMapper.getInventoryInfo(searchCondition);
    }

    @Override
    public List<MInventoryVo> getInventoryInfoList(MInventoryVo searchCondition) {
        return mInventoryMapper.getInventoryInfoList(searchCondition);
    }

    /**
     * 根据 仓库ID 查询仓库库存量大于0, 锁定库存不等于0 的库存
     * @param searchCondition 仓库ID
     * @return List<MInventoryVo>
     */
    @Override
    public List<MInventoryVo> selectInventoryByWarehouse(List<MWarehouseVo> searchCondition) {
        return mInventoryMapper.selectInventoryByWarehouse(searchCondition);
    }

    /**
     * 查询库存异常数据
     *
     * @param searchCondition 入参
     * @return IPage<MMonitorInventoryVo>
     */
    @Override
    public IPage<MMonitorInventoryVo> selectInventoryDiff(MMonitorInventoryVo searchCondition) {
        // 分页条件
        Page<MInventoryV2Entity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 关闭 sql 优化
        pageCondition.setOptimizeCountSql(false);
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mInventoryMapper.selectInventoryDiff(searchCondition, pageCondition);
    }

    /**
     * 查询可用库存
     *
     * @param skuId       商品ID
     * @param warehouseId 仓库ID
     * @param ownerId     货主ID
     * @return
     */
    @Override
    public BigDecimal getQtyAvaibleBySWO(Integer skuId, Integer warehouseId, Integer ownerId) {
        return mInventoryMapper.getQtyAvaibleBySWO(skuId, warehouseId, ownerId);
    }
}
