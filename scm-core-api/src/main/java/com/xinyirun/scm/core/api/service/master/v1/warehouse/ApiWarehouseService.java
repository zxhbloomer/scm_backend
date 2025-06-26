package com.xinyirun.scm.core.api.service.master.v1.warehouse;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.api.vo.master.warehouse.ApiWarehouseVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiWarehouseService extends IService<MWarehouseEntity> {

    /**
     * 仓库下拉
     */
    List<ApiWarehouseVo> getWarehouse(ApiWarehouseVo vo);
}
