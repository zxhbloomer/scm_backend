package com.xinyirun.scm.core.system.service.business.warehouse.relation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.warehouse.position.BWarehousePositionEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.BWarehousePositionDataVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.BWarehousePositionVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.MWarehousePositionTransferVo;
import com.xinyirun.scm.bean.system.vo.business.warehouse.position.MWarehouseTransferVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
public interface IBWarehousePositionService extends IService<BWarehousePositionEntity> {

    /**
     * 更新仓库关系数据）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Boolean> setWarehouse(BWarehousePositionDataVo vo);

    /**
     * 获取所有数据
     */
    List<BWarehousePositionVo> select(BWarehousePositionVo searchCondition);

    /**
     * 获取所有数据
     */
    Integer selectCountByWarehouseCode(BWarehousePositionVo searchCondition);

    /**
     * 获取仓库清单，为穿梭框服务
     * @return
     */
    MWarehousePositionTransferVo getWarehouseTransferList(MWarehouseTransferVo condition);

    /**
     * 保存穿梭框数据，仓库组设置
     * @return
     */
    String setWarehouseTransfer(MWarehouseTransferVo bean);
}
