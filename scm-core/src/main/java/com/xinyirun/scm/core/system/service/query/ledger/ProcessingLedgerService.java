package com.xinyirun.scm.core.system.service.query.ledger;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.report.ledger.*;

import java.util.List;

/**
 * @Author: Wqf
 * @Description: 台账
 * @CreateTime : 2023/8/1 15:05
 */


public interface ProcessingLedgerService {

    /**
     * 饲用稻谷定向出入库进度报备表/明细表
     * @param searchCondition
     * @return
     */
    IPage<ProcessingRiceWarehouseInProgressVo> queryRicePageList(ProcessingRiceWarehouseInProgressVo searchCondition);

    /**
     * 饲用稻谷定向出入库进度报备表/明细表  合计
     * @param searchCondition
     * @return
     */
    ProcessingRiceWarehouseInProgressVo queryRicePageListSum(ProcessingRiceWarehouseInProgressVo searchCondition);

    /**
     * 饲用稻谷定向出入库进度报备表/明细表  导出
     * @param searchCondition
     * @return
     */
    List<ProcessingRiceWarehouseInProgressExportVo> queryRicePageListExport(ProcessingRiceWarehouseInProgressVo searchCondition);

    /**
     * 玉米进度表
     * @param searchCondition
     * @return
     */
    IPage<ProcessingMaizeAndWheatWarehouseInProgressVo> queryMaizePageList(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition);

    /**
     * 玉米进度表 合计
     * @param searchCondition
     * @return
     */
    ProcessingMaizeAndWheatWarehouseInProgressVo queryMaizePageListSum(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition);

    /**
     * 玉米进度表 导出
     * @param searchCondition
     * @return
     */
    List<ProcessingMaizeAndWheatWarehouseInProgressExportVo> queryMaizePageListExport(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition);

    /**
     * 小麦进度表
     * @param searchCondition
     * @return
     */
    IPage<ProcessingMaizeAndWheatWarehouseInProgressVo> queryWheatPageList(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition);

    /**
     * 小麦进度表 求和
     * @param searchCondition
     * @return
     */
    ProcessingMaizeAndWheatWarehouseInProgressVo queryWheatPageListSum(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition);

    /**
     * 小麦进度表 导出
     * @param searchCondition
     * @return
     */
    List<ProcessingMaizeAndWheatWarehouseInProgressExportVo> queryWheatListExport(ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition);


    /**
     * 稻壳出库明细表
     * @param param
     * @return
     */
    IPage<ProcessingRiceHullWarehouseOutDetailVo> queryRiceHullPageList(ProcessingRiceHullWarehouseOutDetailVo param);

    /**
     * 稻壳出库明细表 导出
     * @param param
     * @return
     */
    List<ProcessingRiceHullWarehouseOutDetailExportVo> exportRicehullOutList(ProcessingRiceHullWarehouseOutDetailVo param);

    /**
     * 糙米出库进度表
     * @param param
     * @return
     */
    IPage<ProcessingGrainWarehouseInOutDetailVo> queryGrainOutPageList(ProcessingGrainWarehouseInOutDetailVo param);

    /**
     * 糙米出库进度表 合计
     * @param param
     * @return
     */
    ProcessingGrainWarehouseInOutDetailVo queryGrainOutPageListSum(ProcessingGrainWarehouseInOutDetailVo param);

    /**
     * 糙米出库进度表 导出
     * @param param
     * @return
     */
    List<ProcessingGrainWarehouseOutDetailExportVo> queryGrainOutPageListExport(ProcessingGrainWarehouseInOutDetailVo param);

    /**
     * 糙米入库进度表
     * @param param
     * @return
     */
    IPage<ProcessingGrainWarehouseInOutDetailVo> queryGrainInPageList(ProcessingGrainWarehouseInOutDetailVo param);

    /**
     * 糙米入库进度表, 合计
     * @param param
     * @return
     */
    ProcessingGrainWarehouseInOutDetailVo queryGrainInListSum(ProcessingGrainWarehouseInOutDetailVo param);

    /**
     * 糙米入库进度表, 导出
     * @param param
     * @return
     */
    List<ProcessingGrainWarehouseInDetailExportVo> exportGrainInList(ProcessingGrainWarehouseInOutDetailVo param);


    /**
     * 混合物出库进度表
     * @param param
     * @return
     */
    IPage<ProcessingComboWarehouseOutProgressVo> queryComboOutPageList(ProcessingComboWarehouseOutProgressVo param);

    /**
     * 混合物 求和
     * @param param
     * @return
     */
    ProcessingComboWarehouseOutProgressVo queryComboOutListSum(ProcessingComboWarehouseOutProgressVo param);

    /**
     * 混合物 导出
     * @param param
     * @return
     */
    List<ProcessingComboWarehouseOutProgressExportVo> exportComboList(ProcessingComboWarehouseOutProgressVo param);
}
