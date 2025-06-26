package com.xinyirun.scm.core.api.service.business.v1.inventory;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiInventoryVo;

import java.util.List;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiInventoryService extends IService<MInventoryEntity> {
        /**
         * 分页查询库存
         */
        List<ApiInventoryVo> getInventory(ApiInventoryVo vo);

        List<ApiDailyInventoryVo> getDailyInventory();

        List<ApiMaterialConvertPriceVo> getMaterialConvertPrice();
}
