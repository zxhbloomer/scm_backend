package com.xinyirun.scm.core.system.service.business.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventorySumVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo;
import com.xinyirun.scm.bean.system.vo.excel.query.MDailyInventoryExportVo;

import java.util.List;

/**
 * <p>
 * 入库单 服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-23
 */
public interface IBDailyInventoryService extends IService<BDailyInventoryEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BDailyInventoryVo> selectPage(BDailyInventoryVo searchCondition);

    IPage<BDailyInventoryVo> selectPageNew(BDailyInventoryVo searchCondition);

    /**
     * 获取合计信息，页面查询
     */
    BDailyInventorySumVo selectSumData(BDailyInventoryVo searchCondition);

    BDailyInventorySumVo selectSumDataNew(BDailyInventoryVo searchCondition);

    List<MDailyInventoryExportVo> selectExportList(List<BDailyInventoryVo> searchCondition);

    List<MDailyInventoryExportVo> selectExportAllList(BDailyInventoryVo searchCondition);

    /**
     * 分页查询库存
     */
    List<ApiInventoryVo> getInventory(ApiInventoryVo vo);

    List<ApiDailyInventoryVo> getDailyInventory();

    List<ApiMaterialConvertPriceVo> getMaterialConvertPrice();
}
