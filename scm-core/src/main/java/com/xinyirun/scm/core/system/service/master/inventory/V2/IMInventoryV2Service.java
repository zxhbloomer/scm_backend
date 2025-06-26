package com.xinyirun.scm.core.system.service.master.inventory.V2;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.inventory.v2.MInventoryV2Entity;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventorySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MMonitorInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMInventoryV2Service extends IService<MInventoryV2Entity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MInventoryVo> selectPage(MInventoryVo searchCondition);

    /**
     * 获取列表，页面查询-按货主
     */
    IPage<MInventoryVo> selectPageByOwner(MInventoryVo searchCondition);

    /**
     * 获取列表，页面查询-按货主
     */
    MInventorySumVo selectSumByOwner(MInventoryVo searchCondition);

    /**
     * 获取列表，页面查询-按货主规格
     */
    IPage<MInventoryVo> selectPageByOwnerSpec(MInventoryVo searchCondition);

    /**
     * 获取列表，页面查询-按货主规格
     */
    MInventorySumVo selectPageByOwnerSpecSum(MInventoryVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<MInventoryVo> selectList(MInventoryVo searchCondition);

    /**
     * 获取库存
     * @param bin_id 库位id
     * @param owner_id 货主id
     * @param lot 批次
     * @param sku_id 规格id
     * @return List<MInventoryV2Entity>
     */
    List<MInventoryV2Entity> getInventory(Integer bin_id, Integer owner_id, String lot, Integer sku_id);

    /**
     * 获取库存
     * 悲观锁，本身没有事务，由调用方进行事务控制
     * @param bin_id 库位id
     * @param owner_id 货主id
     * @param lot 批次
     * @param sku_id 规格id
     * @return List<MInventoryV2Entity>
     */
    List<MInventoryV2Entity> getInventoryForUpdate(Integer bin_id, Integer owner_id, String lot, Integer sku_id);

    /**
     * 获取库存
     * 悲观锁，本身没有事务，由调用方进行事务控制
     * @param bin_id 库位id
     * @param owner_id 货主id
     * @param sku_id 规格id
     * @return List<MInventoryV2Entity>
     */
    List<MInventoryV2Entity> getInventoryForUpdate(Integer bin_id, Integer owner_id, Integer sku_id);

    /**
     * 悲观锁查询
     * 悲观锁，本身没有事务，由调用方进行事务控制
     * @return MInventoryV2Entity
     */
    MInventoryV2Entity getInventoryByIdForUpdate(Integer id);

    /**
     * 根据货主 仓库 物料 查询库存信息
     * @param searchCondition 查询条件
     * @return MInventoryVo
     */
    MInventoryVo getInventoryInfo(MInventoryVo searchCondition) ;

    /**
     * 根据货主 物料 查询库存信息
     * @param searchCondition 查询条件
     * @return MInventoryVo
     */
    List<MInventoryVo> getInventoryInfoList(MInventoryVo searchCondition) ;

    /**
     * 根据 仓库ID 查询仓库库存
     * @param searchCondition 仓库ID
     * @return List<MInventoryVo>
     */
    List<MInventoryVo> selectInventoryByWarehouse(List<MWarehouseVo> searchCondition);

    /**
     * 查询库存异常数据
     * @param searchCondition 入参
     * @return IPage<MMonitorInventoryVo>
     */
    IPage<MMonitorInventoryVo> selectInventoryDiff(MMonitorInventoryVo searchCondition);

    /**
     * 查询可用库存
     * @param skuId 商品ID
     * @param warehouseId 仓库ID
     * @param ownerId 货主ID
     * @return
     */
    BigDecimal getQtyAvaibleBySWO(Integer skuId, Integer warehouseId, Integer ownerId);
}
