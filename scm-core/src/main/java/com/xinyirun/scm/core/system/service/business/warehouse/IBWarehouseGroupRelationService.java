package com.xinyirun.scm.core.system.service.business.warehouse;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.warehouse.BWarehouseGroupRelationEntity;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseTransferVo;

/**
 * <p>
 * 仓库关系表-一级 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
public interface IBWarehouseGroupRelationService extends IService<BWarehouseGroupRelationEntity> {

    /**
     * 获取仓库清单，为穿梭框服务
     * @return
     */
    BWarehouseGroupTransferVo getWarehouseTransferList(BWarehouseTransferVo condition);

    /**
     * 保存仓库框数据，仓库组-仓库设置
     * @return
     */
    BWarehouseGroupTransferVo setWarehouseTransfer(BWarehouseTransferVo bean);

}
