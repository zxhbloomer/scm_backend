package com.xinyirun.scm.core.system.service.business.warehouse.relation;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.relation.MWarehouseRelationEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.*;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MWarehouseVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
public interface IMWarehouseRelationService extends IService<MWarehouseRelationEntity> {

    /**
     * 获取所有数据，左侧树数据
     */
    List<MRelationTreeVo> getTreeList(MRelationTreeVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MWarehouseRelationVo vo);

    /**
     * 获取数据byid
     * @param bean
     * @return
     */
    MWarehouseRelationVo selectByid(MWarehouseRelationVo bean);

    /**
     * 删除
     * @param vo
     * @return
     */
    Boolean deleteById(MWarehouseRelationVo vo);

    /**
     * 根据code，进行 like 'code%'，匹配当前结点以及子结点
     * @param vo
     * @return
     */
    List<MWarehouseRelationVo> getDataByCode(MWarehouseRelationVo vo);

    /**
     * 获取所有的子节点数量，仅仅是数量
     * @param searchCondition
     * @return
     */
    MRelationCountsVo getAllRelationDataCount(MWarehouseRelationVo searchCondition);

    /**
     * 获取仓库分组数据
     * @param searchCondition
     * @return
     */
    List<MRelationTreeVo> getRelations(MWarehouseRelationVo searchCondition);

    /**
     * 获取所有数据
     */
    List<MRelationTreeVo> select(MWarehouseRelationVo searchCondition) ;

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    IPage<MWarehouseVo> getAllWarehouseListByPosition(MRelationTreeVo searchCondition);

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    Integer getAllWarehouseListByPositionCount(MRelationTreeVo searchCondition);

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    IPage<MWarehouseVo> getWarehouse(MRelationTreeVo searchCondition);

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    List<MWarehouseVo> getWarehouseList(MRelationTreeVo searchCondition);

    /**
     * 获取仓库清单，为穿梭框服务
     * @return
     */
    MWarehouseGroupTransferVo getWarehouseTransferList(MWarehouseTransferVo condition);

    /**
     * 保存穿梭框数据，仓库组设置
     * @return
     */
    String setWarehouseTransfer(MWarehouseTransferVo bean);

}
