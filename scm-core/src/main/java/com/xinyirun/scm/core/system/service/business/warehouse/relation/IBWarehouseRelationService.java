package com.xinyirun.scm.core.system.service.business.warehouse.relation;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.warehouse.relation.BWarehouseRelationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationDataVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
public interface IBWarehouseRelationService extends IService<BWarehouseRelationEntity> {

    /**
     * 更新仓库关系数据）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Boolean> setRelation(BWarehouseRelationDataVo vo);

    /**
     * 更新仓库关系数据）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BWarehouseRelationVo vo);

    /**
     * 按员工ID删除数据
     * @param staffId
     */
    void deleteByStaffId(Integer staffId);

    /**
     * 按岗位ID删除数据
     * @param positionId
     */
    void deleteByPositionId(Integer positionId);

    /**
     * 获取所有数据
     */
    List<BWarehouseRelationVo> select(BWarehouseRelationVo searchCondition);

    /**
     * 获取所有数据
     */
    Integer selectCountByRelationCode(BWarehouseRelationVo searchCondition);
}
