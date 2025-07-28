package com.xinyirun.scm.core.system.service.business.adjust;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.business.adjust.BAdjustDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.*;

import java.util.List;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBAdjustDetailService extends IService<BAdjustDetailEntity> {

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BAdjustVo vo);

    /**
     * 插入一条已审核记录
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insertAudit(BAdjustVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BAdjustVo vo);

    /**
     * 悲观锁
     * @param id
     * @return
     */
    BAdjustDetailEntity setAdjustForUpdate(Integer id);

    /**
     * 按仓库类型仓库商品-调整
     * @param searchCondition
     * @return
     */
    IPage<BWarehouseGoodsVo> queryAdjustInventory(BWarehouseGoodsVo searchCondition);

    /**
     * 按仓库类型仓库商品-库存
     * @param searchCondition
     * @return
     */
    IPage<BWarehouseGoodsVo> queryInventoryList(BWarehouseGoodsVo searchCondition);

    /**
     * 计算全部数量
     * @param searchCondition
     * @return
     */
    BWarehouseGoodsVo queryReportInventorySum(BWarehouseGoodsVo searchCondition);

    /**
     * 导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsExportVo> queryReportInventoryExport(List<BWarehouseGoodsVo> searchCondition);

    /**
     * 导出全部
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsExportVo> queryReportInventoryExportAll(BWarehouseGoodsVo searchCondition);

    /**
     * 合计
     * @param searchCondition
     * @return
     */
    BWarehouseGoodsVo queryAdjustInventorySum(BWarehouseGoodsVo searchCondition);

    /**
     * 导出全部
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsAdjustExportVo> queryAdjustInventoryExportAll(BWarehouseGoodsVo searchCondition);

    /**
     * 导出部分
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsAdjustExportVo> queryAdjustInventoryExport(List<BWarehouseGoodsVo> searchCondition);

    /**
     * 库存导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsTotalExportVo> queryReportTotalExportAll(BWarehouseGoodsVo searchCondition);

    /**
     * 库存部分导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseGoodsTotalExportVo> queryReportTotalExport(List<BWarehouseGoodsVo> searchCondition);

    /**
     * 按仓库Id， 商品ID导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseInventoryExportVo> queryReportExportAll(BWarehouseGoodsVo searchCondition);

    /**
     * 按仓库Id， 商品ID导出
     * @param searchCondition
     * @return
     */
    List<BWarehouseInventoryExportVo> queryReportExport(List<BWarehouseGoodsVo> searchCondition);

    /**
     * 按仓库类型汇总商品
     * @return
     */
    List<BWarehouseGoodsVo> queryWarehouseTypeList(BWarehouseGoodsVo searchCondition);

    /**
     * 按仓库类型, 仓库商品, 存货
     * @param searchCondition
     * @return
     */
    IPage<BWarehouseGoodsVo> selectTotalPageList(BWarehouseGoodsVo searchCondition);

    /**
     * 按仓库类型, 仓库商品, 存货 合計
     * @param searchCondition
     * @return
     */
    BWarehouseGoodsVo selectTotalPageListSum(BWarehouseGoodsVo searchCondition);
}