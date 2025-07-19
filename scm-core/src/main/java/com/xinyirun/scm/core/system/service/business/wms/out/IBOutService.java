package com.xinyirun.scm.core.system.service.business.wms.out;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BQtyLossScheduleReportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BWarehouseGoodsOutExportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BWarehouseGoodsVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutImportVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutSumVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutExportVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 出库单 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IBOutService extends IService<BOutEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BOutVo> selectPage(BOutVo searchCondition);

    /**
     * 获取待办数量
     */
    Integer selectTodoCount(BOutVo searchCondition);

    /**
     * 获取合计信息，页面查询
     */
    BOutSumVo selectSumData(BOutVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BOutVo> selectList(BOutVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BOutVo> selectListByPlanId(Integer plan_id);

    /**
     * 获取列表，页面查询
     */
    List<BOutExportVo> selectExportList(List<BOutVo> searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BOutExportVo> selectExportAllList(BOutVo searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BOutVo vo);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    BOutVo selectById(int id);

    /**
     * 批量提交
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> submit(List<BOutVo> searchCondition);

    /**
     * 批量审核
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> audit(List<BOutVo> searchCondition);

    /**
     * 批量作废审核
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancelAudit(List<BOutVo> searchCondition);


    /**
     * 批量驳回
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> reject(List<BOutVo> searchCondition);

    /**
     * 批量作废驳回
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancelReject(List<BOutVo> searchCondition);

    /**
     * 批量作废
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancel(List<BOutVo> searchCondition);

    /**
     * 批量完成
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> finish(List<BOutVo> searchCondition);

    /**
     * 悲观锁
     * @param id
     * @return
     */
    BOutEntity setBillOutForUpdate(Integer id);

    /**
     * 列表查询
     */
    List<BOutEntity> selectIdsOut(List<BOutVo> searchCondition);

    /**
     * 导入入库单
     * @param list
     * @return
     */
    List<BOutVo> importBOut(List<BOutImportVo> list);

    /**
     * "按仓库类型仓库商品-出库
     * @param searchCondition
     * @return
     */
    IPage<BWarehouseGoodsVo> queryOutInventory(BWarehouseGoodsVo searchCondition);

    /**
     * 合计
     * @param searchCondition
     * @return
     */
    BWarehouseGoodsVo queryOutInventorySum(BWarehouseGoodsVo searchCondition);

    /**
     * 全部导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsOutExportVo> queryOutInventoryExportAll(BWarehouseGoodsVo searchCondition);

    /**
     * 批量导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsOutExportVo> queryOutInventoryExport(List<BWarehouseGoodsVo> searchCondition);

    /**
     * 当日累计出库量
     * @param param 入参
     * @return MQtyLossScheduleReportVo
     */
    List<BQtyLossScheduleReportVo> getOutStatistics(BQtyLossScheduleReportVo param);

    /**
     * 根据 计划 id 查询 主键id
     * @param plan_id 计划id集合
     * @return
     */
    List<BOutVo> selectIdsByOutPlanIds(List<Integer> plan_id);

    /**
     * 查询 出库单 商品 code 和 审核时间
     * @param id
     * @return
     */
    BOutVo selectEdtAndGoodsCode(Integer id);

    /**
     * 当日累计物流统计区域，增加原粮出库数量，取值采购合同关联的，且审批通过时间是当天的，且仓库类型是直属库的出库单.出库数量(换算前)
     * @return
     */
    BigDecimal getOutRawGrainCount(BQtyLossScheduleReportVo param);

    /**
     * 查询出库单列表 不查询数量
     * @param searchCondition
     * @return
     */
    List<BOutVo> selectPageListNotCount(BOutVo searchCondition);

    /**
     * 查询出库单列表 查询数量
     * @param searchCondition 查询条件
     * @return BOutVo
     */
    BOutVo selectPageListCount(BOutVo searchCondition);

    /**
     * 更新
     * @param bean 参数
     * @return UpdateResultAo<BOutVo>
     */
    UpdateResultAo<BOutVo> updateOut(BOutVo bean);

    /**
     * 出库单直接作废
     * @param searchConditionList
     */
    void cancelDirect(List<BOutVo> searchConditionList);

}
