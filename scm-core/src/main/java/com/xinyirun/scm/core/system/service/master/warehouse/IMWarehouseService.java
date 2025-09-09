package com.xinyirun.scm.core.system.service.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.*;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMWarehouseService extends IService<MWarehouseEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MWarehouseVo> selectPage(MWarehouseVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<MWarehouseVo> selectList(MWarehouseVo searchCondition) ;

    /**
     * 获取仓库库区库位信息
     */
    MWarehouseLocationBinVo selectWarehouseLocationBin(int warehouse_id) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MWarehouseVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MWarehouseVo vo);


    /**
     * 通过name查询
     *
     */
    List<MWarehouseEntity> selectByName(String name, Integer id);

    /**
     * 通过code查询
     *
     */
    List<MWarehouseEntity> selectByCode(String code, Integer id);

    /**
     * 通过shortName查询
     *
     */
    List<MWarehouseEntity> selectByShortName(String shortName, Integer id);

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MWarehouseVo> searchCondition);

    /**
     * 批量停用
     */
    void disSabledByIdsIn(List<MWarehouseVo> searchCondition);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MWarehouseVo> searchCondition);

    /**
     * 查询by id，返回结果
     */
    MWarehouseVo selectById(int id);

    /**
     * 仓库导出 全部
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    List<MWarehouseExportVo> exportAll(MWarehouseVo searchCondition);

    /**
     * 仓库导出 部分
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    List<MWarehouseExportVo> export(List<MWarehouseVo> searchCondition);

    /**
     * 导出专用查询方法 (完全按照岗位模式设计)
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     */
    List<MWarehouseExportVo> selectExportList(MWarehouseVo searchCondition);

    /**
     * 根据 仓库 code 查询三大件
     * @param warehouse_code
     * @return
     */
    List<MBLWBo> selectBLWByCode(String warehouse_code);

    /**
     * 获取穿梭框数据
     * @param searchCondition
     * @return
     */
    MWarehouseGroupTransferVo getWarehouseStaffTransferList(MWGroupTransferVo searchCondition);

    /**
     * 保存穿梭框数据，仓库组设置
     * @return
     */
    String setWarehouseGroupTransfer(MWGroupTransferVo bean);

    /**
     * 获取穿梭框数据-按员工
     * @param searchCondition
     * @return
     */
    MWarehouseStaffTransferVo getWarehouseStaffTransferList(MWStaffTransferVo searchCondition);

    /**
     * 保存穿梭框数据，员工设置仓库
     * @return
     */
    String setWarehouseStaffTransfer(MWStaffTransferVo bean);

    /**
     * 逻辑删除复原（按照岗位标准命名，单个删除）
     * @param searchCondition 仓库删除条件
     */
    void delete(MWarehouseVo searchCondition);
}
