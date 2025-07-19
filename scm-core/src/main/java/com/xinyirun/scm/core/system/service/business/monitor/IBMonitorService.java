package com.xinyirun.scm.core.system.service.business.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiMonitorVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.monitor.*;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.*;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 监管任务service
 */
public interface IBMonitorService extends IService<BMonitorEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BMonitorVo> selectPage(BMonitorVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BMonitorVo> selectList(BMonitorVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BMonitorExportVo> selectExportList(BMonitorVo searchCondition);

    /**
     * 获取列表，页面查询
     */
    List<BMonitorExportVo> selectExportAllList(BMonitorVo searchCondition);

    /**
     * 查询明细
     */
    BMonitorVo getDetail(BMonitorVo searchCondition);

    /**
     * 查询明细
     */
    BMonitorVo selectById(Integer id);

    /**
     * 查询下一条数据
     */
    BMonitorVo getPrevData(BMonitorVo searchCondition);

    /**
     * 查询下一条数据
     */
    BMonitorVo getNextData(BMonitorVo searchCondition);

    /**
     * 查询明细-新
     */
    BMonitorVo get(BMonitorVo searchCondition);

    /**
     * 查询明细
     */
    BMonitorVo getFiles(BMonitorVo searchCondition);

    /**
     * 批量作废
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> cancel(List<BMonitorVo> searchCondition);

    /**
     * 入库审核
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> auditIn(List<BMonitorVo> searchCondition);

    /**
     * 出库审核
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> auditOut(List<BMonitorVo> searchCondition);

    /**
     * 批量驳回
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> reject(List<BMonitorVo> searchCondition);

    /**
     * 批量结算
     * @param searchCondition
     * @return
     */
    UpdateResultAo<Boolean> settlement(List<BMonitorVo> searchCondition);

    /**
     * 修改监管任务司机
     */
    void saveMonitorDriver(BMonitorVo vo);

    /**
     * 修改监管任务车牌
     */
    void saveMonitorVehicle(BMonitorVo vo);


    /**
     * 修改监管出库数据
     */
    void saveMonitorOutDelivery(BMonitorVo searchCondition);

    /**
     * 修改监管入库数据
     */
    void saveMonitorInUnload(BMonitorVo searchCondition);

    /**
     * 修改监管任务
     */
    void saveMonitor(BMonitorVo searchCondition);

    /**
     * 导出监管任务附件
     */
    List<BMonitorFileApiVo> export(List<BMonitorVo> searchCondition) throws Exception;

    /**
     * 导出监管任务附件
     */
    void exportAll(HttpServletResponse response) throws Exception;

    /**
     * 修改监管入库数
     */
    SFileInfoVo saveMonitorFile(BMonitorFileSaveVo searchCondition);

    /**
     * 查询明细
     */
    public void refreshTrack(BMonitorVo vo);

    /**
     * 查询 监管 合计
     * @param searchCondition
     * @return
     */
    BMonitorSumVo selectSum(BMonitorVo searchCondition);

    /**
     * id查询出库数据
     */
    BMonitorOutDeliveryVo selectByOutId(Integer id, String type);

    /**
     * 损耗报表
     * @param param
     * @return
     */
    IPage<BContractReportVo> queryQtyLossList(BContractReportVo param);

    /**
     * 损耗报表 求和
     * @param param
     * @return
     */
    BContractReportVo queryQtyLossListSum(BContractReportVo param);

    /**
     * 损耗报表 全部导出
     * @param param
     * @return
     */
    List<BQtyLossReportExportVo> queryQtyLossListExportAll(BContractReportVo param);

    /**
     * 损耗报表 部分导出
     * @param param
     * @return
     */
    List<BQtyLossReportExportVo> queryQtyLossListExport(List<BContractReportVo> param);

    /**
     * 物流订单损耗明细
     * @param param
     * @return
     */
    IPage<BQtyLossScheduleReportVo> queryScheduleList(BQtyLossScheduleReportVo param);

    /**
     * 物流订单损耗明细 合计
     * @param param
     * @return
     */
    BQtyLossScheduleReportVo queryScheduleListSum(BQtyLossScheduleReportVo param);

    /**
     * 物流订单损耗明细 全部导出
     * @param param
     * @return
     */
    List<BQtyLossScheduleDetailExportVo> queryScheduleListExportAll(BQtyLossScheduleReportVo param);

    /**
     * 物流订单损耗明细 部分导出
     * @param param
     * @return
     */
    List<BQtyLossScheduleDetailExportVo> queryScheduleListExport(List<BQtyLossScheduleReportVo> param);

    /**
     * 监管任务损耗明细
     * @param param
     * @return
     */
    IPage<BQtyLossScheduleReportVo> queryMonitorList(BQtyLossScheduleReportVo param);

    /**
     * 监管任务损耗明细 合计
     * @param param
     * @return
     */
    BQtyLossScheduleReportVo queryMonitorListSum(BQtyLossScheduleReportVo param);

    /**
     * 监管任务， 全部导出
     * @param param
     * @return
     */
    List<BQtyLossMonitorDetailExportVo> queryMonitorListExportAll(BQtyLossScheduleReportVo param);

    /**
     * 监管任务， 部分导出
     * @param param
     * @return
     */
    List<BQtyLossMonitorDetailExportVo> queryMonitorListExport(List<BQtyLossScheduleReportVo> param);

    /**
     * 在途报表 全部导出
     * @param param
     * @return
     */
    List<BInTransitReportExportVo> queryOnWayListExportAll(BContractReportVo param);

    /**
     * 在途报表 部分导出
     * @param param
     * @return
     */
    List<BInTransitReportExportVo> queryOnWayListExport(List<BContractReportVo> param);

    /**
     * 物流订单在途明细 全部导出
     * @param param
     * @return
     */
    List<BScheduleLossInTransitExportVo> queryScheduleListWayExportAll(BQtyLossScheduleReportVo param);

    /**
     * 物流订单在途明细 部分导出
     * @param param
     * @return
     */
    List<BScheduleLossInTransitExportVo> queryScheduleListWayExport(List<BQtyLossScheduleReportVo> param);

    /**
     * 监管任务在途明细 全部导出
     * @param param
     * @return
     */
    List<BMonitorLossInTransitExportVo> queryMonitorWayListExportAll(BQtyLossScheduleReportVo param);

    /**
     * 监管任务在途明细 部分导出
     * @param param 入参
     * @return List<MQtyLossMonitorWayDetailExportVo>
     */
    List<BMonitorLossInTransitExportVo> queryMonitorWayListExport(List<BQtyLossScheduleReportVo> param);

    /**
     * 在途 / 损耗 汇总
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    IPage<BContractReportVo> queryQtyTotalList(BContractReportVo param);

    /**
     * 在途 / 损耗 汇总求和
     * @param param 参数
     * @return MContractReportVo
     */
    BContractReportVo queryQtyTotalSumList(BContractReportVo param);

    /**
     * 损耗报表导出 全部
     * @param param
     * @return
     */
    List<BQtyLossExportVo> queryQtyLossAllExportAll(BContractReportVo param);

    /**
     * 损耗报表导出 部分
     * @param param
     * @return
     */
    List<BQtyLossExportVo> queryQtyLossExport(List<BContractReportVo> param);

    /**
     * 在途报表导出 全部
     * @param param
     * @return
     */
    List<BQtyInTransitExportVo> queryQtyOnWayAllExportAll(BContractReportVo param);

    /**
     * 在途报表导出 部分
     * @param param
     * @return
     */
    List<BQtyInTransitExportVo> queryQtyOnWayExport(List<BContractReportVo> param);

    /**
     * 当日累计调度统计
     * @param param
     * @return
     */
    BQtyLossScheduleReportVo getScheduleStatistics(BQtyLossScheduleReportVo param);

    /**
     * 更新验车状态
     * @param vo
     */
    void saveValidatVehicle(BMonitorVo vo);

    /**
     * 校验 物流订单下的 监管任务
     * @param scheduleId 物流订单Id
     */
    void checkMonitorStatus(Integer scheduleId);

    /**
     * 查询 列表数据
     * @param searchCondition
     * @return
     */
    List<BMonitorVo> selectListByParam(BMonitorVo searchCondition);


    BMonitorVo selectCount(BMonitorVo searchCondition);

    /**
     * 查询监管任务详情
     * @param id 将官任务 ID
     * @return BMonitorSyncVo
     */
    ApiMonitorVo selectMonitor2Sync(Integer id);

    /**
     * 全部同步, 只同步同步失败的和未同步的
     * @return
     */
//    List<BMonitorVo> selectAll2Sync();

    List<Integer> selectActiveMonitorByContainerId(Integer id);

    /**
     * 监管任务 删除
     * @param vo
     */
    void delete(List<BMonitorVo> vo);

    /**
     * 查询可以同步的监管任务
     * @param searchConditionList
     * @return
     */
    List<BMonitorVo> selectSyncData(List<BMonitorVo> searchConditionList);

    /**
     * 根据 codeList 查询
     * @param searchConditionList
     * @return
     */
    List<BMonitorVo> selectListByCodeList(List<String> searchConditionList);

    /**
     * 查询 验车 和 规格 日志
     * @param id
     * @return
     */
    BMonitorVo selectValidateAndTrackLog(Integer id);

    /**
     * 查询入库单 id
     * @param id
     * @return
     */
    BMonitorVo selectInByMonitorId(Integer id);

    /**
     * 执行状态回滚待审核
     */
    UpdateResultAo<Boolean> statusRollback(List<BMonitorVo> searchConditionList);

    /**
     * 在途 包含铁路虚拟库
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    IPage<BContractReportVo> queryQtyInventorTotalList(BContractReportVo param);

    /**
     * 在途报表明细包含铁路港口码头虚拟库
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    IPage<BContractReportVo> queryQtyInventorLossList(BContractReportVo param);

    /**
     * 在途报表明细包含铁路港口码头虚拟库 合计
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    BContractReportVo queryQtyInventorLossListSum(BContractReportVo param);

    /**
     * 在途报表包含铁路港口码头虚拟库 汇总 求和
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    BContractReportVo queryQtyInventorSumList(BContractReportVo param);


    /**
     * 在途报表汇总包含铁路港口码头虚拟库 全部导出
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    List<BQtyInTransitExportVo> queryQtyOnWayByInventorAllExportAll(BContractReportVo param);

    /**
     * 在途报表汇总包含铁路港口码头虚拟库 部分导出
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    List<BQtyInTransitExportVo> queryQtyOnWayByInventorExport(List<BContractReportVo> param);

    /**
     * 在途报表明细包含铁路港口码头虚拟库 全部导出
     * @param param 入参
     * @return IPage<MContractReportVo>
     */
    List<BInTransitReportExportVo> queryOnWayByInventorListExportAll(BContractReportVo param);


    /**
     * 在途报表明细包含铁路港口码头虚拟库 部分导出
     * @param param
     * @return
     */
    List<BInTransitReportExportVo> queryOnWayByInventorListExport(List<BContractReportVo> param);

    /**
     * 直采直销作废审核
     */
    UpdateResultAo<Boolean> auditDirect(List<BMonitorVo> searchConditionList);

    /**
     *  直采直销附件导出
     */
    List<BMonitorFileApiVo> exportDirect(List<BMonitorVo> searchCondition);

    /**
     *  直采直销监管任务数据导出
     */
    List<BMonitorDirectExportVo> exportDirectData(BMonitorVo searchCondition);

    /**
     *  直采直销监管任务数据导出
     */
    List<BMonitorDirectExportVo> exportDirectDataAll(BMonitorVo searchCondition);
}
